package ru.mellman.conv3rter;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ru.mellman.conv3rter.app.MainActivity;
import ru.mellman.conv3rter.data_adapters.CurrencyRate;
import ru.mellman.conv3rter.parse.HttpHandler;
import ru.mellman.conv3rter.parse.JSONObjParser;
import ru.mellman.conv3rter.data_adapters.CoursesOfCurrency;

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
                if (!futureJSONCourses.isDone()){
                    Log.e(TAG, "TASK JSONCourses Output: " + futureJSONCourses.get());
                }
                if (!futureJSONRates.isDone()){
                    Log.e(TAG, "TASK JSONRates Output: " + futureJSONRates.get());
                }
            }
            catch (InterruptedException | ExecutionException | TimeoutException e){e.printStackTrace();}
        }
    }

    /**
     * Load local data of courses
     * @param jsonCourse JSONObject of Courses
     * @param jsonRate JSONObject of Rates
     * @return DataObject when has: list of Courses, list of Rates
     * @throws InterruptedException ex---
     * @throws TimeoutException ThreadTimeout
     */
    public  static DataObject loadDataOfCurrency(String jsonCourse, String jsonRate) throws InterruptedException, TimeoutException{
        _jsonCourses = jsonCourse;
        _jsonRates = jsonRate;
        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<ArrayList<CoursesOfCurrency>> futureCourses = executorService.submit(new getCurrentCourseList());
        Future<ArrayList<CurrencyRate>> futureRates = executorService.submit(new getCurrentRateList());
        while (true){
            try{
                if (futureCourses.isDone() && futureRates.isDone()){
                    ArrayList<CoursesOfCurrency> currencyList = futureCourses.get(30, TimeUnit.SECONDS);
                    ArrayList<CurrencyRate> rateList = futureRates.get(30, TimeUnit.SECONDS);
                    executorService.shutdownNow();
                    return new DataObject(currencyList, rateList, "", "", "", "");
                }
                if (!futureCourses.isDone()){
                    Log.e(TAG, "TASK JSONCourses Output: " + futureCourses.get());
                }
                if (!futureRates.isDone()){
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

