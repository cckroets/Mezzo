package cs446.mezzo.view;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import cs446.mezzo.app.BaseMezzoFragment;

/**
 * @author curtiskroetsch
 */
public final class ViewUtil {

    private ViewUtil() {
    }

    /**
     * Returns darkerColor version of specified <code>color</code>.
     */
    public static int darkerColor(int color) {
        final float factor = 0.7f;
        final int r = Color.red(color);
        final int g = Color.green(color);
        final int b = Color.blue(color);

        return Color.argb(Color.alpha(color),
                Math.max((int) (r * factor), 0),
                Math.max((int) (g * factor), 0),
                Math.max((int) (b * factor), 0));
    }

    public static void tintViews(int color, ImageView... views) {
        final ColorStateList csl = ColorStateList.valueOf(color);
        for (ImageView view : views) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.setImageTintList(csl);
            } else {
                view.getDrawable().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
            }
        }
    }

    public static void tintSeekbar(SeekBar seekBar, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            seekBar.setProgressTintList(ColorStateList.valueOf(color));
            seekBar.setThumbTintList(ColorStateList.valueOf(color));
        }
    }

    public static void tintTextView(TextView view, int color) {
        view.setTextColor(color);
    }

    public static void tintDecor(BaseMezzoFragment fragment, int toolbarColor) {
        if (!fragment.isAdded() || fragment.getActivity() == null) {
            return;
        }
        final int statusBarColor = darkerColor(toolbarColor);
        fragment.getMezzoActivity().getToolbar().setBackgroundColor(toolbarColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fragment.getMezzoActivity().getWindow().setStatusBarColor(statusBarColor);
        }
    }

}
