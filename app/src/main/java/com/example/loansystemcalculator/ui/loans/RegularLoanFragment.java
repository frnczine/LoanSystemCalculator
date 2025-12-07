package com.example.loansystemcalculator.ui.loans;
import android.content.Context;
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

    private EditText editMonths;
    private TextView tvBasicSalary, tvMaxLoan, tvServiceCharge, tvInterest, tvMonthlyPayment, tvTakeHomeAmount;
    private Button btnCalculate, btnApply;

    private DatabaseHelper db;
    private LoanCalculator loanCalculator;

    private int userId;
    private String userEmail;

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

        ImageView btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        }
    }

    private void initializeDatabase() {
        db = new DatabaseHelper(getContext());
    }

    private void loadUserInfo() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);

        basicSalary = (double) prefs.getFloat("basic_salary", 0);
        userEmail = prefs.getString("email", "");
        userId = db.getUserIdByEmail(userEmail);

        tvBasicSalary.setText(String.format("Basic Salary: ₱%,.2f", basicSalary));

        if (basicSalary == 0) {
            Toast.makeText(getContext(), "Basic salary not found. Please re-login.", Toast.LENGTH_LONG).show();
        }
    }





    private void calculateMaxLoan() {
        maxLoanAmount = loanCalculator.calculateMaxRegularLoan(basicSalary);

        tvBasicSalary.setText(String.format("Basic Salary: ₱%,.2f", basicSalary));
        tvMaxLoan.setText(String.format("Maximum Loan Amount: ₱%,.2f", maxLoanAmount));
    }

    private void setupClickListeners() {
        btnCalculate.setOnClickListener(v -> calculateLoan());
        btnApply.setOnClickListener(v -> applyForLoan());
    }

    private void calculateLoan() {
        try {
            int months = Integer.parseInt(editMonths.getText().toString().trim());
            double loanAmount = maxLoanAmount;

            if (months < 1 || months > 24) {
                Toast.makeText(getContext(), "Regular loan term must be 1–24 months", Toast.LENGTH_SHORT).show();
                return;
            }

            // Interest rate
            double interestRate = loanCalculator.getRegularLoanInterestRate(months);

            // Service charge (2%)
            double serviceCharge = loanCalculator.calculateServiceCharge(loanAmount, "regular");

            // Total interest
            double interest = loanCalculator.calculateInterest(loanAmount, interestRate, months);

            // Total amount payable
            double totalAmount = loanAmount + serviceCharge + interest;

            // Monthly payment
            double monthlyPayment = totalAmount / months;

            // Take-home ONLY deducts service charge
            double takeHomeAmount = loanAmount - serviceCharge;

            tvServiceCharge.setText(String.format("Service Charge: ₱%,.2f", serviceCharge));
            tvInterest.setText(String.format("Total Interest: ₱%,.2f (%.2f%%)", interest, interestRate * 100));
            tvMonthlyPayment.setText(String.format("Monthly Payment: ₱%,.2f", monthlyPayment));
            tvTakeHomeAmount.setText(String.format("Take Home Amount: ₱%,.2f", takeHomeAmount));

            btnApply.setEnabled(true);

        } catch (Exception e) {
            Toast.makeText(getContext(), "Please enter valid months", Toast.LENGTH_SHORT).show();
        }
    }

    private void applyForLoan() {
        try {
            int months = Integer.parseInt(editMonths.getText().toString().trim());
            double loanAmount = maxLoanAmount;

            if (db.hasPendingLoan(userId, "regular")) {
                Toast.makeText(getContext(), "You already have a pending regular loan.", Toast.LENGTH_SHORT).show();
                return;
            }

            double interestRate = loanCalculator.getRegularLoanInterestRate(months);
            double serviceCharge = loanCalculator.calculateServiceCharge(loanAmount, "regular");
            double interest = loanCalculator.calculateInterest(loanAmount, interestRate, months);

            double totalAmount = loanAmount + serviceCharge + interest;
            double monthlyPayment = totalAmount / months;

            // Correct take-home (interest NOT deducted)
            double takeHomeAmount = loanAmount - serviceCharge;

            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

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
                    date
            );

            if (success) {
                Toast.makeText(getContext(), "Regular loan application submitted!", Toast.LENGTH_SHORT).show();
                clearForm();
            } else {
                Toast.makeText(getContext(), "Loan submission failed.", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(getContext(), "Please calculate the loan first", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearForm() {
        editMonths.setText("");
        tvServiceCharge.setText("Service Charge: ₱0.00");
        tvInterest.setText("Total Interest: ₱0.00");
        tvMonthlyPayment.setText("Monthly Payment: ₱0.00");
        tvTakeHomeAmount.setText("Take Home Amount: ₱0.00");
        btnApply.setEnabled(false);
    }
}
