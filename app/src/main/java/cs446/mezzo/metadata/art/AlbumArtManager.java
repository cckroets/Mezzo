package cs446.mezzo.metadata.art;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.model.stream.StreamModelLoader;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
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

    private static final float THUMBNAIL_SCALE = 0.3f;
    private static final String TAG = AlbumArtManager.class.getName();

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
        Glide.get(context).setMemoryCategory(MemoryCategory.HIGH);
    }

    public void setAlbumArt(final ImageView view, final Song song) {
        Glide.clear(view);
        Glide.with(mContext)
                .using(mCoverArtLoader)
                .load(song)
                .thumbnail(THUMBNAIL_SCALE)
                .centerCrop()
                .crossFade()
                .into(view);
    }

    public void setAlbumArt(final ImageView view, final Song song, final Callback<Palette> paletteCallback) {
        Glide.clear(view);
        final DrawableRequestBuilder<Song> builder = Glide.with(mContext)
                .using(mCoverArtLoader)
                .load(song)
                .centerCrop();
        if (mPaletteCache.getPalette(song) == null) {
            builder.transform(new PaletteTransform(song));
        }
        builder.listener(new RequestListener<Song, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, Song model, com.bumptech.glide.request.target.Target<GlideDrawable> target, boolean isFirstResource) {
                paletteCallback.onFailure(e);
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, Song model, com.bumptech.glide.request.target.Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                Log.d(TAG, "onResourceReady " + model.getTitle());
                final Palette palette = mPaletteCache.getPalette(model);
                if (palette == null) {
                    paletteCallback.onFailure(new IllegalStateException("Palette wasn't loaded"));
                } else {
                    paletteCallback.onSuccess(palette);
                }
                return false;
            }
        }).into(view);
    }


    public void getAlbumArt(final Song song, final Callback<Bitmap> callback) {
        Glide.with(mContext)
                .using(mCoverArtLoader)
                .load(song)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        callback.onSuccess(resource);
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        callback.onFailure(e);
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
            Log.d(TAG, "transforming " + mSong.getTitle());
            if (mPaletteCache.getPalette(mSong) == null) {
                mPaletteCache.putPalette(mSong, toTransform);
            }
            return toTransform;
        }

        @Override
        public String getId() {
            return "t_" + System.currentTimeMillis();
        }
    }

}
