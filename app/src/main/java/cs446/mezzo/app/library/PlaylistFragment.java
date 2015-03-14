package cs446.mezzo.app.library;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cs446.mezzo.R;
import cs446.mezzo.music.Song;
import cs446.mezzo.music.playlists.Playlist;

/**
 * @author curtiskroetsch
 */
public class PlaylistFragment extends AbsSongsFragment {

    private static final String KEY_NAME = "name";
    private static final String KEY_SONGS = "songs";
    private static final String KEY_PARCELED = "saved";

    private String mPlaylistName;


    public PlaylistFragment() {

    }

    /**
     * Create an instance of the fragment where the playlist is saved inside of the PlaylistManager
     * @param name
     * @return
     */
    public static PlaylistFragment createFromSavedPlaylist(String name) {
        final PlaylistFragment fragment = new PlaylistFragment();
        final Bundle args = new Bundle();
        args.putString(KEY_NAME, name);
        args.putBoolean(KEY_PARCELED, false);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Create an instance of the fragment where the playlist is not saved in the PlaylistManager,
     * and so we must save it somewhere ourselves.
     * @param playlist
     * @return
     */
    public static PlaylistFragment createFromOnTheFly(Playlist playlist) {
        final PlaylistFragment fragment = new PlaylistFragment();
        final Bundle args = new Bundle();
        args.putString(KEY_NAME, playlist.getName());
        args.putBoolean(KEY_PARCELED, true);
        args.putParcelableArray(KEY_SONGS, songsToParcel(playlist.getSongs()));
        fragment.setArguments(args);
        return fragment;
    }

    private static Parcelable[] songsToParcel(Collection<Song> songs) {
        final Parcelable[] parcelables = new Parcelable[songs.size()];
        songs.toArray(parcelables);
        return parcelables;
    }

    private static List<Song> loadSongs(Bundle bundle) {
        final Parcelable[] parcelables = bundle.getParcelableArray(KEY_SONGS);
        final List<Song> songs = new ArrayList<>(parcelables.length);
        for (Parcelable parcelable : parcelables) {
            songs.add((Song) parcelable);
        }
        return songs;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public List<Song> buildSongsList() {
        mPlaylistName = getArguments().getString(KEY_NAME);
        Log.d("PLAYLIST NAME", mPlaylistName);
        final boolean bundled = getArguments().getBoolean(KEY_PARCELED, false);
        final List<Song> songs;
        if (bundled) {
            songs = loadSongs(getArguments());
        } else {
            songs = new ArrayList<>(getPlaylistManager().getPlaylist(mPlaylistName).getSongs());
        }
        return songs;
    }

    public void onRemoveFromPlaylist(Song song) {
        getPlaylistManager().removeSongFromPlaylist(mPlaylistName, song);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item, Song song) {
        switch (item.getItemId()) {
            case R.id.action_remove:
                onRemoveFromPlaylist(song);
                break;
            default:
                return super.onMenuItemClick(item, song);
        }
        return true;
    }

    @Override
    public int getMenuResId() {
        return R.menu.menu_song_item;
    }

    @Override
    public String getTitle() {
        return mPlaylistName;
    }

    @Override
    public boolean isTopLevel() {
        return true;
    }
}
