package com.aranga.moodloop;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.aranga.moodloop.ui.MainActivity;

public class splash_screen extends Activity {

    ImageView moodloop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        moodloop = (ImageView) findViewById(R.id.moodloop_logo_holder);
        moodloop.setImageResource(R.drawable.mood_loop_other);
        final Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(splash_screen.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        };
        thread.start();

    }
}
