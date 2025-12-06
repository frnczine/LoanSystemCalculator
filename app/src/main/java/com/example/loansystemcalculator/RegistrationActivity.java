package com.example.loansystemcalculator;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RegistrationActivity extends AppCompatActivity {

    private EditText editFirstName, editLastName, editPassword, editDateHired;
    private TextView txtEmployeeId;
    private Button btnRegister;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initialize views
        editFirstName = findViewById(R.id.edit_firstName);
        editLastName = findViewById(R.id.edit_lastName);
        editPassword = findViewById(R.id.edit_pass);
        editDateHired = findViewById(R.id.edit_DateHired);
        txtEmployeeId = findViewById(R.id.txt_empID);
        btnRegister = findViewById(R.id.btn_Register);

        // Initialize database helper
        db = new DatabaseHelper(this);

        // Generate and display employee ID
        generateEmployeeId();

        // Date picker for date hired
        editDateHired.setOnClickListener(v -> showDatePicker());

        // Register button click
        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void generateEmployeeId() {
        // Generate a unique employee ID with format: ABC + timestamp
        String timestamp = String.valueOf(System.currentTimeMillis());
        String employeeId = "ABC" + timestamp.substring(timestamp.length() - 6);
        txtEmployeeId.setText(employeeId);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(
                RegistrationActivity.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Format date as YYYY-MM-DD
                    String formattedMonth = (selectedMonth + 1) < 10 ?
                            "0" + (selectedMonth + 1) : String.valueOf(selectedMonth + 1);
                    String formattedDay = selectedDay < 10 ?
                            "0" + selectedDay : String.valueOf(selectedDay);
                    String date = selectedYear + "-" + formattedMonth + "-" + formattedDay;
                    editDateHired.setText(date);
                },
                year, month, day
        );

        // Set max date to today (can't be hired in the future)
        datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePicker.show();
    }

    private void registerUser() {
        String firstName = editFirstName.getText().toString().trim();
        String lastName = editLastName.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String dateHired = editDateHired.getText().toString().trim();
        String employeeId = txtEmployeeId.getText().toString().trim();

        // Validation
        if (firstName.isEmpty() || lastName.isEmpty() || password.isEmpty() || dateHired.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // Calculate basic salary (default to 15000, can be updated later)
        double basicSalary = 15000.00;

        // Generate email from name (firstname.lastname@abc.com)
        String email = generateEmail(firstName, lastName);

        // Check if email already exists
        if (db.checkEmailExists(email)) {
            Toast.makeText(this, "User with similar name already exists. Please use a different name.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Register user in database
        long result = db.registerUser(firstName, lastName, email, password, dateHired, employeeId, basicSalary);

        if (result != -1) {
            // Auto login after successful registration
            SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            // Store ALL user information
            editor.putBoolean("is_logged_in", true);
            editor.putString("user_type", "user");
            editor.putString("email", email);
            editor.putString("first_name", firstName);
            editor.putString("last_name", lastName);
            editor.putString("employee_id", employeeId);
            editor.putString("date_hired", dateHired);
            editor.putFloat("basic_salary", (float) basicSalary);

            // Store full name for display
            editor.putString("full_name", firstName + " " + lastName);
            editor.putString("display_name", firstName); // For "Hi, FirstName!"

            editor.apply();

            Toast.makeText(this, "Registration successful! Welcome, " + firstName + " " + lastName, Toast.LENGTH_SHORT).show();

            // Navigate to MainActivity (user home)
            Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private String generateEmail(String firstName, String lastName) {

        String cleanFirstName = firstName.toLowerCase().replaceAll("[^a-z]", "");
        String cleanLastName = lastName.toLowerCase().replaceAll("[^a-z]", "");

        //default
        if (cleanFirstName.isEmpty()) cleanFirstName = "user";
        if (cleanLastName.isEmpty()) cleanLastName = "new";

        String baseEmail = cleanFirstName + "." + cleanLastName + "@abc.com";

        int counter = 1;
        String email = baseEmail;
        while (db.checkEmailExists(email)) {
            email = cleanFirstName + "." + cleanLastName + counter + "@abc.com";
            counter++;
        }

        return email;
    }
}