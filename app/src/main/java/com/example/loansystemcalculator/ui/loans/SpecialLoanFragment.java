package com.example.loansystemcalculator.ui.loans;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.loansystemcalculator.DatabaseHelper;
import com.example.loansystemcalculator.LoanCalculator;
import com.example.loansystemcalculator.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SpecialLoanFragment extends Fragment {

    private EditText editLoanAmount, editMonths;
    private TextView tvServiceCharge, tvInterest, tvMonthlyPayment, tvTakeHomeAmount, tvEligibility;
    private Button btnCalculate, btnApply;
    private DatabaseHelper db;
    private LoanCalculator loanCalculator;
    private String userEmail;
    private int userId;
    private String dateHired;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_special_loan, container, false);

        initializeViews(view);
        initializeDatabase();
        loadUserInfo();
        checkEligibility();
        setupClickListeners();

        return view;
    }

    private void initializeViews(View view) {
        editLoanAmount = view.findViewById(R.id.edit_loanAmount);
        editMonths = view.findViewById(R.id.edit_loanMonth);
        tvServiceCharge = view.findViewById(R.id.tvServiceCharge);
        tvInterest = view.findViewById(R.id.tvInterest);
        tvMonthlyPayment = view.findViewById(R.id.tvMonthlyPayment);
        tvTakeHomeAmount = view.findViewById(R.id.tvTakeHomeAmount);
        tvEligibility = view.findViewById(R.id.tvEligibility);
        btnCalculate = view.findViewById(R.id.btn_Calculate);
        btnApply = view.findViewById(R.id.btn_Apply);

        loanCalculator = new LoanCalculator();

        // Setup back button
        ImageView btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> goBack());
        }
    }

    private void initializeDatabase() {
        db = new DatabaseHelper(getContext());
    }

    private void loadUserInfo() {
        SharedPreferences prefs = getActivity().getSharedPreferences("user_session", 0);
        userEmail = prefs.getString("email", "");
        userId = db.getUserIdByEmail(userEmail);
        dateHired = prefs.getString("date_hired", "");
    }

    private void checkEligibility() {
        if (dateHired == null || dateHired.isEmpty()) {
            tvEligibility.setText("Eligibility: Not eligible - Date hired not found");
            tvEligibility.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            disableForm();
            return;
        }

        try {
            int yearsOfService = calculateYearsOfService(dateHired);
            if (yearsOfService >= 5) {
                tvEligibility.setText("Eligibility: Eligible (" + yearsOfService + " years of service)");
                tvEligibility.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                enableForm();
            } else {
                tvEligibility.setText("Eligibility: Not eligible - Need 5+ years of service (You have " + yearsOfService + " years)");
                tvEligibility.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                disableForm();
            }
        } catch (Exception e) {
            tvEligibility.setText("Eligibility: Error checking eligibility");
            tvEligibility.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            disableForm();
        }
    }

    private int calculateYearsOfService(String dateHired) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date hireDate = sdf.parse(dateHired);
            Date currentDate = new Date();

            Calendar hireCal = Calendar.getInstance();
            hireCal.setTime(hireDate);
            Calendar currentCal = Calendar.getInstance();
            currentCal.setTime(currentDate);

            int years = currentCal.get(Calendar.YEAR) - hireCal.get(Calendar.YEAR);

            // Adjust if hire date hasn't occurred yet this year
            if (currentCal.get(Calendar.MONTH) < hireCal.get(Calendar.MONTH) ||
                    (currentCal.get(Calendar.MONTH) == hireCal.get(Calendar.MONTH) &&
                            currentCal.get(Calendar.DAY_OF_MONTH) < hireCal.get(Calendar.DAY_OF_MONTH))) {
                years--;
            }

            return years;
        } catch (Exception e) {
            return 0;
        }
    }

    private void disableForm() {
        editLoanAmount.setEnabled(false);
        editMonths.setEnabled(false);
        btnCalculate.setEnabled(false);
        btnApply.setEnabled(false);
    }

    private void enableForm() {
        editLoanAmount.setEnabled(true);
        editMonths.setEnabled(true);
        btnCalculate.setEnabled(true);
    }

    private void setupClickListeners() {
        btnCalculate.setOnClickListener(v -> calculateLoan());
        btnApply.setOnClickListener(v -> applyForLoan());
    }

    private void calculateLoan() {
        try {
            double loanAmount = Double.parseDouble(editLoanAmount.getText().toString().trim());
            int months = Integer.parseInt(editMonths.getText().toString().trim());

            // Validate special loan limits
            if (loanAmount < 50000 || loanAmount > 100000) {
                Toast.makeText(getContext(), "Special loan amount must be between ₱50,000 and ₱100,000", Toast.LENGTH_SHORT).show();
                return;
            }

            if (months < 1 || months > 18) {
                Toast.makeText(getContext(), "Special loan term must be 1-18 months", Toast.LENGTH_SHORT).show();
                return;
            }

            // Calculate loan details (special loan rates)
            double interestRate = loanCalculator.getSpecialLoanInterestRate(months);
            double serviceCharge = loanCalculator.calculateServiceCharge(loanAmount, "special");
            double interest = loanCalculator.calculateInterest(loanAmount, interestRate, months);
            double totalAmount = loanAmount + serviceCharge + interest;
            double monthlyPayment = totalAmount / months;
            double takeHomeAmount = loanAmount - serviceCharge;

            // Update UI with results
            tvServiceCharge.setText(String.format("Service Charge: ₱%,.2f", serviceCharge));
            tvInterest.setText(String.format("Total Interest: ₱%,.2f", interest));
            tvMonthlyPayment.setText(String.format("Monthly Payment: ₱%,.2f", monthlyPayment));
            tvTakeHomeAmount.setText(String.format("Take Home Amount: ₱%,.2f", takeHomeAmount));

            // Enable apply button
            btnApply.setEnabled(true);

        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Please enter valid numbers", Toast.LENGTH_SHORT).show();
        }
    }

    private void applyForLoan() {
        try {
            double loanAmount = Double.parseDouble(editLoanAmount.getText().toString().trim());
            int months = Integer.parseInt(editMonths.getText().toString().trim());

            // Validate again
            if (loanAmount < 50000 || loanAmount > 100000) {
                Toast.makeText(getContext(), "Special loan amount must be between ₱50,000 and ₱100,000", Toast.LENGTH_SHORT).show();
                return;
            }

            if (months < 1 || months > 18) {
                Toast.makeText(getContext(), "Special loan term must be 1-18 months", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check eligibility again
            int yearsOfService = calculateYearsOfService(dateHired);
            if (yearsOfService < 5) {
                Toast.makeText(getContext(), "You are not eligible for special loan (5+ years required)", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if user already has pending special loan
            if (db.hasPendingLoan(userId, "special")) {
                Toast.makeText(getContext(), "You already have a pending special loan application", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get user's basic salary for loan calculations
            double basicSalary = db.getUserBasicSalary(userId);

            // Calculate loan details
            double interestRate = loanCalculator.getSpecialLoanInterestRate(months);
            double serviceCharge = loanCalculator.calculateServiceCharge(loanAmount, "special");
            double interest = loanCalculator.calculateInterest(loanAmount, interestRate, months);
            double totalAmount = loanAmount + serviceCharge + interest;
            double monthlyPayment = totalAmount / months;
            double takeHomeAmount = loanAmount - serviceCharge;

            // Get current date
            String applicationDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            // Apply for loan
            boolean success = db.applyForLoan(
                    userId,
                    "special",
                    loanAmount,
                    months,
                    interestRate,
                    serviceCharge,
                    totalAmount,
                    monthlyPayment,
                    takeHomeAmount,
                    basicSalary,
                    applicationDate
            );

            if (success) {
                Toast.makeText(getContext(), "Special loan application submitted successfully!", Toast.LENGTH_SHORT).show();
                clearForm();
            } else {
                Toast.makeText(getContext(), "Failed to submit loan application. Please try again.", Toast.LENGTH_SHORT).show();
            }

        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Please calculate the loan first", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearForm() {
        editLoanAmount.setText("");
        editMonths.setText("");
        tvServiceCharge.setText("Service Charge: ₱0.00");
        tvInterest.setText("Total Interest: ₱0.00");
        tvMonthlyPayment.setText("Monthly Payment: ₱0.00");
        tvTakeHomeAmount.setText("Take Home Amount: ₱0.00");
        btnApply.setEnabled(false);
    }

    private void goBack() {
        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }
}