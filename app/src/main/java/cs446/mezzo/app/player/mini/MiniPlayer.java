package cs446.mezzo.app.player.mini;

import android.animation.LayoutTransition;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.graphics.Palette;
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
import cs446.mezzo.events.navigation.GoHomeEvent;
import cs446.mezzo.metadata.art.AlbumArtManager;
import cs446.mezzo.data.Callback;
import cs446.mezzo.events.EventBus;
import cs446.mezzo.events.control.PauseToggleEvent;
import cs446.mezzo.events.control.PlayNextEvent;
import cs446.mezzo.events.playback.SongPauseEvent;
import cs446.mezzo.events.playback.SongPlayEvent;
import cs446.mezzo.music.Song;
import cs446.mezzo.overlay.Overlay;
import cs446.mezzo.view.AutoResizeTextView;
import cs446.mezzo.view.MezzoImageView;
import cs446.mezzo.view.ViewUtil;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

/**
 * @author curtiskroetsch
 */
public class MiniPlayer extends Overlay {

    @InjectView(R.id.player_album_art)
    MezzoImageView mCoverArt;

    @InjectView(R.id.ph_logo)
    ImageView mLogoView;

    @InjectView(R.id.miniplayer_artwork_border)
    ImageView mMiniPlayerBorder;

    @InjectView(R.id.miniplayer_artwork_border2)
    ImageView mMiniPlayerBorder2;

    @InjectView(R.id.miniplayer_container)
    LinearLayout mMiniPlayerCont;

    @InjectView(R.id.player_title)
    TextView mTextView;

    @InjectView(R.id.ph_text)
    AutoResizeTextView mTextView2;

    @InjectView(R.id.player_to_app)
    ImageButton mHomeButton;

    @InjectView(R.id.player_pause)
    ImageButton mPauseButton;

    @InjectView(R.id.player_next)
    ImageButton mNextButton;

    @Inject
    AlbumArtManager mArtManager;

    @InjectResource(R.drawable.ic_miniplayer_pause)
    Drawable mPauseDrawable;

    @InjectResource(R.drawable.ic_miniplayer_play_arrow)
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
        mMiniPlayerBorder.setVisibility(View.GONE);
        mMiniPlayerBorder2.setVisibility(View.GONE);
        getOverlayManager().add(mDismissal);

        final LayoutTransition transition = new LayoutTransition();
        transition.setStagger(LayoutTransition.APPEARING, 0);
        transition.setStartDelay(LayoutTransition.APPEARING, 3000);
        mMiniPlayerCont.setLayoutTransition(transition);

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
            public void onOpenSm(View view, MotionEvent event) {
                mExpanded = true;
                mPauseButton.setVisibility(View.VISIBLE);
                mHomeButton.setVisibility(View.GONE);
                mNextButton.setVisibility(View.VISIBLE);
                mMiniPlayerBorder2.setVisibility(View.VISIBLE);
                mMiniPlayerBorder.setVisibility(View.GONE);
            }

            @Override
            public void onOpenLg(View view, MotionEvent event) {
                mExpanded = true;
                mPauseButton.setVisibility(View.VISIBLE);
                mHomeButton.setVisibility(View.VISIBLE);
                mNextButton.setVisibility(View.VISIBLE);
                mMiniPlayerBorder.setVisibility(View.VISIBLE);
                mMiniPlayerBorder2.setVisibility(View.GONE);
            }

            @Override
            public void onClose(View view, MotionEvent event) {
                mExpanded = false;
                mPauseButton.setVisibility(View.GONE);
                mHomeButton.setVisibility(View.GONE);
                mNextButton.setVisibility(View.GONE);
                mMiniPlayerBorder.setVisibility(View.GONE);
                mMiniPlayerBorder2.setVisibility(View.GONE);
            }

            @Override
            public void onButtonsClick(View view, MotionEvent event) {
                final int X = (int) event.getRawX();
                if ((X <= 325) && (X > 175)) {
                    mPlaying = !mPlaying;
                    EventBus.post(new PauseToggleEvent());
                } else if ((X > 325) && (X <= 475)) {
                    EventBus.post(new PlayNextEvent());
                }
            }

            @Override
            public void onClick(View view, MotionEvent event) {
                mExpanded = !mExpanded;
                mPauseButton.setVisibility(mExpanded ? View.VISIBLE : View.GONE);
                mHomeButton.setVisibility(mExpanded ? View.VISIBLE : View.GONE);
                mNextButton.setVisibility(mExpanded ? View.VISIBLE : View.GONE);
                mMiniPlayerBorder.setVisibility(mExpanded ? View.VISIBLE : View.GONE);
                mMiniPlayerBorder2.setVisibility(mExpanded ? View.VISIBLE : View.GONE);
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
                EventBus.post(new GoHomeEvent());
            }
        });

        updateSongView();
    }

    public void updateSongView() {
        mTextView.setText(mSong.getTitle());
        mCoverArt.bindWithSong(mArtManager, mSong, new Callback<Palette>() {
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
        mLogoView.setVisibility(View.INVISIBLE);

        final int defaultColor = getContext().getResources().getColor(R.color.default_muted);
        final int vibrantColor = palette.getVibrantColor(defaultColor);
        ViewUtil.tintViews(vibrantColor, mHomeButton, mPauseButton, mNextButton);
    }

    private void onPaletteFailed() {
        final ColorDrawable cd = (ColorDrawable) mTextView2.getBackground();
        final int backgroundColor = cd.getColor();
        ViewUtil.tintViews(backgroundColor, mHomeButton, mPauseButton, mNextButton);
        mTextView2.setTextColor(Color.TRANSPARENT);
        mLogoView.getBackground().setColorFilter(backgroundColor, PorterDuff.Mode.OVERLAY);
        mLogoView.setVisibility(View.VISIBLE);
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
