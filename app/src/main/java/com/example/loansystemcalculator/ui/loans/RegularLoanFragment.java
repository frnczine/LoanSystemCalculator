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

public class RegularLoanFragment extends Fragment {

    private EditText editLoanAmount, editMonths;
    private TextView tvBasicSalary, tvMaxLoan, tvServiceCharge, tvInterest, tvMonthlyPayment, tvTakeHomeAmount;
    private Button btnCalculate, btnApply;
    private DatabaseHelper db;
    private LoanCalculator loanCalculator;
    private String userEmail;
    private int userId;
    private double basicSalary;
    private double maxLoanAmount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_regular_loan, container, false);

        initializeViews(view);
        initializeDatabase();
        loadUserInfo();
        calculateMaxLoan();
        setupClickListeners();

        return view;
    }

    private void initializeViews(View view) {
        editLoanAmount = view.findViewById(R.id.edit_loanAmount);
        editMonths = view.findViewById(R.id.edit_loanMonth);
        tvBasicSalary = view.findViewById(R.id.tvBasicSalary);
        tvMaxLoan = view.findViewById(R.id.tvMaxLoan);
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
        basicSalary = prefs.getFloat("basic_salary", 0);
    }

    private void calculateMaxLoan() {
        // Regular loan: Salary × 2.5
        maxLoanAmount = basicSalary * 2.5;

        tvBasicSalary.setText(String.format("Basic Salary: ₱%,.2f", basicSalary));
        tvMaxLoan.setText(String.format("Maximum Loan Amount: ₱%,.2f", maxLoanAmount));

        // Set hint with max amount
        editLoanAmount.setHint("Up to ₱" + String.format("%,.0f", maxLoanAmount));
    }

    private void setupClickListeners() {
        btnCalculate.setOnClickListener(v -> calculateLoan());
        btnApply.setOnClickListener(v -> applyForLoan());
    }

    private void calculateLoan() {
        try {
            double loanAmount = Double.parseDouble(editLoanAmount.getText().toString().trim());
            int months = Integer.parseInt(editMonths.getText().toString().trim());

            // Validate regular loan limits
            if (loanAmount <= 0 || loanAmount > maxLoanAmount) {
                Toast.makeText(getContext(),
                        String.format("Regular loan amount must be between ₱1 and ₱%,.2f", maxLoanAmount),
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (months < 1 || months > 24) {
                Toast.makeText(getContext(), "Regular loan term must be 1-24 months", Toast.LENGTH_SHORT).show();
                return;
            }

            // Calculate loan details with variable rates
            double interestRate = loanCalculator.getRegularLoanInterestRate(months);
            double serviceCharge = loanCalculator.calculateServiceCharge(loanAmount, "regular");
            double interest = loanCalculator.calculateInterest(loanAmount, interestRate, months);
            double totalAmount = loanAmount + serviceCharge + interest;
            double monthlyPayment = totalAmount / months;
            double takeHomeAmount = loanAmount - serviceCharge;

            // Update UI with results
            tvServiceCharge.setText(String.format("Service Charge: ₱%,.2f", serviceCharge));
            tvInterest.setText(String.format("Total Interest: ₱%,.2f (%.1f%%)", interest, interestRate * 100));
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
            if (loanAmount <= 0 || loanAmount > maxLoanAmount) {
                Toast.makeText(getContext(),
                        String.format("Regular loan amount must be between ₱1 and ₱%,.2f", maxLoanAmount),
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (months < 1 || months > 24) {
                Toast.makeText(getContext(), "Regular loan term must be 1-24 months", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if user already has pending regular loan
            if (db.hasPendingLoan(userId, "regular")) {
                Toast.makeText(getContext(), "You already have a pending regular loan application", Toast.LENGTH_SHORT).show();
                return;
            }

            // Calculate loan details with variable rates
            double interestRate = loanCalculator.getRegularLoanInterestRate(months);
            double serviceCharge = loanCalculator.calculateServiceCharge(loanAmount, "regular");
            double interest = loanCalculator.calculateInterest(loanAmount, interestRate, months);
            double totalAmount = loanAmount + serviceCharge + interest;
            double monthlyPayment = totalAmount / months;
            double takeHomeAmount = loanAmount - serviceCharge;

            // Get current date
            String applicationDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            // Apply for loan
            boolean success = db.applyForLoan(
                    userId,
                    "regular",
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
                Toast.makeText(getContext(), "Regular loan application submitted successfully!", Toast.LENGTH_SHORT).show();
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