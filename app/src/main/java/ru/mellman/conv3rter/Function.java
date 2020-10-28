package ru.mellman.conv3rter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class Function {
    public static Double convert(Double from, Double to, Double count) {
        return (to * count) / from;
    }

    public static String getDecimalToFormat(Double decimal) {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("#.##", decimalFormatSymbols);
        return decimalFormat.format(decimal);
    }

    public static String readJSONObject(Context context) {
        String json;
        try {
            InputStream is = context.getAssets().open("CurrencyInfo.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            try {
                is.read(buffer);
            } catch (NullPointerException | IOException ex) {
                ex.printStackTrace();
                return null;
            }
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static void copyToClipboard(String buffer, Context context) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("", buffer);
        assert clipboardManager != null;
        clipboardManager.setPrimaryClip(clipData);
    }

    public static String getCurrentDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    public static String getYesterdayDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime yesterday = LocalDateTime.now();
        yesterday = yesterday.minusDays(1);
        return dtf.format(yesterday);
    }

    public static String getDate(String date) {
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
