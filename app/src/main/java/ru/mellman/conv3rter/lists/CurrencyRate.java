package ru.mellman.conv3rter.lists;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;

public class CurrencyRate implements Parcelable {

    private String charCode;
    private Double rate;

    {
        charCode = "---";
        rate = 0.0;
    }
    public CurrencyRate(){

    }

    public CurrencyRate(String charCode, Double rate){
        super();
        this.charCode = charCode;
        this.rate = rate;
    }

    protected CurrencyRate(Parcel in){
        charCode = in.readString();
        rate = in.readDouble();
    }

    public static final Creator<CurrencyRate> CREATOR = new Creator<CurrencyRate>() {
        @Override
        public CurrencyRate createFromParcel(Parcel in) {
            return new CurrencyRate(in);
        }

        @Override
        public CurrencyRate[] newArray(int size) {
            return new CurrencyRate[size];
        }
    };

    public String getCharCode(){
        return charCode;
    }
    public void setCharCode(String charCode){
        this.charCode = charCode;
    }

    public Double getRate(){
        return rate;
    }
    public void setCourseValue(Double rate){
        this.rate = rate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(charCode);
        dest.writeDouble(rate);
    }
}
