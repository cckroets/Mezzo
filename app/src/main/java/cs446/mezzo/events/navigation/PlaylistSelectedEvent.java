package cs446.mezzo.events.navigation;

import java.util.Collection;

import cs446.mezzo.music.Song;

/**
 * @author curtiskroetsch
 */
public class PlaylistSelectedEvent {

    private String mName;
    private Collection<Song> mSongs;

    public PlaylistSelectedEvent(String name, Collection<Song> songs) {
        mName = name;
        mSongs = songs;
    }

    public String getName() {
        return mName;
    }

    public Collection<Song> getSongs() {
        return mSongs;
    }
}
