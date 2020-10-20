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
    public ConverterPreferenceManager(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public void setFirstLaunch(boolean isFirstTime){
        editor = sharedPreferences.edit();
        editor.putBoolean(FIRST_LAUNCH, isFirstTime);
        editor.apply();
    }
    public void saveSpinnerPos(int selectedPosFrom, int selectedPosTo) {
        editor = sharedPreferences.edit();
        editor.putInt("fromPos", selectedPosFrom);
        editor.putInt("toPos", selectedPosTo);
        editor.apply();
    }
    public void saveSpinnerPosTo(int selectedPosTo){
        editor = sharedPreferences.edit();
        editor.putInt("toPos", selectedPosTo);
        editor.apply();
    }
    public void saveSpinnerPosFrom(int selectedPosFrom){
        editor = sharedPreferences.edit();
        editor.putInt("fromPos", selectedPosFrom);
        editor.apply();
    }

    public boolean getFirstLaunch(){
        return sharedPreferences.getBoolean(FIRST_LAUNCH, true);
    }
    public int getSpinnerPosFrom(){
        return sharedPreferences.getInt("fromPos",34);
    }
    public int getSpinnerPosTo(){
        return sharedPreferences.getInt("toPos",11);
    }

}
