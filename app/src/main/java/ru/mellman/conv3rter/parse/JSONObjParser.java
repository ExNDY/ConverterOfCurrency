package ru.mellman.conv3rter.parse;


import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import ru.mellman.conv3rter.activity.MainActivity;
import ru.mellman.conv3rter.CoursesOfCurrency;
import ru.mellman.conv3rter.data_course_of_currency.CurrencyRate;

public class JSONObjParser {
    private static CoursesOfCurrency getCourse(JSONObject response) throws JSONException {
        String id = response.getString("ID");
        String charName = response.getString("CharCode");
        String name = response.getString("Name");
        int nominal = Integer.parseInt(response.getString("Nominal"));
        Double course = Double.valueOf(response.getString("Value"));
        Double previous = Double.valueOf(response.getString("Previous"));
        return new CoursesOfCurrency(id, charName, name, nominal, course, previous);
    }

    @NonNull
    private static CurrencyRate getRate(@NonNull JSONObject response, String key) throws JSONException {
        Double CourseValue = response.getDouble(key);
        return new CurrencyRate(key, CourseValue);
    }

    public static ArrayList<CoursesOfCurrency> parseJSONToCourseList(String jSONObj) {
        ArrayList<CoursesOfCurrency> courseList = new ArrayList<>();
        try {
            JSONObject CurrencyObj = new JSONObject(jSONObj);
            JSONObject allCurrency = (JSONObject) CurrencyObj.get("Valute");
            Iterator<String> keyIterator = allCurrency.keys();
            while (keyIterator.hasNext()) {
                String key = keyIterator.next();
                courseList.add(getCourse(allCurrency.getJSONObject(key)));
            }
        } catch (final JSONException e) {
            Log.e(MainActivity.class.getSimpleName(), "Json parsing error: " + e.getMessage());
        }
        return courseList;
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
