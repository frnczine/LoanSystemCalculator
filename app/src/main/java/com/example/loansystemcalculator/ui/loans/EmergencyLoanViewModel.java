package com.example.loansystemcalculator.ui.loans;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EmergencyLoanViewModel extends ViewModel {

    private final MutableLiveData<Double> loanAmount = new MutableLiveData<>(0.0);
    private final MutableLiveData<Integer> months = new MutableLiveData<>(0);
    private final MutableLiveData<Double> serviceCharge = new MutableLiveData<>(0.0);
    private final MutableLiveData<Double> interest = new MutableLiveData<>(0.0);
    private final MutableLiveData<Double> monthlyPayment = new MutableLiveData<>(0.0);
    private final MutableLiveData<Double> takeHomeAmount = new MutableLiveData<>(0.0);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>("");
    private final MutableLiveData<Boolean> canApply = new MutableLiveData<>(false);

    // Getters
    public LiveData<Double> getLoanAmount() {
        return loanAmount;
    }

    public LiveData<Integer> getMonths() {
        return months;
    }

    public LiveData<Double> getServiceCharge() {
        return serviceCharge;
    }

    public LiveData<Double> getInterest() {
        return interest;
    }

    public LiveData<Double> getMonthlyPayment() {
        return monthlyPayment;
    }

    public LiveData<Double> getTakeHomeAmount() {
        return takeHomeAmount;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getCanApply() {
        return canApply;
    }

    // Setters
    public void setLoanAmount(double amount) {
        loanAmount.setValue(amount);
    }

    public void setMonths(int monthsValue) {
        months.setValue(monthsValue);
    }

    public void setServiceCharge(double charge) {
        serviceCharge.setValue(charge);
    }

    public void setInterest(double interestValue) {
        interest.setValue(interestValue);
    }

    public void setMonthlyPayment(double payment) {
        monthlyPayment.setValue(payment);
    }

    public void setTakeHomeAmount(double amount) {
        takeHomeAmount.setValue(amount);
    }

    public void setErrorMessage(String message) {
        errorMessage.setValue(message);
    }

    public void setCanApply(boolean canApplyValue) {
        canApply.setValue(canApplyValue);
    }

    // Business logic methods
    public boolean validateLoanAmount(double amount) {
        return amount >= 5000 && amount <= 25000;
    }

    public boolean validateMonths(int monthsValue) {
        return monthsValue >= 1 && monthsValue <= 6;
    }

    public void calculateLoan(double amount, int monthsValue) {
        if (!validateLoanAmount(amount)) {
            setErrorMessage("Loan amount must be between ₱5,000 and ₱25,000");
            setCanApply(false);
            return;
        }

        if (!validateMonths(monthsValue)) {
            setErrorMessage("Loan term must be 1-6 months");
            setCanApply(false);
            return;
        }

        // Emergency loan calculations
        double interestRate = 0.006; // 0.60% per month
        double serviceChargeValue = amount * 0.01; // 1% service charge
        double interestValue = amount * interestRate * monthsValue;
        double totalAmount = amount + serviceChargeValue + interestValue;
        double monthlyPaymentValue = totalAmount / monthsValue;
        double takeHomeAmountValue = amount - serviceChargeValue;

        // Update LiveData
        setServiceCharge(serviceChargeValue);
        setInterest(interestValue);
        setMonthlyPayment(monthlyPaymentValue);
        setTakeHomeAmount(takeHomeAmountValue);
        setErrorMessage("");
        setCanApply(true);
    }

    public void reset() {
        setLoanAmount(0.0);
        setMonths(0);
        setServiceCharge(0.0);
        setInterest(0.0);
        setMonthlyPayment(0.0);
        setTakeHomeAmount(0.0);
        setErrorMessage("");
        setCanApply(false);
    }
}