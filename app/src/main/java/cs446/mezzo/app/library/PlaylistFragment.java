package cs446.mezzo.app.library;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import cs446.mezzo.R;
import cs446.mezzo.data.DataHolder;
import cs446.mezzo.music.FileSong;
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

    private static String[] songsToArray(Collection<Song> songs) {
        final String[] array = new String[songs.size()];
        final Iterator<Song> songIterator = songs.iterator();
        for (int i = 0; i < songs.size(); i++) {
            array[i] = songIterator.next().getFile().getPath();
        }
        return array;
    }

    private static List<Song> songsFromArray(String[] paths) {
        final List<Song> songs = new ArrayList<>(paths.length);
        for (String path : paths) {
            songs.add(new FileSong(path));
        }
        return songs;
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
