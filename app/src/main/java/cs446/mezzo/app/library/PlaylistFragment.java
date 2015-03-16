package cs446.mezzo.app.library;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cs446.mezzo.R;
import cs446.mezzo.events.EventBus;
import cs446.mezzo.events.control.ShuffleAllEvent;
import cs446.mezzo.music.Song;
import cs446.mezzo.music.playlists.Playlist;
import cs446.mezzo.music.playlists.StatCollector;

/**
 * @author curtiskroetsch
 */
public class PlaylistFragment extends AbsSongsFragment {

    private static final String KEY_NAME = "name";
    private static final String KEY_SONGS = "songs";
    private static final String KEY_PARCELED = "saved";

    @Inject
    StatCollector mStatCollector;

    private String mPlaylistName;
    private List<Song> mSongs;

    public PlaylistFragment() {

    }

    /**
     * Create an instance of the fragment where the playlist is saved inside of the PlaylistManager
     *
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
     *
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
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        final boolean isEditable = getPlaylistManager().isEditable(mPlaylistName);
        inflater.inflate(R.menu.menu_playlist, menu);
        menu.findItem(R.id.action_delete_playlist).setVisible(isEditable);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_shuffle_all:
                shuffleAll();
                break;
            case R.id.action_delete_playlist:
                deletePlaylist();
                break;
            case R.id.action_report_stats:
                reportStats();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void reportStats() {
        final String report = mStatCollector.buildStatsReport(mSongs);
        final Intent sendEmail = new Intent();
        sendEmail.setAction(Intent.ACTION_SEND);
        sendEmail.putExtra(Intent.EXTRA_TEXT, report);
        sendEmail.setType("text/plain");

        final Intent openInChooser = Intent.createChooser(sendEmail, "Export using...");
        startActivityForResult(openInChooser, 1);
    }

    private void deletePlaylist() {
        getPlaylistManager().deletePlaylist(mPlaylistName);
    }

    private void shuffleAll() {
        EventBus.post(new ShuffleAllEvent(mSongs));
    }

    @Override
    public List<Song> buildSongsList() {
        mPlaylistName = getArguments().getString(KEY_NAME);
        Log.d("PLAYLIST NAME", mPlaylistName);
        final boolean bundled = getArguments().getBoolean(KEY_PARCELED, false);
        if (bundled) {
            mSongs = loadSongs(getArguments());
        } else {
            mSongs = new ArrayList<>(getPlaylistManager().getPlaylist(mPlaylistName).getSongs());
        }
        return mSongs;
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
