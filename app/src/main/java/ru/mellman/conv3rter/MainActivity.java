package ru.mellman.conv3rter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private EditText txtValueFrom, txtValueTo;
    private String TAG = MainActivity.class.getSimpleName();
    private ListView lwValute;
    private ArrayList<HashMap<String, String>> valuteList;
    private Spinner spinnerFrom, spinnerTo;
    private ArrayAdapter<String> spinnerAdapterFrom, spinnerAdapterTo;
    private ArrayList keylist;
    private MaterialToolbar toolbar;
    public static final String PREF = "converterPreferences";
    public static final String STREAM_JSON = "inputjSONStr";
    public static final String DATE_NOW = "Date";
    SharedPreferences sharedPreferences;
    private int selectedCurrencyPos;
    ArrayList<CoursesOfCurrency> courseList;
    CoursesOfCurrencyAdapter currencyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        initSpinner();
        keylist = new ArrayList();
        valuteList = new ArrayList<>();
        lwValute = (ListView) findViewById(R.id.valuteListView);
        if (getIntent().hasExtra("cl")){
            courseList = getIntent().getParcelableArrayListExtra("cl");
        }
        if (savedInstanceState != null) {
            txtValueFrom.setText(savedInstanceState.getString("AreaFrom"));
            keylist = savedInstanceState.getStringArrayList("keylist");
            courseList = savedInstanceState.getParcelableArrayList("courseList");
            selectedCurrencyPos = savedInstanceState.getInt("selectedCurrencyPos");
            spinnerTo.setSelection(selectedCurrencyPos,true);
            calculateCourseBy(txtValueFrom, txtValueTo, selectedCurrencyPos);
        }
        else
        {
            assert courseList != null;
            keylist = getKeysNames(courseList);
        }

        String[] fromData = {"RUB"};
        spinnerAdapterFrom = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, fromData);
        spinnerFrom.setAdapter(spinnerAdapterFrom);


        spinnerAdapterTo = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, keylist);
        spinnerAdapterTo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTo.setAdapter(spinnerAdapterTo);

        currencyAdapter = new CoursesOfCurrencyAdapter(this,R.layout.list_item ,courseList);
        lwValute.setAdapter(currencyAdapter);
        TextEditWatcher textEditWatcher = new TextEditWatcher(txtValueFrom);
        txtValueFrom.addTextChangedListener(textEditWatcher);

    }

    protected void onResume() {
        super.onResume();

    }

    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("AreaFrom", txtValueFrom.getText().toString());
        outState.putStringArrayList("keylist", keylist);
        outState.putParcelableArrayList("courseList", courseList);
        outState.putInt("selectedCurrencyPos", selectedCurrencyPos);

    }
    void initSpinner(){
        AdapterView.OnItemSelectedListener itemSelectedListenerFrom = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        spinnerFrom.setOnItemSelectedListener(itemSelectedListenerFrom);


        AdapterView.OnItemSelectedListener itemSelectedListenerTo = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCurrencyPos = (int)parent.getItemIdAtPosition(position);
                calculateCourseBy(txtValueFrom,txtValueTo, selectedCurrencyPos);
                //Toast.makeText(MainActivity.this, "POS: " + " value:" +valueOfCurrentValute, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        spinnerTo.setOnItemSelectedListener(itemSelectedListenerTo);
    }


    private void calculateCourseBy(EditText editTextFrom, EditText editTextTo, int pos){
        double val = Double.parseDouble(editTextFrom.getText().toString());
        double courseby = courseList.get(pos).getCourseByCurrentCurrency(val);
        String s = String.format("%.2f", courseby);
        editTextTo.setText(s);
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
            calculateCourseBy(txtValueFrom,txtValueTo,selectedCurrencyPos);
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
                calculateCourseBy(txtValueFrom,txtValueTo, selectedCurrencyPos);
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
        calculateCourseBy(txtValueFrom,txtValueTo, selectedCurrencyPos);
    }
    private void InputDot(){
        int checkDot = txtValueFrom.getText().toString().indexOf(".");
        if(checkDot==-1){
            String text = txtValueFrom.getText().toString()+".";
            txtValueFrom.setText(text);
            calculateCourseBy(txtValueFrom,txtValueTo, selectedCurrencyPos);
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
        calculateCourseBy(txtValueFrom, txtValueTo, selectedCurrencyPos);
    }
    private void SwitchCurrency( ){
        /*
        int posFrom = spinnerFrom.getSelectedItemPosition();
        int posTo = spinnerTo.getSelectedItemPosition();
        if(spinnerFrom.getAdapter()==spinnerAdapterFrom){
            spinnerFrom.setAdapter(spinnerAdapterTo);
            spinnerTo.setAdapter(spinnerAdapterFrom);
        }
        if(spinnerFrom.getAdapter()==spinnerAdapterTo){
            spinnerFrom.setAdapter(spinnerAdapterFrom);
            spinnerTo.setAdapter(spinnerAdapterTo);
        }
        spinnerFrom.setSelection(posTo,true);
        spinnerTo.setSelection(posFrom, true);
        String temp = txtValueFrom.getText().toString();
        txtValueFrom.setText(txtValueTo.getText().toString());
        txtValueTo.setText(temp);*/
        Toast.makeText(getApplicationContext(), "Не работает. :'(", Toast.LENGTH_LONG).show();
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
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            currencyAdapter.notifyDataSetChanged();
            keylist = getKeysNames(courseList);
            spinnerAdapterTo.notifyDataSetChanged();
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
    private  ArrayList getKeysNames(ArrayList<CoursesOfCurrency> courseList){
        ArrayList keysList = new ArrayList();
        for (int i = 0; i<courseList.size(); i++) keysList.add(courseList.get(i).getCharCode());
        return keysList;
    }
}

