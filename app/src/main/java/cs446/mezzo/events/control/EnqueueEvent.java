package cs446.mezzo.events.control;

import cs446.mezzo.music.Song;

/**
 * @author curtiskroetsch
 */
public class EnqueueEvent {

    private Song mSong;

    public EnqueueEvent(Song song) {
        mSong = song;
    }

    public Song getSong() {
        return mSong;
    }
}
