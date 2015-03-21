package cs446.mezzo.app.player.mini;

import android.animation.ValueAnimator;
import android.graphics.Point;
import android.os.Handler;
import android.view.Display;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;

import cs446.mezzo.overlay.Overlay;

/**
 * @author curtiskroetsch
 */
public class DragClickListener implements View.OnTouchListener {

    private static final int BUTTONS_WIDTH = 220;
    private static final double CLICK_THRESHOLD = 200; //ms

    private Overlay mOverlay;
    private Display mDisplay;
    private View mTapTarget;

    private int mDeltaX;
    private int mDeltaY;
    private int mMaxY;
    private int mDownX;
    private int mDownY;

    private boolean mIsDrag;
    private boolean mIsClick;
    private boolean mIsLgOpen;
    private boolean mIsSmOpen;
    private boolean mScreenPressed;

    private ValueAnimator.AnimatorUpdateListener mMagnetUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            mOverlay.getLayoutParams().x = (Integer) animation.getAnimatedValue();
            mOverlay.updateViewLayout();
        }
    };

    public DragClickListener(Overlay overlay, View target) {
        mTapTarget = target;
        mOverlay = overlay;
        mDisplay = mOverlay.getOverlayManager().getWindowManager().getDefaultDisplay();
        setConstraints();
    }

    private void setConstraints() {
        if (mMaxY != 0) {
            return;
        }
        final Point point = new Point();
        mDisplay.getSize(point);
        mMaxY = point.y - mTapTarget.getHeight();
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mScreenPressed = true;
                mDeltaX = X - mOverlay.getLayoutParams().x;
                mDeltaY = Y - mOverlay.getLayoutParams().y;
                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                mDownX = X;
                mDownY = Y;

                // delay
                final long threshold = (long) (CLICK_THRESHOLD * .9);
                if (!mIsLgOpen && !mIsSmOpen) {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!mIsLgOpen && !mIsSmOpen && mScreenPressed) {
                                onOpenSm(v, event);
                                mIsSmOpen = true;
                            }
                        }
                    }, threshold);
                }
                break;
            case MotionEvent.ACTION_UP:
                mScreenPressed = false;
                final long mTimeUp = event.getEventTime();
                final long mTimeDown = event.getDownTime();
                mIsClick = (Math.abs(mTimeUp - mTimeDown) < CLICK_THRESHOLD);

                moveToSide(v, event);
                onStopDrag(v, event);

                if (mIsLgOpen) {
                    onClose(v, event);
                    mIsLgOpen = false;
                } else if (mIsSmOpen) {
                    onButtonsClick(v, event);
                    onClose(v, event);
                    mIsSmOpen = false;
                } else if (mIsClick && !mIsLgOpen) {
                    onOpenLg(v, event);
                    mIsLgOpen = true;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                mIsDrag = Math.abs(mDownX - X) > v.getWidth() + BUTTONS_WIDTH || Math.abs(mDownY - Y) > v.getHeight();
                if (mIsDrag) {
                    onClose(v, event);
                    onStartDrag(v, event);
                    onDrag(v, event);
                    moveOverlay(X - mDeltaX, Math.min(Y - mDeltaY, mMaxY));
                }
                break;
            default:
                break;
        }
        return true;
    }

    private void moveOverlay(int deltaX, int deltaY) {
        mOverlay.getLayoutParams().x = deltaX;
        mOverlay.getLayoutParams().y = deltaY;
        mOverlay.updateViewLayout();
    }

    private void moveToSide(View view, MotionEvent event) {
        final int xPos = mOverlay.getLayoutParams().x;
        final int rawX = (int) event.getRawX();
        final int width = mDisplay.getWidth();
        final int overlayWidth = view.getWidth();

        final int finalPos = 0; //(rawX < width / 2) ? 0 : width - overlayWidth;
        final ValueAnimator animator = ValueAnimator.ofInt(xPos, finalPos);
        animator.addUpdateListener(mMagnetUpdateListener);
        animator.start();
    }

    public void onOpenSm(View view, MotionEvent event) {

    }

    public void onOpenLg(View view, MotionEvent event) {

    }

    public void onClose(View view, MotionEvent event) {

    }

    public void onButtonsClick(View view, MotionEvent event) {

    }

    public void onStartDrag(View view, MotionEvent event) {

    }

    public void onClick(View view, MotionEvent event) {

    }

    public void onDrag(View view, MotionEvent event) {

    }

    public void onStopDrag(View view, MotionEvent event) {

    }
}
