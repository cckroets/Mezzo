package cs446.mezzo.sources;

import android.text.TextUtils;
import android.util.Log;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.squareup.otto.Subscribe;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import cs446.mezzo.data.Preferences;
import cs446.mezzo.events.EventBus;
import cs446.mezzo.events.playlists.PlaylistsChangedEvent;
import cs446.mezzo.events.system.ActivityStoppedEvent;
import cs446.mezzo.music.FileSong;
import cs446.mezzo.music.Song;

/**
 * @author curtiskroetsch
 */
@Singleton
public class PlaylistManager {

    private static final String TAG = PlaylistManager.class.getName();
    private static final String KEY_PLAYLISTS = "playlists";
    private static final String KEY_PREFIX_PLAYLIST = "pl-";

    @Inject
    Preferences mPreferences;

    Map<String, Set<Song>> mPlaylists;
    boolean mModified;

    @Inject
    public PlaylistManager() {
        EventBus.register(this);
        mModified = false;
    }

    public boolean createPlaylist(String name) {
        ensureLoaded();
        if (TextUtils.isEmpty(name) || mPlaylists.containsKey(name)) {
            return false;
        }
        final Set<Song> songs = new LinkedHashSet<>();
        mPlaylists.put(name, songs);
        mModified = true;
        EventBus.post(new PlaylistsChangedEvent(name, songs));
        return true;
    }

    public void deletePlaylist(String name) {
        ensureLoaded();
        mPlaylists.remove(name);
        mModified = true;
        EventBus.post(new PlaylistsChangedEvent(name, null));
    }

    private void ensureLoaded() {
        if (mPlaylists == null) {
            mPlaylists = loadPlaylists();
        }
    }

    private Map<String, Set<Song>> loadPlaylists() {
        final Set<String> playlistNames = mPreferences.getStrings(KEY_PLAYLISTS);
        final Map<String, Set<Song>> playlists = new LinkedHashMap<>(playlistNames.size());
        for (String playlistName : playlistNames) {
            final Set<String> paths = mPreferences.getStrings(KEY_PREFIX_PLAYLIST + playlistName);
            final Set<Song> songs = new LinkedHashSet<>(paths.size());
            for (String filename : paths) {
                songs.add(new FileSong(filename));
            }
            playlists.put(playlistName, songs);
        }
        return playlists;
    }

    private void savePlaylists(Map<String, Set<Song>> playlists) {
        Log.d(TAG, "saving playlists");
        mPreferences.putNewStrings(KEY_PLAYLISTS, playlists.keySet());
        for (Map.Entry<String, Set<Song>> playlist : playlists.entrySet()) {
            final Set<String> paths = new HashSet<>(playlist.getValue().size());
            for (Song song : playlist.getValue()) {
                paths.add(song.getFile().getAbsolutePath());
            }
            mPreferences.putNewStrings(KEY_PREFIX_PLAYLIST + playlist.getKey(), paths);
        }
    }

    public Map<String, Set<Song>> getPlaylists() {
        ensureLoaded();
        return mPlaylists;
    }

    public void addSongToPlaylist(String playlistName, Song song) {
        ensureLoaded();
        mPlaylists.get(playlistName).add(song);
        mModified = true;
        EventBus.post(new PlaylistsChangedEvent(playlistName, mPlaylists.get(playlistName)));
    }

    public void removeSongFromPlaylist(String playlistName, Song song) {
        ensureLoaded();
        mPlaylists.get(playlistName).remove(song);
        mModified = true;
        EventBus.post(new PlaylistsChangedEvent(playlistName, null));
    }

    @Subscribe
    public void onActivityStopped(ActivityStoppedEvent event) {
        if (mModified && mPlaylists != null) {
            savePlaylists(mPlaylists);
            mModified = false;
        }
    }
}
