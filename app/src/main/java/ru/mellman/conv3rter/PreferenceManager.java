package ru.mellman.conv3rter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.mellman.conv3rter.activity.MainActivity;
import ru.mellman.conv3rter.activity.SplashScreenActivity;
import ru.mellman.conv3rter.data_course_of_currency.ResultDTObj;
import ru.mellman.conv3rter.lists.Courses;
import ru.mellman.conv3rter.lists.CurrencyRate;

public class PreferenceManager {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;

    private static final String FIRST_LAUNCH = "firstLaunch";
    private static final String PREF = "converterPreferences";
    public static final String DAYNIGHT_MODE = "DayNight_Mode";
    public static final String SPINNER_POSITION_FROM = "SpinnerPositionFrom";
    public static final String SPINNER_POSITION_TO = "SpinnerPositionTo";
    public static final String DATE_OF_UPDATE = "DayOfUpdate";
    public static final String COURSES = "CoursesList";
    public static final String RATES = "RatesList";

    public PreferenceManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public void FirstLaunchSettings() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                editor = sharedPreferences.edit();
                editor.putBoolean(FIRST_LAUNCH, false);
                editor.putInt(SPINNER_POSITION_FROM, 6);
                editor.putInt(SPINNER_POSITION_TO, 10);
                editor.putBoolean(DAYNIGHT_MODE, false);
                editor.apply();
            }
        }).start();
    }

    public ArrayList<Courses> readCoursesList() {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Courses>>() {
        }.getType();
        return gson.fromJson(sharedPreferences.getString(COURSES, ""), type);
    }

    public void saveData(final ArrayList<Courses> courses, final ArrayList<CurrencyRate> rates, final String date){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Gson gson = new Gson();
                String coursesStream = gson.toJson(courses);
                String ratesStream = gson.toJson(rates);
                editor = sharedPreferences.edit();
                editor.putString(COURSES, coursesStream);
                editor.putString(RATES, ratesStream);
                editor.putString(DATE_OF_UPDATE, date);
                editor.apply();
            }
        }).start();
    }
    public ResultDTObj readData(){
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Courses>>() {
        }.getType();
        ArrayList<Courses> courses = gson.fromJson(sharedPreferences.getString(COURSES, ""), type);
        type = new TypeToken<ArrayList<CurrencyRate>>() {
        }.getType();
        ArrayList<CurrencyRate> rates =  gson.fromJson(sharedPreferences.getString(RATES,""), type);
        return new ResultDTObj(courses, rates, null, null);
    }

    public void setThemeMode(boolean isNight) {
        editor = sharedPreferences.edit();
        editor.putBoolean(DAYNIGHT_MODE, isNight);
        editor.apply();
    }
    public void saveSpinnerPosition(int selectedPosTo, int selectedPosFrom){
        editor = sharedPreferences.edit();
        editor.putInt(SPINNER_POSITION_TO, selectedPosTo);
        editor.putInt(SPINNER_POSITION_FROM, selectedPosFrom);
        editor.apply();

    }
    public void saveSpinnerPosTo(int selectedPosTo) {
        editor = sharedPreferences.edit();
        editor.putInt(SPINNER_POSITION_TO, selectedPosTo);
        editor.apply();
    }

    public void saveSpinnerPosFrom(int selectedPosFrom) {
        editor = sharedPreferences.edit();
        editor.putInt(SPINNER_POSITION_FROM, selectedPosFrom);
        editor.apply();
    }

    public boolean getFirstLaunch() {
        return sharedPreferences.getBoolean(FIRST_LAUNCH, true);
    }
    public String getDateOfUpdate() {
        return sharedPreferences.getString(DATE_OF_UPDATE, "");
    }

    public boolean getThemeMode() {
        return sharedPreferences.getBoolean(DAYNIGHT_MODE, false);
    }

    public int getSpinnerPosFrom() {
        return sharedPreferences.getInt(SPINNER_POSITION_FROM, 6);
    }

    public int getSpinnerPosTo() {
        return sharedPreferences.getInt(SPINNER_POSITION_TO, 10);
    }

}
