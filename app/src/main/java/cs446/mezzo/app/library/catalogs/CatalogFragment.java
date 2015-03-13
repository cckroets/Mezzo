package cs446.mezzo.app.library.catalogs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.inject.Inject;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import cs446.mezzo.R;
import cs446.mezzo.app.BaseMezzoFragment;
import cs446.mezzo.events.EventBus;
import cs446.mezzo.events.navigation.PlaylistSelectedEvent;
import cs446.mezzo.injection.Injector;
import cs446.mezzo.metadata.art.AlbumArtManager;
import cs446.mezzo.music.Song;
import cs446.mezzo.sources.LocalMusicFetcher;
import cs446.mezzo.view.MezzoImageView;
import jp.co.recruit_mp.android.widget.HeaderFooterGridView;
import roboguice.inject.InjectView;

/**
 * @author curtiskroetsch
 */
public abstract class CatalogFragment extends BaseMezzoFragment implements AdapterView.OnItemClickListener {

    private static final int MAX_VIEWS = 4;

    @Inject
    LocalMusicFetcher mMusicFetcher;

    @Inject
    AlbumArtManager mArtManager;

    @Inject
    LayoutInflater mLayoutInflater;

    @InjectView(R.id.catalog_grid)
    HeaderFooterGridView mCatalog;

    private Map<String, Collection<Song>> mCategories;

    public CatalogFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCategories = buildCategories(mMusicFetcher);
    }

    protected abstract Map<String, Collection<Song>> buildCategories(LocalMusicFetcher fetcher);

    public Map<String, Collection<Song>> getCategories() {
        return mCategories;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_catalog, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final View header = mLayoutInflater.inflate(getHeaderLayout(), null);
        final View footer = mLayoutInflater.inflate(getFooterLayout(), null);
        mCatalog.setOnItemClickListener(this);
        mCatalog.addHeaderView(header);
        mCatalog.addFooterView(footer);
        Injector.injectViews(this, header);
        Injector.injectViews(this, footer);
        updateAdapter();
    }

    public int getHeaderLayout() {
        return R.layout.header_default;
    }

    public int getFooterLayout() {
        return R.layout.header_default;
    }

    public void updateAdapter() {
        final String[] keys = new String[mCategories.size()];
        mCategories.keySet().toArray(keys);
        mCatalog.setAdapter(new PlaylistAdapter(getActivity(), keys));
    }

    public void updateContent() {
        mCategories = buildCategories(mMusicFetcher);
        updateAdapter();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    private Song[] songCollectionToArray(Collection<Song> songCollection) {
        final int size = Math.min(MAX_VIEWS, songCollection.size());
        final Song[] songs = new Song[size];
        final Iterator<Song> iterator = songCollection.iterator();
        for (int i = 0; i < size; i++) {
            songs[i] = iterator.next();
        }
        return songs;
    }

    private static class ViewHolder {
        TextView titleView;
        MezzoImageView[] imageView;
        View column2;
        View hitTarget;
    }

    public class PlaylistAdapter extends ArrayAdapter<String> {

        private LayoutInflater mInflater;

        public PlaylistAdapter(Context c, String[] songs) {
            super(c, 0, songs);
            mInflater = LayoutInflater.from(c);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View view;
            final ViewHolder viewHolder;
            final String title = getItem(position);
            final Collection<Song> allSongs = mCategories.get(title);
            final Song[] viewSongs = songCollectionToArray(allSongs);

            if (convertView != null && convertView.getTag() != null) {
                view = convertView;
                viewHolder = (ViewHolder) convertView.getTag();
            } else {
                view = mInflater.inflate(R.layout.view_playlist_2, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.titleView = (TextView) view.findViewById(R.id.item_title);
                viewHolder.imageView = new MezzoImageView[MAX_VIEWS];
                int i = 0;
                viewHolder.imageView[i++] = (MezzoImageView) view.findViewById(R.id.item_image_1);
                viewHolder.imageView[i++] = (MezzoImageView) view.findViewById(R.id.item_image_2);
                viewHolder.imageView[i++] = (MezzoImageView) view.findViewById(R.id.item_image_3);
                viewHolder.imageView[i] = (MezzoImageView) view.findViewById(R.id.item_image_4);
                viewHolder.hitTarget = view.findViewById(R.id.playlist_target);
                viewHolder.column2 = view.findViewById(R.id.item_column_2);
                view.setTag(viewHolder);
            }

            for (int i = 0; i < MAX_VIEWS; i++) {
                if (viewSongs.length > i) {
                    viewHolder.imageView[i].setVisibility(View.VISIBLE);
                    viewHolder.imageView[i].bindWithSong(mArtManager, viewSongs[i]);
                } else {
                    viewHolder.imageView[i].setVisibility(View.GONE);
                }
            }

            view.setVisibility(View.VISIBLE);
            viewHolder.hitTarget.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.post(new PlaylistSelectedEvent(title, allSongs));
                }
            });
            viewHolder.column2.setVisibility(viewSongs.length > 1 ? View.VISIBLE : View.GONE);
            viewHolder.titleView.setText(title);
            return view;
        }
    }
}
