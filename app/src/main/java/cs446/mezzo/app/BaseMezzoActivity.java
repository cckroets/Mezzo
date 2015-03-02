package cs446.mezzo.app;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.List;

import roboguice.activity.RoboActionBarActivity;

/**
 * @author curtiskroetsch
 */
public abstract class BaseMezzoActivity extends RoboActionBarActivity {

    Handler mHandler;

    Fragment mSecondaryFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler(Looper.getMainLooper());
    }

    public void post(Runnable runnable) {
        mHandler.post(runnable);
    }

    public void postDelayed(Runnable runnable, long delayMs) {
        mHandler.postDelayed(runnable, delayMs);
    }

    public void removeCallbacks(Runnable runnable) {
        mHandler.removeCallbacks(runnable);
    }

    /**
     * Get the currently visible fragment from the activity.
     *
     * @return
     */
    public BaseMezzoFragment getVisibleFragment() {
        final FragmentManager fragmentManager = BaseMezzoActivity.this.getSupportFragmentManager();
        final List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments == null) {
            return null;
        }
        for (Fragment fragment : fragments) {
            if (fragment instanceof BaseMezzoFragment && fragment.isVisible())
                return (BaseMezzoFragment) fragment;
        }
        return null;
    }


    public void setFragment(BaseMezzoFragment fragment) {
        setFragment(fragment, getMainFragmentContainer());
    }

    protected void setInitialFragment(BaseMezzoFragment fragment) {
        setInitialFragment(fragment, getMainFragmentContainer());
    }

    private void setInitialFragment(Fragment fragment, int contId) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(contId, fragment)
                .commit();
    }

    private void setFragment(Fragment fragment, @IdRes int containerId) {
        if (fragment == getVisibleFragment()) {
            return;
        }
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(containerId, fragment, fragment.getTag())
                .commit();
    }

    protected void setSecondaryFragment(Fragment fragment) {
        setInitialFragment(fragment, getSecondaryFragmentContainer());
        mSecondaryFragment = fragment;
    }

    public void showSecondaryFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .show(mSecondaryFragment)
                .commit();
    }

    public void hideSecondaryFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .hide(mSecondaryFragment)
                .commit();
    }

    protected abstract int getMainFragmentContainer();

    protected abstract int getSecondaryFragmentContainer();
}
