package cs446.mezzo.overlay;

import android.app.Activity;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.inject.Inject;
import com.squareup.otto.Subscribe;

import cs446.mezzo.R;
import cs446.mezzo.app.MainActivity;
import cs446.mezzo.app.player.mini.MiniPlayer;
import cs446.mezzo.data.Callback;
import cs446.mezzo.events.EventBus;
import cs446.mezzo.events.navigation.GoHomeEvent;
import cs446.mezzo.events.system.ActivityStoppedEvent;
import cs446.mezzo.metadata.art.AlbumArtManager;
import cs446.mezzo.player.SongPlayer;
import cs446.mezzo.events.playback.SongPlayEvent;
import cs446.mezzo.music.Song;
import roboguice.service.RoboService;

/**
 * @author curtiskroetsch
 */
public class OverlayService extends RoboService implements Application.ActivityLifecycleCallbacks {

    private static final String TAG = OverlayService.class.getName();

    @Inject
    OverlayManager mOverlayManager;

    @Inject
    SongPlayer mMusicPlayer;

    @Inject
    AlbumArtManager mArtManager;

    Overlay mMiniPlayer;

    int mNotificationId = 32;

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
    public void onOpenApp(GoHomeEvent event) {
        final Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        startActivity(intent);
    }

    @Subscribe
    public void onSongPlay(SongPlayEvent event) {
        final Song song = event.getSong();
        if (song == null) {
            return;
        }
        Log.d(TAG, "onSongPlay " + event.getSong().getTitle());
        final Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        final PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        buildNotificationAndStart(pendInt, song);
    }

    private void buildNotificationAndStart(PendingIntent intent, Song song) {
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentIntent(intent)
                .setSmallIcon(R.drawable.ic_av_play_circle_fill)
                .setContentTitle("Mezzo")
                .setContentText(song.getTitle());
        mArtManager.getAlbumArt(song, new Callback<Bitmap>() {
            @Override
            public void onSuccess(Bitmap data) {
                builder.setLargeIcon(data);
                startForeground(mNotificationId, builder.build());
            }

            @Override
            public void onFailure(Exception e) {
                startForeground(mNotificationId, builder.build());
            }
        });
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
        EventBus.post(new ActivityStoppedEvent());
        Log.d(TAG, "ACTIVITY STOPPED");
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
        Log.d(TAG, "ACTIVITY DESTROYED");
    }
}
