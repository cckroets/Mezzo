package cs446.mezzo.app.player;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.squareup.otto.Subscribe;

import cs446.mezzo.R;
import cs446.mezzo.app.BaseMezzoFragment;
import cs446.mezzo.art.AlbumArtManager;
import cs446.mezzo.art.LyricResult;
import cs446.mezzo.art.LyricsManager;
import cs446.mezzo.data.Callback;
import cs446.mezzo.events.EventBus;
import cs446.mezzo.events.control.PauseToggleEvent;
import cs446.mezzo.events.control.PlayNextEvent;
import cs446.mezzo.events.control.PlayPrevEvent;
import cs446.mezzo.events.control.RepeatToggleEvent;
import cs446.mezzo.events.control.SeekSetEvent;
import cs446.mezzo.events.control.ShuffleToggleEvent;
import cs446.mezzo.events.playback.SeekEvent;
import cs446.mezzo.events.playback.SongPauseEvent;
import cs446.mezzo.events.playback.SongPlayEvent;
import cs446.mezzo.music.MusicUtil;
import cs446.mezzo.music.Song;
import roboguice.inject.InjectView;

/**
 * @author curtiskroetsch
 */
public class NowPlayingFragment extends BaseMezzoFragment implements SeekBar.OnSeekBarChangeListener {

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

    @InjectView(R.id.player_lyrics_body)
    TextView mLyricsBody;

    @InjectView(R.id.player_lyrics_copyright)
    TextView mLyricsSecondary;

    @InjectView(R.id.player_lyrics_container)
    View mLyricsContainer;

    @InjectView(R.id.player_get_lyrics)
    View mGetLyricsBtn;

    @InjectView(R.id.player_hide_lyrics)
    View mHideLyricsBtn;

    @Inject
    AlbumArtManager mArtManager;

    @Inject
    LyricsManager mLyricsManager;

    Song mSong;

    boolean mIsUserSeeking;

    public static NowPlayingFragment create() {
        return new NowPlayingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.register(this); // Also gets a song from SongPlayEvent Producer
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
        mSeekBar.setOnSeekBarChangeListener(this);
        setEventClick(mPauseBtn, new PauseToggleEvent());
        setEventClick(mNextBtn, new PlayNextEvent());
        setEventClick(mPrevBtn, new PlayPrevEvent());
        setEventClick(mRepeatBtn, new RepeatToggleEvent());
        setEventClick(mShuffleBtn, new ShuffleToggleEvent());
        mGetLyricsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGetLyrics();
            }
        });
        mHideLyricsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHideLyrics();
            }
        });
        updateSongView();
    }

    private void onGetLyrics() {
        mLyricsManager.getLyrics(mSong, new Callback<LyricResult>() {
            @Override
            public void onSuccess(LyricResult data) {
                if (isAdded()) {
                    mLyricsBody.setText(data.getLyrics());
                    mLyricsSecondary.setText(data.getCopyright());
                    mLyricsContainer.animate().alpha(1f).start();
                    mGetLyricsBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Exception e) {
                if (isAdded()) {
                    Toast.makeText(getActivity(), "Lyrics could not be found", Toast.LENGTH_LONG).show();
                    mGetLyricsBtn.setVisibility(View.INVISIBLE);
                    mGetLyricsBtn.setEnabled(false);
                    mHideLyricsBtn.setEnabled(false);
                }
            }
        });
    }

    private void onHideLyrics() {
        mLyricsContainer.animate().alpha(0).start();
        mGetLyricsBtn.setVisibility(View.VISIBLE);
    }

    private void updateSongView() {
        mTitle.setText(mSong.getTitle());
        mAlbumArtist.setText(mSong.getArtist() + " - " + mSong.getAlbum());
        mDuration.setText(MusicUtil.formatTime(mSong.getDuration()));
        mLyricsContainer.setAlpha(0);
        mLyricsBody.setText(null);
        mLyricsSecondary.setText(null);
        mGetLyricsBtn.setVisibility(View.VISIBLE);
        mGetLyricsBtn.setEnabled(true);
        mHideLyricsBtn.setEnabled(true);
        updateCoverArt();
        updateSeekbar();
    }

    private void updateCoverArt() {
        mArtManager.setAlbumArt(mCovertArt, mSong);
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
    public void onDestroyView() {
        super.onDestroyView();
        getMezzoActivity().showSecondaryFragment();
    }

    @Override
    public void onDestroy() {
        EventBus.unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void onSongPlayEvent(SongPlayEvent event) {
        if (mSong == null) {
            mSong = event.getSong();
        } else if (mSong == event.getSong()) {
            mPauseBtn.setImageResource(R.drawable.ic_av_pause);
            updateSeekbar();
        } else {
            mSong = event.getSong();
            mPauseBtn.setImageResource(R.drawable.ic_av_pause);
            updateSongView();
        }
    }

    @Subscribe
    public void onSeek(SeekEvent event) {
        if (!mIsUserSeeking) {
            final int seekpos = event.getSeekPos();
            mSeekBar.setProgress(seekpos);
            mSeekPosition.setText(MusicUtil.formatTime(seekpos, mSong.getDuration()));
        }
    }

    @Subscribe
    public void onSongPaused(SongPauseEvent event) {
        mPauseBtn.setImageResource(event.isPaused() ?
                R.drawable.ic_av_play_arrow :
                R.drawable.ic_av_pause);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mIsUserSeeking = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mIsUserSeeking = false;
        EventBus.post(new SeekSetEvent(seekBar.getProgress()));
    }

}
