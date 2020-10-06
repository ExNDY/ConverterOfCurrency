package ru.mellman.conv3rter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.util.Log;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class Function {
    public static Double convert(Double from, Double to, Double count){
        return (to*count)/from;
    }
    public static String getDecimalToFormat(Double decimal){
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("#.##", decimalFormatSymbols);
        return decimalFormat.format(decimal);
    }
    public static void copyToClipboard(String buffer, Context context){
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("",buffer);
        assert clipboardManager != null;
        clipboardManager.setPrimaryClip(clipData);
    }
    public static String getDate(String date){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.ENGLISH);
        try {
            cal.setTime(Objects.requireNonNull(df.parse(date)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
        date = simpleDateFormat.format(cal.getTime());
        return date;
    }

    public static String getFormatDate(String date){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.ENGLISH);
        try {
            cal.setTime(Objects.requireNonNull(df.parse(date)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM", Locale.ENGLISH);
        date = simpleDateFormat.format(cal.getTime());
        return date;
    }

    public static boolean checkTheNeedForAnUpdate(String previousDateTimeUpdate){
        boolean needUpdate;
        if (!previousDateTimeUpdate.equals("")){
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.ENGLISH);
            try {
                cal.setTime(Objects.requireNonNull(df.parse(previousDateTimeUpdate)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            cal.add(Calendar.DATE, 1);
            Date dateNextUpdate = cal.getTime();
            Date dateCurrent = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
            needUpdate = dateCurrent.after(dateNextUpdate);
        }
        else {needUpdate=true;}
        return needUpdate;
    }
    //Check InternetConnection WIFI, MOBILE
    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR");
                    return true;
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI");
                    return true;
                }  else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)){
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET");
                    return true;
                }
            }
        }
        return false;
    }
}
