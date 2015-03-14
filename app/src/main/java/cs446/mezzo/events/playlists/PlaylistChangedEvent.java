package cs446.mezzo.events.playlists;

import cs446.mezzo.music.playlists.Playlist;

/**
 * @author curtiskroetsch
 */
public class PlaylistChangedEvent {

    private Playlist mPlaylist;

    public PlaylistChangedEvent(Playlist playlist) {
        mPlaylist = playlist;
    }

    public Playlist getPlaylist() {
        return mPlaylist;
    }
}
