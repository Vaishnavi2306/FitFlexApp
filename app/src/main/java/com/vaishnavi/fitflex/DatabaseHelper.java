package com.vaishnavi.fitflex;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "FitFlex.db"; // Name of the database
    private static final int DATABASE_VERSION = 1; // Database version

    // Table name
    private static final String TABLE_NAME = "UserData";

    // Column names
    private static final String COLUMN_ID = "id"; // Primary key
    private static final String COLUMN_NAME = "name"; // User's name
    private static final String COLUMN_AGE = "age"; // User's age
    private static final String COLUMN_GENDER = "gender"; // User's gender
    private static final String COLUMN_DOB = "dob"; // Date of birth
    private static final String COLUMN_START_DATE = "start_date"; // Start date
    private static final String COLUMN_TARGET_DATE = "target_date"; // Target date
    private static final String COLUMN_WEIGHT = "weight"; // User's current weight
    private static final String COLUMN_HEIGHT = "height"; // User's height
    private static final String COLUMN_TARGET_WEIGHT = "target_weight"; // User's target weight
    private static final String COLUMN_BMI = "bmi"; // BMI value
    private static final String COLUMN_TOTAL_DAYS = "total_days"; // Total days for the program

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d("DatabaseHelper", "DatabaseHelper constructor called.");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DatabaseHelper", "Creating database and tables");
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_AGE + " INTEGER, " +
                COLUMN_GENDER + " TEXT, " +
                COLUMN_DOB + " TEXT, " +
                COLUMN_START_DATE + " TEXT, " +
                COLUMN_TARGET_DATE + " TEXT, " +
                COLUMN_WEIGHT + " REAL, " +
                COLUMN_HEIGHT + " REAL, " +
                COLUMN_TARGET_WEIGHT + " REAL, " +
                COLUMN_BMI + " REAL, " +
                COLUMN_TOTAL_DAYS + " INTEGER" +
                ")";
        db.execSQL(createTableQuery); // Execute SQL to create table
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DatabaseHelper", "Upgrading database from version " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME); // Drop existing table
        onCreate(db); // Create new table
    }

    // Method to save user data
    public void saveData(String name, int age, String gender, String dob, String startDate, String targetDate,
                         float weight, float height, float targetWeight, float bmi, long totalDays) {
        SQLiteDatabase db = this.getWritableDatabase(); // Get writable database
        ContentValues values = new ContentValues(); // Create ContentValues to hold data

        // Put values into ContentValues
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_AGE, age);
        values.put(COLUMN_GENDER, gender);
        values.put(COLUMN_DOB, dob);
        values.put(COLUMN_START_DATE, startDate);
        values.put(COLUMN_TARGET_DATE, targetDate);
        values.put(COLUMN_WEIGHT, weight);
        values.put(COLUMN_HEIGHT, height);
        values.put(COLUMN_TARGET_WEIGHT, targetWeight);
        values.put(COLUMN_BMI, bmi);
        values.put(COLUMN_TOTAL_DAYS, totalDays);

        long result = db.insert(TABLE_NAME, null, values); // Insert data into the table
        if (result == -1) {
            Log.d("DatabaseHelper", "Data saving failed.");
        } else {
            Log.d("DatabaseHelper", "Data saved successfully with ID: " + result);
        }
        db.close(); // Close database connection
    }

    // Method to retrieve total days for a user
    public long getTotalDays(String name) {
        SQLiteDatabase db = this.getReadableDatabase(); // Get readable database
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_TOTAL_DAYS},
                COLUMN_NAME + "=?", new String[]{name}, null, null, null); // Query to get total days

        if (cursor != null && cursor.moveToFirst()) {
            long totalDays = cursor.getLong(0); // Retrieve total days
            cursor.close(); // Close cursor
            return totalDays; // Return total days
        }
        return 0; // Return 0 if no data found
    }

    // Method to calculate BMI
    public float calculateBMI(float weight, float height) {
        // BMI calculation formula: weight (kg) / height^2 (meters)
        return weight / (height * height);
    }

    // Method to get BMI for a user
    public float getBMI(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_BMI},
                COLUMN_NAME + "=?", new String[]{name}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            float bmi = cursor.getFloat(0); // Retrieve BMI
            cursor.close(); // Close cursor
            return bmi; // Return BMI
        }
        return 0; // Return 0 if no data found
    }

    // Method to get user data by name
    public Cursor getUserData(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_NAME, null, COLUMN_NAME + "=?", new String[]{name}, null, null, null);
    }

    // Method to update user data
    public void updateUserData(String name, int age, String gender, String dob, String startDate, String targetDate,
                               float weight, float height, float targetWeight, float bmi, long totalDays) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_AGE, age);
        values.put(COLUMN_GENDER, gender);
        values.put(COLUMN_DOB, dob);
        values.put(COLUMN_START_DATE, startDate);
        values.put(COLUMN_TARGET_DATE, targetDate);
        values.put(COLUMN_WEIGHT, weight);
        values.put(COLUMN_HEIGHT, height);
        values.put(COLUMN_TARGET_WEIGHT, targetWeight);
        values.put(COLUMN_BMI, bmi);
        values.put(COLUMN_TOTAL_DAYS, totalDays);

        db.update(TABLE_NAME, values, COLUMN_NAME + "=?", new String[]{name});
        db.close();
    }

    // Method to delete user data
    public void deleteUserData(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_NAME + "=?", new String[]{name});
        db.close();
    }
}
