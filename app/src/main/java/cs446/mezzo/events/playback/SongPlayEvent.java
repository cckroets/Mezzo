package cs446.mezzo.events.playback;

import cs446.mezzo.music.Song;

/**
 * @author curtiskroetsch
 */
public class SongPlayEvent {

    private Song mSong;

    public SongPlayEvent(Song song) {
        mSong = song;
    }

    public Song getSong() {
        return mSong;
    }
}
