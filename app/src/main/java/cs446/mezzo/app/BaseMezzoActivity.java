package cs446.mezzo.app;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;

import cs446.mezzo.R;
import roboguice.activity.RoboActionBarActivity;

/**
 * @author curtiskroetsch
 */
public abstract class BaseMezzoActivity extends RoboActionBarActivity
        implements FragmentManager.OnBackStackChangedListener {

    Handler mHandler;

    Fragment mSecondaryFragment;

    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    protected void onDestroy() {
        getSupportFragmentManager().removeOnBackStackChangedListener(this);
        super.onDestroy();
    }

    @Override
    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        super.setSupportActionBar(toolbar);
        mToolbar = toolbar;
    }

    public Toolbar getToolbar() {
        return mToolbar;
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

    protected void setInitialFragment(MezzoPage fragment) {
        setInitialFragment(fragment.getFragment(), getMainFragmentContainer());
    }

    private void setInitialFragment(Fragment fragment, @IdRes int contId) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(contId, fragment)
                .setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom)
                .commit();
    }

    private void setFragment(Fragment fragment, @IdRes int containerId) {
        if (fragment == getFragment()) {
            return;
        }
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .setCustomAnimations(R.anim.abc_slide_in_bottom, 0, 0, R.anim.abc_slide_out_bottom)
                .add(containerId, fragment, fragment.getTag())
                .commitAllowingStateLoss();
    }

    protected void setSecondaryFragment(Fragment fragment) {
        setInitialFragment(fragment, getSecondaryFragmentContainer());
        mSecondaryFragment = fragment;
    }

    public void showSecondaryFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom)
                .show(mSecondaryFragment)
                .commitAllowingStateLoss();
    }

    public void hideSecondaryFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom)
                .hide(mSecondaryFragment)
                .commitAllowingStateLoss();
    }

    public MezzoPage getFragment() {
        return (MezzoPage) getSupportFragmentManager().findFragmentById(getMainFragmentContainer());
    }

    public void setFragment(MezzoPage fragment) {
        setFragment(fragment.getFragment(), getMainFragmentContainer());
    }

    @Override
    public void onBackStackChanged() {
        final MezzoPage fragment = getFragment();
        if (fragment != null && fragment.isTopLevel()) {
            getSupportActionBar().setTitle(fragment.getTitle());
            mToolbar.setBackgroundColor(getResources().getColor(R.color.primary));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.primary_dark));
            }
            fragment.onResume();
        }
    }

    @Override
    public void onBackPressed() {
        final MezzoPage fragment = getFragment();
        if (fragment != null && !fragment.onBackPress()) {
            super.onBackPressed();
        }
    }

    @IdRes
    protected abstract int getMainFragmentContainer();

    @IdRes
    protected abstract int getSecondaryFragmentContainer();
}
