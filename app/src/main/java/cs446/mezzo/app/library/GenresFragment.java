package cs446.mezzo.app.library;

import cs446.mezzo.music.Song;

/**
 * @author ulkarakhundzada
 */

public class GenresFragment extends PlaylistFragment {

    public GenresFragment() {
        super();
    }

    @Override
    protected String[] getCategoriesForSong(Song song) {
        final String[] genres = new String[song.getGenres().size()];
        song.getGenres().toArray(genres);
        return genres;
    }

    @Override
    public String getTitle() {
        return "Genres";
    }


}
