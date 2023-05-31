package com.gifari.tugasakhir;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends AppCompatActivity {
    private int waktu_loading=3000;
    MediaPlayer audio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        audio = MediaPlayer.create(this, R.raw.soundd);
        audio.setVolume(2,2);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent home=new Intent(Splash.this, HalamanUtama.class);
                startActivity(home);
                finish();
            }
        }, waktu_loading);
        audio.start();
    }
}