package cs446.mezzo.app;

import android.support.v4.app.Fragment;

/**
 * @author curtiskroetsch
 */
public interface MezzoPage {

    BaseMezzoActivity getMezzoActivity();

    boolean isTopLevel();

    boolean onBackPress();

    String getTitle();

    void onResume();

    Fragment getFragment();
}
