package cs446.mezzo.overlay;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.google.inject.Inject;
import com.squareup.otto.Subscribe;

import cs446.mezzo.app.MainActivity;
import cs446.mezzo.app.miniplayer.MiniPlayer;
import cs446.mezzo.events.EventBus;
import cs446.mezzo.events.control.PauseToggleEvent;
import cs446.mezzo.events.navigation.OpenAppEvent;
import cs446.mezzo.events.playback.SongPlayEvent;
import roboguice.service.RoboService;

/**
 * @author curtiskroetsch
 */
public class OverlayService extends RoboService implements Application.ActivityLifecycleCallbacks {

    @Inject
    OverlayManager mOverlayManager;

    Overlay mMiniPlayer;

    boolean mSongPlaying;

    @Override
    public void onCreate() {
        super.onCreate();
        getApplication().registerActivityLifecycleCallbacks(this);
        mSongPlaying = false;
        EventBus.register(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Subscribe
    public void onOpenApp(OpenAppEvent event) {
        final Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        startActivity(intent);
    }

    @Subscribe
    public void onSongPlay(SongPlayEvent event) {
        mSongPlaying = true;
    }

    @Subscribe
    public void onPauseToggle(PauseToggleEvent event) {
        mSongPlaying = !mSongPlaying;
    }

    @Override
    public void onDestroy() {
        EventBus.unregister(this);
        getApplication().unregisterActivityLifecycleCallbacks(this);
        super.onDestroy();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (mMiniPlayer != null) {
            mOverlayManager.hide(mMiniPlayer);
            mOverlayManager.remove(mMiniPlayer);
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (mSongPlaying) {
            mMiniPlayer = new MiniPlayer();
            mOverlayManager.add(mMiniPlayer);
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
