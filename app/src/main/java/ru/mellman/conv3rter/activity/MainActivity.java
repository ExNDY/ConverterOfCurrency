package ru.mellman.conv3rter.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;

import ru.mellman.conv3rter.BottomSheetDialog;
import ru.mellman.conv3rter.DataTasks;
import ru.mellman.conv3rter.Function;
import ru.mellman.conv3rter.PreferenceManager;
import ru.mellman.conv3rter.R;
import ru.mellman.conv3rter.Snackbar;
import ru.mellman.conv3rter.adapters.CourseDataAdapter;
import ru.mellman.conv3rter.data_course_of_currency.ResultDTObj;
import ru.mellman.conv3rter.lists.Courses;
import ru.mellman.conv3rter.lists.CurrencyRate;
import ru.mellman.conv3rter.adapters.RatesDataAdapter;
import ru.mellman.conv3rter.network.NetworkUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView currencyValueFrom, currencyValueTo;
    private Spinner spinnerFrom, spinnerTo;
    private SwitchMaterial switchThemeMaterial;
    private ImageButton btnSwitchCurrency;

    public static final String PREF = "converterPreferences";
    public static final String COURSES = "CoursesList";
    public static final String RATES = "RatesList";
    public static final String LAST_UPDATE_DATETIME = "lastUpdate";

    ArrayList<Courses> coursesList;
    ArrayList<CurrencyRate> ratesList;
    RecyclerView coursesListRv;
    CourseDataAdapter adapter;
    RatesDataAdapter currencyRateListAdapter;
    BottomSheetDialog bottomSheetDialog;
    String dateUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        initSpinner();
        if (getIntent().hasExtra(COURSES)) {
            coursesList = getIntent().getParcelableArrayListExtra(COURSES);
        }
        if (getIntent().hasExtra(RATES)) {
            ratesList = getIntent().getParcelableArrayListExtra(RATES);
        }
        if (savedInstanceState != null) {

            currencyValueFrom.setText(savedInstanceState.getString("AreaFrom"));
            coursesList = savedInstanceState.getParcelableArrayList(COURSES);
            ratesList = savedInstanceState.getParcelableArrayList(RATES);
        }
        currencyRateListAdapter = new RatesDataAdapter(this, ratesList);
        spinnerFrom.setAdapter(currencyRateListAdapter);
        spinnerTo.setAdapter(currencyRateListAdapter);
        PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
        spinnerFrom.setSelection(preferenceManager.getSpinnerPosFrom(), true);
        spinnerTo.setSelection(preferenceManager.getSpinnerPosTo(), true);

        adapter = new CourseDataAdapter(getApplicationContext(), coursesList, "USD", new CourseDataAdapter.CourseListInterface() {
            @Override
            public void onItemClick(int position) {
                Courses c = coursesList.get(position);
                bottomSheetDialog = new BottomSheetDialog();
                Bundle bundle = new Bundle();
                bundle.putParcelable("currency", c);
                bottomSheetDialog.setArguments(bundle);
                if(getSupportFragmentManager().findFragmentByTag(BottomSheetDialog.TAG)==null){
                    bottomSheetDialog.show(getSupportFragmentManager(), BottomSheetDialog.TAG);
                }
            }

            @Override
            public void onLongItemClick(int position) {

            }
        });
        coursesListRv.setAdapter(adapter);
    }

    protected void onResume() {
        super.onResume();
        if (getIntent().hasExtra("fromValue")) {
            currencyValueFrom.setText(getIntent().getStringExtra("fromValue"));
        }
    }

    protected void onPause() {
        super.onPause();

        Intent main = getIntent();
        main.putExtra("fromValue", currencyValueFrom.getText().toString());
    }

    @Override
    public void recreate() {
        Intent main = getIntent();
        main.putExtra("fromValue", currencyValueFrom.getText().toString());
        startActivity(main);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("AreaFrom", currencyValueFrom.getText().toString());
        outState.putParcelableArrayList(COURSES, coursesList);
        outState.putParcelableArrayList(RATES, ratesList);
        outState.putInt("selectedPosTo", spinnerFrom.getSelectedItemPosition());
        outState.putInt("selectedPosFrom", spinnerTo.getSelectedItemPosition());
    }

    void initSpinner() {
        spinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
                preferenceManager.saveSpinnerPosFrom(i);
                calculateCourseBy(currencyValueFrom.getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
                preferenceManager.saveSpinnerPosTo(i);
                calculateCourseBy(currencyValueFrom.getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private void calculateCourseBy(@NonNull String editTextFrom) {
        if (!editTextFrom.equals("")) {
            try {
                double _val = Double.parseDouble(editTextFrom);
                _val = Function.convert(ratesList.get(spinnerFrom.getSelectedItemPosition()).getRate(), ratesList.get(spinnerTo.getSelectedItemPosition()).getRate(), _val);
                String result = Function.getDecimalToFormat(_val);
                currencyValueTo.setText(result);
            } catch (Exception e) {
                currencyValueTo.setText(getResources().getString(R.string.error));
            }

        }

    }

    private void initUI() {
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
        switchThemeMaterial = findViewById(R.id.dayNightSwitcher);
        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo = findViewById(R.id.spinnerTo);
        currencyValueFrom = findViewById(R.id.valueFrom);
        currencyValueTo = findViewById(R.id.valueTo);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        coursesListRv = findViewById(R.id.coursesList);
        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);

        pullToRefresh.setColorSchemeColors(getResources().getColor(R.color.PullToRefreshColor, getApplicationContext().getTheme()));
        pullToRefresh.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.PullToRefreshProgressBarColor, getApplicationContext().getTheme()));
        currencyValueTo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences(PREF, MODE_PRIVATE);
                CurrencyRate from = (CurrencyRate) spinnerFrom.getSelectedItem();
                CurrencyRate to = (CurrencyRate) spinnerTo.getSelectedItem();
                String buf = getResources().getString(R.string.clipboardstring_1) + " " + currencyValueFrom.getText().toString() + from.getCharCode()
                        + " " + getResources().getString(R.string.clipboardstring_2) + " " + currencyValueTo.getText().toString()
                        + to.getCharCode() + " " + getResources().getString(R.string.clipboardstring_3)
                        + Function.getDate(sharedPreferences.getString(LAST_UPDATE_DATETIME, ""));
                Function.copyToClipboard(buf, getApplicationContext());
                showSnackBar(getResources().getString(R.string.copy_to_clipboard));
                return true;
            }
        });
        btn0.setOnClickListener(this);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);
        btn8.setOnClickListener(this);
        btn9.setOnClickListener(this);
        btnDot.setOnClickListener(this);
        btnDel.setOnClickListener(this);
        btnSwitchCurrency.setOnClickListener(this);
        btnDel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                currencyValueFrom.setText("0");
                calculateCourseBy(currencyValueFrom.getText().toString());
                return true;
            }
        });
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworkUtils.pingInternetConnection()){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ResultDTObj newData = DataTasks.update(getApplicationContext());
                            if (newData.getResult().equals("FAIL")){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        pullToRefresh.setRefreshing(false);
                                        showSnackBar(getString(R.string.fail_on_update));
                                    }
                                });
                            }
                            else
                            if(newData.getResult().equals("OK")){
                                coursesList = newData.getCourses();
                                ratesList = newData.getRates();
                                dateUpdate = newData.getDateOfUpdate();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        pullToRefresh.setRefreshing(false);
                                        PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
                                        preferenceManager.saveData(coursesList, ratesList, dateUpdate);
                                        adapter.notifyDataSetChanged();
                                        currencyRateListAdapter.notifyDataSetChanged();
                                        showSnackBar(getString(R.string.dataBaseWasUpdated));
                                    }
                                });
                            }
                        }
                    }).start();
                }
                else {
                    pullToRefresh.setRefreshing(false);
                    showSnackBar(getString(R.string.offline));
                }
            }
        });
        switchThemeMaterial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPreferences = getSharedPreferences(PREF, MODE_PRIVATE);
                SharedPreferences.Editor ed = sharedPreferences.edit();
                if (!isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    ed.putBoolean("DayNight_Mode", false);

                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    ed.putBoolean("DayNight_Mode", true);

                }
                ed.apply();
            }
        });

        currencyValueFrom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                calculateCourseBy(currencyValueFrom.getText().toString());
            }
        });
        currencyValueFrom.setShowSoftInputOnFocus(false);
        SharedPreferences sharedPreferences = getSharedPreferences(PREF, MODE_PRIVATE);
        checkThemeMod(sharedPreferences);
    }

    private void InputNum(int num) {
        String txt = currencyValueFrom.getText().toString();
        int length = txt.length();
        if (length < 15) {
            if (txt.contains(".")) {
                int checkDot = txt.indexOf(".");
                int dif = txt.length() - checkDot;
                if (dif <= 2) {
                    if (txt.equals("0")) {
                        String text = String.valueOf(num);
                        currencyValueFrom.setText(text);
                    } else {
                        String text = txt + num;
                        currencyValueFrom.setText(text);
                    }
                } else {
                    showSnackBar(getString(R.string.after_dots_symbol_limit));
                }
            } else {
                if (txt.equals("0")) {
                    String text = String.valueOf(num);
                    currencyValueFrom.setText(text);
                } else {
                    String text = txt + num;
                    currencyValueFrom.setText(text);
                }
            }
        } else {
            showSnackBar(getString(R.string.string_limit));
        }

    }

    private void InputDot() {
        String txt = currencyValueFrom.getText().toString();
        int checkDot = txt.indexOf(".");
        if (checkDot == -1) {
            String text = txt + ".";
            currencyValueFrom.setText(text);
        } else {
            showSnackBar(getString(R.string.dot_in_line));
        }
    }

    private void DelNumber() {
        if (currencyValueFrom.length() != 0) {
            if (currencyValueFrom.length() - 1 == 0) {
                currencyValueFrom.setText("0");
            } else {
                String txt = currencyValueFrom.getText().toString();
                currencyValueFrom.setText(txt.substring(0, txt.length() - 1));
            }
        }
    }

    private void SwitchCurrency() {

        Animation rotate180_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate180_anim);
        btnSwitchCurrency.startAnimation(rotate180_anim);

        int posFrom = spinnerFrom.getSelectedItemPosition();
        int posTo = spinnerTo.getSelectedItemPosition();
        spinnerFrom.setSelection(posTo, true);
        spinnerTo.setSelection(posFrom, true);


        if (currencyValueFrom.length() != 0) {
            try {
                double val = Double.parseDouble(currencyValueTo.getText().toString());
                String s = Function.getDecimalToFormat(val);
                currencyValueTo.setText("");
                currencyValueFrom.setText(s);
            } catch (Exception e) {
                currencyValueTo.setText(getResources().getString(R.string.error));
            }

        }
    }

    private void checkThemeMod(@NonNull SharedPreferences sharedPreferences) {
        switchThemeMaterial.setChecked(sharedPreferences.getBoolean("DayNight_Mode", false));
    }

    /*
        private void checkUpdate(@NonNull SharedPreferences sharedPreferences) {
            boolean check = Function.checkTheNeedForAnUpdate(sharedPreferences.getString(LAST_UPDATE_DATETIME, ""));
            if (check && Function.isOnline(getApplicationContext())) {
                try {
                    DataObject data = DataTasks.getDataOfCurrency();
                    currencyList = data.getCoursesList();
                    currencyRateList = data.getRatesList();
                    String jsonCourses = data.getJsonObjectCourses();
                    String jsonRates = data.getJsonObjectRates();

                    SharedPreferences.Editor ed = sharedPreferences.edit();
                    ed.putString(JSON_COURSES, jsonCourses);
                    ed.putString(JSON_RATES, jsonRates);
                    ed.putString(LAST_UPDATE_DATETIME, data.getDateUpdate());
                    ed.putString(PREVIOUS_UPDATE_DATETIME, data.getDatePreviousUpdate());
                    ed.apply();

                    currencyListAdapter.notifyDataSetChanged();
                    currencyRateListAdapter.notifyDataSetChanged();
                    SnackBarShow(getResources().getString(R.string.dataBaseWasUpdated));
                } catch (InterruptedException | TimeoutException e) {
                    e.printStackTrace();
                }

            } else {
                if (!check && Function.isOnline(getApplicationContext())) {
                    SnackBarShow(getResources().getString(R.string.dataBaseIsActual));
                }
                if (!Function.isOnline(getApplicationContext())) {
                    SnackBarShow(getResources().getString(R.string.offline));
                }

            }
        }
    */
    private void showSnackBar(String message) {
        ViewGroup view = findViewById(android.R.id.content);
        final Snackbar snackbar = Snackbar.make(view, Snackbar.LENGTH_SHORT);
        View snackView = snackbar.getView();
        snackView.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorOfSnackBar, getApplicationContext().getTheme()));
        snackbar.setText(message);
        snackbar.show();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button0) {
            InputNum(0);
        }
        if (view.getId() == R.id.button1) {
            InputNum(1);
        }
        if (view.getId() == R.id.button2) {
            InputNum(2);
        }
        if (view.getId() == R.id.button3) {
            InputNum(3);
        }
        if (view.getId() == R.id.button4) {
            InputNum(4);
        }
        if (view.getId() == R.id.button5) {
            InputNum(5);
        }
        if (view.getId() == R.id.button6) {
            InputNum(6);
        }
        if (view.getId() == R.id.button7) {
            InputNum(7);
        }
        if (view.getId() == R.id.button8) {
            InputNum(8);
        }
        if (view.getId() == R.id.button9) {
            InputNum(9);
        }
        if (view.getId() == R.id.buttonDot) {
            InputDot();
        }
        if (view.getId() == R.id.buttonDel) {
            DelNumber();
        }
        if (view.getId() == R.id.switchFromToButton) {
            SwitchCurrency();
        }
    }
}

