package com.example.loansystemcalculator.models;


public class LoanApplication {
    private int id;
    private int userId;
    private String loanType;
    private double loanAmount;
    private int monthsToPay;
    private double interestRate;
    private double serviceCharge;
    private double totalAmount;
    private double monthlyAmortization;
    private double takeHomeAmount;
    private String applicationDate;
    private String status;

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getLoanType() { return loanType; }
    public void setLoanType(String loanType) { this.loanType = loanType; }

    public double getLoanAmount() { return loanAmount; }
    public void setLoanAmount(double loanAmount) { this.loanAmount = loanAmount; }

    public int getMonthsToPay() { return monthsToPay; }
    public void setMonthsToPay(int monthsToPay) { this.monthsToPay = monthsToPay; }

    public double getInterestRate() { return interestRate; }
    public void setInterestRate(double interestRate) { this.interestRate = interestRate; }

    public double getServiceCharge() { return serviceCharge; }
    public void setServiceCharge(double serviceCharge) { this.serviceCharge = serviceCharge; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public double getMonthlyAmortization() { return monthlyAmortization; }
    public void setMonthlyAmortization(double monthlyAmortization) { this.monthlyAmortization = monthlyAmortization; }

    public double getTakeHomeAmount() { return takeHomeAmount; }
    public void setTakeHomeAmount(double takeHomeAmount) { this.takeHomeAmount = takeHomeAmount; }

    public String getApplicationDate() { return applicationDate; }
    public void setApplicationDate(String applicationDate) { this.applicationDate = applicationDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}