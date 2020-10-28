package ru.mellman.conv3rter;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ru.mellman.conv3rter.activity.MainActivity;
import ru.mellman.conv3rter.data_course_of_currency.Courses;
import ru.mellman.conv3rter.data_course_of_currency.CoursesConstructor;
import ru.mellman.conv3rter.data_course_of_currency.CurrencyInfo;
import ru.mellman.conv3rter.data_course_of_currency.CurrencyOfRates;
import ru.mellman.conv3rter.data_course_of_currency.CurrencyRate;
import ru.mellman.conv3rter.data_course_of_currency.DataLists;
import ru.mellman.conv3rter.data_course_of_currency.ParseOfCurrencyInfo;
import ru.mellman.conv3rter.parse.HttpHandler;
import ru.mellman.conv3rter.parse.JSONObjParser;

public class DataTasks {
    private static String TAG = MainActivity.class.getSimpleName();
    private static String _jsonCourses;
    private static String _jsonRates;


    /**
     * Load data of courses from Network
     * @return DataObject when has: list of Courses, list of Rates, JSONObject Course list, JSONObject Rate list
     * @throws InterruptedException ex
     * @throws TimeoutException ex
     */
    ///
    public static DataObject getDataOfCurrency() throws InterruptedException, TimeoutException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<String> futureJSONCourses = executorService.submit(new getCurrentCourseJSONObject());
        Future<String> futureJSONRates = executorService.submit(new getCurrentRateJSONObject());
        while (true){
            try{
                if (futureJSONCourses.isDone() && futureJSONRates.isDone()){
                    _jsonCourses = futureJSONCourses.get(10, TimeUnit.SECONDS);
                    Future<ArrayList<CoursesOfCurrency>> futureCourses = executorService.submit(new getCurrentCourseList());

                    _jsonRates = futureJSONRates.get(10, TimeUnit.SECONDS);
                    Future<ArrayList<CurrencyRate>> futureRates = executorService.submit(new getCurrentRateList());

                    Future<String> futureDateTimeUpdate = executorService.submit(new getDateTimeLastUpdate());
                    Future<String> futureDateTimePreviousUpdate = executorService.submit(new getDateTimePreviousUpdate());
                    ArrayList<CoursesOfCurrency> _currencyList = futureCourses.get(30, TimeUnit.SECONDS);
                    ArrayList<CurrencyRate> _rateList = futureRates.get(30, TimeUnit.SECONDS);

                    String dateTimeUpdate = futureDateTimeUpdate.get(10, TimeUnit.SECONDS);
                    String dateTimePreviousUpdate = futureDateTimePreviousUpdate.get(10, TimeUnit.SECONDS);
                    executorService.shutdownNow();
                    return new DataObject(_currencyList, _rateList, _jsonCourses, _jsonRates, dateTimeUpdate, dateTimePreviousUpdate);
                }
                /*
                if (!futureJSONCourses.isDone()){
                    Log.e(TAG, "TASK JSONCourses Output: " + futureJSONCourses.get());
                }
                if (!futureJSONRates.isDone()){
                    Log.e(TAG, "TASK JSONRates Output: " + futureJSONRates.get());
                }*/
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }
        }
    }

    public static DataLists getData(Context context) {
        ArrayList<CurrencyRate> previousDayCourses;
        ArrayList<CurrencyRate> currentDayCourses;
        HashMap<String, CurrencyInfo> currencyInfo;
        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<String> futureRates = executorService.submit(new getCourseInfoJSONObject());
        Future<HashMap<String, CurrencyInfo>> futureCurrencyInfoList = executorService.submit(new ParseOfCurrencyInfo(context));
        while (true) {
            try {
                if (futureRates.isDone()) {
                    String ratesJSONObject = futureRates.get(30, TimeUnit.SECONDS);
                    Future<ArrayList<CurrencyRate>> futureYesterdayDateRateList = executorService.submit(new CurrencyOfRates(ratesJSONObject, Function.getYesterdayDate()));
                    Future<ArrayList<CurrencyRate>> futureCurrentDateRateList = executorService.submit(new CurrencyOfRates(ratesJSONObject, Function.getCurrentDate()));
                    while (true) {
                        if (futureYesterdayDateRateList.isDone() && futureCurrentDateRateList.isDone()) {
                            previousDayCourses = futureYesterdayDateRateList.get(20, TimeUnit.SECONDS);
                            currentDayCourses = futureCurrentDateRateList.get(20, TimeUnit.SECONDS);
                            if (futureCurrencyInfoList.isDone()) {
                                currencyInfo = futureCurrencyInfoList.get(20, TimeUnit.SECONDS);
                                return new DataLists(previousDayCourses, currentDayCourses, currencyInfo);
                            }
                            executorService.shutdownNow();
                        }
                    }
                }

            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }
        }
    }

    public static ArrayList<Courses> getCourseList(Context context) {
        DataLists lists = getData(context);

        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<ArrayList<Courses>> futureCourses = executorService.submit(new CoursesConstructor(lists));
        while (true) {
            try {
                if (futureCourses.isDone()) {
                    ArrayList<Courses> courses = futureCourses.get(20, TimeUnit.SECONDS);
                    executorService.shutdownNow();
                    return courses;
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }
        }
    }

    private static class getCourseInfoJSONObject implements Callable<String> {
        @Override
        public String call() throws Exception {
            HttpHandler steamHandler = new HttpHandler();
            String dateNow = Function.getCurrentDate();
            String dateYesterday = Function.getYesterdayDate();
            String jsonObject = "https://api.exchangeratesapi.io/history?start_at=" + dateYesterday + "&end_at=" + dateNow + "";
            return steamHandler.makeServiceCall(jsonObject);
        }
    }


    /**
     * Load local data of courses
     *
     * @param jsonCourse JSONObject of Courses
     * @param jsonRate   JSONObject of Rates
     * @return DataObject when has: list of Courses, list of Rates
     * @throws InterruptedException ex---
     * @throws TimeoutException     ThreadTimeout
     */
    @NonNull
    @Contract("_, _ -> new")
    public static DataObject loadDataOfCurrency(String jsonCourse, String jsonRate) throws InterruptedException, TimeoutException {
        _jsonCourses = jsonCourse;
        _jsonRates = jsonRate;
        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<ArrayList<CoursesOfCurrency>> futureCourses = executorService.submit(new getCurrentCourseList());
        Future<ArrayList<CurrencyRate>> futureRates = executorService.submit(new getCurrentRateList());
        while (true) {
            try {
                if (futureCourses.isDone() && futureRates.isDone()) {
                    ArrayList<CoursesOfCurrency> currencyList = futureCourses.get(30, TimeUnit.SECONDS);
                    ArrayList<CurrencyRate> rateList = futureRates.get(30, TimeUnit.SECONDS);
                    executorService.shutdownNow();
                    return new DataObject(currencyList, rateList, "", "", "", "");
                }
                if (!futureCourses.isDone()) {
                    Log.e(TAG, "TASK JSONCourses Output: " + futureCourses.get());
                }
                if (!futureRates.isDone()) {
                    Log.e(TAG, "TASK JSONRates Output: " + futureRates.get());
                }
            }
            catch (InterruptedException | ExecutionException | TimeoutException e){e.printStackTrace();}
        }
    }

    /**
     * JSON response for Courses
     */
    private static class getCurrentCourseJSONObject implements Callable<String>{
        @Override
        public String call() throws Exception {
            HttpHandler steamHandler = new HttpHandler();
            String jsonObject = "https://www.cbr-xml-daily.ru/daily_json.js";
            return steamHandler.makeServiceCall(jsonObject);
        }
    }
    /**
     * JSON response for Rates
     */
    private static class getCurrentRateJSONObject implements Callable<String>{
        @Override
        public String call() throws Exception {
            HttpHandler steamHandler = new HttpHandler();
            String jsonObject = "https://www.cbr-xml-daily.ru/latest.js";
            return steamHandler.makeServiceCall(jsonObject);
        }
    }

    /**
     * Return list of Courses from JSONObject
     */
    private static class getCurrentCourseList implements Callable<ArrayList<CoursesOfCurrency>>{
        ArrayList<CoursesOfCurrency> list = new ArrayList<>();
        @Override
        public ArrayList<CoursesOfCurrency> call() throws JSONException {
            if (_jsonCourses != null) {
                list = JSONObjParser.parseJSONToCourseList(_jsonCourses);
            } else {
                Log.e(TAG, "Couldn't get jSON from server.");
            }
            return list;
        }
    }
    /**
     * Return list of Rates from JSONObject
     */
    private static class getCurrentRateList implements Callable<ArrayList<CurrencyRate>>{
        ArrayList<CurrencyRate> list = new ArrayList<>();
        @Override
        public ArrayList<CurrencyRate> call() throws JSONException {
            if (_jsonRates != null) {
                list = JSONObjParser.parseJSONToRateList(_jsonRates);
            } else {
                Log.e(TAG, "Couldn't get jSON from server.");
            }
            return list;
        }
    }
    private static class getDateTimeLastUpdate implements Callable<String>{
        @Override
        public String call() throws Exception {
            JSONObject CurrencyObj = new JSONObject(_jsonCourses);
            return CurrencyObj.getString("Date");
        }
    }
    private static class getDateTimePreviousUpdate implements Callable<String>{
        @Override
        public String call() throws Exception {
            JSONObject CurrencyObj = new JSONObject(_jsonCourses);
            return CurrencyObj.getString("PreviousDate");
        }
    }
}

