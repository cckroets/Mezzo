package cs446.mezzo.app.library;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.inject.Inject;

import java.util.List;

import cs446.mezzo.R;
import cs446.mezzo.data.Callback;
import cs446.mezzo.injection.Nullable;
import cs446.mezzo.sources.MusicSource;
import cs446.mezzo.sources.dropbox.DropboxSource;
import roboguice.inject.InjectView;

/**
 * @author curtiskroetsch
 */
public class DropboxFragment extends MusicSourceFragment {

    private static final String TAG = DropboxFragment.class.getName();

    @Nullable
    @InjectView(R.id.source_sign_in)
    View mSignInButton;

    @Nullable
    @InjectView(R.id.source_download_all)
    View mDownloadAll;

    @Nullable
    @InjectView(R.id.source_refresh)
    View mRefresh;

    @Inject
    DropboxSource mDropboxSource;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgressBar.setVisibility(View.GONE);
        mProgressBar.bringToFront();
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuthenticator.startAuthentication(getActivity());
            }
        });
        mDownloadAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDropboxSource.searchForSongs(new Callback<List<MusicSource.MusicFile>>() {
                    @Override
                    public void onSuccess(List<MusicSource.MusicFile> data) {
                        downloadAll();
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                }, false);
            }
        });
        mRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSongSearchStart();
            }
        });
    }

    @Override
    public void onAuthenticated() {
        Log.d(TAG, "onAuthenticated");
        mSignInButton.setVisibility(View.GONE);
        mDownloadAll.setVisibility(View.VISIBLE);
        super.onAuthenticated();
    }

    @Override
    public void onNotAuthenticated() {
        Log.d(TAG, "onNotAuthenticated");
        mSignInButton.setVisibility(View.VISIBLE);
        mDownloadAll.setVisibility(View.GONE);
    }

    @Override
    public void onSongSearchStart() {
        Log.d(TAG, "onSongSearchStart");
        mProgressBar.setVisibility(View.VISIBLE);
        super.onSongSearchStart();
    }

    @Override
    public void onSongSearchComplete(List<MusicSource.MusicFile> data) {
        Log.d(TAG, "onSongSearchComplete");
        mProgressBar.setVisibility(View.GONE);
        super.onSongSearchComplete(data);
    }

    @Override
    public void onSearchFailure() {
        Log.d(TAG, "onSearchFailure");
        mProgressBar.setVisibility(View.GONE);
        super.onSearchFailure();
    }

    @Override
    public int getHeaderLayoutId() {
        return R.layout.header_music_source_dropbox;
    }

    @Override
    protected MusicSource buildMusicSource() {
        return mDropboxSource;
    }

    @Override
    public String getTitle() {
        return "Dropbox";
    }
}
