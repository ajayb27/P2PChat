package com.elan.p2pchat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.elan.p2pchat.R;

public class SplashActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView textView;
    private Animation animation1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        imageView=(ImageView)findViewById(R.id.imageView);
        textView=(TextView)findViewById(R.id.title_app);

        animation1= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.splash);

        imageView.setAnimation(animation1);
        textView.setAnimation(animation1);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i=new Intent(SplashActivity.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        },5000);
    }
}
