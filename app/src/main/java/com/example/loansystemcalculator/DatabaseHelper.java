package com.example.loansystemcalculator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "loan_db";
    private static final int DB_VERSION = 2;

    // Users table
    private static final String TABLE_USERS = "users";
    private static final String COL_USER_ID = "user_id";
    private static final String COL_EMPLOYEE_ID = "employee_id";
    private static final String COL_FIRST_NAME = "first_name";
    private static final String COL_MIDDLE_NAME = "middle_name";
    private static final String COL_LAST_NAME = "last_name";
    private static final String COL_EMAIL = "email";
    private static final String COL_PASSWORD = "password";
    private static final String COL_DATE_HIRED = "date_hired";
    private static final String COL_BASIC_SALARY = "basic_salary";
    private static final String COL_IS_ADMIN = "is_admin";

    // Loans table
    private static final String TABLE_LOANS = "loans";
    private static final String COL_LOAN_ID = "loan_id";
    private static final String COL_LOAN_TYPE = "loan_type";
    private static final String COL_LOAN_AMOUNT = "loan_amount";
    private static final String COL_MONTHS_TO_PAY = "months_to_pay";
    private static final String COL_INTEREST_RATE = "interest_rate";
    private static final String COL_SERVICE_CHARGE = "service_charge";
    private static final String COL_TOTAL_AMOUNT = "total_amount";
    private static final String COL_MONTHLY_AMORTIZATION = "monthly_amortization";
    private static final String COL_TAKE_HOME_AMOUNT = "take_home_amount";
    private static final String COL_APPLICATION_DATE = "application_date";
    private static final String COL_STATUS = "status";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_EMPLOYEE_ID + " TEXT UNIQUE," +
                COL_FIRST_NAME + " TEXT," +
                COL_MIDDLE_NAME + " TEXT," +
                COL_LAST_NAME + " TEXT," +
                COL_EMAIL + " TEXT UNIQUE," +
                COL_PASSWORD + " TEXT," +
                COL_DATE_HIRED + " TEXT," +
                COL_BASIC_SALARY + " REAL," +
                COL_IS_ADMIN + " INTEGER DEFAULT 0)";
        db.execSQL(createUsersTable);

        // Create loans table
        String createLoansTable = "CREATE TABLE " + TABLE_LOANS + " (" +
                COL_LOAN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_USER_ID + " INTEGER," +
                COL_LOAN_TYPE + " TEXT," +
                COL_LOAN_AMOUNT + " REAL," +
                COL_MONTHS_TO_PAY + " INTEGER," +
                COL_INTEREST_RATE + " REAL," +
                COL_SERVICE_CHARGE + " REAL," +
                COL_TOTAL_AMOUNT + " REAL," +
                COL_MONTHLY_AMORTIZATION + " REAL," +
                COL_TAKE_HOME_AMOUNT + " REAL," +
                COL_BASIC_SALARY + " REAL," +
                COL_APPLICATION_DATE + " TEXT," +
                COL_STATUS + " TEXT DEFAULT 'Pending'," +
                "FOREIGN KEY(" + COL_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "))";
        db.execSQL(createLoansTable);

        // Insert default admin account
        createAdminAccount(db);
    }

    private void createAdminAccount(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(COL_EMPLOYEE_ID, "ADMIN001");
        values.put(COL_FIRST_NAME, "Admin");
        values.put(COL_LAST_NAME, "User");
        values.put(COL_EMAIL, "admin@abc.com");
        values.put(COL_PASSWORD, "admin");
        values.put(COL_DATE_HIRED, "2020-01-01");
        values.put(COL_BASIC_SALARY, 50000.00);
        values.put(COL_IS_ADMIN, 1);

        db.insert(TABLE_USERS, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOANS);
        onCreate(db);
    }

    // Create admin account if not exists
    public void createAdminIfNotExists() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COL_EMAIL + " = 'admin@abc.com'", null);

        if (cursor.getCount() == 0) {
            createAdminAccount(db);
        }
        cursor.close();
    }

    // Check user login
    public boolean checkUserLogin(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_USERS +
                        " WHERE " + COL_EMAIL + " = ? AND " + COL_PASSWORD + " = ? AND " + COL_IS_ADMIN + " = 0",
                new String[]{email, password}
        );

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Register new user
    public long registerUser(String firstName, String middleName, String lastName, String email, String password,
                             String dateHired, String employeeId, double basicSalary) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_FIRST_NAME, firstName);
        values.put(COL_MIDDLE_NAME, middleName);
        values.put(COL_LAST_NAME, lastName);
        values.put(COL_EMAIL, email);
        values.put(COL_PASSWORD, password);
        values.put(COL_DATE_HIRED, dateHired);
        values.put(COL_EMPLOYEE_ID, employeeId);
        values.put(COL_BASIC_SALARY, basicSalary);
        values.put(COL_IS_ADMIN, 0);

        return db.insert(TABLE_USERS, null, values);
    }

    // Check if email already exists
    public boolean checkEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_USERS + " WHERE " + COL_EMAIL + " = ?",
                new String[]{email}
        );

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Get user ID by email
    public int getUserIdByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COL_USER_ID + " FROM " + TABLE_USERS +
                        " WHERE " + COL_EMAIL + " = ?",
                new String[]{email}
        );

        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(0);
        }
        cursor.close();
        return userId;
    }

    // Get user by email
    public Cursor getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_USERS +
                        " WHERE " + COL_EMAIL + " = ?",
                new String[]{email}
        );
    }

    // Get user details by ID (returns User object) (INIBA NI ANA)

    public String getUserLastName(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT last_name FROM users WHERE user_id = ?",
                new String[]{String.valueOf(userId)}
        );

        String lastName = "";
        if (cursor.moveToFirst()) {
            lastName = cursor.getString(0);
        }
        cursor.close();
        return lastName;
    }

    public User getUserDetails(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_USERS +
                        " WHERE " + COL_USER_ID + " = ?",
                new String[]{String.valueOf(userId)}
        );

        User user = null;
        if (cursor.moveToFirst()) {
            user = new User();
            user.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID)));
            user.setEmployeeId(cursor.getString(cursor.getColumnIndexOrThrow(COL_EMPLOYEE_ID)));
            user.setFirstName(cursor.getString(cursor.getColumnIndexOrThrow(COL_FIRST_NAME)));
            user.setMiddleName(cursor.getString(cursor.getColumnIndexOrThrow(COL_MIDDLE_NAME)));
            user.setLastName(cursor.getString(cursor.getColumnIndexOrThrow(COL_LAST_NAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL)));
            user.setDateHired(cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE_HIRED)));
            user.setBasicSalary(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_BASIC_SALARY)));
        }
        cursor.close();
        return user;
    }


    // Get user's basic salary
    public double getUserBasicSalary(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COL_BASIC_SALARY + " FROM " + TABLE_USERS +
                        " WHERE " + COL_USER_ID + " = ?",
                new String[]{String.valueOf(userId)}
        );

        double basic_salary = 0;
        if (cursor.moveToFirst()) {
            basic_salary = cursor.getDouble(0);
        }
        cursor.close();
        return basic_salary;
    }

    // Get user's first name
    public String getUserFirstName(int userId) { //change into ID?
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COL_FIRST_NAME + " FROM " + TABLE_USERS +
                        " WHERE " + COL_USER_ID + " = ?",
                new String[]{String.valueOf(userId)}
        );

        String firstName = "";
        if (cursor.moveToFirst()) {
            firstName = cursor.getString(0);
        }
        cursor.close();
        return firstName;
    }

    // Check if user has pending loan of specific type
    public boolean hasPendingLoan(int userId, String loanType) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_LOANS +
                        " WHERE " + COL_USER_ID + " = ? AND " +
                        COL_LOAN_TYPE + " = ? AND " +
                        COL_STATUS + " = 'Pending'",
                new String[]{String.valueOf(userId), loanType}
        );

        boolean hasLoan = cursor.getCount() > 0;
        cursor.close();
        return hasLoan;
    }

    // Apply for loan
    public boolean applyForLoan(int userId, String loanType, double loanAmount,
                                int months, double interestRate, double serviceCharge,
                                double totalAmount, double monthlyAmortization,
                                double takeHomeAmount, double basicSalary,
                                String applicationDate) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_USER_ID, userId);
        values.put(COL_LOAN_TYPE, loanType);
        values.put(COL_LOAN_AMOUNT, loanAmount);
        values.put(COL_MONTHS_TO_PAY, months);
        values.put(COL_INTEREST_RATE, interestRate);
        values.put(COL_SERVICE_CHARGE, serviceCharge);
        values.put(COL_TOTAL_AMOUNT, totalAmount);
        values.put(COL_MONTHLY_AMORTIZATION, monthlyAmortization);
        values.put(COL_TAKE_HOME_AMOUNT, takeHomeAmount);
        values.put(COL_BASIC_SALARY, basicSalary);
        values.put(COL_APPLICATION_DATE, applicationDate);
        values.put(COL_STATUS, "Pending");

        long result = db.insert(TABLE_LOANS, null, values);
        return result != -1;
    }

    // Get user's loans
    public Cursor getUserLoans(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_LOANS +
                        " WHERE " + COL_USER_ID + " = ? ORDER BY " + COL_APPLICATION_DATE + " DESC",
                new String[]{String.valueOf(userId)}
        );
    }

    // Get pending loan count
    public int getPendingLoanCount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_LOANS +
                        " WHERE " + COL_USER_ID + " = ? AND " + COL_STATUS + " = 'Pending'",
                new String[]{String.valueOf(userId)}
        );

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    // Get approved loan count
    public int getApprovedLoanCount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_LOANS +
                        " WHERE " + COL_USER_ID + " = ? AND " + COL_STATUS + " = 'Approved'",
                new String[]{String.valueOf(userId)}
        );

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    // Close database connection
    @Override
    public synchronized void close() {
        super.close();
    }

    public static class User {
        private int userId;
        private String employeeId;
        private String firstName;
        private String middleName;
        private String lastName;
        private String email;
        private String dateHired;
        private double basicSalary;

        // Getters and setters
        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getEmployeeId() {
            return employeeId;
        }

        public void setEmployeeId(String employeeId) {
            this.employeeId = employeeId;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getMiddleName() {
            return middleName;
        }

        public void setMiddleName(String firstName) {
            this.middleName = middleName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getDateHired() {
            return dateHired;
        }

        public void setDateHired(String dateHired) {
            this.dateHired = dateHired;
        }

        public double getBasicSalary() {
            return basicSalary;
        }

        public void setBasicSalary(double basicSalary) {
            this.basicSalary = basicSalary;
        }
    }

    // ADMIN SIDE
    public ArrayList<AdminLoan> getAllLoans(String filterStatus) {
        ArrayList<AdminLoan> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query =
                "SELECT l.loan_id, l.user_id, l.application_date, " + "u.first_name || ' ' || u.middle_name || ' ' " +
                        "|| u.last_name AS full_name, " + "u.email AS client_email, u.employee_id," +
                        "l.basic_salary, l.loan_type, l.loan_amount, l.status, u.date_hired " +
                        "FROM loans l " +
                        "JOIN users u ON l.user_id = u.user_id ";

        if (!filterStatus.equalsIgnoreCase("All")) {
            query += "WHERE l.status = '" + filterStatus + "' ";
        }

        query += "ORDER BY l.application_date DESC";

        Cursor cursor = db.rawQuery(query, null);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        if (cursor.moveToFirst()) {
            do {
                AdminLoan loan = new AdminLoan();
                loan.loanId = cursor.getInt(cursor.getColumnIndexOrThrow("loan_id"));
                loan.applicationDate = cursor.getString(cursor.getColumnIndexOrThrow("application_date"));
                loan.employeeId = cursor.getString(cursor.getColumnIndexOrThrow("employee_id"));
                loan.fullName = cursor.getString(cursor.getColumnIndexOrThrow("full_name"));
                loan.clientEmail = cursor.getString(cursor.getColumnIndexOrThrow("client_email"));
                loan.basicSalary = cursor.getDouble(cursor.getColumnIndexOrThrow("basic_salary"));
                loan.loanType = cursor.getString(cursor.getColumnIndexOrThrow("loan_type"));
                loan.loanAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("loan_amount"));
                loan.status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                loan.dateHired = cursor.getString(cursor.getColumnIndexOrThrow("date_hired"));

                // Calculate years of service (termYears)
                try {
                    long hired = format.parse(loan.dateHired).getTime();
                    long applied = format.parse(loan.applicationDate).getTime();
                    double years = (applied - hired) / (1000.0 * 60 * 60 * 24 * 365);
                    loan.termYears = Math.floor(years);
                } catch (Exception e) {
                    loan.termYears = 0;
                }

                list.add(loan);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public void updateLoanStatus(int loanId, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", newStatus);
        db.update("loans", values, "loan_id = ?", new String[]{String.valueOf(loanId)});
        db.close();
    }
}