package cs446.mezzo.events.control;

import java.util.List;

import cs446.mezzo.music.Song;

/**
 * @author curtiskroetsch
 */
public class SelectSongEvent {

    List<Song> mPlaylist;
    int mStartIndex;

    public SelectSongEvent(List<Song> playlist, int startIndex) {
        mPlaylist = playlist;
        mStartIndex = startIndex;
    }

    public List<Song> getPlaylist() {
        return mPlaylist;
    }

    public int getStartIndex() {
        return mStartIndex;
    }
}
