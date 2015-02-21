package cs446.mezzo.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.inject.Inject;
import com.squareup.otto.Subscribe;

import cs446.mezzo.R;
import cs446.mezzo.events.EventBus;
import cs446.mezzo.events.control.PauseToggleEvent;
import cs446.mezzo.events.playback.SongPlayEvent;
import cs446.mezzo.music.AlbumArtManager;
import cs446.mezzo.music.Song;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

/**
 * @author curtiskroetsch
 */
public class MusicControlFragment extends RoboFragment {

    @InjectView(R.id.player_album_art)
    ImageView mAlbumArtView;

    @InjectView(R.id.player_artist)
    TextView mArtistView;

    @InjectView(R.id.player_title)
    TextView mTitleView;

    @InjectView(R.id.player_play_btn)
    ImageView mPlayButton;

    @InjectView(R.id.player_view)
    View mPlayerView;

    @Inject
    AlbumArtManager mArtManager;

    private Song mCurrentSong;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_music_controls, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPlayerView.setVisibility(View.GONE);
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.post(new PauseToggleEvent());
            }
        });
    }

    private void updateSongView() {
        final String artist = TextUtils.isEmpty(mCurrentSong.getArtist()) ?
                getString(R.string.default_artist) :
                mCurrentSong.getArtist();
        mTitleView.setText(mCurrentSong.getTitle());
        mArtistView.setText(artist);
        mAlbumArtView.setImageBitmap(mArtManager.getAlbumArt(mCurrentSong));
    }

    @Subscribe
    public void onSongPlayEvent(SongPlayEvent event) {
        mPlayerView.setVisibility(View.VISIBLE);
        mCurrentSong = event.getSong();
        updateSongView();
    }

    @Override
    public void onDestroy() {
        EventBus.unregister(this);
        super.onDestroy();
    }
}
