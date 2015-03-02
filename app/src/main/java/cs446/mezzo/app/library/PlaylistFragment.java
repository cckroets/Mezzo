package cs446.mezzo.app.library;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cs446.mezzo.R;
import cs446.mezzo.app.BaseMezzoFragment;
import cs446.mezzo.music.Song;
import cs446.mezzo.sources.LocalMusicFetcher;
import roboguice.inject.InjectView;

/**
 * @author curtiskroetsch
 */
public abstract class PlaylistFragment extends BaseMezzoFragment implements AdapterView.OnItemClickListener {

    @Inject
    LocalMusicFetcher mMusicFetcher;

    @InjectView(R.id.song_list)
    ListView mListView;

    private Map<String, List<Song>> mCategories;

    public PlaylistFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCategories = buildCategories(mMusicFetcher);
    }

    protected abstract String[] getCategoriesForSong(Song song);

    protected Map<String, List<Song>> buildCategories(LocalMusicFetcher fetcher) {
        final Map<String, List<Song>> categories = new HashMap<>();
        final List<Song> songList = fetcher.getLocalSongs();

        for (int i = 0; i < songList.size(); i++) {

            for (String category : getCategoriesForSong(songList.get(i))) {

                if (categories.get(category) == null) {
                    final List<Song> songs = new ArrayList<>();
                    songs.add(songList.get(i));
                    categories.put(category, songs);
                } else {
                    categories.get(category).add(songList.get(i));
                }
            }
        }
        return categories;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_songs, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final String[] keys = new String[mCategories.size()];
        mCategories.keySet().toArray(keys);
        final PlaylistAdapter songAdapter = new PlaylistAdapter(getActivity(), keys);
        mListView.setAdapter(songAdapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    private static class ViewHolder {
        TextView titleView;
    }

    public class PlaylistAdapter extends ArrayAdapter<String> {

        private LayoutInflater mInflater;

        public PlaylistAdapter(Context c, String[] songs) {
            super(c, 0, songs);
            mInflater = LayoutInflater.from(c);
        }

        @Override
        public android.view.View getView(int position, View convertView, ViewGroup parent) {

            final View view;
            final ViewHolder viewHolder;
            final String title = getItem(position);

            if (convertView != null) {
                view = convertView;
                viewHolder = (ViewHolder) convertView.getTag();
            } else {
                view = mInflater.inflate(R.layout.view_list_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.titleView = (TextView) view.findViewById(R.id.item_title);
                view.setTag(viewHolder);
            }

            viewHolder.titleView.setText(title);
            return view;
        }

    }

}
