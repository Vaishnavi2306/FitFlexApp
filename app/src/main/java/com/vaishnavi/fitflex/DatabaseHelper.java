package com.vaishnavi.fitflex;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "fitflex.db";
    public static final int DATABASE_VERSION = 5; // Incremented version to ensure onCreate is called

    // Table for user details
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

    // Table for daily progress
    public static final String TABLE_NAME_DAILY_PROGRESS = "daily_progress";
    public static final String COLUMN_PROGRESS_DATE = "date";
    public static final String COLUMN_PROGRESS_WEIGHT = "weight";
    public static final String COLUMN_PROGRESS_HEIGHT = "height";
    public static final String COLUMN_PROGRESS_BMI = "bmi";
    public static final String COLUMN_PROGRESS_IMAGE = "image"; // You can use a BLOB for images

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
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PROGRESS_DATE + " TEXT, " +
                    COLUMN_PROGRESS_WEIGHT + " REAL, " +
                    COLUMN_PROGRESS_HEIGHT + " REAL, " +
                    COLUMN_PROGRESS_BMI + " REAL, " +
                    COLUMN_PROGRESS_IMAGE + " BLOB" +
                    ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DatabaseHelper", "Creating tables");
        db.execSQL(CREATE_TABLE_USER_DETAILS); // Create user_details table
        db.execSQL(CREATE_TABLE_DAILY_PROGRESS); // Create daily_progress table
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DatabaseHelper", "Upgrading database from version " + oldVersion + " to " + newVersion);
        // Drop older versions of both tables if they exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_USER_DETAILS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_DAILY_PROGRESS);
        onCreate(db); // Recreate the tables
    }

    // Method to save user data into the user_details table
    public void saveUserData(String name, int age, String gender, String dob, String startDate, String targetDate,
                             float weight, float height, float targetWeight, float bmi, long totalDays) {

        Log.d("DatabaseHelper", "Saving user data for: " + name);

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


    public void saveProgressData(String date, float weight, float height, float bmi, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_PROGRESS_DATE, date);
            values.put(COLUMN_PROGRESS_WEIGHT, weight);
            values.put(COLUMN_PROGRESS_HEIGHT, height);
            values.put(COLUMN_PROGRESS_BMI, bmi);
            values.put(COLUMN_PROGRESS_IMAGE, image);

            db.insert(TABLE_NAME_DAILY_PROGRESS, null, values);
        } finally {
                db.close();
        }
    }



    // Overloaded method to save daily progress without an image
    public void saveProgressData(String date, float weight, float height, float bmi) {
        saveProgressData(date, weight, height, bmi, null); // Call the other method with null for the image
    }

    public void updateUserData(String name, int age, String gender, String dob, String startDate, String targetDate,
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

        // Update the user details in the user_details table
        // Assuming you have only one user, update the first row
        db.update(TABLE_NAME_USER_DETAILS, values, COLUMN_ID + "= ?", new String[]{"1"});  // ID is 1 for the first user

        db.close();
    }

    public void deleteUserData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME_USER_DETAILS, null, null);  // Delete all rows in user_details
        db.close();
    }
    public void deleteAllDailyProgress() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME_DAILY_PROGRESS, null, null);  // Delete all rows in daily_progress
        db.close();
    }
}
