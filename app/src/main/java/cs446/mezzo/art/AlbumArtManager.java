package cs446.mezzo.art;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.widget.ImageView;

import com.google.inject.Inject;
import com.squareup.picasso.Picasso;

import java.util.Collection;

import cs446.mezzo.R;
import cs446.mezzo.data.Preferences;
import cs446.mezzo.music.Song;
import cs446.mezzo.net.CoverArtArchive;
import cs446.mezzo.net.MusicBrainz;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.inject.InjectResource;

/**
 * @author curtiskroetsch
 */
public class AlbumArtManager {

    private static final String KEY_MBIDS = "mbids";
    private static final String TAG = AlbumArtManager.class.getName();

    @Inject
    Context mContext;

    @Inject
    MusicBrainz.API mMusicBrainz;

    @Inject
    CoverArtArchive.API mArtArchive;

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
    }

    private void setDefaultCoverArt(ImageView view) {
        view.setImageDrawable(mDefaultCoverArt);
    }

    public void setAlbumArt(final ImageView view, final Song song) {
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
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "MusicBrainz failed " + error.getMessage());
                Log.e(TAG, "url = " + error.getUrl());
                setDefaultCoverArt(view);
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


    private class CoverArtFetcher implements Callback<Image> {

        private ImageView mImageView;
        private boolean mFetched;
        private int mFailures;
        private int mMaxFailures;

        public CoverArtFetcher() {

        }

        public void fetch(Collection<String> ids, ImageView view) {
            mFetched = false;
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
                        .into(mImageView);
            }
        }

        @Override
        public void failure(RetrofitError error) {
            Log.e(TAG, "ArtArchive failed : " + error.getMessage());
            mFailures++;
            if (mFailures == mMaxFailures) {
                setDefaultCoverArt(mImageView);
            }
        }
    }
}
