package ru.mellman.conv3rter;

import android.content.ContentProvider;
import android.content.Context;
import android.content.SharedPreferences;

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

    public ConverterPreferenceManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public void FirstLaunchSettings() {
        editor = sharedPreferences.edit();
        editor.putBoolean(FIRST_LAUNCH, false);
        editor.putInt(SPINNER_POSITION_FROM, 34);
        editor.putInt(SPINNER_POSITION_TO, 10);
        editor.putBoolean(DAYNIGHT_MODE, false);
        editor.apply();
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
        return sharedPreferences.getInt(SPINNER_POSITION_FROM, 34);
    }

    public int getSpinnerPosTo() {
        return sharedPreferences.getInt(SPINNER_POSITION_TO, 11);
    }

}
