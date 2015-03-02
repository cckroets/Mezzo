package cs446.mezzo.art;

import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.squareup.picasso.Transformation;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author curtiskroetsch
 */
@Singleton
public final class PaletteCache implements Transformation {

    private final Map<Bitmap, Palette> mCache = new WeakHashMap<Bitmap, Palette>();

    @Inject
    public PaletteCache() {

    }

    public Palette getPalette(Bitmap bitmap) {
        return mCache.get(bitmap);
    }

    @Override public Bitmap transform(Bitmap source) {
        final Palette palette = Palette.generate(source);
        mCache.put(source, palette);
        return source;
    }

    @Override
    public String key() {
        return "palette";
    }
}
