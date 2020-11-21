package ru.mellman.conv3rter;

import java.util.Comparator;

import ru.mellman.conv3rter.lists.Courses;
import ru.mellman.conv3rter.lists.CurrencyRate;

public class Compare {
    public static Comparator<CurrencyRate> RatesNameComparator = new Comparator<CurrencyRate>() {
        @Override
        public int compare(CurrencyRate o1, CurrencyRate o2) {
            String rate1 = o1.getCharCode().toUpperCase();
            String rate2 = o2.getCharCode().toUpperCase();
            return rate1.compareTo(rate2);
        }
    };
    public static Comparator<Courses> CoursesNameComparator = new Comparator<Courses>() {
        @Override
        public int compare(Courses o1, Courses o2) {
            String c1 = o1.getName().toUpperCase();
            String c2 = o2.getName().toUpperCase();
            return c1.compareTo(c2);
        }
    };
}
