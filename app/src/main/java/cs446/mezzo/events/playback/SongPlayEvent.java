package cs446.mezzo.events.playback;

import cs446.mezzo.music.Song;

/**
 * @author curtiskroetsch
 */
public class SongPlayEvent {

    private boolean mIsPlaying;
    private Song mSong;

    public SongPlayEvent(Song song, boolean playing) {
        mSong = song;
        mIsPlaying = playing;
    }

    public Song getSong() {
        return mSong;
    }

    public boolean isPlaying() {
        return mIsPlaying;
    }
}
