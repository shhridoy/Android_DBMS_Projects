package com.shhridoy.moviereview;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.widget.RatingBar;

public class SplashScreeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_scree);

        RatingBar ratingBar = findViewById(R.id.RBSplash);
        ratingBar.setRating(5.0f);
        ratingBar.setEnabled(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashScreeActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, 2500);

    }
}
