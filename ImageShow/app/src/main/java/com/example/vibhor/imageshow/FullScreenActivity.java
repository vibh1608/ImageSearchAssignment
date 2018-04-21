package com.example.vibhor.imageshow;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

public class FullScreenActivity extends AppCompatActivity
{

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);

        imageView = findViewById(R.id.fullImage);
        String s = getIntent().getStringExtra("fullimagepath");

        Glide.with(FullScreenActivity.this)
                .load(s)
                .centerCrop()
                .fitCenter()
                .error(R.drawable.default_pic)
                .into(imageView);
    }
}
