package cs446.mezzo.app.library;

import cs446.mezzo.music.Song;

/**
 * @author ulkarakhundzada
 */

public class AlbumsFragment extends PlaylistFragment {

    public AlbumsFragment() {
        super();
    }

    @Override
    protected String[] getCategoriesForSong(Song song) {
        if (song.getAlbum() != null) {
            return new String[]{song.getAlbum()};
        } else {
            return new String[]{};
        }
    }

    @Override
    public String getTitle() {
        return "Albums";
    }


}
