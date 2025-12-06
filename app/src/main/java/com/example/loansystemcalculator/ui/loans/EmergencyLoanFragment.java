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
import java.util.Date;
import java.util.Locale;

public class EmergencyLoanFragment extends Fragment {

    private EditText editLoanAmount, editMonths;
    private TextView tvServiceCharge, tvInterest, tvMonthlyPayment, tvTakeHomeAmount;
    private Button btnCalculate, btnApply;
    private DatabaseHelper db;
    private LoanCalculator loanCalculator;
    private String userEmail;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_emergency_loan, container, false);

        initializeViews(view);
        initializeDatabase();
        loadUserInfo();
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
    }

    private void setupClickListeners() {
        btnCalculate.setOnClickListener(v -> calculateLoan());
        btnApply.setOnClickListener(v -> applyForLoan());
    }

    private void calculateLoan() {
        try {
            double loanAmount = Double.parseDouble(editLoanAmount.getText().toString().trim());
            int months = Integer.parseInt(editMonths.getText().toString().trim());

            // Validate emergency loan limits
            if (loanAmount < 5000 || loanAmount > 25000) {
                Toast.makeText(getContext(), "Emergency loan amount must be between ₱5,000 and ₱25,000", Toast.LENGTH_SHORT).show();
                return;
            }

            if (months < 1 || months > 6) {
                Toast.makeText(getContext(), "Emergency loan term must be 1-6 months", Toast.LENGTH_SHORT).show();
                return;
            }

            // Calculate loan details
            double interestRate = 0.006; // 0.60% per month
            double serviceCharge = loanCalculator.calculateServiceCharge(loanAmount, "emergency");
            double interest = loanCalculator.calculateInterest(loanAmount, interestRate, months);
            double totalAmount = loanAmount + serviceCharge + interest;
            double monthlyPayment = totalAmount / months;
            double takeHomeAmount = loanAmount - serviceCharge; // FIXED: This is correct!

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
            if (loanAmount < 5000 || loanAmount > 25000) {
                Toast.makeText(getContext(), "Emergency loan amount must be between ₱5,000 and ₱25,000", Toast.LENGTH_SHORT).show();
                return;
            }

            if (months < 1 || months > 6) {
                Toast.makeText(getContext(), "Emergency loan term must be 1-6 months", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if user already has pending emergency loan
            if (db.hasPendingLoan(userId, "emergency")) {
                Toast.makeText(getContext(), "You already have a pending emergency loan application", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get user's basic salary for loan calculations
            double basicSalary = db.getUserBasicSalary(userId);

            // Calculate loan details
            double interestRate = 0.006; // 0.60%
            double serviceCharge = loanCalculator.calculateServiceCharge(loanAmount, "emergency");
            double interest = loanCalculator.calculateInterest(loanAmount, interestRate, months);
            double totalAmount = loanAmount + serviceCharge + interest;
            double monthlyPayment = totalAmount / months;
            double takeHomeAmount = loanAmount - serviceCharge;

            // Get current date
            String applicationDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            // Apply for loan
            boolean success = db.applyForLoan(
                    userId,
                    "emergency",
                    loanAmount,
                    months,
                    interestRate,
                    serviceCharge,
                    totalAmount,
                    monthlyPayment,
                    takeHomeAmount, // This is already correct
                    basicSalary,
                    applicationDate
            );

            if (success) {
                Toast.makeText(getContext(), "Emergency loan application submitted successfully!", Toast.LENGTH_SHORT).show();
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