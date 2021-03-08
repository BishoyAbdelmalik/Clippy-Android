package apps.vip.clippy;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.Button;
import android.widget.ImageView;


public class screenshot_page extends AppCompatActivity {
    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = 1.0f;
    private  ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screenshot_page);
        img=findViewById(R.id.screenshot);

        ForegroundService.getScreenshot(img);
        Button refresh=findViewById(R.id.refresh_screenshot);
        refresh.setOnClickListener(v -> ForegroundService.getScreenshot(img));

        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

    }
    // this redirects all touch events in the activity to the gesture detector
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mScaleGestureDetector.onTouchEvent(event);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        // when a scale gesture is detected, use it to resize the image
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector){
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            img.setScaleX(mScaleFactor);
            img.setScaleY(mScaleFactor);
            return true;
        }

    }

}