package cs446.mezzo.app;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;

import java.util.List;

import roboguice.activity.RoboActionBarActivity;

/**
 * @author curtiskroetsch
 */
public abstract class BaseMezzoActivity extends RoboActionBarActivity {

    /**
     * Get the currently visible fragment from the activity.
     * @return
     */
    protected BaseMezzoFragment getVisibleFragment() {
        final FragmentManager fragmentManager = BaseMezzoActivity.this.getSupportFragmentManager();
        final List<Fragment> fragments = fragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof BaseMezzoFragment && fragment.isVisible())
                return (BaseMezzoFragment) fragment;
        }
        return null;
    }


    protected void setFragment(Fragment fragment, @IdRes int containerId) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(containerId, fragment, fragment.getTag())
                .commit();
    }

}
