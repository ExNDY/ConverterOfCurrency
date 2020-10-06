package ru.mellman.conv3rter.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import ru.mellman.conv3rter.DataObject;
import ru.mellman.conv3rter.DataTasks;
import ru.mellman.conv3rter.Function;
import ru.mellman.conv3rter.R;
import ru.mellman.conv3rter.Snackbar;
import ru.mellman.conv3rter.data_adapters.CoursesOfCurrency;
import ru.mellman.conv3rter.data_adapters.CoursesOfCurrencyAdapter;
import ru.mellman.conv3rter.data_adapters.CurrencyRate;
import ru.mellman.conv3rter.data_adapters.CurrencyRateAdapter;

public class MainActivity extends AppCompatActivity {
    private TextView _valueFrom, _valueTo;
    private ListView _listOfCurrencyRates;
    private Spinner _spinnerFrom, _spinnerTo;
    private SwitchMaterial _switchThemeMaterial;
    private ImageButton btnSwitchCurrency;
    public static final String PREF = "converterPreferences";
    public static final String JSON_COURSES = "JSON_Courses";
    public static final String JSON_RATES = "JSON_Rates";
    public static final String LAST_UPDATE_DATETIME = "lastUpdate";
    private int _selectedPosFrom, _selectedPosTo;
    ArrayList<CoursesOfCurrency> currencyList;
    CoursesOfCurrencyAdapter currencyListAdapter;
    ArrayList<CurrencyRate> currencyRateList;
    CurrencyRateAdapter currencyRateListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        initSpinner();

        if (getIntent().hasExtra(JSON_COURSES)){
            currencyList = getIntent().getParcelableArrayListExtra(JSON_COURSES);
        }
        if (getIntent().hasExtra(JSON_RATES)){
            currencyRateList = getIntent().getParcelableArrayListExtra(JSON_RATES);
        }
        if (savedInstanceState != null) {
            _valueFrom.setText(savedInstanceState.getString("AreaFrom"));

            currencyList = savedInstanceState.getParcelableArrayList("courseList");
            currencyRateList = savedInstanceState.getParcelableArrayList("currencyRates");
        }
        currencyRateListAdapter = new CurrencyRateAdapter(this, currencyRateList);
        _spinnerFrom.setAdapter(currencyRateListAdapter);
        _spinnerTo.setAdapter(currencyRateListAdapter);
        loadSpinnerPos();
        currencyListAdapter = new CoursesOfCurrencyAdapter(this,R.layout.list_item , currencyList);
        _listOfCurrencyRates.setAdapter(currencyListAdapter);
    }

    protected void onResume() {
        super.onResume();
        if (getIntent().hasExtra("fromValue")){
            _valueFrom.setText(getIntent().getStringExtra("fromValue"));
        }
    }
    protected void onPause(){
        super.onPause();
    }
    @Override
    public void recreate(){
        Intent main = getIntent();
        main.putExtra("fromValue", _valueFrom.getText().toString());
        startActivity(main);
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        finish();


    }
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("AreaFrom", _valueFrom.getText().toString());
        outState.putParcelableArrayList("courseList", currencyList);
        outState.putParcelableArrayList("currencyRates", currencyRateList);
        outState.putInt("selectedPosTo", _selectedPosTo);
        outState.putInt("selectedPosFrom", _selectedPosFrom);
    }
    void initSpinner(){
        AdapterView.OnItemSelectedListener itemSelectedListenerFrom = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                _selectedPosFrom = (int)parent.getItemIdAtPosition(position);
                saveSpinnerPos(_selectedPosFrom, _selectedPosTo);
                calculateCourseBy(_valueFrom.getText().toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        _spinnerFrom.setOnItemSelectedListener(itemSelectedListenerFrom);


        AdapterView.OnItemSelectedListener itemSelectedListenerTo = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                _selectedPosTo = (int)parent.getItemIdAtPosition(position);
                saveSpinnerPos(_selectedPosFrom, _selectedPosTo);
                calculateCourseBy(_valueFrom.getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        _spinnerTo.setOnItemSelectedListener(itemSelectedListenerTo);
    }


    private void calculateCourseBy(String editTextFrom){
        if(!editTextFrom.equals("")){
            try {
                double _val = Double.parseDouble(editTextFrom);
                _val = Function.convert(currencyRateList.get(_selectedPosFrom).getRate(), currencyRateList.get(_selectedPosTo).getRate(),_val);
                String result = Function.getDecimalToFormat(_val);
                _valueTo.setText(result);
            }
            catch (Exception e){
                _valueTo.setText(getResources().getString(R.string.error));
            }

        }

    }

    private void initUI(){
        Button btn1 = findViewById(R.id.button1);
        Button btn2 = findViewById(R.id.button2);
        Button btn3 = findViewById(R.id.button3);
        Button btn4 = findViewById(R.id.button4);
        Button btn5 = findViewById(R.id.button5);
        Button btn6 = findViewById(R.id.button6);
        Button btn7 = findViewById(R.id.button7);
        Button btn8 = findViewById(R.id.button8);
        Button btn9 = findViewById(R.id.button9);
        Button btn0 = findViewById(R.id.button0);
        Button btnDot = findViewById(R.id.buttonDot);
        Button btnDel = findViewById(R.id.buttonDel);
        btnSwitchCurrency = findViewById(R.id.switchFromToButton);
        _switchThemeMaterial = findViewById(R.id.dayNightSwitcher);
        _spinnerFrom = findViewById(R.id.spinnerFrom);
        _spinnerTo = findViewById(R.id.spinnerTo);
        _valueFrom = findViewById(R.id.valueFrom);
        _valueTo = findViewById(R.id.valueTo);
        _listOfCurrencyRates = findViewById(R.id.currencyListView);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        _valueTo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences(PREF,MODE_PRIVATE);
                CurrencyRate from = (CurrencyRate) _spinnerFrom.getSelectedItem();
                CurrencyRate to = (CurrencyRate) _spinnerTo.getSelectedItem();
                String buf = getResources().getString(R.string.clipboardstring_1) +" "+ _valueFrom.getText().toString()+from.getCharCode()
                        +" "+ getResources().getString(R.string.clipboardstring_2)+" "+_valueTo.getText().toString()
                        +to.getCharCode()+" "+getResources().getString(R.string.clipboardstring_3)
                        +Function.getDate(sharedPreferences.getString(LAST_UPDATE_DATETIME,""));
                Function.copyToClipboard(buf, getApplicationContext());
                SnackBarShow(getResources().getString(R.string.copy_to_clipboard));
                return true;
            }
        });
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.button0){
                    InputNum(0);
                }
                if(v.getId() == R.id.button1){
                    InputNum(1);
                }
                if(v.getId() == R.id.button2){
                    InputNum(2);
                }
                if(v.getId() == R.id.button3){
                    InputNum(3);
                }
                if(v.getId() == R.id.button4){
                    InputNum(4);
                }
                if(v.getId() == R.id.button5){
                    InputNum(5);
                }
                if(v.getId() == R.id.button6){
                    InputNum(6);
                }
                if(v.getId() == R.id.button7){
                    InputNum(7);
                }
                if(v.getId() == R.id.button8){
                    InputNum(8);
                }
                if(v.getId() == R.id.button9){
                    InputNum(9);
                }
                if(v.getId() == R.id.buttonDot){
                    InputDot();
                }
                if(v.getId() == R.id.buttonDel){
                    DelNumber();
                }
                if(v.getId()==R.id.switchFromToButton){
                    SwitchCurrency();
                }
            }
        };
        btn0.setOnClickListener(listener);
        btn1.setOnClickListener(listener);
        btn2.setOnClickListener(listener);
        btn3.setOnClickListener(listener);
        btn4.setOnClickListener(listener);
        btn5.setOnClickListener(listener);
        btn6.setOnClickListener(listener);
        btn7.setOnClickListener(listener);
        btn8.setOnClickListener(listener);
        btn9.setOnClickListener(listener);
        btnDot.setOnClickListener(listener);
        btnDel.setOnClickListener(listener);
        btnSwitchCurrency.setOnClickListener(listener);
        btnDel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                _valueFrom.setText("0");
                calculateCourseBy(_valueFrom.getText().toString());
                return true;
            }
        });
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SharedPreferences sharedPreferences = getSharedPreferences(PREF,MODE_PRIVATE);
                checkUpdate(sharedPreferences);
                pullToRefresh.setRefreshing(false);
            }
        });
        _switchThemeMaterial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPreferences = getSharedPreferences(PREF,MODE_PRIVATE);
                SharedPreferences.Editor ed = sharedPreferences.edit();
                if(!isChecked){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    ed.putBoolean("DayNight_Mode",false);

                }
                else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    ed.putBoolean("DayNight_Mode",true);

                }
                ed.apply();
            }
        });
        _valueFrom.setShowSoftInputOnFocus(false);

        SharedPreferences sharedPreferences = getSharedPreferences(PREF,MODE_PRIVATE);
        checkThemeMod(sharedPreferences);
    }
    private void InputNum(int num){
        String txt = _valueFrom.getText().toString();
        int length = txt.length();
        if (length<15){
            if(txt.contains(".")){
                int checkDot = txt.indexOf(".");
                int dif = txt.length()-checkDot;
                if (dif<=2){
                    if(txt.equals("0")){
                        String text = String.valueOf(num);
                        _valueFrom.setText(text);
                    }
                    else
                    {
                        String text = txt + num;
                        _valueFrom.setText(text);
                    }
                }
                else {
                    SnackBarShow(getString(R.string.after_dots_symbol_limit));
                }
            }
            else {
                if(txt.equals("0")){
                    String text = String.valueOf(num);
                    _valueFrom.setText(text);
                }
                else
                {
                    String text = txt + num;
                    _valueFrom.setText(text);
                }
            }
            calculateCourseBy(_valueFrom.getText().toString());
        }
        else {
            SnackBarShow(getString(R.string.string_limit));
        }

    }
    private void InputDot(){
        String txt = _valueFrom.getText().toString();
        int checkDot = txt.indexOf(".");
        if(checkDot==-1){
            String text = txt+".";
            _valueFrom.setText(text);
            calculateCourseBy(_valueFrom.getText().toString());
        }
        else {
            SnackBarShow(getString(R.string.dot_in_line));
        }
    }
    private void DelNumber(){
        if(_valueFrom.length()!=0){
            if(_valueFrom.length()-1==0)
            {
                _valueFrom.setText("0");
            }
            else
            {
                String txt = _valueFrom.getText().toString();
                _valueFrom.setText(txt.substring(0, txt.length()-1));
            }
        }
        calculateCourseBy(_valueFrom.getText().toString());
    }
    private void saveSpinnerPos(int selectedPosFrom, int selectedPosTo){
        SharedPreferences sharedPreferences = getSharedPreferences(PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putInt("fromPos",selectedPosFrom);
        ed.putInt("toPos",selectedPosTo);
        ed.apply();
    }
    private void loadSpinnerPos(){
        SharedPreferences sharedPreferences = getSharedPreferences(PREF, Context.MODE_PRIVATE);
        _spinnerFrom.setSelection(sharedPreferences.getInt("fromPos",34));
        _spinnerTo.setSelection(sharedPreferences.getInt("toPos", 11));
    }
    private void SwitchCurrency( ){

        Animation rotate180_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate180_anim);
        btnSwitchCurrency.startAnimation(rotate180_anim);

        int posFrom = _spinnerFrom.getSelectedItemPosition();
        int posTo = _spinnerTo.getSelectedItemPosition();
        _spinnerFrom.setSelection(posTo,true);
        _spinnerTo.setSelection(posFrom, true);


        if (_valueFrom.length()!=0){
            try {
                double val = Double.parseDouble(_valueTo.getText().toString());
                String s = Function.getDecimalToFormat(val);
                _valueTo.setText("");
                _valueFrom.setText(s);
            }
            catch (Exception e){
                _valueTo.setText(getResources().getString(R.string.error));
            }

        }
    }
    private void checkThemeMod(SharedPreferences sharedPreferences){
        if (sharedPreferences.getBoolean("DayNight_Mode", false)){
            _switchThemeMaterial.setChecked(true);
        }
        else {
            _switchThemeMaterial.setChecked(false);
        }
    }
    private void checkUpdate(SharedPreferences sharedPreferences){
        boolean check = Function.checkTheNeedForAnUpdate(sharedPreferences.getString(LAST_UPDATE_DATETIME, ""));
        if (check && Function.isOnline(getApplicationContext())){
            try {
                DataObject data = DataTasks.getDataOfCurrency();
                currencyList = data.getCoursesList();
                currencyRateList = data.getRatesList();
                String jsonCourses = data.getJsonObjectCourses();
                String jsonRates = data.getJsonObjectRates();

                SharedPreferences.Editor ed = sharedPreferences.edit();
                ed.putString(JSON_COURSES, jsonCourses);
                ed.putString(JSON_RATES,jsonRates);
                ed.putString(LAST_UPDATE_DATETIME, data.getDateUpdate());
                ed.apply();

                currencyListAdapter.notifyDataSetChanged();
                currencyRateListAdapter.notifyDataSetChanged();
                SnackBarShow(getResources().getString(R.string.dataBaseWasUpdated));
            } catch (InterruptedException | TimeoutException e) {
                e.printStackTrace();
            }

        }
        else {
            if (!check && Function.isOnline(getApplicationContext())){
                SnackBarShow(getResources().getString(R.string.dataBaseIsActual));
            }
            if (!Function.isOnline(getApplicationContext())){
                SnackBarShow(getResources().getString(R.string.offline));
            }

        }
    }
    private void SnackBarShow(String message){
        ViewGroup view = (ViewGroup) findViewById(android.R.id.content);
        final Snackbar snackbar = Snackbar.make(view, Snackbar.LENGTH_SHORT);
        View snackView = snackbar.getView();
        snackView.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorOfSnackBar, getApplicationContext().getTheme()));
        snackbar.setText(message);
        snackbar.show();
    }
}

