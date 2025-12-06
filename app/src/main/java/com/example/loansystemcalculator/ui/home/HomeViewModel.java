package com.example.loansystemcalculator.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> welcomeMessage = new MutableLiveData<>("Welcome!");
    private final MutableLiveData<Integer> pendingLoans = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> approvedLoans = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> hasEmergencyLoan = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> hasSpecialLoan = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> hasRegularLoan = new MutableLiveData<>(false);

    public LiveData<String> getWelcomeMessage() {
        return welcomeMessage;
    }

    public LiveData<Integer> getPendingLoans() {
        return pendingLoans;
    }

    public LiveData<Integer> getApprovedLoans() {
        return approvedLoans;
    }

    public LiveData<Boolean> getHasEmergencyLoan() {
        return hasEmergencyLoan;
    }

    public LiveData<Boolean> getHasSpecialLoan() {
        return hasSpecialLoan;
    }

    public LiveData<Boolean> getHasRegularLoan() {
        return hasRegularLoan;
    }

    public void setWelcomeMessage(String firstName, String lastName) {
        welcomeMessage.setValue("Welcome, " + firstName + " " + lastName + "!");
    }

    public void setPendingLoans(int count) {
        pendingLoans.setValue(count);
    }

    public void setApprovedLoans(int count) {
        approvedLoans.setValue(count);
    }

    public void setHasEmergencyLoan(boolean hasLoan) {
        hasEmergencyLoan.setValue(hasLoan);
    }

    public void setHasSpecialLoan(boolean hasLoan) {
        hasSpecialLoan.setValue(hasLoan);
    }

    public void setHasRegularLoan(boolean hasLoan) {
        hasRegularLoan.setValue(hasLoan);
    }

    public boolean canApplyForEmergencyLoan() {
        return !Boolean.TRUE.equals(hasEmergencyLoan.getValue());
    }

    public boolean canApplyForSpecialLoan() {
        return !Boolean.TRUE.equals(hasSpecialLoan.getValue());
    }

    public boolean canApplyForRegularLoan() {
        return !Boolean.TRUE.equals(hasRegularLoan.getValue());
    }

    public void updateLoanStatus(String loanType, boolean hasLoan) {
        switch (loanType) {
            case "emergency":
                setHasEmergencyLoan(hasLoan);
                break;
            case "special":
                setHasSpecialLoan(hasLoan);
                break;
            case "regular":
                setHasRegularLoan(hasLoan);
                break;
        }
    }
}