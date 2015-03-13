package cs446.mezzo.player;

import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.util.Log;

import com.google.inject.Inject;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

import cs446.mezzo.events.EventBus;
import cs446.mezzo.events.control.EnqueueEvent;
import cs446.mezzo.events.control.PauseToggleEvent;
import cs446.mezzo.events.control.PlayNextEvent;
import cs446.mezzo.events.control.PlayPrevEvent;
import cs446.mezzo.events.control.RepeatToggleEvent;
import cs446.mezzo.events.control.SeekSetEvent;
import cs446.mezzo.events.control.SelectSongEvent;
import cs446.mezzo.events.control.ShuffleToggleEvent;
import cs446.mezzo.events.playback.SongPlayEvent;
import roboguice.service.RoboService;

/**
 * Created by ulkarakhundzada on 2015-02-17.
 */
public class MusicService extends RoboService
        implements AudioManager.OnAudioFocusChangeListener {

    private static final String TAG = MusicService.class.getName();

    @Inject
    SongPlayer mSongPlayer;

    @Inject
    AudioManager mAudioManager;

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.register(this);
        mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        EventBus.unregister(this);
        mSongPlayer.releaseResources();
        super.onDestroy();
    }

    @Subscribe
    public void onPauseToggleEvent(PauseToggleEvent event) {
        mSongPlayer.togglePause();
    }

    @Subscribe
    public void onPlayNextEvent(PlayNextEvent event) {
        mSongPlayer.playNext();
    }

    @Subscribe
    public void onPlayPrevEvent(PlayPrevEvent event) {
        mSongPlayer.playPrevious();
    }

    @Subscribe
    public void onRepeatToggleEvent(RepeatToggleEvent event) {
        mSongPlayer.setRepeat(!mSongPlayer.getRepeatMode());
    }

    @Subscribe
    public void onShuffleToggleEvent(ShuffleToggleEvent event) {
        mSongPlayer.setShuffle(!mSongPlayer.getShuffleMode());
    }

    @Subscribe
    public void onSongSelected(SelectSongEvent event) {
        mSongPlayer.setPlaylist(event.getPlaylist());
        mSongPlayer.setSong(event.getStartIndex());
    }

    @Subscribe
    public void onSetSeekEvent(SeekSetEvent event) {
        mSongPlayer.setSeek(event.getSeekPos());
    }

    @Subscribe
    public void onEnqueueEvent(EnqueueEvent event) {
        mSongPlayer.enqueueSong(event.getSong());
    }

    @Produce
    public SongPlayEvent produceCurrentSong() {
        final boolean playing = mSongPlayer.getCurrentSong() != null && !mSongPlayer.isPaused();
        return new SongPlayEvent(mSongPlayer.getCurrentSong(), playing);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                mSongPlayer.setVolumeHigh();
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
                if (!mSongPlayer.isPaused()) {
                    mSongPlayer.togglePause();
                }
                mSongPlayer.releaseResources();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                if (!mSongPlayer.isPaused()) {
                    mSongPlayer.togglePause();
                }
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                mSongPlayer.setVolumeLow();
                break;

            default:
                Log.w(TAG, "invalid focusChange = " + focusChange);
                break;
        }
    }
}
