package ru.mellman.conv3rter;

import android.os.Parcel;
import android.os.Parcelable;

public class CoursesOfCurrency implements Parcelable {

    private String charCode;
    private String name;
    private int nominal;
    private Double courseValue;
    private Double previous;
    {
        charCode = "---";
        name = "Undefined";
        nominal = 0;
        courseValue = 0.0;
        previous = 0.0;
    }
    public CoursesOfCurrency(){

    }

    public CoursesOfCurrency(String charCode, String name, int nominal, Double value, Double previous){
        super();
        this.charCode = charCode;
        this.name = name;
        this.nominal = nominal;
        this.courseValue = value;
        this.previous = previous;
    }

    protected CoursesOfCurrency(Parcel in){
        charCode = in.readString();
        name = in.readString();
        nominal = in.readInt();
        courseValue = in.readDouble();
        previous = in.readDouble();
    }

    public static final Creator<CoursesOfCurrency> CREATOR = new Creator<CoursesOfCurrency>() {
        @Override
        public CoursesOfCurrency createFromParcel(Parcel in) {
            return new CoursesOfCurrency(in);
        }

        @Override
        public CoursesOfCurrency[] newArray(int size) {
            return new CoursesOfCurrency[size];
        }
    };

    public String getCharCode(){
        return charCode;
    }
    public void setCharCode(String charCode){
        this.charCode = charCode;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public int getNominal(){
        return nominal;
    }
    public void setNominal(int nominal){
        this.nominal = nominal;
    }
    public Double getCourseValue(){
        return courseValue;
    }
    public void setCourseValue(Double courseValue){
        this.courseValue = courseValue;
    }
    public Double getPrevious(){
        return previous;
    }
    public void setPrevious(Double previous){
        this.previous = previous;
    }
    public Double getCourseByCurrentCurrency(Double valueMoney){
        valueMoney = valueMoney/(this.courseValue/this.nominal);
        return valueMoney;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(charCode);
        dest.writeString(name);
        dest.writeInt(nominal);
        dest.writeDouble(courseValue);
        dest.writeDouble(previous);
    }
}
