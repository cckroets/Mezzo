package cs446.mezzo.art;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
<<<<<<< Updated upstream
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.support.v7.graphics.Palette;
=======
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
>>>>>>> Stashed changes
import android.util.Log;
import android.widget.ImageView;

import com.google.inject.Inject;
<<<<<<< Updated upstream
import com.google.inject.Singleton;
=======
>>>>>>> Stashed changes
import com.squareup.picasso.Picasso;

import java.util.Collection;

import cs446.mezzo.R;
<<<<<<< Updated upstream
import cs446.mezzo.data.Callback;
import cs446.mezzo.music.Song;
import cs446.mezzo.net.CoverArtArchive;
=======
import cs446.mezzo.data.Preferences;
import cs446.mezzo.music.Song;
import cs446.mezzo.net.CoverArtArchive;
import cs446.mezzo.net.MusicBrainz;
import retrofit.Callback;
>>>>>>> Stashed changes
import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.inject.InjectResource;

/**
 * @author curtiskroetsch
 */
<<<<<<< Updated upstream
@Singleton
public class AlbumArtManager {

=======
public class AlbumArtManager {

    private static final String KEY_MBIDS = "mbids";
>>>>>>> Stashed changes
    private static final String TAG = AlbumArtManager.class.getName();

    @Inject
    Context mContext;

    @Inject
<<<<<<< Updated upstream
    MusicBrainzManager mMusicBrainz;
=======
    MusicBrainz.API mMusicBrainz;
>>>>>>> Stashed changes

    @Inject
    CoverArtArchive.API mArtArchive;

<<<<<<< Updated upstream
    @InjectResource(R.drawable.ic_default_art)
    Drawable mDefaultCoverArt;

    @Inject
    PaletteCache mPaletteCache;

    @Inject
    public AlbumArtManager() {

=======
    @Inject
    Preferences mPreferences;

    @InjectResource(R.drawable.ic_default_art)
    Drawable mDefaultCoverArt;

    CoverArtFetcher mCoverArtFetcher;

    @Inject
    public AlbumArtManager() {
        mCoverArtFetcher = new CoverArtFetcher();
    }

    private static String generateKey(Song song) {
        return KEY_MBIDS + song.getDataSource().toString();
    }

    private void saveMbids(Song song, Collection<String> mbids) {
        mPreferences.putStrings(generateKey(song), mbids);
    }

    private Collection<String> loadMbids(Song song) {
        return mPreferences.getStrings(generateKey(song));
>>>>>>> Stashed changes
    }

    private void setDefaultCoverArt(ImageView view) {
        view.setImageDrawable(mDefaultCoverArt);
    }

    public void setAlbumArt(final ImageView view, final Song song) {
<<<<<<< Updated upstream
        setAlbumArt(view, song, null);
    }

    public void setAlbumArt(final ImageView view, final Song song, final Callback<Palette> paletteCallback) {
        final Bitmap encodedCoverArt = getAlbumArt(song);
        if (encodedCoverArt != null) {
            view.setImageBitmap(encodedCoverArt);
            Palette.generateAsync(encodedCoverArt, new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    if (paletteCallback != null) {
                        paletteCallback.onSuccess(palette);
                    }
                }
            });
            return;
        }
        loadAlbumArtFromNetwork(song, view, paletteCallback);
    }

    private void loadAlbumArtFromNetwork(final Song song, final ImageView view, final Callback<Palette> paletteCallback) {
        mMusicBrainz.getRecording(song, new Callback<Recording>() {
            @Override
            public void onSuccess(Recording recording) {
                final Collection<String> mbids = recording.getReleaseMBIDs();
                Log.d(TAG, mbids.toString());
                if (mbids.isEmpty()) {
                    setDefaultCoverArt(view);
                } else {
                    new CoverArtFetcher().fetch(mbids, view, paletteCallback);
=======
        final Bitmap encodedCoverArt = getAlbumArt(song);
        if (encodedCoverArt != null) {
            view.setImageBitmap(encodedCoverArt);
            return;
        }
        final Collection<String> mbids = loadMbids(song);
        if (mbids != null) {
            mCoverArtFetcher.fetch(mbids, view);
        } else {
            loadAlbumArtFromNetwork(song, view);
        }
    }

    private void loadAlbumArtFromNetwork(final Song song, final ImageView view) {
        final Query query = new Query(song.getArtist(), song.getAlbum());
        mMusicBrainz.getReleaseGroups(query.toString(), new Callback<ReleaseGroupCollection>() {
            @Override
            public void success(ReleaseGroupCollection releaseGroupCollection, Response response) {
                final Collection<String> mbids = releaseGroupCollection.getMBIDs();
                if (mbids.isEmpty()) {
                    setDefaultCoverArt(view);
                } else {
                    saveMbids(song, mbids);
                    mCoverArtFetcher.fetch(mbids, view);
>>>>>>> Stashed changes
                }
            }

            @Override
<<<<<<< Updated upstream
            public void onFailure(Exception error) {
                Log.e(TAG, "MusicBrainz failed " + error.getMessage());
                setDefaultCoverArt(view);
                if (paletteCallback != null) {
                    paletteCallback.onFailure(error);
                }
=======
            public void failure(RetrofitError error) {
                Log.e(TAG, "MusicBrainz failed " + error.getMessage());
                Log.e(TAG, "url = " + error.getUrl());
                setDefaultCoverArt(view);
>>>>>>> Stashed changes
            }
        });
    }

    public Bitmap getAlbumArt(Song song) {
        if (song == null) {
            return null;
        }
        final MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(mContext, song.getDataSource());
        final byte[] bitmapData = retriever.getEmbeddedPicture();
        retriever.release();
        return (bitmapData == null) ? null : BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length);
    }


<<<<<<< Updated upstream
    private class CoverArtFetcher implements retrofit.Callback<Image> {

        private ImageView mImageView;
        private Callback<Palette> mPaletteCallback;
=======
    private class CoverArtFetcher implements Callback<Image> {

        private ImageView mImageView;
>>>>>>> Stashed changes
        private boolean mFetched;
        private int mFailures;
        private int mMaxFailures;

        public CoverArtFetcher() {

        }

<<<<<<< Updated upstream
        public void fetch(Collection<String> ids, ImageView view, Callback<Palette> callback) {
            mFetched = false;
            mPaletteCallback = callback;
=======
        public void fetch(Collection<String> ids, ImageView view) {
            mFetched = false;
>>>>>>> Stashed changes
            mImageView = view;
            mFailures = 0;
            mMaxFailures = ids.size();
            for (String id : ids) {
                mArtArchive.getImage(id, this);
            }
        }

        @Override
        public void success(Image image, Response response) {
            if (!mFetched) {
                mFetched = true;
                Picasso.with(mContext)
                        .load(image.getUrl())
                        .placeholder(mDefaultCoverArt)
                        .fit().centerCrop()
<<<<<<< Updated upstream
                        .transform(mPaletteCache)
                        .into(mImageView, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                if (mPaletteCallback != null) {
                                    final Bitmap key = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
                                    final Palette palette = mPaletteCache.getPalette(key);
                                    mPaletteCallback.onSuccess(palette);
                                }
                            }

                            @Override
                            public void onError() {
                                if (mPaletteCallback != null) {
                                    mPaletteCallback.onFailure(new RuntimeException("Could not load image"));
                                }
                            }
                        });
=======
                        .into(mImageView);
>>>>>>> Stashed changes
            }
        }

        @Override
        public void failure(RetrofitError error) {
            Log.e(TAG, "ArtArchive failed : " + error.getMessage());
            mFailures++;
            if (mFailures == mMaxFailures) {
                setDefaultCoverArt(mImageView);
<<<<<<< Updated upstream
                if (mPaletteCallback != null) {
                    mPaletteCallback.onFailure(error);
                }
=======
>>>>>>> Stashed changes
            }
        }
    }
}
