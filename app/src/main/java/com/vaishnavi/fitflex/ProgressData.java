package com.vaishnavi.fitflex;

import android.graphics.Bitmap;

public class ProgressData {
    private String date;
    private float weight;
    private float bmi;
    private Bitmap image;

    // Constructor
    public ProgressData(String date, float weight, float bmi, Bitmap image) {
        this.date = date;
        this.weight = weight;
        this.bmi = bmi;
        this.image = image;
    }

    // Getter and Setter methods

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getBmi() {
        return bmi;
    }

    public void setBmi(float bmi) {
        this.bmi = bmi;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
