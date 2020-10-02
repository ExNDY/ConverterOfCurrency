package ru.mellman.conv3rter.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import ru.mellman.conv3rter.DataObject;
import ru.mellman.conv3rter.DataTasks;
import ru.mellman.conv3rter.Function;
import ru.mellman.conv3rter.R;
import ru.mellman.conv3rter.data_adapters.CoursesOfCurrency;
import ru.mellman.conv3rter.data_adapters.CoursesOfCurrencyAdapter;
import ru.mellman.conv3rter.data_adapters.CurrencyRate;
import ru.mellman.conv3rter.data_adapters.CurrencyRateAdapter;

public class MainActivity extends AppCompatActivity {
    private EditText _txtValueFrom, _txtValueTo;
    private ListView _listOfCurrencyRates;
    private Spinner _spinnerFrom, _spinnerTo;
    private SwitchMaterial _switchThemeMaterial;
    private ImageButton btnSwitchCurrency;
    private String TAG = MainActivity.class.getSimpleName();
    public static final String PREF = "converterPreferences";
    public static final String JSON_COURSES = "JSON_Courses";
    public static final String JSON_RATES = "JSON_Rates";
    public static final String LAST_UPDATE_DATETIME = "lastUpdate";
    private int _selectedPosFrom, _selectedPosTo;
    ArrayList<CoursesOfCurrency> courseList;
    CoursesOfCurrencyAdapter currencyAdapter;
    ArrayList<CurrencyRate> currencyRateList;
    CurrencyRateAdapter currencyRateAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        initSpinner();

        if (getIntent().hasExtra("cl")){
            courseList = getIntent().getParcelableArrayListExtra("cl");
        }
        if (getIntent().hasExtra("crl")){
            currencyRateList = getIntent().getParcelableArrayListExtra("crl");
        }
        if (savedInstanceState != null) {
            _txtValueFrom.setText(savedInstanceState.getString("AreaFrom"));

            courseList = savedInstanceState.getParcelableArrayList("courseList");
            currencyRateList = savedInstanceState.getParcelableArrayList("currencyRates");

        }
        currencyRateAdapter = new CurrencyRateAdapter(this, currencyRateList);
        _spinnerFrom.setAdapter(currencyRateAdapter);
        _spinnerTo.setAdapter(currencyRateAdapter);
        loadSpinnerPos();
        currencyAdapter = new CoursesOfCurrencyAdapter(this,R.layout.list_item ,courseList);
        _listOfCurrencyRates.setAdapter(currencyAdapter);

        _txtValueFrom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                calculateCourseBy(_txtValueFrom, _txtValueTo);

            }
        });

    }

    protected void onResume() {
        super.onResume();
        if (getIntent().hasExtra("fromValue")){
            _txtValueFrom.setText(getIntent().getStringExtra("fromValue"));
        }
    }
    protected void onPause(){
        super.onPause();
    }
    @Override
    public void recreate(){
        Intent main = getIntent();
        main.putExtra("fromValue", _txtValueFrom.getText().toString());
        startActivity(main);
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        finish();


    }
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("AreaFrom", _txtValueFrom.getText().toString());
        outState.putParcelableArrayList("courseList", courseList);
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
                calculateCourseBy(_txtValueFrom, _txtValueTo);
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
                calculateCourseBy(_txtValueFrom, _txtValueTo);
                //Toast.makeText(MainActivity.this, "POS: " + " value:" +valueOfCurrentValute, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        _spinnerTo.setOnItemSelectedListener(itemSelectedListenerTo);
    }


    private void calculateCourseBy(EditText editTextFrom, EditText editTextTo){
        if(!editTextFrom.getText().toString().equals("")){
            try {
                double _val = Double.parseDouble(editTextFrom.getText().toString());
                _val = Function.convert(currencyRateList.get(_selectedPosFrom).getRate(), currencyRateList.get(_selectedPosTo).getRate(),_val);
                editTextTo.setText(Function.getDecimalToFormat(_val));
            }
            catch (Exception e){
                editTextTo.setText("ERROR");
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
        _txtValueFrom = findViewById(R.id.valueFrom);
        _txtValueTo = findViewById(R.id.valueTo);
        _listOfCurrencyRates = findViewById(R.id.valuteListView);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
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
                _txtValueFrom.setText("0");
                calculateCourseBy(_txtValueFrom, _txtValueTo);
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
        _txtValueFrom.setShowSoftInputOnFocus(false);
        SharedPreferences sharedPreferences = getSharedPreferences(PREF,MODE_PRIVATE);
        checkThemeMod(sharedPreferences);
    }
    private void InputNum(int num){
        if(_txtValueFrom.getText().toString().equals("0")){
            String text = String.valueOf(num);
            _txtValueFrom.setText(text);
        }
        else
        {
            String text = _txtValueFrom.getText().toString() + num;
            _txtValueFrom.setText(text);
        }
    }
    private void InputDot(){
        int checkDot = _txtValueFrom.getText().toString().indexOf(".");
        if(checkDot==-1){
            String text = _txtValueFrom.getText().toString()+".";
            _txtValueFrom.setText(text);
            calculateCourseBy(_txtValueFrom, _txtValueTo);
        }
        else {
            Toast.makeText(MainActivity.this, getString(R.string.dot_in_line), Toast.LENGTH_LONG).show();
        }
    }
    private void DelNumber(){
        if(_txtValueFrom.length()-1==0)
        {
            _txtValueFrom.setText("0");
        }
        else
        {
            _txtValueFrom.setText(_txtValueFrom.getText().delete(_txtValueFrom.getText().length() - 1, _txtValueFrom.getText().length()));
        }
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
        if (_txtValueFrom.length()!=0){
            try {
                double val = Double.parseDouble(_txtValueTo.getText().toString());
                String s = Function.getDecimalToFormat(val);
                _txtValueTo.setText("");
                _txtValueFrom.setText(s);
            }
            catch (Exception e){
                _txtValueTo.setText("ERROR");
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
        if (Function.checkTheNeedForAnUpdate(sharedPreferences.getString(LAST_UPDATE_DATETIME, "")) && Function.isOnline(getApplicationContext())){
            try {
                DataObject data = DataTasks.getDataOfCurrency();
                courseList = data.getCoursesList();
                currencyRateList = data.getRatesList();
                String jsonCourses = data.getJsonObjectCourses();
                String jsonRates = data.getJsonObjectRates();

                SharedPreferences.Editor ed = sharedPreferences.edit();
                ed.putString(JSON_COURSES, jsonCourses);
                ed.putString(JSON_RATES,jsonRates);
                ed.putString(LAST_UPDATE_DATETIME, data.getDateUpdate());
                ed.apply();

                currencyAdapter.notifyDataSetChanged();
                currencyRateAdapter.notifyDataSetChanged();
            } catch (InterruptedException | TimeoutException e) {
                e.printStackTrace();
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "DB is actual", Toast.LENGTH_LONG).show();
        }
    }
}

