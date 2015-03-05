package cs446.mezzo.app.library;

import cs446.mezzo.music.Song;

/**
 * @author curtiskroetsch
 */
public class PlaylistsCatalogFragment extends CatalogFragment {

    @Override
    protected String[] getCategoriesForSong(Song song) {
        return new String[0];
    }

    @Override
    public String getTitle() {
        return "Playlists";
    }
}
