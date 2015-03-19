package cs446.mezzo.events.playlists;

import cs446.mezzo.music.playlists.Playlist;

/**
 * @author curtiskroetsch
 */
public class PlaylistChangedEvent {

    private Playlist mPlaylist;
    private boolean mDeleted;

    public PlaylistChangedEvent(Playlist playlist) {
        this(playlist, false);
    }

    public PlaylistChangedEvent(Playlist playlist, boolean deleted) {
        mPlaylist = playlist;
        mDeleted = deleted;
    }

    public boolean isDeleted() {
        return mDeleted;
    }

    public Playlist getPlaylist() {
        return mPlaylist;
    }
}
