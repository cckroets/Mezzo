package cs446.mezzo.metadata.art;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import java.util.List;

import cs446.mezzo.R;
import cs446.mezzo.data.Callback;
import cs446.mezzo.metadata.MusicBrainzManager;
import cs446.mezzo.metadata.Recording;
import cs446.mezzo.music.Song;
import cs446.mezzo.net.CoverArtArchive;
import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.inject.InjectResource;

/**
 * @author curtiskroetsch
 */
@Singleton
public class AlbumArtManager {

    private static final String TAG = AlbumArtManager.class.getName();
    private static final int CACHE_SIZE = 100;

    Context mContext;

    @Inject
    MusicBrainzManager mMusicBrainz;

    @Inject
    CoverArtArchive.API mArtArchive;

    @InjectResource(R.drawable.ic_default_art)
    Drawable mDefaultCoverArt;

    @Inject
    PaletteCache mPaletteCache;

    private Picasso mPicasso;
    private LruCache<String, String> mUrls;

    @Inject
    public AlbumArtManager(Context context) {
        mUrls = new LruCache<>(CACHE_SIZE);
        mPicasso = new Picasso.Builder(context)
                .addRequestHandler(new CoverArtRequestHandler(context))
                .indicatorsEnabled(true)
                .build();
        mContext = context;
    }

    private void setDefaultCoverArt(ImageView view) {
        view.setImageDrawable(mDefaultCoverArt);
    }

    public void setAlbumArt(final ImageView view, final Song song) {
        mPicasso.cancelRequest(view);
        mPicasso.load(song.getDataSource())
                .fit().centerCrop()
                .into(view, new com.squareup.picasso.Callback.EmptyCallback() {
                    @Override
                    public void onError() {
                        Log.e("PICASSO", "no metadata, load from network");
                        loadAlbumArtFromNetwork(song, view, null, true);
                    }
                });
    }

    public void setAlbumArt(final ImageView view, final Song song, final Callback<Palette> paletteCallback) {
        mPicasso.cancelRequest(view);
        mPicasso.load(song.getDataSource())
                .noPlaceholder()
                .fit().centerCrop()
                .transform(mPaletteCache)
                .into(view, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        final Bitmap key = ((BitmapDrawable) view.getDrawable()).getBitmap();
                        final Palette palette = mPaletteCache.getPalette(key);
                        paletteCallback.onSuccess(palette);
                    }

                    @Override
                    public void onError() {
                        loadAlbumArtFromNetwork(song, view, paletteCallback, false);
                    }
                });
    }

    private void loadAlbumArtFromNetwork(final Song song, final ImageView view, final Callback<Palette> paletteCallback, final boolean placeHolder) {
        mMusicBrainz.getRecording(song, new Callback<Recording>() {
            @Override
            public void onSuccess(Recording recording) {
                final List<String> mbids = recording.getReleaseMBIDs();
                Log.d(TAG, mbids.toString());
                if (mbids.isEmpty()) {
                    setDefaultCoverArt(view);
                } else {
                    new NetworkFetcher().fetch(song.getDataSource().toString(),
                            mbids, view, paletteCallback, placeHolder);
                }
            }

            @Override
            public void onFailure(Exception error) {
                Log.e(TAG, "MusicBrainz failed " + error.getMessage());
                setDefaultCoverArt(view);
                if (paletteCallback != null) {
                    paletteCallback.onFailure(error);
                }
            }
        });
    }

    public void getAlbumArt(final Song song, final Callback<Bitmap> callback) {
        mPicasso.load(song.getDataSource())
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        callback.onSuccess(bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        callback.onFailure(new RuntimeException("Could not load bitmap"));
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
    }


    private class NetworkFetcher implements retrofit.Callback<Image> {

        private ImageView mImageView;
        private Callback<Palette> mPaletteCallback;
        private List<String> mIds;
        private int mIndex;
        private boolean mPlaceHolder;
        private String mSongId;

        public void fetch(String songId, List<String> ids, ImageView view, Callback<Palette> callback, boolean placeHolder) {
            mPaletteCallback = callback;
            mImageView = view;
            mPlaceHolder = placeHolder;
            mIndex = 0;
            mIds = ids;
            mSongId = songId;
            final String url = mUrls.get(songId);
            if (url != null) {
                loadImage(url);
            } else {
                mArtArchive.getImage(mIds.get(mIndex), this);
            }
        }

        private void loadImage(String url) {
            final RequestCreator creator = mPicasso.load(url).fit().centerCrop();
            if (!mPlaceHolder) {
                creator.noPlaceholder();
            }
            if (mPaletteCallback != null) {
                creator.transform(mPaletteCache)
                        .into(mImageView, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                final Bitmap key = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
                                final Palette palette = mPaletteCache.getPalette(key);
                                mPaletteCallback.onSuccess(palette);
                            }

                            @Override
                            public void onError() {
                                mPaletteCallback.onFailure(new RuntimeException("Could not load image"));
                            }
                        });
            } else {
                creator.into(mImageView);
            }
        }

        @Override
        public void success(Image image, Response response) {
            mUrls.put(mSongId, image.getUrl());
            loadImage(image.getUrl());
        }

        @Override
        public void failure(RetrofitError error) {
            mIndex++;
            if (mIndex >= mIds.size()) {
                setDefaultCoverArt(mImageView);
                if (mPaletteCallback != null) {
                    mPaletteCallback.onFailure(error);
                }
            } else {
                mArtArchive.getImage(mIds.get(mIndex), this);
            }
        }
    }
}
