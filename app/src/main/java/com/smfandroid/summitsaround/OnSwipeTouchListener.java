package com.smfandroid.summitsaround;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * Created by clementdaviller on 12/04/15.
 */
public class OnSwipeTouchListener implements View.OnTouchListener {

    private View v;

    public void setV(View v) {
        this.v = v;
    }

    @SuppressWarnings("deprecation")
    private final GestureDetector gestureDetector = new GestureDetector(new GestureListener());

    public boolean onTouch(final View v, final MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            onTouch(e);
            return true;
        }


        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                    }
                } else {
                    // onTouch(e);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }
    public void onTouch(MotionEvent e) {
    }

    public void onSwipeRight() {
       Toast.makeText(v.getContext(), "swiped right ", Toast.LENGTH_LONG).show();
    }

    public void onSwipeLeft() {
        //Toast T = Toast.makeText(this, "swiped left", Toast.LENGTH_LONG);
        //T.show();
    }

    public void onSwipeTop() {
       // Toast.makeText(this, "swiped top", Toast.LENGTH_LONG).show();
    }

    public void onSwipeBottom() {
        //Toast.makeText(this, "swiped bottom", Toast.LENGTH_LONG).show();
    }
}
