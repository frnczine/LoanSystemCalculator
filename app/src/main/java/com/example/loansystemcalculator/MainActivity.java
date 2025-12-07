package com.example.loansystemcalculator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.loansystemcalculator.databinding.ActivityMainBinding;
import com.example.loansystemcalculator.ui.home.HomeFragment;
import com.example.loansystemcalculator.ui.loans.EmergencyLoanFragment;
import com.example.loansystemcalculator.ui.loans.MyLoansFragment;
import com.example.loansystemcalculator.ui.loans.RegularLoanFragment;
import com.example.loansystemcalculator.ui.loans.SpecialLoanFragment;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private TextView txtNavHeaderName, txtNavHeaderEmail;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Check if user is logged in
        prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        if (!prefs.getBoolean("is_logged_in", false)) {
            // User not logged in, redirect to login
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        Toolbar toolbar = binding.appBarMain.toolbar;
        setSupportActionBar(toolbar);

        binding.appBarMain.fab.setVisibility(View.GONE);

        drawer = binding.drawerLayout;
        navigationView = binding.navView;

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                    // Update checked item in navigation drawer
                    navigationView.setCheckedItem(R.id.nav_home);
                } else {
                    // Allow default back behavior (e.g., exit app)
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        updateNavHeader();

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment(), "Home");
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    private void updateNavHeader() {
        View headerView = navigationView.getHeaderView(0);
        txtNavHeaderName = headerView.findViewById(R.id.txtNavHeaderName);
        txtNavHeaderEmail = headerView.findViewById(R.id.txtNavHeaderEmail);

        String firstName = prefs.getString("first_name", "");
        String lastName = prefs.getString("last_name", "");
        String email = prefs.getString("email", "");

        if (txtNavHeaderName != null) {
            txtNavHeaderName.setText(firstName + " " + lastName);
        }

        if (txtNavHeaderEmail != null) {
            txtNavHeaderEmail.setText(email);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        Fragment fragment = null;
        String title = getString(R.string.app_name);

        if (id == R.id.nav_home) {
            fragment = new HomeFragment();
            title = "Home";
        } else if (id == R.id.nav_emergency_loan) {
            fragment = new EmergencyLoanFragment();
            title = "Emergency Loan";
        } else if (id == R.id.nav_special_loan) {
            fragment = new SpecialLoanFragment();
            title = "Special Loan";
        } else if (id == R.id.nav_regular_loan) {
            fragment = new RegularLoanFragment();
            title = "Regular Loan";
        } else if (id == R.id.nav_my_loans) {
            fragment = new MyLoansFragment();
            title = "My Loans";
        } else if (id == R.id.nav_logout) {
            logout();
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }

        if (fragment != null) {
            loadFragment(fragment, title);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadFragment(Fragment fragment, String title) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment_content_main, fragment);

        // Only add to back stack if not home fragment
        if (!(fragment instanceof HomeFragment)) {
            transaction.addToBackStack(null);
        }

        transaction.commit();

        // Update toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    private void logout() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

/*
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            // Update checked item in navigation drawer
            navigationView.setCheckedItem(R.id.nav_home);
        } else {
            super.onBackPressed();
        }
    }
   */


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateNavHeader();
    }
}