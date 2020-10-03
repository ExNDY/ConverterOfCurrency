package ru.mellman.conv3rter.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import ru.mellman.conv3rter.DataTasks;
import ru.mellman.conv3rter.Function;
import ru.mellman.conv3rter.NetworkChangeReceiver;
import ru.mellman.conv3rter.Snackbar;
import ru.mellman.conv3rter.data_adapters.CoursesOfCurrency;
import ru.mellman.conv3rter.data_adapters.CurrencyRate;
import ru.mellman.conv3rter.DataObject;
import ru.mellman.conv3rter.R;

public class SplashScreenActivity extends AppCompatActivity implements NetworkChangeReceiver.NetworkChangeReceiverListener {
    public static final String PREF = "converterPreferences";
    public static final String JSON_COURSES = "JSON_Courses";
    public static final String JSON_RATES = "JSON_Rates";
    public static final String FIRST_START = "firstStart";
    public static final String LAST_UPDATE_DATETIME = "lastUpdate";
    ArrayList<CoursesOfCurrency> courseList;
    ArrayList<CurrencyRate> currencyRateList;
    TextView statusApp;
    private NetworkChangeReceiver networkChangeReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF, Context.MODE_PRIVATE);
        networkChangeReceiver = new NetworkChangeReceiver(this);
        if (sharedPreferences.getBoolean("DayNight_Mode", false)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        statusApp = findViewById(R.id.textSplashScreen_statusApp);
    }
    @Override
    protected void onStart(){
        super.onStart();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, filter);
    }
    @Override
    protected void onStop(){
        super.onStop();
    }

    @Override
    protected void onPause(){
        unregisterReceiver(networkChangeReceiver);
        super.onPause();

    }
    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    public void isNetworkOffline() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF, Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean(FIRST_START,true)){
            OfflineSnackBar(getResources().getString(R.string.offline));
            statusApp.setText(R.string.first_start_error);
        }
        else{
            if(!sharedPreferences.getString(JSON_COURSES, "").equals("") && !sharedPreferences.getString(JSON_RATES, "").equals("")){
                loadData();
                start();
                unregisterReceiver(networkChangeReceiver);
            }
            else {
                OfflineSnackBar(getResources().getString(R.string.offline));
                statusApp.setText(R.string.database_error);
            }
        }
    }

    @Override
    public void isNetworkOnline() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF, Context.MODE_PRIVATE);
        if(sharedPreferences.getBoolean(FIRST_START,true)){
            downloadData();
            firstLaunch(sharedPreferences);
        }
        else {
            if(Function.checkTheNeedForAnUpdate(sharedPreferences.getString(LAST_UPDATE_DATETIME,"")) || sharedPreferences.getString(JSON_COURSES, "").equals("") || sharedPreferences.getString(JSON_RATES, "").equals("")){

                downloadData();
            }
            else {
                loadData();
            }
        }
        start();
    }

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
    private void OfflineSnackBar(String message){
        ViewGroup view = (ViewGroup) findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(view, Snackbar.LENGTH_INDEFINITE);
        View snackView = snackbar.getView();
        snackView.setBackgroundColor(getResources().getColor(R.color.colorREDForSnackBar, getApplicationContext().getTheme()));
        snackbar.setText(message);
        snackbar.show();
    }

    private void OnlineSnackBar(){
        ViewGroup view = (ViewGroup) findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(view, Snackbar.LENGTH_INDEFINITE);
        View snackView = snackbar.getView();
        snackView.setBackgroundColor(getResources().getColor(R.color.colorGREENForSnackBar, getApplicationContext().getTheme()));
        snackbar.setText("Online");
        snackbar.show();
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
            ed.putString(LAST_UPDATE_DATETIME, data.getDateUpdate());
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
}