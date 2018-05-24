package com.mngl.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.mngl.R;
import com.mngl.utils.SharedPref;


/**
 * Created by bitware on 9/3/18.
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        init();
    }

    private void init() {
        SharedPref shared_pref = new SharedPref(SplashActivity.this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (SharedPref.getPreferences().getString(SharedPref.LOG_IN, "").equalsIgnoreCase("yes")) {
                    Intent i = new Intent(SplashActivity.this, DrawerActivity.class);
                    startActivity(i);
                }else {
                    Intent i = new Intent(SplashActivity.this,ActivityLogin.class);
                    startActivity(i);
                }
            }
        }, 1500);
    }
}
