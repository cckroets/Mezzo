package cs446.mezzo.app.library;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cs446.mezzo.R;
import cs446.mezzo.app.BaseMezzoFragment;
import cs446.mezzo.metadata.art.AlbumArtManager;
import cs446.mezzo.music.Song;
import cs446.mezzo.sources.LocalMusicFetcher;
import roboguice.inject.InjectView;

/**
 * @author curtiskroetsch
 */
public abstract class CatalogFragment extends BaseMezzoFragment implements AdapterView.OnItemClickListener {

    @Inject
    LocalMusicFetcher mMusicFetcher;

    @Inject
    AlbumArtManager mArtManager;

    @InjectView(R.id.catalog_grid)
    GridView mCatalog;

    private Map<String, List<Song>> mCategories;

    public CatalogFragment() {

    }

    @Override
    public void invalidateActionBar() {

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
        return inflater.inflate(R.layout.fragment_catalog, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final String[] keys = new String[mCategories.size()];
        mCategories.keySet().toArray(keys);
        final PlaylistAdapter songAdapter = new PlaylistAdapter(getActivity(), keys);
        mCatalog.setAdapter(songAdapter);
        mCatalog.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    private static class ViewHolder {
        TextView titleView;
        ImageView imageView;
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
            final List<Song> songs = mCategories.get(title);

            if (convertView != null) {
                view = convertView;
                viewHolder = (ViewHolder) convertView.getTag();
            } else {
                view = mInflater.inflate(R.layout.view_playlist, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.titleView = (TextView) view.findViewById(R.id.item_title);
                viewHolder.imageView = (ImageView) view.findViewById(R.id.item_image);
                view.setTag(viewHolder);
            }

            //mArtManager.setAlbumArt(viewHolder.imageView, songs.get(0));
            mArtManager.setAlbumArt(viewHolder.imageView, songs.get(0));
            viewHolder.titleView.setText(title);
            return view;
        }

    }

}
