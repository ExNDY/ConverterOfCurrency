package ru.mellman.conv3rter;

import java.util.ArrayList;

import ru.mellman.conv3rter.data_adapters.CoursesOfCurrency;
import ru.mellman.conv3rter.data_adapters.CurrencyRate;

final public class DataObject {
    private ArrayList<CoursesOfCurrency> coursesList;
    private ArrayList<CurrencyRate> ratesList;
    private String jsonObjectCourses;
    private String jsonObjectRates;

    public DataObject(ArrayList<CoursesOfCurrency> coursesList, ArrayList<CurrencyRate> ratesList, String jsonObjectCourses, String jsonObjectRates){
        this.coursesList = coursesList;
        this.ratesList = ratesList;
        this.jsonObjectCourses = jsonObjectCourses;
        this.jsonObjectRates = jsonObjectRates;
    }
    public ArrayList<CoursesOfCurrency> getCoursesList(){
        return coursesList;
    }
    public ArrayList<CurrencyRate> getRatesList(){
        return ratesList;
    }
    public String getJsonObjectCourses(){
        return jsonObjectCourses;
    }
    public String getJsonObjectRates(){
        return jsonObjectRates;
    }

}
