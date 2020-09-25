package ru.mellman.conv3rter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private EditText txtValueFrom, txtValueTo;
    private String TAG = MainActivity.class.getSimpleName();
    private ListView lwValute;
    private ArrayList<HashMap<String, String>> valuteList;
    private Spinner spinnerFrom, spinnerTo;
    private ArrayAdapter<String> spinnerAdapterFrom, spinnerAdapterTo;
    private MaterialToolbar toolbar;
    private SwitchMaterial switchMaterial;
    public static final String PREF = "converterPreferences";
    public static final String STREAM_JSON = "inputjSONStr";
    public static final String JSON_RATES = "jSON_Rates";
    public static final String DATE_NOW = "Date";
    SharedPreferences sharedPreferences;
    private int selectedPosFrom, selectedPosTo;
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
        valuteList = new ArrayList<>();
        lwValute = (ListView) findViewById(R.id.valuteListView);
        if (getIntent().hasExtra("cl")){
            courseList = getIntent().getParcelableArrayListExtra("cl");
        }
        if (getIntent().hasExtra("crl")){
            currencyRateList = getIntent().getParcelableArrayListExtra("crl");
        }
        if (savedInstanceState != null) {
            txtValueFrom.setText(savedInstanceState.getString("AreaFrom"));

            courseList = savedInstanceState.getParcelableArrayList("courseList");
            currencyRateList = savedInstanceState.getParcelableArrayList("currencyRates");

        }
        currencyRateAdapter = new CurrencyRateAdapter(this, currencyRateList);
        spinnerFrom.setAdapter(currencyRateAdapter);
        spinnerTo.setAdapter(currencyRateAdapter);
        loadSpinnerPos();
        currencyAdapter = new CoursesOfCurrencyAdapter(this,R.layout.list_item ,courseList);
        lwValute.setAdapter(currencyAdapter);


        TextEditWatcher textEditWatcher = new TextEditWatcher(txtValueFrom);
        txtValueFrom.addTextChangedListener(textEditWatcher);
        //calculateCourseBy(txtValueFrom, txtValueTo);
    }

    protected void onResume() {
        super.onResume();

    }
    @Override
    public void recreate(){

        startActivity(getIntent());
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        finish();

    }
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("AreaFrom", txtValueFrom.getText().toString());
        outState.putParcelableArrayList("courseList", courseList);
        outState.putParcelableArrayList("currencyRates", currencyRateList);
        outState.putInt("selectedPosTo", selectedPosTo);
        outState.putInt("selectedPosFrom", selectedPosFrom);
    }
    void initSpinner(){
        AdapterView.OnItemSelectedListener itemSelectedListenerFrom = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPosFrom = (int)parent.getItemIdAtPosition(position);
                saveSpinnerPos(selectedPosFrom, selectedPosTo);
                calculateCourseBy(txtValueFrom,txtValueTo);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        spinnerFrom.setOnItemSelectedListener(itemSelectedListenerFrom);


        AdapterView.OnItemSelectedListener itemSelectedListenerTo = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPosTo = (int)parent.getItemIdAtPosition(position);
                saveSpinnerPos(selectedPosFrom, selectedPosTo);
                calculateCourseBy(txtValueFrom,txtValueTo);
                //Toast.makeText(MainActivity.this, "POS: " + " value:" +valueOfCurrentValute, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        spinnerTo.setOnItemSelectedListener(itemSelectedListenerTo);
    }


    private void calculateCourseBy(EditText editTextFrom, EditText editTextTo){
        double val = Double.parseDouble(editTextFrom.getText().toString());
        double courseby = convert(currencyRateList.get(selectedPosFrom).getRate(), currencyRateList.get(selectedPosTo).getRate(),val);

        editTextTo.setText(getDecimalToFormat(courseby));
    }
    private Double convert(Double from, Double to, Double count){
        double value = (to*count)/from;
        return value;
    }
    private String getDecimalToFormat(Double decimal){
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("#0.00", decimalFormatSymbols);
        String s = decimalFormat.format(decimal);
        return s;
    }
    public class TextEditWatcher implements TextWatcher {
        public EditText editText;
        public TextEditWatcher(EditText et){
            super();
            editText=et;
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            calculateCourseBy(txtValueFrom,txtValueTo);
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
        ImageButton btnSwitch = findViewById(R.id.switchFromToButton);
        switchMaterial = findViewById(R.id.dayNightSwitcher);
        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo = findViewById(R.id.spinnerTo);
        txtValueFrom = findViewById(R.id.valueFrom);
        txtValueTo = findViewById(R.id.valueTo);
        toolbar = findViewById(R.id.toolbar);
        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        ImageButton btnUpdate = findViewById(R.id.updateImageButton);
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
                if(v.getId()==R.id.updateImageButton){
                    new GetValute().execute();
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
        btnSwitch.setOnClickListener(listener);
        btnDel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                txtValueFrom.setText("0");
                calculateCourseBy(txtValueFrom,txtValueTo);
                return true;
            }
        });
        btnUpdate.setOnClickListener(listener);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetValute().execute();
                pullToRefresh.setRefreshing(false);
            }
        });
        switchMaterial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPreferences = getSharedPreferences(PREF,MODE_PRIVATE);
                SharedPreferences.Editor ed = sharedPreferences.edit();
                if(!isChecked){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    ed.putBoolean("DayNight_Mode",false);
                    ed.apply();

                }
                else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    ed.putBoolean("DayNight_Mode",true);
                    ed.apply();

                }
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences(PREF,MODE_PRIVATE);
        checkThemeMod(sharedPreferences);
    }
    private void InputNum(int num){
        if(txtValueFrom.getText().toString().equals("0")){
            String text = String.valueOf(num);
            txtValueFrom.setText(text);
        }
        else
        {
            String text = txtValueFrom.getText().toString() + num;
            txtValueFrom.setText(text);
        }
    }
    private void InputDot(){
        int checkDot = txtValueFrom.getText().toString().indexOf(".");
        if(checkDot==-1){
            String text = txtValueFrom.getText().toString()+".";
            txtValueFrom.setText(text);
            calculateCourseBy(txtValueFrom,txtValueTo);
        }
        else {
            Toast.makeText(MainActivity.this, getString(R.string.dot_in_line), Toast.LENGTH_LONG).show();
        }
    }
    private void DelNumber(){
        if(txtValueFrom.length()-1==0)
        {
            txtValueFrom.setText("0");
        }
        else
        {
            txtValueFrom.setText(txtValueFrom.getText().delete(txtValueFrom.getText().length() - 1, txtValueFrom.getText().length()));
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
        spinnerFrom.setSelection(sharedPreferences.getInt("fromPos",34));
        spinnerTo.setSelection(sharedPreferences.getInt("toPos", 11));
    }
    private void SwitchCurrency( ){
        int posFrom = spinnerFrom.getSelectedItemPosition();
        int posTo = spinnerTo.getSelectedItemPosition();
        spinnerFrom.setSelection(posTo,true);
        spinnerTo.setSelection(posFrom, true);
        double val = Double.parseDouble(txtValueTo.getText().toString());
        String s = getDecimalToFormat(val);
        txtValueTo.setText("");
        txtValueFrom.setText(s);
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
            ImageButton btnUpdate = findViewById(R.id.updateImageButton);
            Animation rotate_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_anim);
            btnUpdate.startAnimation(rotate_anim);
        }

        @Override
        protected Void doInBackground(Void...arg0) {
            currencyRateList = getRate();
            SharedPreferences sharedPreferences;
            sharedPreferences = getSharedPreferences(PREF,MODE_PRIVATE);
            String jsonStr;
            String date = getDateNow();
            final String lastupdate = sharedPreferences.getString(DATE_NOW,"");
            if (date.equals(lastupdate)){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), getString(R.string.lastupdate) + lastupdate, Toast.LENGTH_LONG).show();
                    }
                });
            }
            else{
                HttpHandler steamHandler = new HttpHandler();
                jsonStr = steamHandler.makeServiceCall("https://www.cbr-xml-daily.ru/daily_json.js");
                //Log.e(TAG, "Response from url: " + jsonStr);
                if (jsonStr != null) {
                    sharedPreferences = getSharedPreferences(PREF,MODE_PRIVATE);
                    SharedPreferences.Editor ed = sharedPreferences.edit();
                    ed.putString(STREAM_JSON,jsonStr);
                    ed.putString(DATE_NOW, date);
                    ed.apply();
                    courseList = JSONObjParser.parsejSONToCourseList(jsonStr, getApplicationContext());
                } else {
                    Log.e(TAG, "Couldn't get jSON from server.");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Couldn't get json from server. Check LOG", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            currencyAdapter.notifyDataSetChanged();
            currencyRateAdapter.notifyDataSetChanged();
        }
    }
    private void checkThemeMod(SharedPreferences sharedPreferences){
        if (sharedPreferences.getBoolean("DayNight_Mode", false)){
            switchMaterial.setChecked(true);
        }
        else {
            switchMaterial.setChecked(false);
        }
    }

    private ArrayList<CurrencyRate> getRate() {
        ArrayList<CurrencyRate> rateList = new ArrayList<CurrencyRate>();
        SharedPreferences sharedPreferences;
        sharedPreferences = getSharedPreferences(PREF,MODE_PRIVATE);
        String jsonRate = sharedPreferences.getString(JSON_RATES,"");
        boolean jsonStrEmpty = sharedPreferences.getString(JSON_RATES,"").equals("");
        String date = getDateNow();
        if (date.equals(sharedPreferences.getString(DATE_NOW,"")) && !jsonStrEmpty){
            jsonRate=null;
        }
        else{
            HttpHandler steamHandler = new HttpHandler();
            String path = getString(R.string.json_rates);
            jsonRate = steamHandler.makeServiceCall(path);
            Log.e(TAG, "Response from url: " + jsonRate);
        }
        if (jsonRate==null){
            jsonRate=sharedPreferences.getString(JSON_RATES,"");
        }

        if (jsonRate != null) {
            SharedPreferences.Editor ed = sharedPreferences.edit();
            ed.putString(JSON_RATES,jsonRate);
            //ed.putString(DATE_NOW, date);
            ed.apply();
            rateList = JSONObjParser.parseJSONToCurrencyRateList(jsonRate, getApplicationContext());
        } else {
            Log.e(TAG, "Couldn't get jSON from server.");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Couldn't get json from server. Check LOG", Toast.LENGTH_LONG).show();
                }
            });
        }
        return rateList;
    }

}

