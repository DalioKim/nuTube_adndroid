package com.example.youtube;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import java.io.IOException;
import android.util.Log;

public class TestMpActivity extends AppCompatActivity {

    private final String streamUrl = "http://15.164.98.15/videos/Mandeville.mp3";

    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_mp);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });



        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(this, Uri.parse(streamUrl));

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override

                public void onPrepared(MediaPlayer mp) {
                    Log.i("test","미디어프리페어");

                    mediaPlayer.start();

                }

            });
        Log.i("test","미디어셋");


        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override

            public void onPrepared(MediaPlayer mp) {
                Log.i("test","미디어프리페어");

                mediaPlayer.start();

            }

        });


    }

}
