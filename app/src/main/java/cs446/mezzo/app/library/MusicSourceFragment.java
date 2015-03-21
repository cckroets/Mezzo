package cs446.mezzo.app.library;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cs446.mezzo.R;
import cs446.mezzo.data.Callback;
import cs446.mezzo.data.ProgressableCallback;
import cs446.mezzo.events.EventBus;
import cs446.mezzo.events.control.SelectSongEvent;
import cs446.mezzo.music.Song;
import cs446.mezzo.sources.MusicSource;
import roboguice.inject.InjectView;

/**
 * @author curtiskroetsch
 */
public abstract class MusicSourceFragment extends AbsSongsFragment {

    private static final String TAG = MusicSourceFragment.class.getName();
    private static final int MAX_PROGRESS = 100;

    private static final int TYPE_SONG = 0;
    private static final int TYPE_FILE = 1;
    private static final int TYPE_COUNT = 2;

    protected MusicSource mMusicSource;
    protected List<Integer> mSongPositions;
    protected List<Song> mDownloadedSongs;
    protected MusicSource.Authenticator mAuthenticator;

    @InjectView(R.id.song_list)
    ListView mSongsView;

    @InjectView(R.id.progressBar)
    View mProgressBar;

    @Inject
    LayoutInflater mLayoutInflater;

    private MusicFileAdapter mAdapter;
    private boolean mNeedsAuthentication;
    private Map<MusicSource.MusicFile, FileDownloadCallback> mCallbacks;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMusicSource = buildMusicSource();
        mCallbacks = new HashMap<>();
        mAuthenticator = mMusicSource.getAuthenticator();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_music_source, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mAuthenticator.isAuthenticated()) {
            onAuthenticated();
        } else {
            mNeedsAuthentication = true;
            onNotAuthenticated();
        }
    }

    public abstract void onNotAuthenticated();

    public void onSearchFailure() {
        mNeedsAuthentication = true;
        onNotAuthenticated();
    }

    public void onAuthenticated() {
        onSongSearchStart();
    }

    public abstract int getHeaderLayoutId();

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (mNeedsAuthentication) {
            Log.d(TAG, "onResume: NeedsAuthentication");
            mAuthenticator.finishAuthentication(getActivity());
            if (mAuthenticator.isAuthenticated()) {
                onAuthenticated();
            }
        }
    }

    public void onSongSearchStart() {
        mMusicSource.searchForSongs(new Callback<List<MusicSource.MusicFile>>() {
            @Override
            public void onSuccess(List<MusicSource.MusicFile> data) {
                if (isAdded()) {
                    onSongSearchComplete(data);
                }
            }

            @Override
            public void onFailure(Exception e) {
                if (isAdded()) {
                    Toast.makeText(getActivity(), "Could not getImageView songs from " + mMusicSource.getName(), Toast.LENGTH_LONG).show();
                    MusicSourceFragment.this.onSearchFailure();
                }
            }
        });
    }

    public void onSongSearchComplete(List<MusicSource.MusicFile> data) {
        mAdapter = new MusicFileAdapter(data);
        mSongsView.setAdapter(mAdapter);

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

    private void onSongDownloaded(int position, Song song) {
        final int search = Collections.binarySearch(mSongPositions, position);
        if (search < 0) {
            mSongPositions.add(-search - 1, position);
            mDownloadedSongs.add(-search - 1, song);
        }
    }

    private void onSongClick(int songIndex) {
        EventBus.post(new SelectSongEvent(mDownloadedSongs, songIndex));
    }

    protected abstract MusicSource buildMusicSource();

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final int songIndex = Collections.binarySearch(mSongPositions, position - 1);
        if (songIndex >= 0) {
            onSongClick(songIndex);
        }
    }

    @Override
    public List<Song> buildSongsList() {
        return Collections.emptyList();
    }

    @Override
    protected ListAdapter createAdapter(List<Song> songs) {
        if (mAdapter == null) {
            final List<MusicSource.MusicFile> empty = Collections.emptyList();
            mAdapter = new MusicFileAdapter(empty);
        }
        return mAdapter;
    }

    protected void downloadAll() {
        final Context c = getActivity();
        for (int i = 0; i < mAdapter.getCount(); i++) {
            final MusicSource.MusicFile file = mAdapter.getItem(i);
            if (!mMusicSource.exists(c, file) && !mMusicSource.isDownloading(c, file)) {
                onDownload(file, new FileDownloadCallback(i, file));
            }
        }
    }

    protected void onDownload(MusicSource.MusicFile file, FileDownloadCallback callback) {
        mCallbacks.put(file, callback);
        mMusicSource.download(getActivity(), file, callback);
    }

    protected FileDownloadCallback getCallback(MusicSource.MusicFile file) {
        return mCallbacks.get(file);
    }

    protected void onFinishDownload(MusicSource.MusicFile file) {
        mCallbacks.remove(file);
    }

    @Override
    public int getMenuResId() {
        return R.menu.menu_song_item;
    }

    private static class ViewHolder {
        TextView mPrimary;
        TextView mSecondary;
        ProgressBar mProgressBar;
        View mButton;
        FileDownloadCallback mCallback;
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

        @Override
        public boolean isEnabled(int position) {
            return getItemViewType(position) == TYPE_SONG;
        }

        // Make more efficient? This will recreate all rows, we just want to redraw one.
        private void redrawView(int position) {
            notifyDataSetChanged();
        }

        private View getFileView(final int position, View convertView, ViewGroup parent) {

            final MusicSource.MusicFile file = getItem(position);
            final View view;
            final ViewHolder viewHolder;

            if (convertView != null) {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            } else {
                view = mLayoutInflater.inflate(R.layout.view_music_file, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.mPrimary = (TextView) view.findViewById(R.id.file_name);
                viewHolder.mProgressBar = (ProgressBar) view.findViewById(R.id.file_progress);
                viewHolder.mButton = view.findViewById(R.id.file_download);
                view.setTag(viewHolder);
            }

            final FileDownloadCallback progressCallback = getCallback(file);
            if (progressCallback != null) {
                progressCallback.setViewHolder(viewHolder);
            } else if (viewHolder.mCallback != null) {
                viewHolder.mCallback.setHidden();
            }

            viewHolder.mPrimary.setText(file.getDisplayName());
            if (mMusicSource.isDownloading(getContext(), file)) {
                viewHolder.mButton.setEnabled(false);
                viewHolder.mProgressBar.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mButton.setEnabled(true);
                viewHolder.mProgressBar.setVisibility(View.GONE);
            }

            viewHolder.mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.mButton.setEnabled(false);
                    viewHolder.mProgressBar.setVisibility(View.VISIBLE);
                    final FileDownloadCallback callback = new FileDownloadCallback(position, file);
                    callback.setViewHolder(viewHolder);
                    onDownload(file, callback);
                }
            });

            return view;
        }

        private View getSongView(final int position, View convertView, ViewGroup parent) {

            final View view;
            final ViewHolder viewHolder;
            final Song song = mMusicSource.getSong(getContext(), getItem(position));

            if (convertView != null) {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            } else {
                view = mLayoutInflater.inflate(R.layout.view_song, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.mPrimary = (TextView) view.findViewById(cs446.mezzo.R.id.song_title);
                viewHolder.mSecondary = (TextView) view.findViewById(cs446.mezzo.R.id.song_artist);
                viewHolder.mButton = view.findViewById(R.id.song_menu);
                view.setTag(viewHolder);
            }

            viewHolder.mPrimary.setText(song.getTitle());
            viewHolder.mSecondary.setText(TextUtils.isEmpty(song.getArtist()) ?
                    getString(R.string.default_artist) :
                    song.getArtist());
            viewHolder.mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopup(v, song);
                }
            });
            return view;
        }
    }

    private class FileDownloadCallback implements ProgressableCallback<Song> {

        ViewHolder mViewHolder;
        MusicSource.MusicFile mMusicFile;
        int mProgress;
        int mPosition;

        public FileDownloadCallback(int position, MusicSource.MusicFile musicFile) {
            mMusicFile = musicFile;
            mPosition = position;
        }

        @Override
        public void onProgress(float completion) {
            mProgress = (int) (MAX_PROGRESS * completion);
            if (mViewHolder != null) {
                mViewHolder.mProgressBar.setProgress(mProgress);
            }
        }

        @Override
        public void onSuccess(Song data) {
            Log.d(TAG, "DOWNLOAD SUCCESS " + data.getTitle());
            if (mViewHolder != null) {
                mViewHolder.mProgressBar.setProgress(MAX_PROGRESS);
                mAdapter.redrawView(mPosition);
            }
            onSongDownloaded(mPosition, data);
            onFinishDownload(mMusicFile);
        }

        @Override
        public void onFailure(Exception e) {
            if (mViewHolder != null) {
                mViewHolder.mProgressBar.setProgress(0);
                mViewHolder.mProgressBar.setVisibility(View.GONE);
                mViewHolder.mButton.setEnabled(true);
            }
            onFinishDownload(mMusicFile);
            if (getActivity() != null) {
                Toast.makeText(getActivity(), "Download failed", Toast.LENGTH_SHORT).show();
            }
        }

        public void setHidden() {
            mViewHolder = null;
        }

        public void setViewHolder(ViewHolder viewHolder) {
            mViewHolder = viewHolder;
            mViewHolder.mCallback = this;
            mViewHolder.mProgressBar.setProgress(mProgress);
        }
    }
}
