package cs446.mezzo.app;

/**
 * Created by ulkarakhundzada on 2015-03-03.
 */

import android.content.Intent;
import android.os.Bundle;

import cs446.mezzo.R;
import roboguice.activity.RoboSplashActivity;

public class SplashScreenActivity extends RoboSplashActivity {

    // Splash screen timer
    private static final int SPLASH_TIME_OUT = 800;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        minDisplayMs = SPLASH_TIME_OUT;
    }

    @Override
    protected void startNextActivity() {
        Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
        startActivity(i);
    }
}
