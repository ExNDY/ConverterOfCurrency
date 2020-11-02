package ru.mellman.conv3rter;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import ru.mellman.conv3rter.lists.Courses;

public class ConverterPreferenceManager {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;
    private static final String FIRST_LAUNCH = "firstLaunch";
    private static final String PREF = "converterPreferences";
    public static final String JSON_COURSES = "JSON_Courses";
    public static final String JSON_RATES = "JSON_Rates";
    public static final String LAST_UPDATE_DATETIME = "lastUpdate";
    public static final String PREVIOUS_UPDATE_DATETIME = "previousUpdate";
    public static final String DAYNIGHT_MODE = "DayNight_Mode";
    public static final String SPINNER_POSITION_FROM = "SpinnerPositionFrom";
    public static final String SPINNER_POSITION_TO = "SpinnerPositionTo";
    public static final String COURSES = "CoursesList";

    public ConverterPreferenceManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public void FirstLaunchSettings() {
        editor = sharedPreferences.edit();
        editor.putBoolean(FIRST_LAUNCH, false);
        editor.putInt(SPINNER_POSITION_FROM, 6);
        editor.putInt(SPINNER_POSITION_TO, 10);
        editor.putBoolean(DAYNIGHT_MODE, false);
        editor.apply();
    }

    public void saveCoursesList(ArrayList<Courses> courses) {
        Gson gson = new Gson();
        String coursesStream = gson.toJson(courses);
        editor = sharedPreferences.edit();
        editor.putString(COURSES, coursesStream);
        editor.apply();
    }

    public ArrayList<Courses> readCoursesList() {

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Courses>>() {
        }.getType();
        return gson.fromJson(sharedPreferences.getString(COURSES, ""), type);
    }

    public void setThemeMode(boolean isNight) {
        editor = sharedPreferences.edit();
        editor.putBoolean(DAYNIGHT_MODE, isNight);
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
