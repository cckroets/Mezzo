package cs446.mezzo.music;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import cs446.mezzo.data.Preferences;

/**
 * @author curtiskroetsch
 */
public final class SongSerializer {

    public static void save(Preferences preferences, String key, Song song) {
        preferences.putString(key, song.getFile().getAbsolutePath());
    }

    public static Song load(Preferences preferences, String key) {
        final String path = preferences.getString(key);
        return load(path);
    }

    private static Song load(String path) {
        if (path.endsWith(HttpSong.EXT)) {
            return new HttpSong(path);
        } else {
            return new FileSong(path);
        }
    }

    public static void saveAll(Preferences preferences, String key, Collection<Song> songs) {
        final Set<String> paths = new HashSet<>(songs.size());
        for (Song song : songs) {
            paths.add(song.getFile().getAbsolutePath());
        }
        preferences.putNewStrings(key, paths);
    }

    public static Collection<Song> loadAll(Preferences preferences, String key) {
        final Set<String> paths = preferences.getStrings(key);
        final Set<Song> songs = new LinkedHashSet<>(paths.size());
        for (String path : paths) {
            songs.add(load(path));
        }
        return songs;
    }
}
