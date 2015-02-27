package cs446.mezzo.app.miniplayer;

import android.graphics.drawable.Drawable;
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
import cs446.mezzo.events.EventBus;
import cs446.mezzo.events.control.PauseToggleEvent;
import cs446.mezzo.events.control.PlayNextEvent;
import cs446.mezzo.events.navigation.OpenAppEvent;
import cs446.mezzo.events.playback.SongPlayEvent;
import cs446.mezzo.music.AlbumArtManager;
import cs446.mezzo.music.Song;
import cs446.mezzo.overlay.Overlay;
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
        mControls.setVisibility(View.GONE);
        getOverlayManager().add(mDismissal);
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
                mControls.setVisibility(mExpanded ? View.VISIBLE : View.GONE);
            }
        });

        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlaying = !mPlaying;
                mPauseButton.setImageDrawable(mPlaying ? mPauseDrawable : mPlayDrawable);
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
        mCoverArt.setImageBitmap(mArtManager.getAlbumArt(mSong));
        mTextView.setText(mSong.getTitle());
    }

    @Subscribe
    public void onSongPlay(SongPlayEvent event) {
        mSong = event.getSong();
        if (isVisible()) {
            updateSongView();
        }
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
