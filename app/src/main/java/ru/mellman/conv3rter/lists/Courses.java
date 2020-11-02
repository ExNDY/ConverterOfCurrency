package ru.mellman.conv3rter.lists;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Courses implements Parcelable {
    private String charCode;
    private String symbolCurrency;
    private String name;
    private Double courseValue;
    private Double previousValue;

    {
        charCode = "---";
        symbolCurrency = "-";
        name = "Undefined";
        courseValue = 0.0;
        previousValue = 0.0;
    }

    public Courses() {

    }

    public Courses(String charCode, String symbolCurrency, String name, Double value, Double previous) {
        super();
        this.charCode = charCode;
        this.name = name;
        this.symbolCurrency = symbolCurrency;
        this.courseValue = value;
        this.previousValue = previous;
    }

    protected Courses(Parcel in) {
        charCode = in.readString();
        symbolCurrency = in.readString();
        name = in.readString();
        courseValue = in.readDouble();
        previousValue = in.readDouble();
    }


    public static final Creator<Courses> CREATOR = new Creator<Courses>() {
        @Override
        public Courses createFromParcel(Parcel in) {
            return new Courses(in);
        }

        @Override
        public Courses[] newArray(int size) {
            return new Courses[size];
        }
    };

    public String getCharCode() {
        return charCode;
    }

    public void setCharCode(String charCode) {
        this.charCode = charCode;
    }

    public String getSymbolCurrency() {
        return symbolCurrency;
    }

    public void setSymbolCurrency(String symbolCurrency) {
        this.symbolCurrency = symbolCurrency;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getCourseValue() {
        return courseValue;
    }

    public void setCourseValue(Double courseValue) {
        this.courseValue = courseValue;
    }

    public Double getPreviousValue() {
        return previousValue;
    }

    public void setPreviousValue(Double previousValue) {
        this.previousValue = previousValue;
    }

    public Double getDifference() {
        return courseValue - previousValue;
    }

    public Double getPercentOfDifference() {
        return ((courseValue / previousValue) - 1) * 100;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(charCode);
        dest.writeString(symbolCurrency);
        dest.writeString(name);
        dest.writeDouble(courseValue);
        dest.writeDouble(previousValue);
    }
}

