package cs446.mezzo.metadata.art;

import android.app.ActivityManager;
import android.content.Context;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.squareup.picasso.LruCache;

import static android.content.pm.ApplicationInfo.FLAG_LARGE_HEAP;

/**
 * @author curtiskroetsch
 */
@Singleton
public class ArtCache extends LruCache {

    private static final int TO_BYTES = 1024 * 1024;

    // Target ~25% of the available heap.
    private static final float FILL_FACTOR = 0.25f;

    @Inject
    public ArtCache(Context context, ActivityManager manager) {
        super(calculateMemoryCacheSize(context, manager));
    }

    private static int calculateMemoryCacheSize(Context context, ActivityManager manager) {
        final boolean largeHeap = (context.getApplicationInfo().flags & FLAG_LARGE_HEAP) != 0;
        final int memoryClass = largeHeap ? manager.getLargeMemoryClass() : manager.getMemoryClass();
        return  (int) (memoryClass * TO_BYTES * FILL_FACTOR);
    }
}
