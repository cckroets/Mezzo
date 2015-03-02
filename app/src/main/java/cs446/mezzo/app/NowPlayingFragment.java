package cs446.mezzo.app;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.inject.Inject;
import com.squareup.otto.Subscribe;

import cs446.mezzo.R;
import cs446.mezzo.events.EventBus;
import cs446.mezzo.events.control.PauseToggleEvent;
import cs446.mezzo.events.control.PlayNextEvent;
import cs446.mezzo.events.control.PlayPrevEvent;
import cs446.mezzo.events.control.RepeatToggleEvent;
import cs446.mezzo.events.control.ShuffleToggleEvent;
import cs446.mezzo.events.playback.SongPlayEvent;
import cs446.mezzo.music.AlbumArtManager;
import cs446.mezzo.music.MusicUtil;
import cs446.mezzo.music.Song;
import cs446.mezzo.music.SongPlayer;
import roboguice.inject.InjectView;

/**
 * @author curtiskroetsch
 */
public class NowPlayingFragment extends BaseMezzoFragment implements SeekBar.OnSeekBarChangeListener {

    private static final int SEEK_DELAY_MS = 300;
    private static final String KEY_SONG = "song";

    @InjectView(R.id.player_title)
    TextView mTitle;

    @InjectView(R.id.player_album_artist)
    TextView mAlbumArtist;

    @InjectView(R.id.player_album_art)
    ImageView mCovertArt;

    @InjectView(R.id.player_pause)
    ImageView mPauseBtn;

    @InjectView(R.id.player_next)
    ImageView mNextBtn;

    @InjectView(R.id.player_previous)
    ImageView mPrevBtn;

    @InjectView(R.id.player_shuffle)
    ImageView mShuffleBtn;

    @InjectView(R.id.player_repeat)
    ImageView mRepeatBtn;

    @InjectView(R.id.player_seek_bar)
    SeekBar mSeekBar;

    @InjectView(R.id.player_seek_position)
    TextView mSeekPosition;

    @InjectView(R.id.player_duration)
    TextView mDuration;

    @Inject
    AlbumArtManager mArtManager;

    @Inject
    SongPlayer mSongPlayer;

    Song mSong;

    public static NowPlayingFragment create(Song song) {
        final NowPlayingFragment fragment = new NowPlayingFragment();
        final Bundle args = new Bundle();
        args.putParcelable(KEY_SONG, song);
        fragment.setArguments(args);
        return fragment;
    }

    Runnable mSeekRunnable = new Runnable() {
        @Override
        public void run() {
            final int seekpos = mSongPlayer.getSeekPosition();
            mSeekBar.setProgress(seekpos);
            mSeekPosition.setText(MusicUtil.formatTime(seekpos, mSong.getDuration()));
            if (isAdded()) {
                getMezzoActivity().postDelayed(mSeekRunnable, SEEK_DELAY_MS);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.register(this);
        mSong = getArguments().getParcelable(KEY_SONG);
        invalidateActionBar();
        getMezzoActivity().hideSecondaryFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_now_playing, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMezzoActivity().post(mSeekRunnable);
        mSeekBar.setOnSeekBarChangeListener(this);
        mAlbumArtist.setSelected(true);
        setEventClick(mPauseBtn, new PauseToggleEvent());
        setEventClick(mNextBtn, new PlayNextEvent());
        setEventClick(mPrevBtn, new PlayPrevEvent());
        setEventClick(mRepeatBtn, new RepeatToggleEvent());
        setEventClick(mShuffleBtn, new ShuffleToggleEvent());
        updateSongView();
    }

    private void updateSongView() {
        mTitle.setText(mSong.getTitle());
        mAlbumArtist.setText(mSong.getArtist() + " - " + mSong.getAlbum());
        mDuration.setText(MusicUtil.formatTime(mSong.getDuration()));
        updateCoverArt();
        updateSeekbar();
    }

    private void updateCoverArt() {
        final Bitmap bitmap = mArtManager.getAlbumArt(mSong);
        if (bitmap != null) {
            mCovertArt.setImageBitmap(bitmap);
        }
    }

    private void updateSeekbar() {
        mSeekBar.setProgress(0);
        mSeekBar.setMax((int) mSong.getDuration());
        mSeekPosition.setText(MusicUtil.formatTime(0, mSong.getDuration()));
    }

    private void setEventClick(View view, final Object event) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.post(event);
            }
        });
    }

    @Override
    public String getTitle() {
        return "Now Playing";
    }

    @Override
    public void onDestroy() {
        EventBus.unregister(this);
        getMezzoActivity().showSecondaryFragment();
        super.onDestroy();
    }

    @Subscribe
    public void onSongPlayEvent(SongPlayEvent event) {
        if (mSong == event.getSong()) {
            updateSeekbar();
        } else {
            mSong = event.getSong();
            updateSongView();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        getMezzoActivity().removeCallbacks(mSeekRunnable);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mSongPlayer.setSeek(seekBar.getProgress());
        getMezzoActivity().post(mSeekRunnable);
    }


}
