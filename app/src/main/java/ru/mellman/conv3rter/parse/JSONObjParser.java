package ru.mellman.conv3rter.parse;


import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import ru.mellman.conv3rter.activity.MainActivity;
import ru.mellman.conv3rter.lists.CurrencyRate;

public class JSONObjParser {


    @NonNull
    private static CurrencyRate getRate(@NonNull JSONObject response, String key) throws JSONException {
        Double CourseValue = response.getDouble(key);
        return new CurrencyRate(key, CourseValue);
    }


    public static ArrayList<CurrencyRate> parseJSONToRateList(String jSONObj) {
        ArrayList<CurrencyRate> rateList = new ArrayList<>();
        try {
            JSONObject RatesObj = new JSONObject(jSONObj);
            JSONObject allRates = (JSONObject) RatesObj.get("rates");
            Iterator<String> keys = allRates.keys();
            while (keys.hasNext()){
                rateList.add(getRate(allRates,keys.next()));
            }
            CurrencyRate cr = new CurrencyRate();
            cr.setCharCode("RUB");
            cr.setCourseValue(1.0);
            rateList.add(cr);
        } catch (final JSONException e) {
            Log.e(MainActivity.class.getSimpleName(), "Json parsing error: " + e.getMessage());
        }
        return rateList;
    }
}
