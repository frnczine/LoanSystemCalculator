package com.example.loansystemcalculator.ui.loans;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loansystemcalculator.DatabaseHelper;
import com.example.loansystemcalculator.R;
import com.example.loansystemcalculator.adapters.LoanAdapter;
import com.example.loansystemcalculator.models.LoanApplication;

import java.util.ArrayList;
import java.util.List;

public class MyLoansFragment extends Fragment {

    private MyLoansViewModel viewModel;
    private RecyclerView recyclerView;
    private LoanAdapter adapter;
    private DatabaseHelper db;
    private int userId;
    private TextView tvEmptyMessage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_loans, container, false);

        recyclerView = view.findViewById(R.id.recyclerMyLoans);
        tvEmptyMessage = view.findViewById(R.id.tvEmptyMessage);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(MyLoansViewModel.class);
        db = new DatabaseHelper(getContext());

        loadUserInfo();
        setupRecyclerView();
        observeViewModel();
        loadUserLoans();
    }

    private void loadUserInfo() {
        SharedPreferences prefs = getActivity().getSharedPreferences("user_session", 0);
        String userEmail = prefs.getString("email", "");
        userId = db.getUserIdByEmail(userEmail);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new LoanAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
    }

    private void observeViewModel() {
        // If you're using ViewModel, observe it here
        // Otherwise, just load data directly
    }

    private void loadUserLoans() {
        List<LoanApplication> loans = new ArrayList<>();

        Cursor cursor = db.getUserLoans(userId);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                LoanApplication loan = new LoanApplication();
                loan.setId(cursor.getInt(cursor.getColumnIndexOrThrow("loan_id")));
                loan.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("user_id")));
                loan.setLoanType(cursor.getString(cursor.getColumnIndexOrThrow("loan_type")));
                loan.setLoanAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("loan_amount")));
                loan.setMonthsToPay(cursor.getInt(cursor.getColumnIndexOrThrow("months_to_pay")));
                loan.setInterestRate(cursor.getDouble(cursor.getColumnIndexOrThrow("interest_rate")));
                loan.setServiceCharge(cursor.getDouble(cursor.getColumnIndexOrThrow("service_charge")));
                loan.setTotalAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("total_amount")));
                loan.setMonthlyAmortization(cursor.getDouble(cursor.getColumnIndexOrThrow("monthly_amortization")));
                loan.setTakeHomeAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("take_home_amount")));
                loan.setApplicationDate(cursor.getString(cursor.getColumnIndexOrThrow("application_date")));
                loan.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));

                loans.add(loan);
            } while (cursor.moveToNext());
            cursor.close();
        }

        adapter.setLoans(loans);

        if (loans.isEmpty()) {
            tvEmptyMessage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmptyMessage.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}