package cs446.mezzo.overlay;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import cs446.mezzo.injection.Injector;
import roboguice.RoboGuice;

/**
 * @author curtiskroetsch
 */
public abstract class Overlay {

    Context mContext;
    View mView;
    OverlayManager mOverlayManager;
    boolean mIsVisible;

    WindowManager.LayoutParams mLayoutParams = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT);

    protected abstract View onCreateView(LayoutInflater inflater);

    protected void onViewCreated(View view) {
        RoboGuice.getInjector(getContext()).injectMembersWithoutViews(this);
        Injector.injectViews(this, view);
    }

    protected void onShow(View view) {
        view.setVisibility(View.VISIBLE);
    }

    protected void onHide(View view) {
        view.setVisibility(View.GONE);
    }

    public final void setVisible(boolean isVisible) {
        mIsVisible = isVisible;
    }

    public final boolean isVisible() {
        return mIsVisible;
    }

    public View getView() {
        return mView;
    }

    public final Context getContext() {
        return mContext;
    }

    public final OverlayManager getOverlayManager() {
        return mOverlayManager;
    }

    public WindowManager.LayoutParams getLayoutParams() {
        mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        return mLayoutParams;
    }

    public void updateView() {
        mOverlayManager.mWindowManager.updateViewLayout(mView, getLayoutParams());
    }
}
