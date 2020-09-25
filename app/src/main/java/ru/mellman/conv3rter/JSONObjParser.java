package ru.mellman.conv3rter;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import android.os.Handler;

public class JSONObjParser {

    public static ArrayList<CoursesOfCurrency> parsejSONToCourseList(String jSONObj, final Context context){
        ArrayList<CoursesOfCurrency> courseList = new ArrayList<>();
        try {
            JSONObject allValuteObj = new JSONObject(jSONObj);
            JSONObject allValutes = (JSONObject) allValuteObj.get("Valute");
            Iterator<String> keyIterator = allValutes.keys();
            while (keyIterator.hasNext()){
                String key = keyIterator.next();
                JSONObject val = allValutes.getJSONObject(key);
                CoursesOfCurrency cl = new CoursesOfCurrency();
                cl.setCharCode(val.getString("CharCode"));
                cl.setName(val.getString("Name"));
                int nominal = Integer.parseInt(val.getString("Nominal"));
                cl.setNominal(nominal);
                cl.setCourseValue(Double.valueOf(val.getString("Value")));
                cl.setPrevious(Double.valueOf(val.getString("Previous")));
                courseList.add(cl);
            }
        } catch (final JSONException e) {
            //Log.e(MainActivity.class.getSimpleName(), "Json parsing error: " + e.getMessage());
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
        return courseList;
    }
    public static ArrayList<CurrencyRate> parseJSONToCurrencyRateList(String jSONObj, final Context context) {
        ArrayList<CurrencyRate> currencyRateArrayList = new ArrayList<>();
        try {
            JSONObject allRatesObj = new JSONObject(jSONObj);
            JSONObject allRates = (JSONObject) allRatesObj.get("rates");
            Iterator<String> iterator = allRates.keys();
            while (iterator.hasNext()){
                String code = iterator.next();
                CurrencyRate cr = new CurrencyRate();
                cr.setCharCode(code);
                cr.setCourseValue(allRates.getDouble(code));
                currencyRateArrayList.add(cr);
            }
            CurrencyRate cr = new CurrencyRate();
            cr.setCharCode("RUB");
            cr.setCourseValue(1.0);
            currencyRateArrayList.add(cr);
        } catch (final JSONException e) {
            //Log.e(MainActivity.class.getSimpleName(), "Json parsing error: " + e.getMessage());
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        }
        return currencyRateArrayList;
    }
}
