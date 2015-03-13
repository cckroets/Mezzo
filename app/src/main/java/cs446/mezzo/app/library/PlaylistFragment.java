package cs446.mezzo.app.library;

import android.os.Bundle;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cs446.mezzo.R;
import cs446.mezzo.data.DataHolder;
import cs446.mezzo.music.Song;

/**
 * @author curtiskroetsch
 */
public class PlaylistFragment extends AbsSongsFragment {

    private static final String KEY_NAME = "name";
    private static final String KEY_SONGS = "songs";

    private String mPlaylistName;


    public PlaylistFragment() {

    }

    public static PlaylistFragment create(String name, Collection<Song> songs) {
        final PlaylistFragment fragment = new PlaylistFragment();
        final Bundle args = new Bundle();
        args.putString(KEY_NAME, name);
        DataHolder.save(KEY_SONGS, songs);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPlaylistName = getArguments().getString(KEY_NAME);
    }

    @Override
    public List<Song> buildSongsList() {
        return new ArrayList<Song>((Collection<Song>) DataHolder.retrieve(KEY_SONGS));
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
