package cs446.mezzo.app.miniplayer;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.inject.Inject;

import cs446.mezzo.R;
import cs446.mezzo.music.AlbumArtManager;
import cs446.mezzo.music.Song;
import cs446.mezzo.overlay.Overlay;
import roboguice.inject.InjectView;

/**
 * @author curtiskroetsch
 */
public class MiniPlayer extends Overlay {

    @InjectView(R.id.player_album_art)
    ImageView mCoverArt;

    @InjectView(R.id.player_title)
    TextView mTextView;

    @Inject
    AlbumArtManager mArtManager;

    Song mSong;

    Overlay mDismissal;

    public MiniPlayer(Song song) {
        mSong = song;
    }

    @Override
    protected View onCreateView(LayoutInflater inflater) {
        setVisible(true);
        return inflater.inflate(R.layout.overlay_mini_player, null);
    }

    @Override
    protected void onViewCreated(View view) {
        super.onViewCreated(view);
        mDismissal = new Dismissal();
        getOverlayManager().add(mDismissal);
        view.setOnTouchListener(new DragClickListener(this) {
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
        });
        mCoverArt.setImageBitmap(mArtManager.getAlbumArt(mSong));
        mTextView.setText(mSong.getTitle());
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

}
