package ru.mellman.conv3rter.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

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

import ru.mellman.conv3rter.ConverterPreferenceManager;
import ru.mellman.conv3rter.DataTasks;
import ru.mellman.conv3rter.Function;
import ru.mellman.conv3rter.network.NetworkConnectionMonitor;
import ru.mellman.conv3rter.data_course_of_currency.ResultDTObj;
import ru.mellman.conv3rter.Snackbar;
import ru.mellman.conv3rter.Variables;
import ru.mellman.conv3rter.lists.Courses;
import ru.mellman.conv3rter.lists.CurrencyRate;
import ru.mellman.conv3rter.R;

public class SplashScreenActivity extends AppCompatActivity {
    public static final String PREF = "converterPreferences";
    public static final String JSON_COURSES = "JSON_Courses";
    public static final String JSON_RATES = "JSON_Rates";
    public static final String COURSES = "CoursesList";
    public static final String RATES = "RatesList";
    public static final String LAST_UPDATE_DATETIME = "lastUpdate";
    public static final String PREVIOUS_UPDATE_DATETIME = "previousUpdate";
    TextView statusApp;

    private NetworkConnectionMonitor connectionMonitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF, Context.MODE_PRIVATE);
        connectionMonitor = new NetworkConnectionMonitor(getApplicationContext());
        /*
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
*/
        if (sharedPreferences.getBoolean("DayNight_Mode", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        statusApp = findViewById(R.id.textSplashScreen_statusApp);
        startMainActivity();
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
            showOfflineSnackBar(getResources().getString(R.string.offline));
            statusApp.setText(R.string.first_start_error);
        } else {
            if (!sharedPreferences.getString(JSON_COURSES, "").equals("") && !sharedPreferences.getString(JSON_RATES, "").equals("")) {
                //loadData();
                startMainActivity();
            } else {
                showOfflineSnackBar(getResources().getString(R.string.offline));
                statusApp.setText(R.string.database_error);
            }
        }
    }

    private void Online() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF, Context.MODE_PRIVATE);

        ConverterPreferenceManager preferenceManager = new ConverterPreferenceManager(getApplicationContext());
        if (preferenceManager.getFirstLaunch()) {
            //downloadData();
            preferenceManager.FirstLaunchSettings();
        } else {
            if (Function.checkTheNeedForAnUpdate(sharedPreferences.getString(LAST_UPDATE_DATETIME, "")) || sharedPreferences.getString(JSON_COURSES, "").equals("") || sharedPreferences.getString(JSON_RATES, "").equals("")) {
                //downloadData();
            } else {
                //loadData();
            }
        }
        startMainActivity();
    }

    private void runApp() {
        ConverterPreferenceManager preferenceManager = new ConverterPreferenceManager(getApplicationContext());
        if (preferenceManager.getFirstLaunch()) {
            preferenceManager.FirstLaunchSettings();
        } else {

        }
    }

    private void startMainActivity() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent main = new Intent(SplashScreenActivity.this, MainActivity.class);
                ResultDTObj result = DataTasks.getUpdateData(getApplicationContext());
                if (result.getResult().equals("OK")) {
                    ArrayList<Courses> courses = result.getCourses();
                    ArrayList<CurrencyRate> rates = result.getRates();
                    String dateUpdate = result.getDateOfUpdate();
                    ConverterPreferenceManager converterPreferenceManager = new ConverterPreferenceManager(getApplicationContext());
                    converterPreferenceManager.saveCoursesList(courses);
                    main.putParcelableArrayListExtra(COURSES, courses);
                    main.putParcelableArrayListExtra(RATES, rates);
                    startActivity(main);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                } else {
                    showOfflineSnackBar("DATA IS NOT INITIALIZED");
                }


            }
        }, 0);


    }

    private void showOfflineSnackBar(String message) {
        ViewGroup view = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(view, Snackbar.LENGTH_INDEFINITE);
        View snackView = snackbar.getView();
        snackView.setBackgroundColor(getResources().getColor(R.color.colorREDForSnackBar, getApplicationContext().getTheme()));
        snackbar.setText(message);
        snackbar.show();
    }

    private void showOnlineSnackBar() {
        ViewGroup view = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(view, Snackbar.LENGTH_INDEFINITE);
        View snackView = snackbar.getView();
        snackView.setBackgroundColor(getResources().getColor(R.color.colorGREENForSnackBar, getApplicationContext().getTheme()));
        snackbar.setText("Online");
        snackbar.show();
    }
}