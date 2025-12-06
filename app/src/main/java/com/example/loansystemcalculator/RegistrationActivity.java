package com.example.loansystemcalculator;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.loansystemcalculator.models.User;
import com.example.loansystemcalculator.ui.home.HomeFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class RegistrationActivity extends AppCompatActivity {

    private EditText editFirstName, editMiddleName, editLastName, editPassword, editConfirmPassword, editDateHired, editBasicSalary;
    private TextView txtEmployeeId;
    private Button btnRegister;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initialize views
        editFirstName = findViewById(R.id.edit_firstName);
        editMiddleName = findViewById(R.id.edit_middleName);
        editLastName = findViewById(R.id.edit_lastName);
        editPassword = findViewById(R.id.edit_pass);
        editConfirmPassword = findViewById(R.id.edit_confirm_pass);
        editDateHired = findViewById(R.id.edit_DateHired);
        editBasicSalary = findViewById(R.id.edit_BasicSalary);
        txtEmployeeId = findViewById(R.id.txt_empID);
        btnRegister = findViewById(R.id.btn_Register);

        // Initialize database helper
        db = new DatabaseHelper(this);

        // Add TextWatchers for real-time EmpID generation (including middle name)
        editFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                generateEmpID();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        editMiddleName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                generateEmpID();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        editLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                generateEmpID();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Date picker for date hired
        editDateHired.setOnClickListener(v -> showDatePicker());

        // Register button click
        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void generateEmpID() {
        String firstName = editFirstName.getText().toString().trim();
        String middleName = editMiddleName.getText().toString().trim();
        String lastName = editLastName.getText().toString().trim();

        if (!firstName.isEmpty() && !lastName.isEmpty()) {
            char firstLetter = Character.toUpperCase(firstName.charAt(0));
            char middleLetter = middleName.isEmpty() ? ' ' : Character.toUpperCase(middleName.charAt(0)); // Use space if no middle name
            char lastLetter = Character.toUpperCase(lastName.charAt(0));

            // Generate 5 random digits
            Random random = new Random();
            StringBuilder randomDigits = new StringBuilder();
            for (int i = 0; i < 5; i++) {
                randomDigits.append(random.nextInt(10));
            }

            String empIDString = "" + firstLetter + middleLetter + lastLetter + randomDigits.toString();
            txtEmployeeId.setText(empIDString);
        } else {
            txtEmployeeId.setText("");
        }
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
        String middleName = editMiddleName.getText().toString().trim();
        String lastName = editLastName.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String confirmPassword = editConfirmPassword.getText().toString().trim();
        String dateHired = editDateHired.getText().toString().trim();
        String basicSalaryStr = editBasicSalary.getText().toString().trim();
        String employeeId = txtEmployeeId.getText().toString().trim();

        // Validation
        if (firstName.isEmpty() || lastName.isEmpty() || password.isEmpty() || dateHired.isEmpty() || basicSalaryStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        double basicSalary;
        try {
            basicSalary = Double.parseDouble(basicSalaryStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid basic salary", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate email from name (firstname.lastname@abc.com)
        String email = generateEmail(firstName, lastName);

        // Check if email already exists
        if (db.checkEmailExists(email)) {
            Toast.makeText(this, "User with similar name already exists. Please use a different name.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Register user in database
        long result = db.registerUser(firstName, middleName, lastName, email, password, dateHired, employeeId, basicSalary);

        if (result != -1) {
            // Auto login after successful registration
            SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            // Store ALL user information
            editor.putBoolean("is_logged_in", true);
            editor.putString("user_type", "user");
            editor.putString("email", email);
            editor.putString("first_name", firstName);
            editor.putString("middle_name", middleName); // Added middle name storage
            editor.putString("last_name", lastName);
            editor.putString("employee_id", employeeId);
            editor.putString("date_hired", dateHired);
            editor.putFloat("basic_salary", (float) basicSalary);

            // Store full name for display (include middle name if present)
            String fullName = firstName + (middleName.isEmpty() ? "" : " " + middleName) + " " + lastName;
            editor.putString("full_name", fullName);
            editor.putString("display_name", firstName); // For "Hi, FirstName!"

            editor.apply();

            Toast.makeText(this, "Registration successful! Welcome, " + fullName, Toast.LENGTH_SHORT).show();

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