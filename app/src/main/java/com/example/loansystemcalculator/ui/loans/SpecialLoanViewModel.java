package com.example.loansystemcalculator.ui.loans;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Calendar;

public class SpecialLoanViewModel extends ViewModel {

    private final MutableLiveData<Double> loanAmount = new MutableLiveData<>(0.0);
    private final MutableLiveData<Integer> months = new MutableLiveData<>(0);
    private final MutableLiveData<Double> serviceCharge = new MutableLiveData<>(0.0);
    private final MutableLiveData<Double> interest = new MutableLiveData<>(0.0);
    private final MutableLiveData<Double> monthlyPayment = new MutableLiveData<>(0.0);
    private final MutableLiveData<Double> takeHomeAmount = new MutableLiveData<>(0.0);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>("");
    private final MutableLiveData<Boolean> canApply = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isEligible = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> yearsOfService = new MutableLiveData<>(0);

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

    public LiveData<Boolean> getIsEligible() {
        return isEligible;
    }

    public LiveData<Integer> getYearsOfService() {
        return yearsOfService;
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

    public void setIsEligible(boolean eligible) {
        isEligible.setValue(eligible);
    }

    public void setYearsOfService(int years) {
        yearsOfService.setValue(years);
    }

    // Business logic methods
    public boolean validateLoanAmount(double amount) {
        return amount >= 50000 && amount <= 100000;
    }

    public boolean validateMonths(int monthsValue) {
        return monthsValue >= 1 && monthsValue <= 18;
    }

    public void calculateYearsOfService(String dateHired) {
        try {
            // Parse date hired (assuming format: yyyy-MM-dd)
            String[] parts = dateHired.split("-");
            int hireYear = Integer.parseInt(parts[0]);

            Calendar calendar = Calendar.getInstance();
            int currentYear = calendar.get(Calendar.YEAR);

            int years = currentYear - hireYear;
            setYearsOfService(years);
            setIsEligible(years >= 5);

        } catch (Exception e) {
            setYearsOfService(0);
            setIsEligible(false);
            setErrorMessage("Invalid date format");
        }
    }

    public void calculateLoan(double amount, int monthsValue) {
        if (!getIsEligible().getValue()) {
            setErrorMessage("Not eligible - Need 5+ years of service");
            setCanApply(false);
            return;
        }

        if (!validateLoanAmount(amount)) {
            setErrorMessage("Loan amount must be between ₱50,000 and ₱100,000");
            setCanApply(false);
            return;
        }

        if (!validateMonths(monthsValue)) {
            setErrorMessage("Loan term must be 1-18 months");
            setCanApply(false);
            return;
        }

        // Special loan calculations (variable interest rate based on months)
        double interestRate = getSpecialLoanInterestRate(monthsValue);
        double serviceChargeValue = amount * 0.015; // 1.5% service charge for special loan
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

    private double getSpecialLoanInterestRate(int months) {
        // Variable rates for special loan
        if (months <= 6) {
            return 0.008; // 0.8% for 1-6 months
        } else if (months <= 12) {
            return 0.009; // 0.9% for 7-12 months
        } else {
            return 0.010; // 1.0% for 13-18 months
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
        setIsEligible(false);
        setYearsOfService(0);
    }
}