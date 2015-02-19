package cs446.mezzo.music;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.util.Log;

import com.google.inject.Singleton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cs446.mezzo.events.EventBus;
import cs446.mezzo.events.playback.SongPlayEvent;

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

    Context mContext;

    private MediaPlayer mMediaPlayer;

    private List<Song> mPlaylist;
    private List<Integer> mShuffle;

    private int mCurrentIndex;
    private boolean mShuffleEnabled;

    public MezzoPlayer(Context context) {
        mShuffleEnabled = false;
        mCurrentIndex = 0;
        mContext = context;
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
    public void togglePause() {
        acquireResources();
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        } else {
            mMediaPlayer.start();
        }
    }

    private int getNextIndex() {
        return (mCurrentIndex + 1) % mPlaylist.size();
    }

    private int getPrevIndex() {
        return (mCurrentIndex == 0) ? mPlaylist.size() - 1 : mCurrentIndex - 1;
    }

    private int getSongIndex() {
        return mShuffleEnabled ? mShuffle.get(mCurrentIndex) : mCurrentIndex;
    }

    @Override
    public void playNext() {
        mCurrentIndex = getNextIndex();
        playSong(getSongIndex());
    }

    @Override
    public void playPrevious() {
        mCurrentIndex = getPrevIndex();
        playSong(getSongIndex());
    }

    private void playSong(int songIndex) {
        acquireResources();
        EventBus.post(new SongPlayEvent(mPlaylist.get(songIndex)));
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(mContext, mPlaylist.get(songIndex).getDataSource());
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void setPlaylist(List<Song> playlist) {
        mPlaylist = playlist;
        mCurrentIndex = 0;
        initShuffleIndex();
    }

    @Override
    public void setSong(int songPos) {
        mCurrentIndex = songPos;
        playSong(mCurrentIndex);
    }

    @Override
    public void setSeek(float seekPos) {
        final int seekMilis = (int) ((float) mMediaPlayer.getDuration() * seekPos);
        mMediaPlayer.seekTo(seekMilis);
    }

    @Override
    public List<Song> getPlaylist() {
        return mPlaylist;
    }

    @Override
    public Song getCurrentSong() {
        if (mPlaylist == null) {
            return null;
        }
        return mPlaylist.get(getSongIndex());
    }

    @Override
    public void setShuffle(boolean shouldShuffle) {
        mShuffleEnabled = shouldShuffle;
    }

    @Override
    public void setRepeat(boolean shouldRepeat) {
        mMediaPlayer.setLooping(shouldRepeat);
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
        mMediaPlayer.release();
        mMediaPlayer = null;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "On Prepared");
        mMediaPlayer.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(TAG, "On Complete");
        playNext();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e(TAG, "OnError : what = " + what + ", extra = " + extra + ", mp = " + mp);
        return true;
    }
}
