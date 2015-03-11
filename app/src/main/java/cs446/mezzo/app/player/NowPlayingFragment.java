package cs446.mezzo.app.player;

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
import cs446.mezzo.data.Callback;
import cs446.mezzo.events.EventBus;
import cs446.mezzo.events.control.PauseToggleEvent;
import cs446.mezzo.events.control.PlayNextEvent;
import cs446.mezzo.events.control.PlayPrevEvent;
import cs446.mezzo.events.control.RepeatToggleEvent;
import cs446.mezzo.events.control.SeekSetEvent;
import cs446.mezzo.events.control.ShuffleToggleEvent;
import cs446.mezzo.events.playback.RepeatEvent;
import cs446.mezzo.events.playback.SeekEvent;
import cs446.mezzo.events.playback.ShuffleEvent;
import cs446.mezzo.events.playback.SongPauseEvent;
import cs446.mezzo.events.playback.SongPlayEvent;
import cs446.mezzo.metadata.art.AlbumArtManager;
import cs446.mezzo.metadata.lyrics.LyricResult;
import cs446.mezzo.metadata.lyrics.LyricsManager;
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
        getMezzoActivity().hideSecondaryFragment();
        setHasOptionsMenu(true);
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.register(this); // Also gets a song from SongPlayEvent Producer
        mSeekBar.setOnSeekBarChangeListener(this);
        mSeekBar.setThumb(null);
        EventBus.setEventClick(mPauseBtn, new PauseToggleEvent());
        EventBus.setEventClick(mNextBtn, new PlayNextEvent());
        EventBus.setEventClick(mPrevBtn, new PlayPrevEvent());
        EventBus.setEventClick(mRepeatBtn, new RepeatToggleEvent());
        EventBus.setEventClick(mShuffleBtn, new ShuffleToggleEvent());
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
        updateCoverArt();
        mTitle.setText(mSong.getTitle());
        final String artist = mSong.getArtist() == null ? getString(R.string.default_artist) : mSong.getArtist();
        final String album = mSong.getAlbum() == null ? getString(R.string.default_album) : mSong.getAlbum();
        mAlbumArtist.setText(artist + " - " + album);
        mDuration.setText(MusicUtil.formatTime(mSong.getDuration()));
        mLyricsContainer.setAlpha(0);
        mLyricsBody.setText(null);
        mLyricsSecondary.setText(null);
        if (mLyricsMenuItem != null) {
            mLyricsMenuItem.setChecked(false);
            mLyricsMenuItem.setEnabled(true);
            mLyricsMenuItem.setIcon(R.drawable.ic_av_subtitles);
        }
        updateSeekbar();
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
        final int defaultVibrant = getResources().getColor(R.color.default_vibrant);
        final int defaultMuted = getResources().getColor(R.color.default_muted);
        final int defaultTextPrimary = getResources().getColor(R.color.default_textColor);
        final int defaultTextSecondary = getResources().getColor(R.color.secondary_text);

        final int vibrantColor = palette.getVibrantColor(defaultVibrant);
        final int mutedColor = palette.getMutedColor(defaultMuted);
        final int primaryTextColor = palette.getLightVibrantColor(palette.getVibrantColor(defaultTextPrimary));
        final int secondaryTextColor = palette.getDarkVibrantColor(defaultTextSecondary);

        ViewUtil.tintTextView(mTitle, primaryTextColor);
        ViewUtil.tintTextView(mAlbumArtist, secondaryTextColor);
        ViewUtil.tintDecor(this, vibrantColor);
        ViewUtil.tintSeekbar(mSeekBar, mutedColor);
        mPlayerButtonsContainer.setBackgroundColor(vibrantColor);
    }

    private void onPaletteFailed() {

        final int defaultColor = getResources().getColor(R.color.primary_dark);
        final int defaultVibrant = getResources().getColor(R.color.primary);
        final int defaultMuted = getResources().getColor(R.color.primary_light);
        final int defaultTextPrimary = getResources().getColor(R.color.default_textColor);

        ViewUtil.tintTextView(mTitle, defaultTextPrimary);
        ViewUtil.tintTextView(mAlbumArtist, defaultColor);
        ViewUtil.tintDecor(this, defaultVibrant);
        ViewUtil.tintSeekbar(mSeekBar, defaultMuted);
        mPlayerButtonsContainer.setBackgroundColor(defaultVibrant);
    }

    private void updateSeekbar() {
        mSeekBar.setProgress(0);
        mSeekBar.setMax((int) mSong.getDuration());
        mSeekPosition.setText(MusicUtil.formatTime(0, mSong.getDuration()));
    }

    @Override
    public String getTitle() {
        return "Now Playing";
    }

    @Override
    public void onDestroyView() {
        EventBus.unregister(this);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
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
        mPauseBtn.setImageResource(event.isPlaying() ?
                R.drawable.ic_av_pause :
                R.drawable.ic_av_play_arrow);
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

    @Subscribe
    public void onShuffle(ShuffleEvent event) {
        mShuffleBtn.setSelected(event.isOn());
    }

    @Subscribe
    public void onRepeatEvent(RepeatEvent event) {
        mRepeatBtn.setSelected(event.isOn());
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public boolean isTopLevel() {
        return true;
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
