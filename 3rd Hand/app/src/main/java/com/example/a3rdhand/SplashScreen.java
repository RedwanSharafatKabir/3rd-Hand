package com.example.a3rdhand;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.agrawalsuneet.dotsloader.loaders.AllianceLoader;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class SplashScreen extends AppCompatActivity {

    int SPLASH_TIME_OUT = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

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
