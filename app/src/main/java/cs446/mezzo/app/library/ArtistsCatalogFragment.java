package cs446.mezzo.app.library;

import cs446.mezzo.music.Song;

/**
 * @author ulkarakhundzada
 */

public class ArtistsCatalogFragment extends CatalogFragment {

    public ArtistsCatalogFragment() {
        super();
    }

    @Override
    protected String[] getCategoriesForSong(Song song) {
        if (song.getArtist() != null) {
            return new String[]{song.getArtist()};
        } else {
            return new String[]{};
        }
    }


    @Override
    public String getTitle() {
        return "Artists";
    }


}

