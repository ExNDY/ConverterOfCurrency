package ru.mellman.conv3rter.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.Observer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ru.mellman.conv3rter.PreferenceManager;
import ru.mellman.conv3rter.DataTasks;
import ru.mellman.conv3rter.network.NetworkConnectionMonitor;
import ru.mellman.conv3rter.data_course_of_currency.ResultDTObj;
import ru.mellman.conv3rter.Snackbar;
import ru.mellman.conv3rter.Variables;
import ru.mellman.conv3rter.lists.Courses;
import ru.mellman.conv3rter.lists.CurrencyRate;
import ru.mellman.conv3rter.R;
import ru.mellman.conv3rter.network.NetworkUtils;

public class SplashScreenActivity extends AppCompatActivity {
    public static final String COURSES = "CoursesList";
    public static final String RATES = "RatesList";
    private TextView statusApp;
    private ArrayList<Courses> courses;
    private ArrayList<CurrencyRate> rates;
    private NetworkConnectionMonitor connectionMonitor;
    PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferenceManager = new PreferenceManager(getApplicationContext());
        connectionMonitor = new NetworkConnectionMonitor(getApplicationContext());
        if (preferenceManager.getThemeMode()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        statusApp = findViewById(R.id.textSplashScreen_statusApp);
        connectionMonitor = new NetworkConnectionMonitor(getApplicationContext());
        if(preferenceManager.getFirstLaunch()){
            connectionMonitor.observe(this, new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean isConnected) {
                    if (isConnected){
                        if (NetworkUtils.pingInternetConnection()){
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    loadOnlineData(getApplicationContext());

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            preferenceManager.FirstLaunchSettings();
                                        }
                                    });
                                    startMainActivity();
                                }
                            }).start();
                        }
                        else{
                            showSnackBar(getResources().getString(R.string.internet_connection_without_internet));
                        }
                    }
                    else {
                        showSnackBar(getResources().getString(R.string.offline));
                        statusApp.setText(R.string.first_start_error);
                    }
                }
            });

        }
        else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ResultDTObj data = preferenceManager.readData();
                    if (data.getCourses()!= null && data.getRates()!=null){
                        courses = data.getCourses();
                        rates = data.getRates();
                        startMainActivity();
                    }
                    else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showSnackBar(getResources().getString(R.string.database_error));
                            }
                        });
                    }
                }
            }).start();
        }
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
    void loadOnlineData(Context context){
        ResultDTObj result = DataTasks.update(context);
        assert result != null;
        if (result.getResult().equals("OK")) {
            courses = result.getCourses();
            rates = result.getRates();
            String dateUpdate = result.getDateOfUpdate();
            PreferenceManager preferenceManager = new PreferenceManager(context);
            preferenceManager.saveData(courses, rates, dateUpdate);
        } else {
            showSnackBar("DATA IS NOT INITIALIZED");
        }
    }
    private void startMainActivity() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent main = new Intent(SplashScreenActivity.this, MainActivity.class);
                main.putParcelableArrayListExtra(COURSES, courses);
                main.putParcelableArrayListExtra(RATES, rates);
                startActivity(main);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        }, 50);
    }

    private void showSnackBar(String message) {
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