package cs446.mezzo.app;

import android.app.Activity;
import android.support.v4.app.Fragment;

import roboguice.fragment.RoboFragment;

/**
 * A Base Fragment for each app fragment.
 *
 * @author curtiskroetsch
 */
public abstract class BaseMezzoFragment extends RoboFragment implements MezzoPage {

    @Override
    public void onResume() {
        super.onResume();
        if (isTopLevel()) {
            getMezzoActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof BaseMezzoActivity)) {
            throw new IllegalStateException("All BaseMezzoFragments must belong to a BaseMezzoActivity");
        }
    }

    @Override
    public BaseMezzoActivity getMezzoActivity() {
        return (BaseMezzoActivity) getActivity();
    }

    @Override
    public boolean isTopLevel() {
        return false;
    }

    @Override
    public boolean onBackPress() {
        return false;
    }

    @Override
    public Fragment getFragment() {
        return this;
    }

    public abstract String getTitle();
}
