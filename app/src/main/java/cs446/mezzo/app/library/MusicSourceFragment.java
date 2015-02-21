package cs446.mezzo.app.library;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cs446.mezzo.R;
import cs446.mezzo.app.BaseMezzoFragment;
import cs446.mezzo.app.NowPlayingFragment;
import cs446.mezzo.data.Callback;
import cs446.mezzo.data.ProgressableCallback;
import cs446.mezzo.events.EventBus;
import cs446.mezzo.events.control.SelectSongEvent;
import cs446.mezzo.music.Song;
import cs446.mezzo.sources.MusicSource;
import roboguice.RoboGuice;
import roboguice.inject.InjectView;

/**
 * @author curtiskroetsch
 */
public class MusicSourceFragment extends BaseMezzoFragment {

    private static final String TAG = MusicSourceFragment.class.getName();
    private static final String KEY_SOURCE = "source";
    private static final int MAX_PROGRESS = 100;

    private static final int TYPE_SONG = 0;
    private static final int TYPE_FILE = 1;
    private static final int TYPE_COUNT = 2;


    @InjectView(R.id.song_list)
    ListView mSongsView;

    @Inject
    LayoutInflater mLayoutInflater;

    MusicSource mMusicSource;

    List<Integer> mSongPositions;
    List<Song> mDownloadedSongs;

    public static MusicSourceFragment create(MusicSource source) {
        final MusicSourceFragment fragment = new MusicSourceFragment();
        final Bundle args = new Bundle();
        args.putString(KEY_SOURCE, source.getClass().getName());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String name = getArguments().getString(KEY_SOURCE);
        try {
            final Class sourceClass = Class.forName(name);
            mMusicSource = (MusicSource) RoboGuice.getInjector(getActivity()).getInstance(sourceClass);
        } catch (ClassNotFoundException e) {
            Log.w(TAG, "Class Not Found: " + name);
            mMusicSource = null;
            throw new IllegalArgumentException(e.getMessage());
        }
        invalidateActionBar();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_songs, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMusicSource.getAllSongs(new Callback<List<MusicSource.MusicFile>>() {
            @Override
            public void onSuccess(List<MusicSource.MusicFile> data) {
                if (isAdded()) {
                    final ListAdapter adapter = new MusicFileAdapter(data);
                    mSongsView.setAdapter(adapter);

                    mDownloadedSongs = new ArrayList<Song>(data.size());
                    mSongPositions = new ArrayList<Integer>(data.size());
                    for (int i = 0; i < data.size(); i++) {
                        final MusicSource.MusicFile file = data.get(i);
                        if (mMusicSource.exists(getActivity(), file)) {
                            mDownloadedSongs.add(mMusicSource.getSong(getActivity(), file));
                            mSongPositions.add(i);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getActivity(), "Could not get songs from " + mMusicSource.getName(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void onSongDownloaded(int position, Song song) {
        final int search = Collections.binarySearch(mSongPositions, position);
        if (search < 0) {
            mSongPositions.add(-search - 1, position);
            mDownloadedSongs.add(-search - 1, song);
        }
    }

    private void onSongClick(int songIndex) {
        final Song song = mDownloadedSongs.get(songIndex);
        getMezzoActivity().setFragment(NowPlayingFragment.create(song));
        EventBus.post(new SelectSongEvent(mDownloadedSongs, songIndex));
    }

    @Override
    public String getTitle() {
        return mMusicSource == null ? null : mMusicSource.getName();
    }

    private class MusicFileAdapter extends ArrayAdapter<MusicSource.MusicFile> {

        public MusicFileAdapter(List<MusicSource.MusicFile> musicList) {
            super(getActivity(), 0, musicList);
        }

        @Override
        public int getItemViewType(int position) {
            return mMusicSource.exists(getContext(), getItem(position)) ? TYPE_SONG : TYPE_FILE;
        }

        @Override
        public int getViewTypeCount() {
            return TYPE_COUNT;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            switch (getItemViewType(position)) {
                case TYPE_FILE:
                    return getFileView(position, convertView, parent);
                case TYPE_SONG:
                    return getSongView(position, convertView, parent);
                default:
                    return null;
            }
        }

        // Make more efficient? This will recreate all rows, we just want to redraw one.
        private void redrawView(int position) {
            notifyDataSetChanged();
        }

        private View getFileView(final int position, View convertView, ViewGroup parent) {

            final MusicSource.MusicFile file = getItem(position);
            final View view;
            Log.d(TAG, "getFileView " + file.getFileName());
            if (convertView != null) {
                view = convertView;
            } else {
                view = mLayoutInflater.inflate(R.layout.view_music_file, parent, false);
            }

            final TextView title = (TextView) view.findViewById(R.id.file_name);
            final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.file_progress);
            final ImageView download = (ImageView) view.findViewById(R.id.file_download);

            title.setText(file.getDisplayName());
            progressBar.setVisibility(mMusicSource.isDownloading(getContext(), file) ? View.VISIBLE : View.GONE);
            download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    download.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);
                    mMusicSource.download(getContext(), file, new ProgressableCallback<Song>() {
                        @Override
                        public void onProgress(float completion) {
                            progressBar.setProgress((int) (MAX_PROGRESS * completion));
                        }

                        @Override
                        public void onSuccess(Song data) {
                            Log.d(TAG, "DOWNLOAD SUCCESS " + data.getTitle());
                            progressBar.setProgress(MAX_PROGRESS);
                            redrawView(position);
                            onSongDownloaded(position, data);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            progressBar.setProgress(0);
                            progressBar.setVisibility(View.GONE);
                            download.setEnabled(true);
                            Toast.makeText(getContext(), "Download failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            return view;
        }

        private View getSongView(final int position, View convertView, ViewGroup parent) {

            final View view = (convertView == null) ?
                    mLayoutInflater.inflate(R.layout.view_song, parent, false) :
                    convertView;

            final TextView songView = (TextView) view.findViewById(cs446.mezzo.R.id.song_title);
            final TextView artistView = (TextView) view.findViewById(cs446.mezzo.R.id.song_artist);
            final Song song = mMusicSource.getSong(getContext(), getItem(position));
            Log.d(TAG, "getSongView " + song.getTitle());

            songView.setText(song.getTitle());
            artistView.setText(TextUtils.isEmpty(song.getArtist()) ?
                    getString(R.string.default_artist) :
                    song.getArtist());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int songIndex = Collections.binarySearch(mSongPositions, position);
                    if (songIndex < 0) {
                        songIndex = 0;
                    }
                    onSongClick(songIndex);
                }
            });

            return view;
        }


    }


}
