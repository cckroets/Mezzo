package cs446.mezzo.app.library.catalogs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.inject.Inject;
import com.squareup.otto.Subscribe;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import cs446.mezzo.R;
import cs446.mezzo.events.EventBus;
import cs446.mezzo.events.playlists.PlaylistsChangedEvent;
import cs446.mezzo.events.sources.FileDownloadedEvent;
import cs446.mezzo.injection.Nullable;
import cs446.mezzo.music.Song;
import cs446.mezzo.music.stats.StatCollector;
import cs446.mezzo.sources.LocalMusicFetcher;
import cs446.mezzo.sources.PlaylistManager;
import roboguice.inject.InjectView;

/**
 * @author curtiskroetsch
 */
public class PlaylistsCatalogFragment extends CatalogFragment {

    private static final int TOP_COUNT = 25;

    @Inject
    StatCollector mStatCollector;

    @Inject
    PlaylistManager mPlaylistManager;

    @Nullable
    @InjectView(R.id.playlists_create)
    View mCreatePlaylistButton;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.register(this);
        mCreatePlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreatePlaylist();
            }
        });
    }

    private void onCreatePlaylist() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        final View view = mLayoutInflater.inflate(R.layout.dialog_create_playlist, null);
        alert.setPositiveButton(R.string.create_playlist, null)
                .setNegativeButton(R.string.cancel, null);
        final AlertDialog alertDialog = alert.setCancelable(true)
                .setTitle(R.string.create_playlist_title)
                .setView(view)
                .create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                onCreateDialogShow(alertDialog, view);
            }
        });
        alertDialog.show();
    }

    private void onCreateDialogShow(final AlertDialog alertDialog, final View view) {
        final Button positive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText contents = (EditText) view.findViewById(R.id.dialog_content);
                final String playlistName = contents.getText().toString();
                final boolean created = mPlaylistManager.createPlaylist(playlistName);
                if (created) {
                    alertDialog.dismiss();
                } else {
                    Toast.makeText(getActivity(), R.string.create_playlist_error, Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    @Subscribe
    public void onPlaylistsChanged(PlaylistsChangedEvent event) {
        updateUserPlaylists();
    }

    private void updateUserPlaylists() {
        if (getCategories() != null) {
            getCategories().putAll(mPlaylistManager.getPlaylists());
            updateAdapter();
        }
    }

    @Override
    protected Map<String, Collection<Song>> buildCategories(LocalMusicFetcher fetcher) {
        final Map<String, Collection<Song>> playlists = new LinkedHashMap<>();
        playlists.put(getString(R.string.playlist_most_played),
                mStatCollector.getTopPlayedSongs(TOP_COUNT));
        playlists.put(getString(R.string.playlist_recently_played),
                mStatCollector.getRecentlyPlayedSongs(TOP_COUNT));
        playlists.put(getString(R.string.playlist_recently_added),
                mStatCollector.getRecentlyAddedSongs(TOP_COUNT));
        playlists.putAll(mPlaylistManager.getPlaylists());
        return playlists;
    }

    @Override
    public int getHeaderLayout() {
        return R.layout.header_playlists;
    }

    @Subscribe
    public void onSongDownloaded(FileDownloadedEvent e) {
        updateContent();
    }

    @Override
    public void onDestroyView() {
        EventBus.unregister(this);
        super.onDestroyView();
    }

    @Override
    public String getTitle() {
        return "Playlists";
    }
}
