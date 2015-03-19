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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cs446.mezzo.R;
import cs446.mezzo.events.EventBus;
import cs446.mezzo.events.playlists.PlaylistChangedEvent;
import cs446.mezzo.events.sources.FileDownloadedEvent;
import cs446.mezzo.injection.Nullable;
import cs446.mezzo.music.Song;
import cs446.mezzo.music.playlists.Playlist;
import cs446.mezzo.music.playlists.PlaylistManager;
import cs446.mezzo.sources.LocalMusicFetcher;
import roboguice.inject.InjectView;

/**
 * @author curtiskroetsch
 */
public class PlaylistsCatalogFragment extends CatalogFragment {

    @Inject
    PlaylistManager mPlaylistManager;

    @Inject
    LocalMusicFetcher mMusicFetcher;

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

    private void createPlaylistSongPickerDialog() {
        final List<Song> mAllSongs = mMusicFetcher.getAllSongs();
        final List<String> mSongsNames = new ArrayList<String>();
        for (Song song : mAllSongs) {
            mSongsNames.add(song.getTitle() + " - " + song.getArtist());
        }
        final CharSequence[] items = mSongsNames.toArray(new CharSequence[mSongsNames.size()]);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMultiChoiceItems(items, null,
                new DialogInterface.OnMultiChoiceClickListener() {
                    // indexSelected contains the index of item (of which checkbox checked)
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected,
                                        boolean isChecked) {
                    }
                });

        builder.setPositiveButton(R.string.ok, null)
                .setNegativeButton(R.string.cancel, null);
        final AlertDialog alertDialog = builder.setCancelable(true)
                .setTitle(R.string.choose_playlist_songs_title)
                .create();

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
                    createPlaylistSongPickerDialog();
                } else {
                    Toast.makeText(getActivity(), R.string.create_playlist_error, Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    @Subscribe
    public void onPlaylistsChanged(PlaylistChangedEvent event) {
        if (event.getPlaylist() != null) {
            getCategories().put(event.getPlaylist().getName(), event.getPlaylist());
            updateAdapter();
        }
    }

    @Override
    protected Map<String, Playlist> buildCategories(LocalMusicFetcher fetcher) {
        final Map<String, Playlist> playlists = new LinkedHashMap<>();
        addPlaylists(playlists, mPlaylistManager.getAutoPlaylists());
        addPlaylists(playlists, mPlaylistManager.getUserPlaylists());

        final Playlist favourites = mPlaylistManager.getFavourites();
        playlists.put(favourites.getName(), favourites);
        return playlists;
    }

    @Override
    public boolean isSaved(Playlist playlist) {
        return true;
    }

    private void addPlaylists(Map<String, Playlist> categories, Collection<Playlist> playlists) {
        for (Playlist playlist : playlists) {
            categories.put(playlist.getName(), playlist);
        }
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
