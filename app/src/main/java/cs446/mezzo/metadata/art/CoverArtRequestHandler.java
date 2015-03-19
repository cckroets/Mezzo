package cs446.mezzo.metadata.art;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.util.LruCache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;
import com.squareup.picasso.RequestHandler;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cs446.mezzo.data.Preferences;
import cs446.mezzo.events.EventBus;
import cs446.mezzo.events.system.ActivityStoppedEvent;
import cs446.mezzo.metadata.MusicBrainzManager;
import cs446.mezzo.metadata.Recording;
import cs446.mezzo.net.CoverArtArchive;
import retrofit.RetrofitError;

/**
 * A Request Handler for Cover Arts.
 *
 * First we check if the file is encoded with album art.
 * If not, we ask MusicBrainz for any associated MBIDs (and cache this).
 *
 * We then iterate through the MBIDs and ask the CoverArtArchive API
 * if there is an image for the given MBID. If there is, we have a url
 * to load this image from, and we use vanilla Picasso to load this.
 *
 * We also cache the Url from the first url we getImageView, with a key for the song
 * so that we don't need to iterate through the MBID's each time. Note that
 * the Bitmap is not cached since it is recycled.
 *
 * @author curtiskroetsch
 */
class CoverArtRequestHandler extends RequestHandler {

    private static final String TAG = CoverArtRequestHandler.class.getName();
    private static final String KEY_CACHE = "image-cache";
    private static final int CACHE_SIZE = 200;
    private static final Object NOTHING = new Object();
    private static final int FAILURE_EXPIRE_SECONDS = 3;

    @Inject
    Context mContext;

    @Inject
    CoverArtArchive.API mArtArchive;

    @Inject
    MusicBrainzManager mMusicBrainz;

    Preferences mPreferences;

    private Picasso mPicasso;
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

    @Override
    public boolean canHandleRequest(Request data) {
        final Uri uri = data.uri;
        return ContentResolver.SCHEME_FILE.equals(uri.getScheme()) ||
                (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())
                        && MediaStore.AUTHORITY.equals(uri.getAuthority()));
    }

    @Override
    public Result load(Request data, int networkPolicy) throws IOException {
        final String key = data.uri.toString();
        if (mFailureCache.getIfPresent(key) != null) {
            Log.d(TAG, "known failure = " + data.uri);
            return new Result((Bitmap) null, Picasso.LoadedFrom.DISK);
        }
        Log.d(TAG, "start load = " + data.uri);
        final MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(mContext, data.uri);
        final byte[] bitmapData = retriever.getEmbeddedPicture();
        Log.d(TAG, "got bitmap for " + data.uri + ": " + bitmapData);
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
            return new Result(bitmap, Picasso.LoadedFrom.DISK);
        }
    }

    private Result loadFromNetwork(String key, String title, String artist) throws IOException {
        Log.d(TAG, "loadFromNetwork = " + key);
        final String url = mUrlCache.getIfPresent(key);
        if (url != null) {
            return loadImage(url);
        }

        final Recording recording = mMusicBrainz.getRecordingSync(title, artist);
        final List<String> mbids = recording.getReleaseMBIDs();
        Log.d(TAG, "mbids = " + mbids);

        for (String mbid : mbids) {
            Image image;
            try {
                image = mArtArchive.getReleaseGroupImage(mbid);
                Log.d(TAG, "success mbid = " + mbid);
            } catch (RetrofitError e) {
                Log.d(TAG, "failure mbid = " + mbid);
                continue;
            }
            if (image != null) {
                mUrlCache.put(key, image.getUrl());
                return loadImage(image.getUrl());
            }
        }

        mFailureCache.put(key, NOTHING);
        return new Result((Bitmap) null, Picasso.LoadedFrom.NETWORK);
    }

    private Result loadImage(String url) throws IOException {
        Log.d("RHandler", "loading image " + url);
        if (mPicasso == null) {
            mPicasso = Picasso.with(mContext);
        }
        final Bitmap bitmap = mPicasso.load(url).get();
        return new Result(bitmap, Picasso.LoadedFrom.NETWORK);
    }

    private void saveCache() {
        final Type type = new TypeToken<Map<String, String>>(){ }.getType();
        mPreferences.putObject(KEY_CACHE, type, mUrlCache.asMap());
        Log.d(TAG, "saving to cache");
    }

    private void loadCache() {
        final Type type = new TypeToken<Map<String, String>>(){ }.getType();
        final Map<String, String> cache = mPreferences.getObject(KEY_CACHE, type);
        if (cache != null) {
            Log.d(TAG, "Loading from cache success");
            mUrlCache.putAll(cache);
        } else {
            Log.d(TAG, "Loading from cache failed");
        }
    }

}
