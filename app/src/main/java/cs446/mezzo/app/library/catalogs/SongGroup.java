package cs446.mezzo.app.library.catalogs;

import android.content.res.Resources;
import android.util.Log;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cs446.mezzo.R;
import cs446.mezzo.music.Song;

/**
 * @author curtiskroetsch
 */
public enum SongGroup {
    ARTISTS,
    ALBUMS,
    GENRES;

    public String[] getGroups(Resources resources, Song song) {
        switch (this) {
            case ARTISTS:
                return new String[]{song.getArtist()};
            case ALBUMS:
                return new String[]{song.getAlbum()};
            case GENRES:
                return toLiteralGenres(resources, song.getGenres());
            default:
                return null;

        }
    }

    private String[] toLiteralGenres(Resources resources, Set<String> genres) {
        final String[] result = new String[genres.size()];
        final Pattern pattern = Pattern.compile("\\((\\d+)\\)$");
        int index = 0;
        for (String genre : genres) {
            Log.d("REGEX", genre);
            final Matcher matcher = pattern.matcher(genre);
            Log.d("REGEX", "matcher = " + matcher.toString());
            if (matcher.find()) {
                Log.d("REGEX", genre + " matches");
                final String group = matcher.group(1);
                Log.d("REGEX", "group = " + group);
                final int genreId = Integer.parseInt(group);
                Log.d("REGEX", "genreId = " + genreId);
                final String[] titles = resources.getStringArray(R.array.genres);
                if (genreId < titles.length && genreId >= 0) {
                    result[index] = titles[genreId];
                    continue;
                }
            }
            result[index] = genre;
            index++;
        }
        return result;
    }
}
