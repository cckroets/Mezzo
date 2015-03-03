package cs446.mezzo.app.player;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import cs446.mezzo.view.ViewUtil;
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

    @InjectView(R.id.player_buttons_container)
    View mPlayerButtonsContainer;

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

    MenuItem mLyricsMenuItem;

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
        setHasOptionsMenu(true);
        invalidateActionBar();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_now_playing, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_now_playing, menu);
        mLyricsMenuItem = menu.findItem(R.id.action_lyrics);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_lyrics:
                if (item.isChecked()) {
                    item.setChecked(false);
                    item.setIcon(R.drawable.ic_av_subtitles);
                    onHideLyrics();
                } else {
                    item.setChecked(true);
                    item.setIcon(R.drawable.ic_av_subtitles_on);
                    onGetLyrics();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
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
        updateSongView();

        mShuffleBtn.setOnClickListener(new View.OnClickListener() {
            boolean mSelected;
            @Override
            public void onClick(View v) {
                if (!mSelected) {
                    mShuffleBtn.setSelected(true);
                    mSelected = true;
                }
                else {
                    mShuffleBtn.setSelected(false);
                    mSelected = false;
                }
            }
        });

        mRepeatBtn.setOnClickListener(new View.OnClickListener() {
            boolean mSelected;
            @Override
            public void onClick(View v) {
                if (!mSelected) {
                    mRepeatBtn.setSelected(true);
                    mSelected = true;
                }
                else {
                    mRepeatBtn.setSelected(false);
                    mSelected = false;
                }
            }
        });
    }

    private void onGetLyrics() {
        mLyricsManager.getLyrics(mSong, new Callback<LyricResult>() {
            @Override
            public void onSuccess(LyricResult data) {
                if (isAdded()) {
                    mLyricsBody.setText(data.getLyrics());
                    mLyricsSecondary.setText(data.getCopyright());
                    mLyricsContainer.animate().alpha(1f).start();
                }
            }

            @Override
            public void onFailure(Exception e) {
                if (isAdded()) {
                    Toast.makeText(getActivity(), "Lyrics could not be found", Toast.LENGTH_LONG).show();
                    mLyricsMenuItem.setEnabled(false);
                }
            }
        });
    }

    private void onHideLyrics() {
        mLyricsContainer.animate().alpha(0).start();
    }

    private void updateSongView() {
        mTitle.setText(mSong.getTitle());
        mAlbumArtist.setText(mSong.getArtist() + " - " + mSong.getAlbum());
        mDuration.setText(MusicUtil.formatTime(mSong.getDuration()));
        mLyricsContainer.setAlpha(0);
        mLyricsBody.setText(null);
        mLyricsSecondary.setText(null);
        if (mLyricsMenuItem != null) {
            mLyricsMenuItem.setChecked(false);
            mLyricsMenuItem.setEnabled(true);
            mLyricsMenuItem.setIcon(R.drawable.ic_av_subtitles);
        }
        updateCoverArt();
        updateSeekbar();
    }

    @Override
    public boolean showSecondaryFragment() {
        return false;
    }

    private void updateCoverArt() {
        mArtManager.setAlbumArt(mCovertArt, mSong, new Callback<Palette>() {
            @Override
            public void onSuccess(Palette palette) {
                onPaletteLoaded(palette);
            }

            @Override
            public void onFailure(Exception e) {
                onPaletteFailed();
            }
        });
    }

    private void onPaletteLoaded(Palette palette) {
        if (!isAdded()) {
            return;
        }
        final int defaultColor = getResources().getColor(R.color.primary_dark);

        int vibrantColor = palette.getVibrantColor(getResources().getColor(R.color.primary));
        int mutedColor = palette.getMutedColor(defaultColor);

        if (palette.getLightVibrantColor(palette.getVibrantColor(defaultColor)) == defaultColor) {
            vibrantColor = Color.parseColor("#555555");
            mutedColor = palette.getMutedColor(Color.parseColor("#d3d3d3"));
            ViewUtil.tintTextView(mTitle, Color.parseColor("#ffffff"));
            ViewUtil.tintSeekbar(mSeekBar, Color.parseColor("#555555"));
            ViewUtil.tintDecor(this, Color.parseColor("#555555"));
        }
        else {
            ViewUtil.tintTextView(mTitle, palette.getLightVibrantColor(palette.getVibrantColor(defaultColor)));
            ViewUtil.tintSeekbar(mSeekBar, palette.getVibrantColor(getResources().getColor(R.color.primary)));
            ViewUtil.tintDecor(this, palette.getVibrantColor(getResources().getColor(R.color.primary)));
        }
        mPlayerButtonsContainer.setBackgroundColor(vibrantColor);
        mSeekBar.getProgressDrawable().setColorFilter(mutedColor, PorterDuff.Mode.SRC_ATOP);
        //mSeekBar.getThumb().setColorFilter(lightVibrantColor, PorterDuff.Mode.SRC_ATOP);
        mSeekBar.setThumb(null);
    }

    private void onPaletteFailed() {
        final int defaultColor = getResources().getColor(R.color.primary_dark);
        final int defaultLightColor = getResources().getColor(R.color.primary);
        ViewUtil.tintTextView(mTitle, defaultColor);
        ViewUtil.tintSeekbar(mSeekBar, defaultColor);
        ViewUtil.tintDecor(this, getResources().getColor(R.color.primary));

        mPlayerButtonsContainer.setBackgroundColor(defaultColor);
        mSeekBar.getProgressDrawable().setColorFilter(defaultLightColor, PorterDuff.Mode.SRC_ATOP);
        //mSeekBar.getThumb().setColorFilter(defaultLightColor, PorterDuff.Mode.SRC_ATOP);
        mSeekBar.setThumb(null);
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
    public boolean onBackPress() {
        //ViewUtil.tintDecor(this, getResources().getColor(R.color.primary));
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
