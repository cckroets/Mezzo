package cs446.mezzo.app;

import android.app.Activity;

import roboguice.fragment.RoboFragment;

/**
 * A Base Fragment for each app fragment.
 *
 * @author curtiskroetsch
 */
public abstract class BaseMezzoFragment extends RoboFragment {

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

    public BaseMezzoActivity getMezzoActivity() {
        return (BaseMezzoActivity) getActivity();
    }

    public boolean isTopLevel() {
        return false;
    }

    public boolean onBackPress() {
        return false;
    }

    public abstract String getTitle();
}
