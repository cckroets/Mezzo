package cs446.mezzo.overlay;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.google.inject.Inject;
import com.squareup.otto.Subscribe;

import cs446.mezzo.R;
import cs446.mezzo.app.MainActivity;
import cs446.mezzo.app.player.mini.MiniPlayer;
import cs446.mezzo.events.EventBus;
import cs446.mezzo.events.navigation.OpenAppEvent;
import cs446.mezzo.music.AlbumArtManager;
import cs446.mezzo.music.SongPlayer;
import cs446.mezzo.events.playback.SongPlayEvent;
import cs446.mezzo.music.Song;
import roboguice.service.RoboService;

/**
 * @author curtiskroetsch
 */
public class OverlayService extends RoboService implements Application.ActivityLifecycleCallbacks {

    @Inject
    OverlayManager mOverlayManager;

    @Inject
    SongPlayer mMusicPlayer;

    @Inject
    AlbumArtManager mArtManager;

    Overlay mMiniPlayer;

    int mNotificationId = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        getApplication().registerActivityLifecycleCallbacks(this);
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
        final Song song = event.getSong();
        String mSongTitle = "";
        if (song != null) {
            mSongTitle = song.getTitle();
        }
        final Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        final PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        final Notification.Builder builder = new Notification.Builder(this);
        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.ic_av_play_circle_fill)
                .setLargeIcon(mArtManager.getAlbumArt(song))
                .setOngoing(true)
                .setContentTitle("Mezzo")
                .setContentText(mSongTitle)
                .setWhen(0);
        final Notification not = builder.getNotification();
        startForeground(mNotificationId , not);
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
        if (!mMusicPlayer.isPaused()) {
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
