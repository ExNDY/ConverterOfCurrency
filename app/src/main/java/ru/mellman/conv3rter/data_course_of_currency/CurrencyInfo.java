package ru.mellman.conv3rter.data_course_of_currency;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

public class CurrencyInfo implements Parcelable {
    private String charCode;
    private String name;
    private String symbolCurrency;
    private int decimalDigits;


    public CurrencyInfo(String charCode, String name, String symbolCurrency, int decimalDigits) {
        super();
        this.charCode = charCode;
        this.name = name;
        this.symbolCurrency = symbolCurrency;
        this.decimalDigits = decimalDigits;
    }


    public String getCharCode() {
        return charCode;
    }

    public void setCharCode(String charCode) {
        this.charCode = charCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbolCurrency() {
        return symbolCurrency;
    }

    public void setSymbolCurrency(String symbolCurrency) {
        this.symbolCurrency = symbolCurrency;
    }

    public int getDecimalDigits() {
        return decimalDigits;
    }

    public void setDecimalDigits(int decimalDigits) {
        this.decimalDigits = decimalDigits;
    }


    protected CurrencyInfo(@NonNull Parcel in) {
        charCode = in.readString();
        name = in.readString();
        symbolCurrency = in.readString();
        decimalDigits = in.readInt();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(charCode);
        dest.writeString(name);
        dest.writeString(symbolCurrency);
        dest.writeInt(decimalDigits);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CurrencyInfo> CREATOR = new Creator<CurrencyInfo>() {
        @NonNull
        @Contract("_ -> new")
        @Override
        public CurrencyInfo createFromParcel(Parcel in) {
            return new CurrencyInfo(in);
        }

        @NonNull
        @Contract(value = "_ -> new", pure = true)
        @Override
        public CurrencyInfo[] newArray(int size) {
            return new CurrencyInfo[size];
        }
    };
}
