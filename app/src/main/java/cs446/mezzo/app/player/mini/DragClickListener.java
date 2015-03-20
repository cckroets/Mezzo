package cs446.mezzo.app.player.mini;

import android.animation.ValueAnimator;
import android.graphics.Point;
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

    private Overlay mOverlay;
    private Display mDisplay;
    private View mTapTarget;

    private int mDeltaX;
    private int mDeltaY;
    private int mMaxY;
    private int mState;
    private int mDownX;
    private int mDownY;

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
    public boolean onTouch(View v, MotionEvent event) {
        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mDeltaX = X - mOverlay.getLayoutParams().x;
                mDeltaY = Y - mOverlay.getLayoutParams().y;
                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                onStartDrag(v, event);
                mDownX = X;
                mDownY = Y;
                if (mState == 0) {
                    mState = 1;
                    onOpenSm(v, event);
                }
                break;
            case MotionEvent.ACTION_UP:
                moveToSide(v, event);
                onStopDrag(v, event);
                if (mState == 1) {
                    if (Math.abs(mDownX - X) <= v.getWidth() - 30) {
                        mState = 2;
                        onOpenLg(v, event);
                    } else {
                        onButtonsClick(v, event);
                        mState = 0;
                        onClose(v, event);
                    }
                } else if (mState == 2) {
                    mState = 0;
                    onClose(v, event);
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(mDownX - X) > v.getWidth() + BUTTONS_WIDTH || Math.abs(mDownY - Y) > v.getHeight()) {
                    moveOverlay(X - mDeltaX, Math.min(Y - mDeltaY, mMaxY));
                    onDrag(v, event);
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
