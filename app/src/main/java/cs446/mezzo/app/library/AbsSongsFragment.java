package cs446.mezzo.app.library;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.inject.Inject;

import java.util.Collection;
import java.util.List;

import cs446.mezzo.R;
import cs446.mezzo.app.BaseMezzoFragment;
import cs446.mezzo.events.EventBus;
import cs446.mezzo.events.control.EnqueueEvent;
import cs446.mezzo.events.control.SelectSongEvent;
import cs446.mezzo.music.Song;
import cs446.mezzo.music.playlists.PlaylistManager;
import roboguice.inject.InjectView;

/**
 * @author curtiskroetsch
 */
public abstract class AbsSongsFragment extends BaseMezzoFragment implements AdapterView.OnItemClickListener {

    @InjectView(R.id.song_list)
    ListView mSongView;

    @Inject
    PlaylistManager mPlaylistManager;

    private List<Song> mSongList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSongList = buildSongsList();
    }

    public abstract List<Song> buildSongsList();

    public List<Song> getSongs() {
        return mSongList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_songs, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final SongAdapter songAdapter = new SongAdapter(getActivity(), mSongList);
        mSongView.setAdapter(songAdapter);
        mSongView.setOnItemClickListener(this);
    }

    public void updateSongs() {
        mSongList = buildSongsList();
        mSongView.setAdapter(new SongAdapter(getActivity(), mSongList));
    }

    public void showPopup(View v, final Song song) {
        final PopupMenu popup = new PopupMenu(getActivity(), v);
        final MenuInflater inflater = popup.getMenuInflater();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return AbsSongsFragment.this.onMenuItemClick(item, song);
            }
        });
        inflater.inflate(getMenuResId(), popup.getMenu());
        final MenuItem favouriteButton = popup.getMenu().findItem(R.id.action_favourite);
        favouriteButton.setChecked(mPlaylistManager.isFavourited(song));
        popup.show();
    }

    public abstract int getMenuResId();

    public void onAddToPlaylist(final Song song) {
        final Collection<String> playlistNameSet = mPlaylistManager.getUserPlaylistTitles();
        final String[] playlistNameArray = playlistNameSet.toArray(new String[playlistNameSet.size()]);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setItems(playlistNameArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String selectedPlaylist = playlistNameArray[which];
                        mPlaylistManager.addSongToPlaylist(selectedPlaylist, song);
                    }
                });
        builder.setTitle(R.string.select_playlist).create().show();
    }

    public void onEnqueue(Song song) {
        EventBus.post(new EnqueueEvent(song));
    }

    private void onFavourite(Song song, boolean favourited) {
        if (favourited) {
            mPlaylistManager.addToFavourites(song);
        } else {
            mPlaylistManager.removeFromFavourites(song);
        }
    }

    public PlaylistManager getPlaylistManager() {
        return mPlaylistManager;
    }

    public boolean onMenuItemClick(MenuItem item, Song song) {
        switch (item.getItemId()) {
            case R.id.action_add_to_playlist:
                onAddToPlaylist(song);
                break;
            case R.id.action_enqueue:
                onEnqueue(song);
                break;
            case R.id.action_favourite:
                item.setChecked(!item.isChecked());
                onFavourite(song, item.isChecked());
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        EventBus.post(new SelectSongEvent(mSongList, position));
    }

    private static class ViewHolder {
        TextView primaryView;
        TextView secondaryView;
        View menuButton;
    }

    public class SongAdapter extends ArrayAdapter<Song> {

        private LayoutInflater mInflater;

        public SongAdapter(Context c, List<Song> songs) {
            super(c, 0, songs);
            this.mInflater = LayoutInflater.from(c);
        }

        @Override
        public android.view.View getView(int position, View convertView, ViewGroup parent) {

            final View view;
            final ViewHolder viewHolder;
            final Song song = getItem(position);

            if (convertView == null) {
                view = mInflater.inflate(R.layout.view_song, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.primaryView = (TextView) view.findViewById(R.id.song_title);
                viewHolder.secondaryView = (TextView) view.findViewById(R.id.song_artist);
                viewHolder.menuButton = view.findViewById(R.id.song_menu);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.primaryView.setText(song.getTitle());
            viewHolder.secondaryView.setText(song.getArtist());
            viewHolder.menuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopup(v, song);
                }
            });

            return view;
        }
    }

}
