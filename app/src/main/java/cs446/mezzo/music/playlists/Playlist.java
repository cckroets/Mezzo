package cs446.mezzo.music.playlists;

import java.util.Collection;

import cs446.mezzo.music.Song;

/**
 * @author curtiskroetsch
 */
public class Playlist {
    final String mName;
    final Collection<Song> mSongs;

    public Playlist(String name, Collection<Song> songs) {
        mName = name;
        mSongs = songs;
    }

    public Collection<Song> getSongs() {
        return mSongs;
    }

    public String getName() {
        return mName;
    }

    @Override
    public String toString() {
        return mName;
    }
}
