package com.example.loansystemcalculator.ui.loans;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RegularLoanViewModel extends ViewModel {

    private final MutableLiveData<Double> loanAmount = new MutableLiveData<>(0.0);
    private final MutableLiveData<Integer> months = new MutableLiveData<>(0);
    private final MutableLiveData<Double> serviceCharge = new MutableLiveData<>(0.0);
    private final MutableLiveData<Double> interest = new MutableLiveData<>(0.0);
    private final MutableLiveData<Double> monthlyPayment = new MutableLiveData<>(0.0);
    private final MutableLiveData<Double> takeHomeAmount = new MutableLiveData<>(0.0);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>("");
    private final MutableLiveData<Boolean> canApply = new MutableLiveData<>(false);
    private final MutableLiveData<Double> basicSalary = new MutableLiveData<>(0.0);
    private final MutableLiveData<Double> maxLoanAmount = new MutableLiveData<>(0.0);

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

    public LiveData<Double> getBasicSalary() {
        return basicSalary;
    }

    public LiveData<Double> getMaxLoanAmount() {
        return maxLoanAmount;
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

    public void setBasicSalary(double salary) {
        basicSalary.setValue(salary);
        // Calculate max loan amount: Salary × 2.5
        maxLoanAmount.setValue(salary * 2.5);
    }

    // Business logic methods
    public boolean validateLoanAmount(double amount) {
        double maxAmount = maxLoanAmount.getValue() != null ? maxLoanAmount.getValue() : 0;
        return amount > 0 && amount <= maxAmount;
    }

    public boolean validateMonths(int monthsValue) {
        return monthsValue >= 1 && monthsValue <= 24;
    }

    public void calculateLoan(double amount, int monthsValue) {
        double maxAmount = maxLoanAmount.getValue() != null ? maxLoanAmount.getValue() : 0;

        if (!validateLoanAmount(amount)) {
            setErrorMessage(String.format("Loan amount must be between ₱1 and ₱%,.2f", maxAmount));
            setCanApply(false);
            return;
        }

        if (!validateMonths(monthsValue)) {
            setErrorMessage("Loan term must be 1-24 months");
            setCanApply(false);
            return;
        }

        // Regular loan calculations (variable interest rate based on months)
        double interestRate = getRegularLoanInterestRate(monthsValue);
        double serviceChargeValue = amount * 0.02; // 2% service charge for regular loan
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

    private double getRegularLoanInterestRate(int months) {
        // Variable rates for regular loan
        if (months <= 6) {
            return 0.007; // 0.7% for 1-6 months
        } else if (months <= 12) {
            return 0.008; // 0.8% for 7-12 months
        } else if (months <= 18) {
            return 0.009; // 0.9% for 13-18 months
        } else {
            return 0.010; // 1.0% for 19-24 months
        }
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
        // Don't reset basicSalary and maxLoanAmount
    }
}