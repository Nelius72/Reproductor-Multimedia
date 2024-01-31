package com.example.reproductormultimedia;

import androidx.appcompat.app.AppCompatActivity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;
import android.widget.MediaController;

public class RepVideo extends AppCompatActivity {

    VideoView video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rep_video);

        video= findViewById(R.id.videoView);
        String path="android.resource://"+ getPackageName() + "/" + R.raw.pokemon_opening;
        Uri uri= Uri.parse(path);
        video.setVideoURI(uri);


        MediaController controls= new MediaController(this);
        video.setMediaController(controls);
        controls.setAnchorView(video);
    }

}