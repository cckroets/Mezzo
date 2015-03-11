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

    protected void setInitialFragment(BaseMezzoFragment fragment) {
        setInitialFragment(fragment, getMainFragmentContainer());
    }

    private void setInitialFragment(Fragment fragment, @IdRes int contId) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(contId, fragment)
                .commit();
    }

    private void setFragment(Fragment fragment, @IdRes int containerId) {
        if (fragment == getFragment()) {
            return;
        }
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .add(containerId, fragment, fragment.getTag())
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

    public BaseMezzoFragment getFragment() {
        return (BaseMezzoFragment) getSupportFragmentManager().findFragmentById(getMainFragmentContainer());
    }

    public void setFragment(BaseMezzoFragment fragment) {
        setFragment(fragment, getMainFragmentContainer());
    }

    @Override
    public void onBackStackChanged() {
        final BaseMezzoFragment fragment = getFragment();
        if (fragment != null && fragment.isTopLevel()) {
            getSupportActionBar().setTitle(fragment.getTitle());
            mToolbar.setBackgroundColor(getResources().getColor(R.color.primary));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.primary_dark));
            }
        }
    }

    @Override
    public void onBackPressed() {
        final BaseMezzoFragment fragment = getFragment();
        if (fragment != null && !fragment.onBackPress()) {
            super.onBackPressed();
        }
    }

    @IdRes
    protected abstract int getMainFragmentContainer();

    @IdRes
    protected abstract int getSecondaryFragmentContainer();
}
