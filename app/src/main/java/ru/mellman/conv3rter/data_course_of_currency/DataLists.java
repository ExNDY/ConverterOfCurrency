package ru.mellman.conv3rter.data_course_of_currency;

import java.util.ArrayList;
import java.util.HashMap;

public class DataLists {
    private final ArrayList<CurrencyRate> yesterdayRateList;
    private final ArrayList<CurrencyRate> currentRateList;


    private final HashMap<String, CurrencyInfo> currencyInfo;

    public DataLists(ArrayList<CurrencyRate> yesterdayRateList, ArrayList<CurrencyRate> currentRateList, HashMap<String, CurrencyInfo> currencyInfo) {
        this.yesterdayRateList = yesterdayRateList;
        this.currentRateList = currentRateList;
        this.currencyInfo = currencyInfo;
    }

    public ArrayList<CurrencyRate> getYesterdayRateList() {
        return yesterdayRateList;
    }

    public ArrayList<CurrencyRate> getCurrentRateList() {
        return currentRateList;
    }

    public HashMap<String, CurrencyInfo> getCurrencyInfo() {
        return currencyInfo;
    }
}
