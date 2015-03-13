package cs446.mezzo.app.library;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.inject.Inject;

import java.util.List;

import cs446.mezzo.R;
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
    @InjectView(R.id.source_sync)
    View mSyncButton;

    @Inject
    DropboxSource mDropboxSource;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgressBar.setVisibility(View.GONE);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuthenticator.startAuthentication(getActivity());
            }
        });
    }

    @Override
    public void onAuthenticated() {
        Log.d(TAG, "onAuthenticated");
        mSignInButton.setVisibility(View.GONE);
        mSyncButton.setVisibility(View.VISIBLE);
        super.onAuthenticated();
    }

    @Override
    public void onNotAuthenticated() {
        Log.d(TAG, "onNotAuthenticated");
        mSignInButton.setVisibility(View.VISIBLE);
        mSyncButton.setVisibility(View.GONE);
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
