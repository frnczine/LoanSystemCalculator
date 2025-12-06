package com.example.loansystemcalculator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AdminMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        // Set up logout button
        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> logout());

        // Display admin email
        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        String adminEmail = prefs.getString("email", "admin@abc.com");

        TextView tvAdminEmail = findViewById(R.id.tvAdminEmail);
        tvAdminEmail.setText("Logged in as: " + adminEmail);
    }

    private void logout() {
        // Clear session
        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        // Go to login
        Intent intent = new Intent(AdminMainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Prevent going back to login if logged out
        moveTaskToBack(true);
    }
}