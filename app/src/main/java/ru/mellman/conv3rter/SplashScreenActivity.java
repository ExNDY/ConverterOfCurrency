package ru.mellman.conv3rter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

public class SplashScreenActivity extends AppCompatActivity {
    public static final String PREF = "converterPreferences";
    public static final String DATE_NOW = "Date";
    public static final String STREAM_JSON = "inputjSONStr";
    public static final String JSON_RATES = "jSON_Rates";
    public static final String FIRST_START = "firstStart";
    private String TAG = MainActivity.class.getSimpleName();
    ProgressBar progressBar;
    String date;
    ArrayList<CoursesOfCurrency> courseList;
    ArrayList<CurrencyRate> currencyRateList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF, Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean("DayNight_Mode", false)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        progressBar = findViewById(R.id.progress_circular);
        TextView statusApp = findViewById(R.id.textSplashScreen_statusApp);
        String jSONStr = sharedPreferences.getString(STREAM_JSON,"");
        boolean firstStart = sharedPreferences.getBoolean(FIRST_START,true);
        boolean hasInternet = isOnline(this);
        date = getDateNow();

        if(firstStart){
            SharedPreferences.Editor ed = sharedPreferences.edit();
            ed.putBoolean(FIRST_START,false);
            ed.putInt("fromPos",34);
            ed.putInt("toPos",10);
            ed.putBoolean("DayNight_Mode",false);
            ed.apply();
            if (hasInternet){
                statusApp.setText("");
                new GetValute().execute();
            }
            else{
                statusApp.setText("For first start APP need Internet Connection");
            }
        }
        else {
            if(jSONStr.length()==0) {
                if (!hasInternet){
                    statusApp.setText("DataBase of APP is empty. need Internet Connection");
                }
                else {

                    new GetValute().execute();
                }
            }
            else
            {
                new GetValute().execute();
            }
        }
    }

    private ArrayList<CurrencyRate> getRate() {
        ArrayList<CurrencyRate> rateList = new ArrayList<CurrencyRate>();
        SharedPreferences sharedPreferences;
        sharedPreferences = getSharedPreferences(PREF,MODE_PRIVATE);
        String jsonRate = sharedPreferences.getString(JSON_RATES,"");
        boolean jsonStrEmpty = sharedPreferences.getString(JSON_RATES,"").equals("");
        if (date.equals(sharedPreferences.getString(DATE_NOW,"")) && !jsonStrEmpty){
            jsonRate=null;
        }
        else{
            HttpHandler steamHandler = new HttpHandler();
            String path = getString(R.string.json_rates);
            jsonRate = steamHandler.makeServiceCall(path);
            Log.e(TAG, "Response from url: " + jsonRate);
        }
        /*if (date.equals(sharedPreferences.getString(DATE_NOW,""))){
            jsonRate=null;
        }
        */

        if (jsonRate==null){
            jsonRate=sharedPreferences.getString(JSON_RATES,"");
        }

        if (jsonRate != null) {
            SharedPreferences.Editor ed = sharedPreferences.edit();
            ed.putString(JSON_RATES,jsonRate);
            //ed.putString(DATE_NOW, date);
            ed.apply();
            rateList = JSONObjParser.parseJSONToCurrencyRateList(jsonRate, getApplicationContext());
        } else {
            Log.e(TAG, "Couldn't get jSON from server.");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Couldn't get json from server. Check LOG", Toast.LENGTH_LONG).show();
                }
            });
        }
        return rateList;
    }

    String getDateNow(){
        String dateNow;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 0);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        dateNow = simpleDateFormat.format(cal.getTime());
        return dateNow;
    }
    public class GetValute extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void...arg0) {
            currencyRateList = getRate();
            SharedPreferences sharedPreferences;
            sharedPreferences = getSharedPreferences(PREF,MODE_PRIVATE);
            String jsonStr;
            boolean jsonStrEmpty = sharedPreferences.getString(STREAM_JSON,"").equals("");
            if (date.equals(sharedPreferences.getString(DATE_NOW,"")) && !jsonStrEmpty){
                jsonStr=null;
            }
            else{
                HttpHandler steamHandler = new HttpHandler();
                String path = getString(R.string.json_str);
                jsonStr = steamHandler.makeServiceCall(path);
            }

            //Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr==null){
                jsonStr=sharedPreferences.getString(STREAM_JSON,"");
            }

            if (jsonStr != null) {
                sharedPreferences = getSharedPreferences(PREF,MODE_PRIVATE);
                SharedPreferences.Editor ed = sharedPreferences.edit();
                ed.putString(STREAM_JSON,jsonStr);
                ed.putString(DATE_NOW, date);
                ed.apply();
                courseList = JSONObjParser.parsejSONToCourseList(jsonStr, getApplicationContext());
            } else {
                Log.e(TAG, "Couldn't get jSON from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Couldn't get json from server. Check LOG", Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            progressBar.setVisibility(View.INVISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent main = new Intent(SplashScreenActivity.this, MainActivity.class);
                    main.putParcelableArrayListExtra("cl", courseList);
                    main.putParcelableArrayListExtra("crl", currencyRateList);
                    startActivity(main);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                }
            }, 500);
            //ArrayAdapter<CoursesOfCurrency> arrayAdapter = new ArrayAdapter<CoursesOfCurrency>(this,R.layout.list_item,courseList);
            ;
        }
    }



    //Check InternetConncection WIFI, MOBILE
    private static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR");
                    return true;
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI");
                    return true;
                }  else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)){
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET");
                    return true;
                }
            }
        }
        return false;
    }
}