package ru.mellman.conv3rter.data_course_of_currency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;

public class CoursesConstructor implements Callable<ArrayList<Courses>> {
    DataLists list;
    ArrayList<CurrencyRate> ratesPrevious;
    ArrayList<CurrencyRate> ratesCurrent;
    HashMap<String, CurrencyInfo> info;

    public CoursesConstructor(DataLists list) {
        this.list = list;
    }

    @Override
    public ArrayList<Courses> call() throws Exception {
        ratesPrevious = list.getYesterdayRateList();
        ratesCurrent = list.getCurrentRateList();
        info = list.getCurrencyInfo();

        ArrayList<Courses> coursesArrayList = new ArrayList<>();
        for (int i = 0; i < ratesCurrent.size(); i++) {
            Courses c = new Courses();
            c.setCharCode(ratesCurrent.get(i).getCharCode());
            c.setSymbolCurrency(info.get(ratesCurrent.get(i).getCharCode()).getSymbolCurrency());
            c.setName(info.get(ratesCurrent.get(i).getCharCode()).getName());
            c.setCourseValue(ratesCurrent.get(i).getRate());
            c.setPreviousValue(ratesPrevious.get(i).getRate());
            coursesArrayList.add(c);
        }
        return coursesArrayList;
    }
}
