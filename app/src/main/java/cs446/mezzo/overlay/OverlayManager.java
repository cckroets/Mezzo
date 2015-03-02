package cs446.mezzo.overlay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.HashSet;
import java.util.Set;

/**
 * @author curtiskroetsch
 */
@Singleton
public class OverlayManager {

    @Inject
    WindowManager mWindowManager;

    @Inject
    Context mContext;

    @Inject
    LayoutInflater mLayoutInflater;

    Set<Overlay> mAdded = new HashSet<Overlay>();

    @Inject
    public OverlayManager() {
    }

    public void add(Overlay overlay) {
        if (!mAdded.contains(overlay)) {
            mAdded.add(overlay);
            overlay.mContext = getContext();
            overlay.mOverlayManager = this;
            final View view = overlay.onCreateView(mLayoutInflater);
            overlay.mView = view;
            mWindowManager.addView(view, overlay.getLayoutParams());
            overlay.onViewCreated(view);
        }
    }

    public void show(Overlay overlay) {
        if (!overlay.mIsVisible) {
            overlay.mIsVisible = true;
            overlay.onShow(overlay.mView);
        }
    }

    public void hide(Overlay overlay) {
        if (overlay.isVisible()) {
            overlay.mIsVisible = false;
            overlay.onHide(overlay.mView);
        }
    }

    public void remove(Overlay overlay) {
        if (mAdded.contains(overlay)) {
            overlay.onDestroy();
            mWindowManager.removeViewImmediate(overlay.mView);
            mAdded.remove(overlay);
        }
    }

    public WindowManager getWindowManager() {
        return mWindowManager;
    }

    public Context getContext() {
        return mContext;
    }
}
