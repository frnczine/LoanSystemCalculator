package com.example.loansystemcalculator.ui.loans;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.loansystemcalculator.models.LoanApplication;

import java.util.ArrayList;
import java.util.List;

public class MyLoansViewModel extends ViewModel {

    private final MutableLiveData<List<LoanApplication>> loans = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>("");

    public LiveData<List<LoanApplication>> getLoans() {
        return loans;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void setLoans(List<LoanApplication> loanList) {
        loans.setValue(loanList);
    }

    public void setErrorMessage(String message) {
        errorMessage.setValue(message);
    }

    public void addLoan(LoanApplication loan) {
        List<LoanApplication> currentLoans = loans.getValue();
        if (currentLoans != null) {
            currentLoans.add(0, loan); // Add to beginning
            loans.setValue(currentLoans);
        }
    }

    public void updateLoanStatus(int loanId, String status) {
        List<LoanApplication> currentLoans = loans.getValue();
        if (currentLoans != null) {
            for (LoanApplication loan : currentLoans) {
                if (loan.getId() == loanId) {
                    loan.setStatus(status);
                    break;
                }
            }
            loans.setValue(currentLoans);
        }
    }

    public void clearLoans() {
        loans.setValue(new ArrayList<>());
        errorMessage.setValue("");
    }
}