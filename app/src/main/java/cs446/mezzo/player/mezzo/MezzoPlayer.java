package cs446.mezzo.player.mezzo;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;

import com.google.inject.Singleton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import cs446.mezzo.events.EventBus;
import cs446.mezzo.events.playback.RepeatEvent;
import cs446.mezzo.events.playback.SeekEvent;
import cs446.mezzo.events.playback.ShuffleEvent;
import cs446.mezzo.events.playback.SongPauseEvent;
import cs446.mezzo.events.playback.SongPlayEvent;
import cs446.mezzo.music.Song;
import cs446.mezzo.player.SongPlayer;

/**
 * @author curtiskroetsch
 */
@Singleton
public class MezzoPlayer implements SongPlayer,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener {

    private static final String TAG = MezzoPlayer.class.getName();
    private static final float LOW_VOLUME = 0.1f;
    private static final float MAX_VOLUME = 1.0f;
    private static final int SEEK_DELAY_MS = 300;
    private Runnable mSeekRunnable = new Runnable() {
        @Override
        public void run() {
            EventBus.post(new SeekEvent(getSeekPosition()));
            mHandler.postDelayed(this, SEEK_DELAY_MS);
        }
    };
    private Context mContext;
    private Handler mHandler;
    private MediaPlayer mMediaPlayer;
    private List<Song> mPlaylist;
    private List<Integer> mShuffle;
    private Queue<Song> mQueue;
    private int mCurrentIndex;
    private boolean mShuffleEnabled;
    private PlayerState mState;

    public MezzoPlayer(Context context) {
        mHandler = new Handler(Looper.getMainLooper());
        mShuffleEnabled = false;
        mCurrentIndex = 0;
        mContext = context;
        mQueue = new LinkedList<>();
        mState = new QueueState();
        acquireResources();
    }

    private void acquireResources() {
        if (mMediaPlayer != null) {
            return;
        }
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setVolume(MAX_VOLUME, MAX_VOLUME);
        mMediaPlayer.setWakeMode(mContext, PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
    }

    private void initShuffleIndex() {
        final List<Integer> index = new ArrayList<Integer>(mPlaylist.size());
        for (int i = 0; i < mPlaylist.size(); i++) {
            index.add(i);
        }
        Collections.shuffle(index);
        mShuffle = index;
    }

    @Override
    public void enqueueSong(Song song) {
        mQueue.add(song);
        if (isPaused()) {
            playNext();
        }
    }

    @Override
    public void togglePause() {
        acquireResources();
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            mHandler.removeCallbacks(mSeekRunnable);
            EventBus.post(new SongPauseEvent(true));
        } else {
            mMediaPlayer.start();
            mHandler.post(mSeekRunnable);
            EventBus.post(new SongPauseEvent(false));
        }
    }


    @Override
    public int getSeekPosition() {
        return mMediaPlayer == null ? 0 : mMediaPlayer.getCurrentPosition();
    }

    @Override
    public void playNext() {
        mState.next(this);
        playSong(getCurrentSong());
    }

    @Override
    public void playPrevious() {
        mState.prev(this);
        playSong(getCurrentSong());
    }

    private void playSong(Song song) {
        acquireResources();
        EventBus.post(new SongPlayEvent(song, true));
        if (song == null) {
            return;
        }
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(mContext, song.getDataSource());
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void setSong(int songPos) {
        mCurrentIndex = songPos;
        mState = new PlaylistState();
        playSong(getCurrentSong());
    }

    @Override
    public void setSeek(int seekMs) {
        acquireResources();
        mMediaPlayer.seekTo(seekMs);
    }

    @Override
    public List<Song> getPlaylist() {
        return mPlaylist;
    }

    @Override
    public void setPlaylist(List<Song> playlist) {
        mPlaylist = playlist;
        mCurrentIndex = 0;
        initShuffleIndex();
    }

    @Override
    public Song getCurrentSong() {
        return mState.getCurrentSong(this);
    }

    @Override
    public void setShuffle(boolean shouldShuffle) {
        mShuffleEnabled = shouldShuffle;
        EventBus.post(new ShuffleEvent(shouldShuffle));
    }

    @Override
    public void setRepeat(boolean shouldRepeat) {
        mMediaPlayer.setLooping(shouldRepeat);
        EventBus.post(new RepeatEvent(shouldRepeat));
    }

    @Override
    public boolean isPaused() {
        return (mMediaPlayer == null) || !mMediaPlayer.isPlaying();
    }

    @Override
    public boolean getShuffleMode() {
        return mShuffleEnabled;
    }

    @Override
    public boolean getRepeatMode() {
        return (mMediaPlayer != null) && mMediaPlayer.isLooping();
    }

    @Override
    public void setVolumeLow() {
        acquireResources();
        mMediaPlayer.setVolume(LOW_VOLUME, LOW_VOLUME);
    }

    @Override
    public void setVolumeHigh() {
        acquireResources();
        mMediaPlayer.setVolume(MAX_VOLUME, MAX_VOLUME);
    }

    @Override
    public void releaseResources() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "On Prepared");
        mHandler.post(mSeekRunnable);
        mMediaPlayer.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(TAG, "On Complete");
        if (!mp.isLooping()) {
            mState.onSongComplete(this);
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e(TAG, "OnError : what = " + what + ", extra = " + extra + ", mp = " + mp);
        return true;
    }

    void setState(PlayerState state) {
        mState = state;
    }

    Queue<Song> getQueue() {
        return mQueue;
    }

    int getCurrentIndex() {
        return mCurrentIndex;
    }

    void setCurrentIndex(int index) {
        mCurrentIndex = index;
    }

    List<Integer> getShuffle() {
        return mShuffle;
    }
}
