package ru.mellman.conv3rter.data_course_of_currency;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Callable;

import ru.mellman.conv3rter.activity.MainActivity;

public class CurrencyOfRates implements Callable<ArrayList<CurrencyRate>> {
    private final String datetime;
    private final String JSONObjectString;

    public CurrencyOfRates(String JSONObjectString, String dateOfRate) {
        this.JSONObjectString = JSONObjectString;
        this.datetime = dateOfRate;
    }

    @NonNull
    private static CurrencyRate getRate(@NonNull org.json.JSONObject response, String key) throws JSONException {
        Double CourseValue = response.getDouble(key);
        return new CurrencyRate(key, CourseValue);
    }

    @Override
    public ArrayList<CurrencyRate> call() throws Exception {
        ArrayList<CurrencyRate> currencyRate = new ArrayList<>();
        try {
            JSONObject RatesObj = new JSONObject(JSONObjectString);
            RatesObj = (JSONObject) RatesObj.get("rates");
            JSONObject allRates = (JSONObject) RatesObj.get(datetime);
            Iterator<String> keys = allRates.keys();
            while (keys.hasNext()) {
                currencyRate.add(getRate(allRates, keys.next()));
            }
        } catch (final JSONException e) {
            Log.e(MainActivity.class.getSimpleName(), "Json parsing error: " + e.getMessage());
        }
        return currencyRate;
    }
}