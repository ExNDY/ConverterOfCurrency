package ru.mellman.conv3rter;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Function {
    public static Double convert(Double from, Double to, Double count){
        return (to*count)/from;
    }
    public static String getDecimalToFormat(Double decimal){
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("#0.00", decimalFormatSymbols);
        return decimalFormat.format(decimal);
    }
    public static String getDateNow(){
        String dateNow;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 0);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
        dateNow = simpleDateFormat.format(cal.getTime());
        return dateNow;
    }

    public static String getDateTimeCourseLastUpdate() throws ParseException {
        String s = "2020-10-01T11:30:00+03:00";
        Date date = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.ENGLISH);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        date=df.parse(s);
        assert date != null;
        s = df.format(date);
        return s;
    }
}
