package cs446.mezzo.view;

import android.graphics.Color;

/**
 * @author curtiskroetsch
 */
public final class ViewUtil {

    private ViewUtil() { }

    /**
     * Returns darkerColor version of specified <code>color</code>.
     */
    public static int darkerColor(int color) {
        final float factor = 0.6f;
        final int r = Color.red(color);
        final int g = Color.green(color);
        final int b = Color.blue(color);

        return Color.argb(Color.alpha(color),
                Math.max((int) (r * factor), 0),
                Math.max((int) (g * factor), 0),
                Math.max((int) (b * factor), 0));
    }
}
