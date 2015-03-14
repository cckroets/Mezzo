package cs446.mezzo.events.navigation;

import cs446.mezzo.music.playlists.Playlist;

/**
 * @author curtiskroetsch
 */
public class PlaylistSelectedEvent {

    private Playlist mPlaylist;
    private boolean mSaved;

    public PlaylistSelectedEvent(Playlist playlist, boolean isSaved) {
        mPlaylist = playlist;
        mSaved = isSaved;
    }

    public Playlist getPlaylist() {
        return mPlaylist;
    }

    public boolean isSaved() {
        return mSaved;
    }
}
