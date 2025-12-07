package com.example.loansystemcalculator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText editEmail, editPassword;
    private Button btnLogin;
    private TextView txtRegister;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = findViewById(R.id.edit_email);
        editPassword = findViewById(R.id.edit_password);
        btnLogin = findViewById(R.id.btn_login);
        txtRegister = findViewById(R.id.txt_register);

        db = new DatabaseHelper(this);

        // Check if admin exists, if not create it
        db.createAdminIfNotExists();

        // Check if user is already logged in
        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        if (prefs.getBoolean("is_logged_in", false)) {
            String userType = prefs.getString("user_type", "");
            if (userType.equals("admin")) {
                startActivity(new Intent(LoginActivity.this, AdminMainActivity.class));
            } else {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
            finish();
        }

        btnLogin.setOnClickListener(v -> {
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if admin login
            if (email.equals("admin@abc.com") && password.equals("admin")) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("is_logged_in", true);
                editor.putString("user_type", "admin");
                editor.putString("email", email);
                editor.apply();

                startActivity(new Intent(LoginActivity.this, AdminMainActivity.class));
                finish();
            } else {
                // Check regular user login
                boolean isValid = db.checkUserLogin(email, password);
                if (isValid) {
                    // GET USER ID
                    int userId = db.getUserIdByEmail(email);

                    // GET FIRST & LAST NAME
                    String firstName = db.getUserFirstName(userId);

                    // You must create this method in DB (missing!)
                    String lastName = db.getUserLastName(userId);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("is_logged_in", true);
                    editor.putString("user_type", "user");
                    editor.putString("email", email);


                    // SAVE USER NAME
                    editor.putString("first_name", firstName);
                    editor.putString("last_name", lastName);
                    editor.putFloat("basic_salary", (float) db.getUserBasicSalary(userId));
                    editor.apply();

                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        txtRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
        });
    }
}