package com.example.loansystemcalculator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loansystemcalculator.adapters.AdminLoanAdapter;

import java.util.ArrayList;

public class AdminMainActivity extends AppCompatActivity implements AdminLoanAdapter.OnLoanActionListener {

    private RecyclerView recyclerLoans;
    private AdminLoanAdapter adapter;
    private ArrayList<AdminLoan> loanList;
    private DatabaseHelper db;
    private ImageView btnLogout;
    private TextView tvAdminEmail;
    private Spinner spinnerFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> logout());

        tvAdminEmail = findViewById(R.id.tvAdminEmail);
        recyclerLoans = findViewById(R.id.recyclerLoans);
        spinnerFilter = findViewById(R.id.spinnerFilter);

        //ADMIN
        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        String adminEmail = prefs.getString("email", "admin@abc.com");
        tvAdminEmail.setText("Logged in as: " + adminEmail);

        db = new DatabaseHelper(this);
        recyclerLoans.setLayoutManager(new LinearLayoutManager(this));
        setupFilterSpinner();
        loadLoans("All");

        //LOGOUT
        btnLogout.setOnClickListener(v -> logout());
        this.getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                moveTaskToBack(true);
            }
        });
    }
    //FILTER FOR LOANS
    private void setupFilterSpinner() {
        String[] filterOptions = {"All", "Pending", "Approved", "Rejected"};
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, filterOptions);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(adapterSpinner);

        spinnerFilter.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selectedStatus = filterOptions[position];
                loadLoans(selectedStatus);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                loadLoans("All");
            }
        });
    }

    // Load loans from database based on filter
    private void loadLoans(String filterStatus) {
        loanList = db.getAllLoans(filterStatus);
        adapter = new AdminLoanAdapter(this, loanList, this);
        recyclerLoans.setAdapter(adapter);
    }
    @Override
    public void onApprove(AdminLoan loan) {
        db.updateLoanStatus(loan.loanId, "Approved");
        loadLoans(spinnerFilter.getSelectedItem().toString());
    }
    @Override
    public void onReject(AdminLoan loan) {
        db.updateLoanStatus(loan.loanId, "Rejected");
        loadLoans(spinnerFilter.getSelectedItem().toString());
    }
    private void logout() {
        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(AdminMainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
