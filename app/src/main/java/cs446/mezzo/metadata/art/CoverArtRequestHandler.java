package cs446.mezzo.metadata.art;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.stream.StreamModelLoader;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.squareup.otto.Subscribe;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cs446.mezzo.data.Preferences;
import cs446.mezzo.events.EventBus;
import cs446.mezzo.events.system.ActivityStoppedEvent;
import cs446.mezzo.metadata.MusicBrainzManager;
import cs446.mezzo.metadata.Recording;
import cs446.mezzo.music.Song;
import cs446.mezzo.net.CoverArtArchive;
import retrofit.RetrofitError;

/**
 * A Request Handler for Cover Arts.
 * <p/>
 * First we check if the file is encoded with album art.
 * If not, we ask MusicBrainz for any associated MBIDs (and cache this).
 * <p/>
 * We then iterate through the MBIDs and ask the CoverArtArchive API
 * if there is an image for the given MBID. If there is, we have a url
 * to load this image from, and we use vanilla Picasso to load this.
 * <p/>
 * We also cache the Url from the first url we getImageView, with a key for the song
 * so that we don't need to iterate through the MBID's each time. Note that
 * the Bitmap is not cached since it is recycled.
 *
 * @author curtiskroetsch
 */
class CoverArtRequestHandler implements StreamModelLoader<Song> {

    private static final String TAG = CoverArtRequestHandler.class.getName();
    private static final String KEY_CACHE = "image-cache";
    private static final int CACHE_SIZE = 200;
    private static final Object NOTHING = new Object();
    private static final int FAILURE_EXPIRE_SECONDS = 30;

    @Inject
    Context mContext;

    @Inject
    CoverArtArchive.API mArtArchive;

    @Inject
    MusicBrainzManager mMusicBrainz;

    Preferences mPreferences;

    private Cache<String, String> mUrlCache;
    private Cache<String, Object> mFailureCache;

    @Inject
    public CoverArtRequestHandler(Preferences preferences) {
        mPreferences = preferences;
        mUrlCache = CacheBuilder.newBuilder()
                .maximumSize(CACHE_SIZE)
                .build();
        mFailureCache = CacheBuilder.newBuilder()
                .expireAfterAccess(FAILURE_EXPIRE_SECONDS, TimeUnit.SECONDS)
                .build();
        mPreferences = preferences;
        loadCache();
        EventBus.register(this);
    }

    @Subscribe
    public void onActivityStopped(ActivityStoppedEvent event) {
        saveCache();
    }


    private void saveCache() {
        final Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        mPreferences.putObject(KEY_CACHE, type, mUrlCache.asMap());
        Log.d(TAG, "saving to cache");
    }

    private void loadCache() {
        final Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        final Map<String, String> cache = mPreferences.getObject(KEY_CACHE, type);
        if (cache != null) {
            Log.d(TAG, "Loading from cache success");
            mUrlCache.putAll(cache);
        } else {
            Log.d(TAG, "Loading from cache failed");
        }
    }

    @Override
    public DataFetcher<InputStream> getResourceFetcher(Song model, int width, int height) {
        return new MezzoDataFetcher(model, width, height);
    }

    public class MezzoDataFetcher implements DataFetcher<InputStream> {

        private Song mSong;
        private int mWidth;
        private int mHeight;
        private boolean mCancelled;

        public MezzoDataFetcher(Song song, int w, int h) {
            mSong = song;
            mWidth = w;
            mHeight = h;
            mCancelled = false;
        }

        @Override
        public InputStream loadData(Priority priority) throws Exception {
            final String key = mSong.getDataSource().toString();
            if (mFailureCache.getIfPresent(key) != null) {
                return null;
            }
            Log.d(TAG, "loading... " + key);
            final MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(mContext, mSong.getDataSource());
            final byte[] bitmapData = retriever.getEmbeddedPicture();
            Log.d(TAG, "got bitmap for " + mSong.getDataSource() + ": " + bitmapData);
            final String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            final String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            retriever.release();
            Log.d(TAG, "done with " + key);
            if (bitmapData == null) {
                return loadFromNetwork(key, title, artist);
            } else {
                final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length);
                if (bitmap == null) {
                    return loadFromNetwork(key, title, artist);
                }
                return new ByteArrayInputStream(bitmapData);
            }
        }

        private InputStream loadFromNetwork(String key, String title, String artist) throws Exception {
            if (mCancelled) {
                Log.d(TAG, "cancelling = " + key);
                return null;
            }
            Log.d(TAG, "loadFromNetwork = " + key);
            final String url = mUrlCache.getIfPresent(key);
            if (url != null) {
                return loadImage(url, mWidth, mHeight);
            }

            final Recording recording = mMusicBrainz.getRecordingSync(title, artist);
            final List<String> mbids = recording.getReleaseMBIDs();
            final List<String> blacklist = new ArrayList<>();
            Log.d(TAG, "mbids = " + mbids);

            for (String mbid : mbids) {
                Image image;
                try {
                    image = mArtArchive.getReleaseGroupImage(mbid);
                    Log.d(TAG, "success mbid = " + mbid);
                } catch (RetrofitError e) {
                    if (e.getResponse() != null && e.getResponse().getStatus() == 404) {
                        blacklist.add(mbid);
                    }
                    Log.d(TAG, "failure mbid = " + mbid + (e.getResponse() == null ? " no status" : ", status = " + e.getResponse().getStatus()));
                    continue;
                }
                if (image != null) {
                    mUrlCache.put(key, image.getUrl());
                    mMusicBrainz.removeMbids(title, artist, recording, blacklist);
                    return loadImage(image.getUrl(), mWidth, mHeight);
                }
            }

            mFailureCache.put(key, NOTHING);
            return null;
        }

        private InputStream loadImage(String url, int w, int h) throws Exception {
            final byte[] bytes = Glide.with(mContext).load(url).asBitmap().toBytes().into(w, h).get();
            return new ByteArrayInputStream(bytes);
        }

        @Override
        public void cleanup() {

        }

        @Override
        public String getId() {
            return mSong.getDataSource().toString();
        }

        @Override
        public void cancel() {
            Log.d(TAG, "cancel " + mSong.getDataSource().toString());
            mCancelled = true;
        }
    }
}
