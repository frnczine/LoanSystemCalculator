package com.example.loansystemcalculator.ui.home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.loansystemcalculator.DatabaseHelper;
import com.example.loansystemcalculator.R;
import com.example.loansystemcalculator.ui.loans.EmergencyLoanFragment;
import com.example.loansystemcalculator.ui.loans.MyLoansFragment;
import com.example.loansystemcalculator.ui.loans.RegularLoanFragment;
import com.example.loansystemcalculator.ui.loans.SpecialLoanFragment;

public class HomeFragment extends Fragment {

    private TextView txtWelcome, txtIntroUser, txtIntro2User;
    private CardView cardEmergencyLoan, cardSpecialLoan, cardRegularLoan;
    private DatabaseHelper db;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        initializeViews(root);
        initializeDatabase();
        loadUserInfo();
        setupClickListeners();

        return root;
    }

    private void initializeViews(View view) {
        txtWelcome = view.findViewById(R.id.txtHi);
        txtIntroUser = view.findViewById(R.id.txtIntroUser);
        txtIntro2User = view.findViewById(R.id.txtIntro2User);

        cardEmergencyLoan = view.findViewById(R.id.cardEmergencyLoan);
        cardSpecialLoan = view.findViewById(R.id.cardSpecialLoan);
        cardRegularLoan = view.findViewById(R.id.cardRegularLoan);
    }

    private void initializeDatabase() {
        db = new DatabaseHelper(getContext());
    }

    private void loadUserInfo() {
        try {
            SharedPreferences prefs = getActivity().getSharedPreferences("user_session", 0);

            // Try display_name first (set during registration)
            String displayName = prefs.getString("display_name", "");

            if (displayName != null && !displayName.isEmpty()) {
                txtWelcome.setText("Hi, " + displayName + "!");
            } else {
                // Fallback to first_name
                String firstName = prefs.getString("first_name", "");
                if (firstName != null && !firstName.isEmpty()) {
                    txtWelcome.setText("Hi, " + firstName + "!");
                } else {
                    // Try to get from database
                    String email = prefs.getString("email", "");
                    if (!email.isEmpty()) {
                        userId = db.getUserIdByEmail(email);
                        String dbFirstName = db.getUserFirstName(userId);
                        if (dbFirstName != null && !dbFirstName.isEmpty()) {
                            txtWelcome.setText("Hi, " + dbFirstName + "!");
                        } else {
                            txtWelcome.setText("Hi, User!");
                        }
                    } else {
                        txtWelcome.setText("Hi, User!");
                    }
                }
            }

        } catch (Exception e) {
            txtWelcome.setText("Hi, User!");
        }
    }

    private void setupClickListeners() {
        // Emergency Loan Card
        cardEmergencyLoan.setOnClickListener(v -> navigateToEmergencyLoan());

        // Special Loan Card
        cardSpecialLoan.setOnClickListener(v -> navigateToSpecialLoan());

        // Regular Loan Card
        cardRegularLoan.setOnClickListener(v -> navigateToRegularLoan());
    }

    private void navigateToEmergencyLoan() {
        //Navigate to Emergency Loan Fragment
        getParentFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, new EmergencyLoanFragment())
                .addToBackStack(null)
                .commit();
    }

    private void navigateToSpecialLoan() {
        //Navigate to Special Loan Fragment
        getParentFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, new SpecialLoanFragment())
                .addToBackStack(null)
                .commit();
    }

    private void navigateToRegularLoan() {
        //Navigate to Regular Loan Fragment
        getParentFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, new RegularLoanFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserInfo(); //Refresh user info when fragment resumes
    }
}