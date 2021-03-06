package cs446.mezzo.app.library.catalogs;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cs446.mezzo.events.EventBus;
import cs446.mezzo.events.sources.FileDownloadedEvent;
import cs446.mezzo.music.Song;
import cs446.mezzo.music.playlists.Playlist;
import cs446.mezzo.sources.LocalMusicFetcher;

/**
 * @author curtiskroetsch
 */
public class SongGroupFragment extends CatalogFragment {

    private static final String TAG = SongGroupFragment.class.getName();
    private static final String KEY_GROUPER = "grouper";

    private SongGroup mSongGroup;

    public static SongGroupFragment create(SongGroup songGroup) {
        final SongGroupFragment fragment = new SongGroupFragment();
        final Bundle args = new Bundle();
        args.putString(KEY_GROUPER, songGroup.name());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mSongGroup = SongGroup.valueOf(getArguments().getString(KEY_GROUPER));
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.register(this);
    }

    @Override
    public boolean isSaved(Playlist playlist) {
        return false;
    }

    protected Map<String, Playlist> buildCategories(LocalMusicFetcher fetcher) {
        Log.d(TAG, "buildCategories");
        final Map<String, Playlist> categories = new TreeMap<>();
        final List<Song> songList = fetcher.getAllSongs();

        for (int i = 0; i < songList.size(); i++) {
            addToCategories(songList.get(i), categories);
        }
        return categories;
    }

    private void addToCategories(Song song, final Map<String, Playlist> categories) {
        for (String category : mSongGroup.getGroups(getActivity().getResources(), song)) {
            if (category != null) {
                if (categories.get(category) == null) {
                    final List<Song> songs = new ArrayList<>();
                    songs.add(song);
                    categories.put(category, new Playlist(category, songs));
                } else {
                    categories.get(category).getSongs().add(song);
                }
            }
        }
    }

    @Subscribe
    public void onSongDownloaded(FileDownloadedEvent e) {
        addToCategories(e.getSong(), getCategories());
        updateAdapter();
    }

    @Override
    public void onDestroyView() {
        EventBus.unregister(this);
        super.onDestroyView();
    }

    @Override
    public String getTitle() {
        return mSongGroup == null ? null : mSongGroup.name();
    }
}
