package cs446.mezzo.app;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import cs446.mezzo.R;
import roboguice.fragment.RoboFragment;

/**
 * A Base Fragment for each app fragment.
 *
 * @author curtiskroetsch
 */
public abstract class BaseMezzoFragment extends RoboFragment {

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        invalidateActionBar();
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

    public void invalidateActionBar() {
        getMezzoActivity().setTitle(getTitle());
        getMezzoActivity().getToolbar().setBackgroundColor(getResources().getColor(R.color.primary));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getMezzoActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.primary_dark));
        }
        getMezzoActivity().invalidateOptionsMenu();
    }

    public boolean onBackPress() {
        return false;
    }

    public abstract String getTitle();
}
