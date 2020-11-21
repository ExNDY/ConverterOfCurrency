package ru.mellman.conv3rter.data_course_of_currency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.Callable;

import ru.mellman.conv3rter.lists.Courses;
import ru.mellman.conv3rter.lists.CurrencyInfo;
import ru.mellman.conv3rter.lists.CurrencyRate;

public class CoursesConstructor implements Callable<ArrayList<Courses>> {
    ArrayList<CurrencyRate> ratesPrevious;
    ArrayList<CurrencyRate> ratesCurrent;
    HashMap<String, CurrencyInfo> info;

    public CoursesConstructor(ArrayList<CurrencyRate> ratesPrevious,ArrayList<CurrencyRate> ratesCurrent, HashMap<String, CurrencyInfo> info) {
        this.ratesPrevious = ratesPrevious;
        this.ratesCurrent = ratesCurrent;
        this.info = info;
    }

    @Override
    public ArrayList<Courses> call() throws Exception {

        ArrayList<Courses> coursesArrayList = new ArrayList<>();
        for (int i = 0; i < ratesCurrent.size(); i++) {
            Courses c = new Courses();
            c.setCharCode(ratesCurrent.get(i).getCharCode());
            c.setSymbolCurrency(Objects.requireNonNull(info.get(ratesCurrent.get(i).getCharCode())).getSymbolCurrency());
            c.setName(Objects.requireNonNull(info.get(ratesCurrent.get(i).getCharCode())).getName());
            c.setCourseValue(ratesCurrent.get(i).getRate());
            c.setPreviousValue(ratesPrevious.get(i).getRate());
            coursesArrayList.add(c);
        }
        return coursesArrayList;
    }
}
