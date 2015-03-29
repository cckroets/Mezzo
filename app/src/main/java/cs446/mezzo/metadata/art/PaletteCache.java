package cs446.mezzo.metadata.art;

import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;
import android.util.Log;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import cs446.mezzo.music.Song;

/**
 * @author curtiskroetsch
 */
@Singleton
public final class PaletteCache {

    private static final int CACHE_SIZE = 200;

    private final Cache<String, Palette> mCache;

    @Inject
    public PaletteCache() {
        mCache = CacheBuilder.newBuilder()
                .maximumSize(CACHE_SIZE)
                .build();
    }

    public Palette getPalette(Song song) {
        return mCache.getIfPresent(song.getDataSource().toString());
    }

    public void putPalette(Song song, Bitmap bitmap) {
        mCache.put(song.getDataSource().toString(), Palette.generate(bitmap));
        Log.d("Palette", "Finished " + song.getTitle());
    }
}
