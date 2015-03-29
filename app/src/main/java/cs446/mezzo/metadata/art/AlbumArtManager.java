package cs446.mezzo.metadata.art;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.model.stream.StreamModelLoader;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.google.inject.Inject;
import com.google.inject.Singleton;

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

    StreamModelLoader<Song> mCoverArtLoader;

    @Inject
    public AlbumArtManager(Context context, CoverArtRequestHandler requestHandler) {
        mContext = context;
        mDefaultCoverArt = createErrorDrawable();
        mCoverArtLoader = requestHandler;
    }

    public void setAlbumArt(final ImageView view, final Song song) {
        Glide.clear(view);
        Glide.with(mContext)
                .using(mCoverArtLoader)
                .load(song)
                .fitCenter()
                .crossFade()
                .into(view);
    }

    public void setAlbumArt(final ImageView view, final Song song, final Callback<Palette> paletteCallback) {
        Glide.clear(view);
        Glide.with(mContext)
                .using(mCoverArtLoader)
                .load(song)
                .fitCenter()
                .crossFade()
                .transform(new PaletteTransform(song))
                .listener(new RequestListener<Song, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, Song model, com.bumptech.glide.request.target.Target<GlideDrawable> target, boolean isFirstResource) {
                        paletteCallback.onFailure(e);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, Song model, com.bumptech.glide.request.target.Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        Log.d("Palette", "onResourceReady " + model.getTitle());
                        final Palette palette = mPaletteCache.getPalette(model);
                        if (palette == null) {
                            paletteCallback.onFailure(new IllegalStateException("Palette wasn't loaded"));
                        } else {
                            paletteCallback.onSuccess(palette);
                        }
                        return false;
                    }
                })
                .into(view);
    }


    public void getAlbumArt(final Song song, final Callback<Bitmap> callback) {
        Glide.with(mContext)
                .using(mCoverArtLoader)
                .load(song).asBitmap().listener(new RequestListener<Song, Bitmap>() {
            @Override
            public boolean onException(Exception e, Song model, com.bumptech.glide.request.target.Target<Bitmap> target, boolean isFirstResource) {
                callback.onFailure(e);
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Song model, com.bumptech.glide.request.target.Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                callback.onSuccess(resource);
                return false;
            }
        });
    }

    private Drawable createErrorDrawable() {
        return mContext.getResources().getDrawable(R.color.transparent);
    }

    private class PaletteTransform extends BitmapTransformation {

        private Song mSong;

        public PaletteTransform(Song song) {
            super(mContext);
            mSong = song;
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            Log.d("Palette", "transforming " + mSong.getTitle());
            if (mPaletteCache.getPalette(mSong) == null) {
                mPaletteCache.putPalette(mSong, toTransform);
            }
            return toTransform;
        }

        @Override
        public String getId() {
            return "PALETTE_TRANSFORM";
        }
    }

}
