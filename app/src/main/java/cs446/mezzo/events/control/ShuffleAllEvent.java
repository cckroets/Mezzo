package cs446.mezzo.events.control;

import java.util.List;

import cs446.mezzo.music.Song;

/**
 * @author curtiskroetsch
 */
public class ShuffleAllEvent {

    List<Song> mSongs;

    public ShuffleAllEvent(List<Song> songs) {
        mSongs = songs;
    }

    public List<Song> getSongs() {
        return mSongs;
    }

}
