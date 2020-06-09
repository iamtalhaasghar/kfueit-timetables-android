package pk.edu.kfueit.timetables;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.LinearLayout;

public class SplashActivity extends Activity {


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splash);

        final int SPLASH_DISPLAY_LENGTH = 1000;
        LinearLayout splashLayout = findViewById(R.id.splash_layout);
        splashLayout.setAlpha(0f);
        splashLayout.animate().alpha(1).setDuration(SPLASH_DISPLAY_LENGTH);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

            Data appData = new Data(SplashActivity.this);
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            if (appData.isTimeTablePresent()) {
                intent = new Intent(SplashActivity.this, MainActivity.class);
            }
            startActivity(intent);
            finish();

            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}