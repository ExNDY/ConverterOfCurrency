package ru.mellman.conv3rter.parse;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.Callable;

import ru.mellman.conv3rter.Function;
import ru.mellman.conv3rter.activity.MainActivity;
import ru.mellman.conv3rter.lists.CurrencyInfo;

public class ParseOfCurrencyInfo implements Callable<HashMap<String, CurrencyInfo>> {
    private final Context context;

    public ParseOfCurrencyInfo(Context context) {
        this.context = context;
    }

    @NonNull
    private static CurrencyInfo getRate(@NonNull JSONObject infoObj) throws JSONException {
        String charCode = infoObj.getString("code");
        String name = infoObj.getString("name");
        String symbolCurrency = infoObj.getString("symbol_native");
        int decimalDigits = infoObj.getInt("decimal_digits");
        return new CurrencyInfo(charCode, name, symbolCurrency, decimalDigits);
    }

    @Override
    public HashMap<String, CurrencyInfo> call() throws Exception {
        HashMap<String, CurrencyInfo> currencyInfoArrayList = new HashMap<>();
        try {
            JSONArray infoArray = new JSONArray(Function.readJSONObject(context));
            for (int i = 0; i < infoArray.length(); i++) {
                JSONObject infoObj = infoArray.getJSONObject(i);
                currencyInfoArrayList.put(infoObj.getString("code"), getRate(infoObj));
            }
        } catch (final JSONException e) {
            Log.e(MainActivity.class.getSimpleName(), "Json parsing error: " + e.getMessage());
        }
        return currencyInfoArrayList;
    }
}
