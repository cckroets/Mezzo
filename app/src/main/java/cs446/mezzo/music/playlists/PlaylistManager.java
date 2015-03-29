package cs446.mezzo.music.playlists;

import android.text.TextUtils;
import android.util.Log;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import cs446.mezzo.data.Preferences;
import cs446.mezzo.events.EventBus;
import cs446.mezzo.events.playlists.PlaylistChangedEvent;
import cs446.mezzo.events.system.ActivityStoppedEvent;
import cs446.mezzo.music.Song;
import cs446.mezzo.music.SongSerializer;
import cs446.mezzo.sources.LocalMusicFetcher;

/**
 * @author curtiskroetsch
 */
@Singleton
public class PlaylistManager {

    private static final String TAG = PlaylistManager.class.getName();
    private static final String KEY_PLAYLISTS = "playlists";
    private static final String KEY_PREFIX_PLAYLIST = "pl-";
    private static final String FAVOURITES = "Favorites";

    @Inject
    Preferences mPreferences;

    @Inject
    StatCollector mStatCollector;

    @Inject
    LocalMusicFetcher mLocalMusicFetcher;

    private Map<String, Playlist> mUserPlaylists;
    private Playlist mFavourites;
    private boolean mModified;

    @Inject
    public PlaylistManager() {
        EventBus.register(this);
        mModified = false;
    }

    public boolean createPlaylist(String name) {
        ensureLoaded();
        if (TextUtils.isEmpty(name) || mUserPlaylists.containsKey(name)) {
            return false;
        }
        final Playlist playlist = new Playlist(name, new LinkedHashSet<Song>());
        mUserPlaylists.put(name, playlist);
        mModified = true;
        EventBus.post(new PlaylistChangedEvent(playlist));
        return true;
    }

    public boolean deletePlaylist(String name) {
        ensureLoaded();
        final Playlist removed = mUserPlaylists.remove(name);
        if (removed != null) {
            mModified = true;
            EventBus.post(new PlaylistChangedEvent(removed, true));
            return true;
        }
        return false;
    }

    public boolean isEditable(String playlistName) {
        return mUserPlaylists.containsKey(playlistName);
    }

    public Collection<String> getUserPlaylistTitles() {
        ensureLoaded();
        return mUserPlaylists.keySet();
    }

    public Collection<Playlist> getUserPlaylists() {
        ensureLoaded();
        return mUserPlaylists.values();
    }

    public Collection<Playlist> getAutoPlaylists() {
        final Collection<Playlist> playlistList = new ArrayList<>();
        for (AutoPlaylist auto : AutoPlaylist.values()) {
            playlistList.add(auto.getPlaylist(mLocalMusicFetcher, mStatCollector));
        }
        return playlistList;
    }

    public Playlist getPlaylist(String playlistName) {
        if (playlistName.equals(FAVOURITES)) {
            return mFavourites;
        } else if (mUserPlaylists.containsKey(playlistName)) {
            return mUserPlaylists.get(playlistName);
        }
        final AutoPlaylist autoPlaylist = AutoPlaylist.get(playlistName);
        if (autoPlaylist == null) {
            return null;
        }
        return autoPlaylist.getPlaylist(mLocalMusicFetcher, mStatCollector);
    }

    public Playlist getFavourites() {
        ensureLoaded();
        return mFavourites;
    }

    public void addSongToPlaylist(String playlistName, Song song) {
        ensureLoaded();
        final Playlist playlist = mUserPlaylists.get(playlistName);
        playlist.getSongs().add(song);
        mModified = true;
        EventBus.post(new PlaylistChangedEvent(playlist));
    }

    public void removeSongFromPlaylist(String playlistName, Song song) {
        ensureLoaded();
        mUserPlaylists.get(playlistName).getSongs().remove(song);
        mModified = true;
        EventBus.post(new PlaylistChangedEvent(mUserPlaylists.get(playlistName)));
    }

    public boolean isFavourited(Song song) {
        ensureLoaded();
        return mFavourites.getSongs().contains(song);
    }

    public void addToFavourites(Song song) {
        ensureLoaded();
        mModified = true;
        mFavourites.getSongs().add(song);
        EventBus.post(new PlaylistChangedEvent(mFavourites));
    }

    public void removeFromFavourites(Song song) {
        mFavourites.getSongs().remove(song);
        mModified = true;
        EventBus.post(new PlaylistChangedEvent(mFavourites));
    }

    @Subscribe
    public void onActivityStopped(ActivityStoppedEvent event) {
        if (mModified && mUserPlaylists != null) {
            savePlaylists(mUserPlaylists);
            mModified = false;
        }
    }

    private void ensureLoaded() {
        if (mUserPlaylists == null) {
            mUserPlaylists = loadUserPlaylists();
            mFavourites = loadPlaylist(FAVOURITES);
        }
    }

    private Playlist loadPlaylist(String playlistName) {
        final String key = KEY_PREFIX_PLAYLIST + playlistName;
        final Collection<Song> songs = SongSerializer.loadAll(mPreferences, key);
        return new Playlist(playlistName, songs);
    }

    private Map<String, Playlist> loadUserPlaylists() {
        final Set<String> playlistNames = mPreferences.getStrings(KEY_PLAYLISTS);
        final Map<String, Playlist> playlists = new LinkedHashMap<>(playlistNames.size());
        for (String playlistName : playlistNames) {
            final Playlist playlist = loadPlaylist(playlistName);
            playlists.put(playlistName, playlist);
        }
        return playlists;
    }

    private void savePlaylists(Map<String, Playlist> playlists) {
        Log.d(TAG, "saving playlists");
        mPreferences.putNewStrings(KEY_PLAYLISTS, playlists.keySet());
        for (Playlist playlist : playlists.values()) {
            savePlaylist(playlist);
        }
        savePlaylist(mFavourites);
    }

    private void savePlaylist(Playlist playlist) {
        final String key = KEY_PREFIX_PLAYLIST + playlist.getName();
        SongSerializer.saveAll(mPreferences, key, playlist.getSongs());
    }
}
