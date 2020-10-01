package ru.mellman.conv3rter.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import ru.mellman.conv3rter.DataObject;
import ru.mellman.conv3rter.Function;
import ru.mellman.conv3rter.DataTasks;
import ru.mellman.conv3rter.R;
import ru.mellman.conv3rter.data_adapters.CoursesOfCurrency;
import ru.mellman.conv3rter.data_adapters.CurrencyRate;

public class SplashScreenActivity extends AppCompatActivity {
    public static final String PREF = "converterPreferences";
    public static final String JSON_COURSES = "JSON_Courses";
    public static final String JSON_RATES = "JSON_Rates";
    public static final String FIRST_START = "firstStart";
    private String TAG = MainActivity.class.getSimpleName();
    ProgressBar progressBar;
    ArrayList<CoursesOfCurrency> courseList;
    ArrayList<CurrencyRate> currencyRateList;
    TextView statusApp;
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
        statusApp = findViewById(R.id.textSplashScreen_statusApp);
        progressBar.setVisibility(View.VISIBLE);
        try {
            String d = Function.getDateTimeCourseLastUpdate();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (sharedPreferences.getBoolean(FIRST_START,true) && isOnline(getApplicationContext())){
            firstLaunch(sharedPreferences);
            downloadData();
        }
        else
        {
            if(sharedPreferences.getString(JSON_COURSES, "").equals("") || sharedPreferences.getString(JSON_RATES, "").equals("")){
                downloadData();
            }
            else {
                loadData();
            }
        }
        progressBar.setVisibility(View.INVISIBLE);
        start();
        //run(sharedPreferences, getApplicationContext());
    }
/*
    private void run(SharedPreferences sharedPreferences, Context context){
        String jSONStr = sharedPreferences.getString(JSON_COURSES,"");
        boolean firstStart = sharedPreferences.getBoolean(FIRST_START,true);
        boolean hasInternet = isOnline(this);
        date = Function.getDateNow();
        if(firstStart){
            firstLaunch(sharedPreferences);
            if (hasInternet){
                statusApp.setText("");
                new GetValute().execute();
            }
            else{
                statusApp.setText(R.string.first_start_error);
            }
        }
        else {
            if(jSONStr.length()==0) {
                if (!hasInternet){
                    statusApp.setText(R.string.database_error);
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
*/
    private void start(){
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
        }, 0);
    }

    private void firstLaunch(SharedPreferences sharedPreferences){
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putBoolean(FIRST_START,false);
        ed.putInt("fromPos",34);
        ed.putInt("toPos",10);
        ed.putBoolean("DayNight_Mode",false);
        ed.apply();
    }

    private void downloadData(){
        try {
            DataObject data = DataTasks.getDataOfCurrency();
            courseList = data.getCoursesList();
            currencyRateList = data.getRatesList();
            String jsonCourses = data.getJsonObjectCourses();
            String jsonRates = data.getJsonObjectRates();
            SharedPreferences sharedPreferences = getSharedPreferences(PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor ed = sharedPreferences.edit();
            ed.putString(JSON_COURSES, jsonCourses);
            ed.putString(JSON_RATES,jsonRates);
            ed.apply();
        } catch (InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }
    }
    private void loadData(){
        try {
            SharedPreferences sharedPreferences = getSharedPreferences(PREF, Context.MODE_PRIVATE);
            DataObject data = DataTasks.loadDataOfCurrency(sharedPreferences.getString(JSON_COURSES,""), sharedPreferences.getString(JSON_RATES, ""));
            courseList = data.getCoursesList();
            currencyRateList = data.getRatesList();
        } catch (InterruptedException | TimeoutException e) {
            e.printStackTrace();
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