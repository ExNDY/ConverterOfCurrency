package ru.mellman.conv3rter;

import java.util.ArrayList;

import ru.mellman.conv3rter.data_adapters.CoursesOfCurrency;
import ru.mellman.conv3rter.data_adapters.CurrencyRate;

final public class DataObject {
    private ArrayList<CoursesOfCurrency> coursesList;
    private ArrayList<CurrencyRate> ratesList;
    private String jsonObjectCourses;
    private String jsonObjectRates;
    private String dateUpdate;
    private String datePreviousUpdate;

    /**
     *
     * @param coursesList List of Courses of currency
     * @param ratesList List of Rates of currency
     * @param jsonObjectCourses JSON string with Data of Courses
     * @param jsonObjectRates JSON string with Data of Rates of currency
     * @param dateUpdate Date of last update
     * @param datePreviousUpdate Date of previous update
     */
    public DataObject(ArrayList<CoursesOfCurrency> coursesList, ArrayList<CurrencyRate> ratesList, String jsonObjectCourses, String jsonObjectRates, String dateUpdate, String datePreviousUpdate){
        this.coursesList = coursesList;
        this.ratesList = ratesList;
        this.jsonObjectCourses = jsonObjectCourses;
        this.jsonObjectRates = jsonObjectRates;
        this.dateUpdate = dateUpdate;
        this.datePreviousUpdate = datePreviousUpdate;
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

    public String getDateUpdate() {
        return dateUpdate;
    }
    public String getDatePreviousUpdate() {
        return datePreviousUpdate;
    }
}
