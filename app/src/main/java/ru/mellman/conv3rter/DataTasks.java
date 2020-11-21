package ru.mellman.conv3rter;

import android.content.Context;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ru.mellman.conv3rter.activity.MainActivity;
import ru.mellman.conv3rter.data_course_of_currency.ResultDTObj;
import ru.mellman.conv3rter.lists.Courses;
import ru.mellman.conv3rter.data_course_of_currency.CoursesConstructor;
import ru.mellman.conv3rter.lists.CurrencyInfo;
import ru.mellman.conv3rter.parse.ParseOfCurrencyRates;
import ru.mellman.conv3rter.lists.CurrencyRate;
import ru.mellman.conv3rter.data_course_of_currency.DataLists;
import ru.mellman.conv3rter.parse.ParseOfCurrencyInfo;
import ru.mellman.conv3rter.network.HttpHandler;

public class DataTasks {
    private static String TAG = MainActivity.class.getSimpleName();

    @NonNull
    public static ResultDTObj update(Context context){
        ArrayList<CurrencyRate> latestRatesList = null;
        ArrayList<CurrencyRate> previousRatesList = null;
        HashMap<String, CurrencyInfo> currencyInfo = null;
        ArrayList<Courses> courses = null;
        String latestRates = "";
        String previousRates = "";
        String dateCurrentRates = "";
        boolean isLive = true;
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        Future<String> futureLatestRates = executorService.submit(new getLatestRatesJSONObject());
        while (isLive) {
            try {
                latestRates = futureLatestRates.get(30, TimeUnit.SECONDS);
                Future<String> futureDateOfUpdate = executorService.submit(new getDateTimeLastUpdate(latestRates));
                while (true) {
                    if (futureDateOfUpdate.isDone()) {
                        dateCurrentRates = futureDateOfUpdate.get(5, TimeUnit.SECONDS);
                        Future<String> futurePreviousRates = executorService.submit(new getPreviousRatesJSONObject(dateCurrentRates));
                        Future<HashMap<String, CurrencyInfo>> futureCurrencyInfoList = executorService.submit(new ParseOfCurrencyInfo(context));
                        while (true) {
                            try {
                                if (futurePreviousRates.isDone()) {
                                    previousRates = futurePreviousRates.get(10, TimeUnit.SECONDS);
                                    Future<ArrayList<CurrencyRate>> futureLatestRateList = executorService.submit(new ParseOfCurrencyRates(latestRates));
                                    Future<ArrayList<CurrencyRate>> futurePreviousRateList = executorService.submit(new ParseOfCurrencyRates(previousRates));
                                    while (true) {
                                        if (futureLatestRateList.isDone() && futurePreviousRateList.isDone() && futureCurrencyInfoList.isDone()) {
                                            latestRatesList = futureLatestRateList.get(20, TimeUnit.SECONDS);
                                            previousRatesList = futurePreviousRateList.get(20, TimeUnit.SECONDS);
                                            currencyInfo = futureCurrencyInfoList.get(20, TimeUnit.SECONDS);
                                            Collections.sort(latestRatesList,Compare.RatesNameComparator);
                                            Collections.sort(previousRatesList,Compare.RatesNameComparator);
                                            Future<ArrayList<Courses>> futureCourses = executorService.submit(new CoursesConstructor(previousRatesList, latestRatesList, currencyInfo));
                                            while (true) {
                                                try {
                                                    if (futureCourses.isDone()) {
                                                        courses = futureCourses.get(20, TimeUnit.SECONDS);
                                                        executorService.shutdownNow();
                                                        Collections.sort(courses, Compare.CoursesNameComparator);
                                                        isLive = false;
                                                        return new ResultDTObj(courses, latestRatesList, dateCurrentRates, "OK");
                                                    }
                                                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }
                                }

                            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } catch (ExecutionException | InterruptedException | CancellationException | TimeoutException exception) {
                exception.printStackTrace();
                return new ResultDTObj(null, null, null, "ERROR");

            }
        }
        return null;
    };

    private static class getLatestRatesJSONObject implements Callable<String> {
        @Override
        public String call() throws Exception {
            HttpHandler steamHandler = new HttpHandler();
            String jsonObject = "https://api.exchangeratesapi.io/latest?base=USD";
            return steamHandler.makeServiceCall(jsonObject);
        }
    }

    private static class getPreviousRatesJSONObject implements Callable<String> {
        private final String latestUpdateDate;

        public getPreviousRatesJSONObject(String latestUpdateDate) {
            this.latestUpdateDate = latestUpdateDate;
        }

        @Override
        public String call() throws Exception {
            HttpHandler steamHandler = new HttpHandler();
            String previousDate = Function.getPreviousDate(latestUpdateDate);
            String jsonObject = "https://api.exchangeratesapi.io/" + previousDate + "?base=USD";
            return steamHandler.makeServiceCall(jsonObject);
        }
    }


    private static class getDateTimeLastUpdate implements Callable<String> {
        private final String stream;

        public getDateTimeLastUpdate(String stream) {
            this.stream = stream;
        }

        @Override
        public String call() throws Exception {
            JSONObject CurrencyObj = new JSONObject(stream);
            return CurrencyObj.getString("date");
        }
    }
}

