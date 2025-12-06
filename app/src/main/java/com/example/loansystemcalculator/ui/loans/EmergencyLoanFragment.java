package com.example.loansystemcalculator.ui.loans;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
    private RadioGroup rgPaymentOption;
    private RadioButton rbCash,rbInstallment;
    private EditText editLoanAmount, editMonths;
    private TextView tvServiceCharge, tvInterest, tvMonthlyPayment, tvTakeHomeAmount,tvLoanTermLabel,tvTotalAmount;
    private Button btnCalculate, btnApply;
    private DatabaseHelper db;
    private LoanCalculator loanCalculator;
    private String userEmail;
    private int userId;
    private boolean isCashOption = false;

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
        tvTotalAmount = view.findViewById(R.id.tvTotalAmount);
        btnCalculate = view.findViewById(R.id.btn_Calculate);
        btnApply = view.findViewById(R.id.btn_Apply);
        rbCash = view.findViewById(R.id.rbCash);
        rbInstallment = view.findViewById(R.id.rbInstallment);

        btnApply.setEnabled(false);

        rgPaymentOption = view.findViewById(R.id.rgPaymentOption);
        tvLoanTermLabel = view.findViewById(R.id.tvLoanTermLabel);

        // Initially hide month input if cash is selected
        editMonths.setVisibility(View.VISIBLE); // default: installment
        tvLoanTermLabel.setVisibility(View.VISIBLE);

        rgPaymentOption.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbCash) {
                editMonths.setVisibility(View.GONE);
                tvLoanTermLabel.setVisibility(View.GONE);
                editMonths.setText("");
                isCashOption = true;
            } else {
                editMonths.setVisibility(View.VISIBLE);
                tvLoanTermLabel.setVisibility(View.VISIBLE);
                isCashOption = false;
            }
        });


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
    private boolean isCalculated = false;

    private void calculateLoan() {
        try {
            double loanAmount = Double.parseDouble(editLoanAmount.getText().toString().trim());

            // Validate loan amount
            if (loanAmount < 5000 || loanAmount > 25000) {
                Toast.makeText(getContext(), "Emergency loan amount must be between ₱5,000 and ₱25,000", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isCash = rbCash.isChecked();
            int months;

            if (isCash) {
                // CASH AFTER 6 MONTHS
                months = 6;
            } else {
                // INSTALLMENT
                if (editMonths.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getContext(), "Please enter number of months", Toast.LENGTH_SHORT).show();
                    return;
                }

                months = Integer.parseInt(editMonths.getText().toString().trim());

                if (months < 1 || months > 6) {
                    Toast.makeText(getContext(), "Installment term must be between 1-6 months", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // Fixed emergency interest rate
            double interestRate = 0.006; // 0.60% per month

            // Calculations
            double serviceCharge = loanCalculator.calculateServiceCharge(loanAmount, "emergency");
            double interest = loanCalculator.calculateInterest(loanAmount, interestRate, months);
            double totalAmount = loanAmount + serviceCharge + interest;

            double monthlyPayment;
            if (isCash) {
                monthlyPayment = totalAmount; // PAY EVERYTHING AT ONCE
            } else {
                monthlyPayment = totalAmount / months;
            }

            double takeHomeAmount = loanAmount;

            // Update UI
            tvServiceCharge.setText(String.format("Service Charge: ₱%,.2f", serviceCharge));
            tvInterest.setText(String.format("Total Interest: ₱%,.2f", interest));
            tvMonthlyPayment.setText(String.format(isCash ?
                    "Cash Payment After 6 Months: ₱%,.2f" :
                    "Monthly Payment: ₱%,.2f", monthlyPayment));
            tvTakeHomeAmount.setText(String.format("Take Home Amount: ₱%,.2f", takeHomeAmount));
            tvTotalAmount.setText(String.format("TotalAmount: ₱%,.2f", totalAmount));


            btnApply.setEnabled(true);

            isCalculated = true;

        } catch (Exception e) {
            Toast.makeText(getContext(), "Please enter valid values", Toast.LENGTH_SHORT).show();
        }
    }


    private void applyForLoan() {

        if (!isCalculated) {
            Toast.makeText(getContext(), "Please calculate the loan first", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            double loanAmount = Double.parseDouble(editLoanAmount.getText().toString().trim());

            int months;
            boolean isCash = rbCash.isChecked();

            // Determine loan term
            if (isCash) {
                months = 6; // CASH OPTION ALWAYS 6 MONTHS
            } else {
                if (editMonths.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getContext(), "Please enter number of months", Toast.LENGTH_SHORT).show();
                    return;
                }

                months = Integer.parseInt(editMonths.getText().toString().trim());

                if (months < 1 || months > 6) {
                    Toast.makeText(getContext(), "Installment term must be between 1-6 months", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // Validate amount
            if (loanAmount < 5000 || loanAmount > 25000) {
                Toast.makeText(getContext(), "Emergency loan amount must be between ₱5,000 and ₱25,000", Toast.LENGTH_SHORT).show();
                return;
            }

            // Prevent duplicate loan
            if (db.hasPendingLoan(userId, "emergency")) {
                Toast.makeText(getContext(), "You already have a pending emergency loan application", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get salary (for DB saving)
            double basicSalary = db.getUserBasicSalary(userId);

            // Computation
            double interestRate = 0.006;
            double serviceCharge = loanCalculator.calculateServiceCharge(loanAmount, "emergency");
            double interest = loanCalculator.calculateInterest(loanAmount, interestRate, months);
            double totalAmount = loanAmount + serviceCharge + interest;

            // FIXED: correct monthly payment handling
            double monthlyPayment = isCash ? totalAmount : totalAmount / months;

            double takeHomeAmount = loanAmount - serviceCharge;

            // Date
            String applicationDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            // Save to DB
            boolean success = db.applyForLoan(
                    userId,
                    "emergency",
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
                Toast.makeText(getContext(), "Emergency loan application submitted successfully!", Toast.LENGTH_SHORT).show();
                clearForm();
            } else {
                Toast.makeText(getContext(), "Failed to submit loan application. Please try again.", Toast.LENGTH_SHORT).show();
            }

        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Please calculate the loan first before applying", Toast.LENGTH_SHORT).show();
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

        isCalculated = false;

    }

    private void goBack() {
        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }
}