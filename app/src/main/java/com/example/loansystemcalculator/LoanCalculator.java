package com.example.loansystemcalculator;

public class LoanCalculator {

    // Emergency Loan: 5K-25K, 1-6 months, 0.60% interest
    public static final double EMERGENCY_INTEREST_RATE = 0.006; // 0.60%
    public static final double EMERGENCY_MIN_AMOUNT = 5000;
    public static final double EMERGENCY_MAX_AMOUNT = 25000;
    public static final int EMERGENCY_MIN_MONTHS = 1;
    public static final int EMERGENCY_MAX_MONTHS = 6;

    // Special Loan: 50K-100K, 5+ years members only, 1-18 months
    public static final double SPECIAL_MIN_AMOUNT = 50000;
    public static final double SPECIAL_MAX_AMOUNT = 100000;
    public static final int SPECIAL_MIN_MONTHS = 1;
    public static final int SPECIAL_MAX_MONTHS = 18;
    public static final int SPECIAL_MIN_YEARS_SERVICE = 5;

    // Regular Loan: Salary × 2.5, 1-24 months, variable rates
    public static final double REGULAR_SALARY_MULTIPLIER = 2.5;
    public static final int REGULAR_MIN_MONTHS = 1;
    public static final int REGULAR_MAX_MONTHS = 24;

    // Service charge percentages
    public static final double SERVICE_CHARGE_EMERGENCY = 0.01;  // 1%
    public static final double SERVICE_CHARGE_SPECIAL = 0.015;   // 1.5%
    public static final double SERVICE_CHARGE_REGULAR = 0.02;    // 2%

    // Calculate service charge based on loan type
    public double calculateServiceCharge(double loanAmount, String loanType) {
        switch (loanType.toLowerCase()) {
            case "emergency":
                return loanAmount * SERVICE_CHARGE_EMERGENCY;
            case "special":
                return loanAmount * SERVICE_CHARGE_SPECIAL;
            case "regular":
                return loanAmount * SERVICE_CHARGE_REGULAR;
            default:
                return 0;
        }
    }

    // Calculate total interest
    public double calculateInterest(double loanAmount, double monthlyInterestRate, int months) {
        return loanAmount * monthlyInterestRate * months;
    }

    // Calculate total loan amount (principal + interest + service charge)
    public double calculateTotalAmount(double loanAmount, double serviceCharge, double interest) {
        return loanAmount + serviceCharge + interest;
    }

    // Calculate monthly amortization
    public double calculateMonthlyAmortization(double totalAmount, int months) {
        if (months <= 0) return 0;
        return totalAmount / months;
    }

    // Calculate take home amount (loan amount minus service charge)
    public double calculateTakeHomeAmount(double loanAmount, double serviceCharge) {
        return loanAmount - serviceCharge;
    }

    // Get interest rate for special loan based on months
    public double getSpecialLoanInterestRate(int months) {
        if (months <= 6) {
            return 0.008; // 0.8% for 1-6 months
        } else if (months <= 12) {
            return 0.009; // 0.9% for 7-12 months
        } else {
            return 0.010; // 1.0% for 13-18 months
        }
    }

    // Get interest rate for regular loan based on months
    public double getRegularLoanInterestRate(int months) {
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

    // Validate emergency loan
    public boolean validateEmergencyLoan(double amount, int months) {
        return amount >= EMERGENCY_MIN_AMOUNT &&
                amount <= EMERGENCY_MAX_AMOUNT &&
                months >= EMERGENCY_MIN_MONTHS &&
                months <= EMERGENCY_MAX_MONTHS;
    }

    // Validate special loan
    public boolean validateSpecialLoan(double amount, int months, int yearsOfService) {
        return amount >= SPECIAL_MIN_AMOUNT &&
                amount <= SPECIAL_MAX_AMOUNT &&
                months >= SPECIAL_MIN_MONTHS &&
                months <= SPECIAL_MAX_MONTHS &&
                yearsOfService >= SPECIAL_MIN_YEARS_SERVICE;
    }

    // Validate regular loan
    public boolean validateRegularLoan(double amount, int months, double basicSalary) {
        double maxAmount = basicSalary * REGULAR_SALARY_MULTIPLIER;
        return amount > 0 &&
                amount <= maxAmount &&
                months >= REGULAR_MIN_MONTHS &&
                months <= REGULAR_MAX_MONTHS;
    }

    // Calculate maximum regular loan amount based on salary
    public double calculateMaxRegularLoan(double basicSalary) {
        return basicSalary * REGULAR_SALARY_MULTIPLIER;
    }

    // Calculate years of service from date hired
    public int calculateYearsOfService(String dateHired) {
        try {
            // Assuming date format: yyyy-MM-dd
            String[] parts = dateHired.split("-");
            if (parts.length < 1) return 0;

            int hireYear = Integer.parseInt(parts[0]);
            int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);

            return currentYear - hireYear;
        } catch (Exception e) {
            return 0;
        }
    }

    // Format currency
    public String formatCurrency(double amount) {
        return String.format("₱%,.2f", amount);
    }
}