package com.example.loansystemcalculator.models;

public class User {
    private int userId;
    private String employeeId;
    private String firstName;
    private String lastName;
    private String dateHired;
    private String email;
    private double basicSalary;

    // Getters and setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getDateHired() { return dateHired; }
    public void setDateHired(String dateHired) { this.dateHired = dateHired; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public double getBasicSalary() { return basicSalary; }
    public void setBasicSalary(double basicSalary) { this.basicSalary = basicSalary; }
}