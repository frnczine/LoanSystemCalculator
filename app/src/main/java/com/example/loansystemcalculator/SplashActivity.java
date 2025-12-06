package com.example.loansystemcalculator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DELAY = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        new Handler().postDelayed(() -> {
            // Check if user is already logged in
            SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
            if (prefs.getBoolean("is_logged_in", false)) {
                String userType = prefs.getString("user_type", "");
                if (userType.equals("admin")) {
                    startActivity(new Intent(SplashActivity.this, AdminMainActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }
            } else {
                // Not logged in, go to login
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
            finish();
        }, SPLASH_DELAY);
    }
}