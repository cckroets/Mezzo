package cs446.mezzo.app.player.mini;

import android.animation.LayoutTransition;
import android.graphics.drawable.Drawable;
<<<<<<< Updated upstream
import android.support.v7.graphics.Palette;
=======
>>>>>>> Stashed changes
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.inject.Inject;
import com.squareup.otto.Subscribe;

import cs446.mezzo.R;
<<<<<<< Updated upstream
import cs446.mezzo.art.AlbumArtManager;
import cs446.mezzo.data.Callback;
=======
>>>>>>> Stashed changes
import cs446.mezzo.events.EventBus;
import cs446.mezzo.events.control.PauseToggleEvent;
import cs446.mezzo.events.control.PlayNextEvent;
import cs446.mezzo.events.navigation.OpenAppEvent;
import cs446.mezzo.events.playback.SongPauseEvent;
import cs446.mezzo.events.playback.SongPlayEvent;
<<<<<<< Updated upstream
import cs446.mezzo.music.Song;
import cs446.mezzo.overlay.Overlay;
import cs446.mezzo.view.ViewUtil;
=======
import cs446.mezzo.art.AlbumArtManager;
import cs446.mezzo.music.Song;
import cs446.mezzo.overlay.Overlay;
>>>>>>> Stashed changes
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

/**
 * @author curtiskroetsch
 */
public class MiniPlayer extends Overlay {

    @InjectView(R.id.player_album_art)
    ImageView mCoverArt;

    @InjectView(R.id.player_title)
    TextView mTextView;

    @InjectView(R.id.player_to_app)
    ImageButton mHomeButton;

    @InjectView(R.id.player_pause)
    ImageButton mPauseButton;

    @InjectView(R.id.player_next)
    ImageButton mNextButton;

    @InjectView(R.id.player_control_container)
    LinearLayout mControls;

    @Inject
    AlbumArtManager mArtManager;

    @InjectResource(R.drawable.ic_av_pause_circle_fill)
    Drawable mPauseDrawable;

    @InjectResource(R.drawable.ic_av_play_circle_fill)
    Drawable mPlayDrawable;

    private Song mSong;
    private Overlay mDismissal;
    private boolean mPlaying;
    private boolean mExpanded;

    public MiniPlayer() {

    }

    @Override
    protected View onCreateView(LayoutInflater inflater) {
        EventBus.register(this);
        setVisible(true);
        return inflater.inflate(R.layout.overlay_mini_player, null);
    }

    @Override
    protected void onViewCreated(View view) {
        super.onViewCreated(view);
        mDismissal = new Dismissal();
        mExpanded = false;
        mPlaying = true;
        mPauseButton.setVisibility(View.GONE);
        mHomeButton.setVisibility(View.GONE);
        mNextButton.setVisibility(View.GONE);
        getOverlayManager().add(mDismissal);

        final LayoutTransition transition = new LayoutTransition();
        transition.setStagger(LayoutTransition.APPEARING, 1000);
        mControls.setLayoutTransition(transition);

        mTextView.setOnTouchListener(new DragClickListener(this, view) {
            @Override
            public void onStartDrag(View view, MotionEvent event) {
                getOverlayManager().show(mDismissal);
            }

            @Override
            public void onStopDrag(View view, MotionEvent event) {
                getOverlayManager().hide(mDismissal);
                if (dismissHitTest(mDismissal.getView(), event)) {
                    getOverlayManager().hide(MiniPlayer.this);
                }
            }

            @Override
            public void onClick(View view, MotionEvent event) {
                mExpanded = !mExpanded;
                mPauseButton.setVisibility(mExpanded ? View.VISIBLE : View.GONE);
                mHomeButton.setVisibility(mExpanded ? View.VISIBLE : View.GONE);
                mNextButton.setVisibility(mExpanded ? View.VISIBLE : View.GONE);
            }
        });

        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlaying = !mPlaying;
                EventBus.post(new PauseToggleEvent());
            }
        });
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.post(new PlayNextEvent());
            }
        });
        mHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.post(new OpenAppEvent());
            }
        });

        updateSongView();
    }

    public void updateSongView() {
<<<<<<< Updated upstream
        mTextView.setText(mSong.getTitle());
        mArtManager.setAlbumArt(mCoverArt, mSong, new Callback<Palette>() {
            @Override
            public void onSuccess(Palette data) {
                onPaletteLoaded(data);
            }

            @Override
            public void onFailure(Exception e) {
                onPaletteFailed();
            }
        });
    }

    private void onPaletteLoaded(Palette palette) {
        final int defaultColor = getContext().getResources().getColor(R.color.primary_dark);
        final int tintColor =
                palette.getDarkVibrantColor(
                palette.getVibrantColor(
                palette.getDarkMutedColor(defaultColor)));
        ViewUtil.tintViews(tintColor, mHomeButton, mPauseButton, mNextButton);
    }

    private void onPaletteFailed() {
        final int defaultColor = getContext().getResources().getColor(R.color.primary_dark);
        ViewUtil.tintViews(defaultColor, mHomeButton, mPauseButton, mNextButton);
=======
        mCoverArt.setImageBitmap(mArtManager.getAlbumArt(mSong));
        mTextView.setText(mSong.getTitle());
>>>>>>> Stashed changes
    }

    @Subscribe
    public void onSongPlay(SongPlayEvent event) {
        mSong = event.getSong();
        if (isVisible()) {
            mPauseButton.setImageDrawable(mPauseDrawable);
            updateSongView();
        }
    }

    @Subscribe
    public void onSongPaused(SongPauseEvent event) {
        mPauseButton.setImageDrawable(event.isPaused() ? mPlayDrawable : mPauseDrawable);
    }

    @Override
    protected void onShow(View view) {
        view.animate().alpha(1f).start();
    }

    @Override
    protected void onHide(View view) {
        view.animate().alpha(0f).start();
    }

    @Override
    public WindowManager.LayoutParams getLayoutParams() {
        final WindowManager.LayoutParams params = super.getLayoutParams();
        final int noTouch = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        params.flags = isVisible() ? params.flags & ~noTouch : params.flags | noTouch;
        return params;
    }

    boolean dismissHitTest(View view, MotionEvent event) {
        int[] l = new int[2];
        view.getLocationOnScreen(l);
        return event.getRawY() > l[1];
    }

    @Override
    protected void onDestroy() {
        EventBus.unregister(this);
        super.onDestroy();
    }

}