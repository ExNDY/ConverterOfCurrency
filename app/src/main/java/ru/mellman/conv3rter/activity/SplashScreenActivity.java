package ru.mellman.conv3rter.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.Observer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import ru.mellman.conv3rter.ConverterPreferenceManager;
import ru.mellman.conv3rter.DataTasks;
import ru.mellman.conv3rter.Function;
import ru.mellman.conv3rter.NetworkConnectionMonitor;
import ru.mellman.conv3rter.Snackbar;
import ru.mellman.conv3rter.Variables;
import ru.mellman.conv3rter.CoursesOfCurrency;
import ru.mellman.conv3rter.data_course_of_currency.Courses;
import ru.mellman.conv3rter.data_course_of_currency.CurrencyRate;
import ru.mellman.conv3rter.DataObject;
import ru.mellman.conv3rter.R;
import ru.mellman.conv3rter.data_course_of_currency.DataLists;

public class SplashScreenActivity extends AppCompatActivity {
    public static final String PREF = "converterPreferences";
    public static final String JSON_COURSES = "JSON_Courses";
    public static final String JSON_RATES = "JSON_Rates";
    public static final String COURSES = "CoursesList";
    public static final String LAST_UPDATE_DATETIME = "lastUpdate";
    public static final String PREVIOUS_UPDATE_DATETIME = "previousUpdate";
    ArrayList<CoursesOfCurrency> courseList;
    ArrayList<CurrencyRate> currencyRateList;

    ArrayList<Courses> coursesArrayList;

    TextView statusApp;

    private NetworkConnectionMonitor connectionMonitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        String s = "asD";
        SharedPreferences sharedPreferences = getSharedPreferences(PREF, Context.MODE_PRIVATE);
        connectionMonitor = new NetworkConnectionMonitor(getApplicationContext());
        connectionMonitor.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isConnected) {
                if (isConnected) {
                    Online();
                } else {
                    Offline();
                }
            }
        });

        if (sharedPreferences.getBoolean("DayNight_Mode", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        statusApp = findViewById(R.id.textSplashScreen_statusApp);
    }


    @Override
    protected void onStart(){
        super.onStart();
    }


    @Override
    protected void onPause() {
        if (Variables.isRegistered) {
            connectionMonitor.unregisterDefaultNetworkCallback();
            Variables.isRegistered = false;
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!Variables.isRegistered) {
            connectionMonitor.registerDefaultNetworkCallback();
            Variables.isRegistered = true;
        }
    }

    private void Offline() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF, Context.MODE_PRIVATE);
        ConverterPreferenceManager preferenceManager = new ConverterPreferenceManager(getApplicationContext());
        if (preferenceManager.getFirstLaunch()) {
            OfflineSnackBar(getResources().getString(R.string.offline));
            statusApp.setText(R.string.first_start_error);
        } else {
            if (!sharedPreferences.getString(JSON_COURSES, "").equals("") && !sharedPreferences.getString(JSON_RATES, "").equals("")) {
                loadData();
                startMainActivity();
            } else {
                OfflineSnackBar(getResources().getString(R.string.offline));
                statusApp.setText(R.string.database_error);
            }
        }
    }

    private void Online() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF, Context.MODE_PRIVATE);

        ConverterPreferenceManager preferenceManager = new ConverterPreferenceManager(getApplicationContext());
        if (preferenceManager.getFirstLaunch()) {
            downloadData();
            preferenceManager.FirstLaunchSettings();
        } else {
            if (Function.checkTheNeedForAnUpdate(sharedPreferences.getString(LAST_UPDATE_DATETIME, "")) || sharedPreferences.getString(JSON_COURSES, "").equals("") || sharedPreferences.getString(JSON_RATES, "").equals("")) {
                downloadData();
            } else {
                loadData();
            }
        }
        startMainActivity();
    }


    private void startMainActivity() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent main = new Intent(SplashScreenActivity.this, MainActivity.class);
                coursesArrayList = DataTasks.getCourseList(getApplicationContext());
                main.putParcelableArrayListExtra(COURSES, coursesArrayList);
                main.putParcelableArrayListExtra(JSON_COURSES, courseList);
                main.putParcelableArrayListExtra(JSON_RATES, currencyRateList);
                startActivity(main);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        }, 250);


    }
    private void OfflineSnackBar(String message){
        ViewGroup view = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(view, Snackbar.LENGTH_INDEFINITE);
        View snackView = snackbar.getView();
        snackView.setBackgroundColor(getResources().getColor(R.color.colorREDForSnackBar, getApplicationContext().getTheme()));
        snackbar.setText(message);
        snackbar.show();
    }

    private void OnlineSnackBar(){
        ViewGroup view = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(view, Snackbar.LENGTH_INDEFINITE);
        View snackView = snackbar.getView();
        snackView.setBackgroundColor(getResources().getColor(R.color.colorGREENForSnackBar, getApplicationContext().getTheme()));
        snackbar.setText("Online");
        snackbar.show();
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
            ed.putString(PREVIOUS_UPDATE_DATETIME, data.getDatePreviousUpdate());
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