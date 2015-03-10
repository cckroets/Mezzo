package cs446.mezzo.metadata.art;

import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.squareup.picasso.Transformation;

/**
 * @author curtiskroetsch
 */
@Singleton
public final class PaletteCache implements Transformation {

    private static final String TRANSFORMATION_NAME = "palette";
    private static final int CACHE_SIZE = 100;

    private final Cache<Bitmap, Palette> mCache;

    @Inject
    public PaletteCache() {
        mCache = CacheBuilder.newBuilder()
                .weakKeys()
                .maximumSize(CACHE_SIZE)
                .build();
    }

    public Palette getPalette(Bitmap bitmap) {
        return mCache.getIfPresent(bitmap);
    }

    @Override
    public Bitmap transform(Bitmap source) {
        if (mCache.getIfPresent(source) == null) {
            mCache.put(source, Palette.generate(source));
        }
        return source;
    }

    @Override
    public String key() {
        return TRANSFORMATION_NAME;
    }
}
