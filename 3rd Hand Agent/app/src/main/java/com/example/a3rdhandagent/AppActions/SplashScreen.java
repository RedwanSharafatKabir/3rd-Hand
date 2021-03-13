package com.example.a3rdhandagent.AppActions;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.a3rdhandagent.R;

public class SplashScreen extends AppCompatActivity {

    int SPLASH_TIME_OUT = 2000;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        imageView = findViewById(R.id.splashImageID);
        imageView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in_fade_in));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent it = new Intent(SplashScreen.this, StartScreen.class);
                startActivity(it);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
