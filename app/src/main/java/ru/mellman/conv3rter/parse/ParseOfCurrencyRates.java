package ru.mellman.conv3rter.parse;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Callable;

import ru.mellman.conv3rter.activity.MainActivity;
import ru.mellman.conv3rter.lists.CurrencyRate;

public class ParseOfCurrencyRates implements Callable<ArrayList<CurrencyRate>> {
    private final String JSONObjectString;

    public ParseOfCurrencyRates(String JSONObjectString) {
        this.JSONObjectString = JSONObjectString;
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
            JSONObject allRates = (JSONObject) RatesObj.get("rates");
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