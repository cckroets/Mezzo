package cs446.mezzo.app.player.mini;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import cs446.mezzo.R;
import cs446.mezzo.overlay.Overlay;

/**
 * @author curtiskroetsch
 */
public class Dismissal extends Overlay {

    @Override
    protected View onCreateView(LayoutInflater inflater) {
        setVisible(false);
        return inflater.inflate(R.layout.overlay_dismiss, null);
    }

    @Override
    protected void onViewCreated(View view) {
        super.onViewCreated(view);
        view.setAlpha(0f);
    }

    @Override
    protected void onShow(View view) {
        view.animate().alpha(1f).start();
    }

    @Override
    protected void onHide(View view) {
        view.animate().alpha(0f).start();
    }

    @Override
    public WindowManager.LayoutParams getLayoutParams() {
        final WindowManager.LayoutParams params = super.getLayoutParams();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.BOTTOM;
        params.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        return params;
    }
}
