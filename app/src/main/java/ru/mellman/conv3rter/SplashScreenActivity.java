package ru.mellman.conv3rter;

import androidx.appcompat.app.AppCompatActivity;

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
    public static final String FIRST_START = "firstStart";
    private String TAG = MainActivity.class.getSimpleName();
    ProgressBar progressBar;
    String date;
    ArrayList<CoursesOfCurrency> courseList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        progressBar = findViewById(R.id.progress_circular);
        TextView statusApp = findViewById(R.id.textSplashScreen_statusApp);
        SharedPreferences sharedPreferences = getSharedPreferences(PREF, Context.MODE_PRIVATE);
        String jSONStr = sharedPreferences.getString(STREAM_JSON,"");
        boolean firstStart = sharedPreferences.getBoolean(FIRST_START,true);
        boolean hasInternet = isOnline(this);
        date = getDateNow();
        if(firstStart){
            SharedPreferences.Editor ed = sharedPreferences.edit();
            ed.putBoolean(FIRST_START,false);
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
            SharedPreferences sharedPreferences;
            sharedPreferences = getSharedPreferences(PREF,MODE_PRIVATE);
            String jsonStr;
            if (date.equals(sharedPreferences.getString(DATE_NOW,""))){
                jsonStr=null;
            }
            else{
                HttpHandler steamHandler = new HttpHandler();
                jsonStr = steamHandler.makeServiceCall("https://www.cbr-xml-daily.ru/daily_json.js");
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
                courseList = parsejSONToObj(jsonStr);
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
                    startActivity(main);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                }
            }, 500);
            //ArrayAdapter<CoursesOfCurrency> arrayAdapter = new ArrayAdapter<CoursesOfCurrency>(this,R.layout.list_item,courseList);
            ;
        }


    }
    ArrayList<CoursesOfCurrency> parsejSONToObj(String jSONstr){
        courseList = new ArrayList<>();
        try {
            JSONObject allValuteObj = new JSONObject(jSONstr);
            JSONObject allValutes = (JSONObject) allValuteObj.get("Valute");
            Iterator keyIterator = allValutes.keys();
            while (keyIterator.hasNext()){
                String key = (String)keyIterator.next();
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
            Log.e(TAG, "Json parsing error: " + e.getMessage());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
        return courseList;
    }

    //Check InternetConncection WIFI, MOBILE
    public static boolean isOnline(Context context) {
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