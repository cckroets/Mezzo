package cs446.mezzo.app;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.List;

import roboguice.activity.RoboActionBarActivity;

/**
 * @author curtiskroetsch
 */
public class BaseMezzoActivity extends RoboActionBarActivity {

    public BaseMezzoFragment getVisibleFragment() {
        final FragmentManager fragmentManager = BaseMezzoActivity.this.getSupportFragmentManager();
        final List<Fragment> fragments = fragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof BaseMezzoFragment && fragment.isVisible())
                return (BaseMezzoFragment) fragment;
        }
        return null;
    }
}
