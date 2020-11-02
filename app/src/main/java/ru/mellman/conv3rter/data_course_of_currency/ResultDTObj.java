package ru.mellman.conv3rter.data_course_of_currency;

import java.util.ArrayList;

import ru.mellman.conv3rter.lists.Courses;
import ru.mellman.conv3rter.lists.CurrencyRate;

public class ResultDTObj {
    private final ArrayList<Courses> courses;
    private final ArrayList<CurrencyRate> rates;
    private final String dateOfUpdate;
    private final String result;

    public ArrayList<Courses> getCourses() {
        return courses;
    }

    public ArrayList<CurrencyRate> getRates() {
        return rates;
    }

    public String getDateOfUpdate() {
        return dateOfUpdate;
    }

    public String getResult() {
        return result;
    }

    public ResultDTObj(ArrayList<Courses> courses, ArrayList<CurrencyRate> rates, String dateOfUpdate, String result) {
        this.courses = courses;
        this.rates = rates;
        this.dateOfUpdate = dateOfUpdate;
        this.result = result;
    }
}
