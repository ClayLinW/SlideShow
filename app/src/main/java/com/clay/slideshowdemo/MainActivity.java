package com.clay.slideshowdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void commonSlideshow(View view) {
        startActivity(new Intent(this, CommonSlideshowActivity.class));
    }

    public void pictureSlideshow(View view) {
        startActivity(new Intent(this, PictureSlideshowActivity.class));
    }
}
