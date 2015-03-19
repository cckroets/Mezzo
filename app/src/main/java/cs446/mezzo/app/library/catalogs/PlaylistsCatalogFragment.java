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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    private void createPlaylistSongPickerDialog(final String playlistName) {
        final List<Song> allSongs = mMusicFetcher.getAllSongs();
        final CharSequence[] items = new CharSequence[allSongs.size()];
        for (int i = 0; i < allSongs.size(); i++) {
            final Song song = allSongs.get(i);
            items[i] = song.getTitle() + " - " + song.getArtist();
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final Set<Integer> selected = new HashSet<>();
        builder.setMultiChoiceItems(items, null,
                new DialogInterface.OnMultiChoiceClickListener() {
                    // indexSelected contains the index of item (of which checkbox checked)
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected,
                                        boolean isChecked) {
                        if (isChecked) {
                            selected.add(indexSelected);
                        } else {
                            selected.remove(indexSelected);
                        }
                    }
                });

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int selectedIndex : selected) {
                    final Song song = allSongs.get(selectedIndex);
                    mPlaylistManager.addSongToPlaylist(playlistName, song);
                }
            }
        })
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
                    createPlaylistSongPickerDialog(playlistName);
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
            if (event.isDeleted()) {
                getCategories().remove(event.getPlaylist().getName());
            } else {
                getCategories().put(event.getPlaylist().getName(), event.getPlaylist());
            }
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
        // Nothing needs to be done as long as their is no sort of "downloads" playlist
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
