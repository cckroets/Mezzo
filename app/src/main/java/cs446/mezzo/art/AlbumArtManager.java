package cs446.mezzo.art;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.widget.ImageView;

import com.google.inject.Inject;
import com.google.inject.Singleton;
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
@Singleton
public class AlbumArtManager {

    private static final String TAG = AlbumArtManager.class.getName();

    @Inject
    Context mContext;

    @Inject
    MusicBrainzManager mMusicBrainz;

    @Inject
    CoverArtArchive.API mArtArchive;

    @InjectResource(R.drawable.ic_default_art)
    Drawable mDefaultCoverArt;

    @Inject
    public AlbumArtManager() {

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
        loadAlbumArtFromNetwork(song, view);
    }

    private void loadAlbumArtFromNetwork(final Song song, final ImageView view) {
        mMusicBrainz.getRecording(song, new cs446.mezzo.data.Callback<Recording>() {
            @Override
            public void onSuccess(Recording recording) {
                final Collection<String> mbids = recording.getReleaseMBIDs();
                Log.d(TAG, mbids.toString());
                if (mbids.isEmpty()) {
                    setDefaultCoverArt(view);
                } else {
                    new CoverArtFetcher().fetch(mbids, view);
                }
            }

            @Override
            public void onFailure(Exception error) {
                Log.e(TAG, "MusicBrainz failed " + error.getMessage());
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
