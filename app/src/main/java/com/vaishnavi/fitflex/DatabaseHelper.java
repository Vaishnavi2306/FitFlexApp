package com.vaishnavi.fitflex;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Information
    private static final String DATABASE_NAME = "fitflex.db";
    private static final int DATABASE_VERSION = 1;

    // Table Name
    public static final String TABLE_NAME = "users";

    // Column Names
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_AGE = "age";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_DOB = "dob";
    public static final String COLUMN_START_DATE = "start_date";
    public static final String COLUMN_TARGET_DATE = "target_date";
    public static final String COLUMN_WEIGHT = "weight";
    public static final String COLUMN_HEIGHT = "height";
    public static final String COLUMN_BMI = "bmi";
    public static final String COLUMN_TOTAL_DAYS = "total_days";
    public static final String COLUMN_TARGET_WEIGHT = "target_weight";

    // Create Table SQL Statement
    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT, " +
            COLUMN_AGE + " INTEGER, " +
            COLUMN_GENDER + " TEXT, " +
            COLUMN_DOB + " TEXT, " +
            COLUMN_START_DATE + " TEXT, " +
            COLUMN_TARGET_DATE + " TEXT, " +
            COLUMN_WEIGHT + " REAL, " +
            COLUMN_HEIGHT + " REAL, " +
            COLUMN_BMI + " REAL, " +
            COLUMN_TOTAL_DAYS + " INTEGER, " +
            COLUMN_TARGET_WEIGHT + " REAL" +
            ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Method to save user data
    public long saveUserData(String name, int age, String gender, String dob, String startDate, String targetDate, float weight, float height, float bmi, int totalDays, float targetWeight) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_AGE, age);
        values.put(COLUMN_GENDER, gender);
        values.put(COLUMN_DOB, dob);
        values.put(COLUMN_START_DATE, startDate);
        values.put(COLUMN_TARGET_DATE, targetDate);
        values.put(COLUMN_WEIGHT, weight);
        values.put(COLUMN_HEIGHT, height);
        values.put(COLUMN_BMI, bmi);
        values.put(COLUMN_TOTAL_DAYS, totalDays);
        values.put(COLUMN_TARGET_WEIGHT, targetWeight);

        long newRowId = db.insert(TABLE_NAME, null, values);
        db.close();
        return newRowId;
    }

    // Method to update user data
    public int updateUserData(int id, String name, int age, String gender, String dob, String startDate, String targetDate, float weight, float height, float bmi, int totalDays, float targetWeight) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_AGE, age);
        values.put(COLUMN_GENDER, gender);
        values.put(COLUMN_DOB, dob);
        values.put(COLUMN_START_DATE, startDate);
        values.put(COLUMN_TARGET_DATE, targetDate);
        values.put(COLUMN_WEIGHT, weight);
        values.put(COLUMN_HEIGHT, height);
        values.put(COLUMN_BMI, bmi);
        values.put(COLUMN_TOTAL_DAYS, totalDays);
        values.put(COLUMN_TARGET_WEIGHT, targetWeight);

        int rowsAffected = db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected;
    }

    // Method to delete user data
    public void deleteUserData(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Method to get user data by ID
    public Cursor getUserData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_NAME, null, COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
    }

    // Method to get all user data
    public Cursor getAllUserData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_NAME, null, null, null, null, null, null);
    }
}
