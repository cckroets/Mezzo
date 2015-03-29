package cs446.mezzo.metadata.art;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.graphics.Palette;
import android.widget.ImageView;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import cs446.mezzo.R;
import cs446.mezzo.data.Callback;
import cs446.mezzo.music.Song;

/**
 * @author curtiskroetsch
 */
@Singleton
public class AlbumArtManager {

    Context mContext;

    Drawable mDefaultCoverArt;

    @Inject
    PaletteCache mPaletteCache;

    private Picasso mPicasso;

    @Inject
    public AlbumArtManager(Context context, ArtCache artCache, CoverArtRequestHandler requestHandler) {
        mPicasso = new Picasso.Builder(context)
                .addRequestHandler(requestHandler)
                .indicatorsEnabled(false)
                .memoryCache(artCache)
                .build();
        mContext = context;
        mDefaultCoverArt = createErrorDrawable();
    }

    public void setAlbumArt(final ImageView view, final Song song) {
        mPicasso.cancelRequest(view);
        mPicasso.load(song.getDataSource())
                .placeholder(mDefaultCoverArt)
                .error(mDefaultCoverArt)
                .fit().centerCrop()
                .into(view);
    }

    public void setAlbumArt(final ImageView view, final Song song, final Callback<Palette> paletteCallback) {
        mPicasso.cancelRequest(view);
        mPicasso.load(song.getDataSource())
                .noPlaceholder()
                .error(mDefaultCoverArt)
                .fit().centerCrop()
                .transform(mPaletteCache)
                .into(view, createPaletteCallback(view, paletteCallback));
    }

    private com.squareup.picasso.Callback createPaletteCallback(final ImageView view, final Callback<Palette> paletteCallback) {
        return new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                final Bitmap key = ((BitmapDrawable) view.getDrawable()).getBitmap();
                final Palette palette = mPaletteCache.getPalette(key);
                paletteCallback.onSuccess(palette);
            }

            @Override
            public void onError() {
                paletteCallback.onFailure(new RuntimeException());
            }
        };
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

    private Drawable createErrorDrawable() {
        return mContext.getResources().getDrawable(R.color.transparent);
    }

}
