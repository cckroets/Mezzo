package cs446.mezzo.events.playlists;

import cs446.mezzo.music.playlists.Playlist;

/**
 * @author curtiskroetsch
 */
public class PlaylistsChangedEvent {

    private Playlist mPlaylist;

    public PlaylistsChangedEvent(Playlist playlist) {
        mPlaylist = playlist;
    }

    public Playlist getPlaylist() {
        return mPlaylist;
    }
}
