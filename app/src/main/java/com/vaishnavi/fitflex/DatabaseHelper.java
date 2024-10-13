package com.vaishnavi.fitflex;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "fitflex.db";
    public static final int DATABASE_VERSION = 2;  // Keep the version as-is or increment if needed

    // Table and columns for user data (user_details table)
    public static final String TABLE_NAME_USER_DETAILS = "user_details";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_AGE = "age";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_DOB = "dob";
    public static final String COLUMN_START_DATE = "start_date";
    public static final String COLUMN_TARGET_DATE = "target_date";
    public static final String COLUMN_WEIGHT = "weight";
    public static final String COLUMN_TARGET_WEIGHT = "target_weight";
    public static final String COLUMN_HEIGHT = "height";
    public static final String COLUMN_BMI = "bmi";
    public static final String COLUMN_TOTAL_DAYS = "total_days";

    // New table and columns for daily progress (daily_progress table)
    public static final String TABLE_NAME_DAILY_PROGRESS = "daily_progress";
    public static final String COLUMN_PROGRESS_DATE = "date";
    public static final String COLUMN_PROGRESS_WEIGHT = "weight";
    public static final String COLUMN_PROGRESS_HEIGHT = "height";
    public static final String COLUMN_PROGRESS_BMI = "bmi";
    public static final String COLUMN_PROGRESS_IMAGE = "image";  // Store images as BLOBs

    // SQL to create user_details table
    private static final String CREATE_TABLE_USER_DETAILS =
            "CREATE TABLE " + TABLE_NAME_USER_DETAILS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_AGE + " INTEGER, " +
                    COLUMN_GENDER + " TEXT, " +
                    COLUMN_DOB + " TEXT, " +
                    COLUMN_START_DATE + " TEXT, " +
                    COLUMN_TARGET_DATE + " TEXT, " +
                    COLUMN_WEIGHT + " REAL, " +
                    COLUMN_TARGET_WEIGHT + " REAL, " +
                    COLUMN_HEIGHT + " REAL, " +
                    COLUMN_BMI + " REAL, " +
                    COLUMN_TOTAL_DAYS + " INTEGER" +
                    ");";

    // SQL to create daily_progress table
    private static final String CREATE_TABLE_DAILY_PROGRESS =
            "CREATE TABLE " + TABLE_NAME_DAILY_PROGRESS + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +  // Primary key for daily progress
                    COLUMN_PROGRESS_DATE + " TEXT, " +
                    COLUMN_PROGRESS_WEIGHT + " REAL, " +
                    COLUMN_PROGRESS_HEIGHT + " REAL, " +
                    COLUMN_PROGRESS_BMI + " REAL, " +
                    COLUMN_PROGRESS_IMAGE + " BLOB" +  // Store image as a blob
                    ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DatabaseHelper", "Creating tables");
        // Create both tables
        db.execSQL(CREATE_TABLE_USER_DETAILS);
        db.execSQL(CREATE_TABLE_DAILY_PROGRESS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DatabaseHelper", "Upgrading database from version " + oldVersion + " to " + newVersion);
        // Drop both tables if they exist, and recreate them
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_USER_DETAILS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_DAILY_PROGRESS);
        onCreate(db);  // Recreate the tables
    }

    // Method to save user data into user_details table
    // Method to save user data into user_details table
    public void saveUserData(String name, int age, String gender, String dob, String startDate, String targetDate,
                             float weight, float height, float targetWeight, float bmi, long totalDays) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_AGE, age);
        values.put(COLUMN_GENDER, gender);
        values.put(COLUMN_DOB, dob);
        values.put(COLUMN_START_DATE, startDate);
        values.put(COLUMN_TARGET_DATE, targetDate);
        values.put(COLUMN_WEIGHT, weight);
        values.put(COLUMN_TARGET_WEIGHT, targetWeight);
        values.put(COLUMN_HEIGHT, height);
        values.put(COLUMN_BMI, bmi);
        values.put(COLUMN_TOTAL_DAYS, totalDays);

        db.insert(TABLE_NAME_USER_DETAILS, null, values);
        db.close();
    }


    // Method to save daily progress into daily_progress table
    public void saveProgressData(String date, float weight, float height, float bmi, byte[] image) {

        Log.d("DatabaseHelper", "Saving daily progress for date: " + date);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROGRESS_DATE, date);
        values.put(COLUMN_PROGRESS_WEIGHT, weight);
        values.put(COLUMN_PROGRESS_HEIGHT, height);
        values.put(COLUMN_PROGRESS_BMI, bmi);

        // If the image is provided, store it as a BLOB
        if (image != null) {
            values.put(COLUMN_PROGRESS_IMAGE, image);
        }

        db.insert(TABLE_NAME_DAILY_PROGRESS, null, values);
        db.close();
    }
}
