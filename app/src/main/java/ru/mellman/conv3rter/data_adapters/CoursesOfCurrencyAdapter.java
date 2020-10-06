package ru.mellman.conv3rter.data_adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.material.textview.MaterialTextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import ru.mellman.conv3rter.R;

public class CoursesOfCurrencyAdapter extends ArrayAdapter<CoursesOfCurrency> {
    private Context context;
    private int layoutResourceId;
    private ArrayList<CoursesOfCurrency> data;

    public CoursesOfCurrencyAdapter(@NonNull Context context,@LayoutRes int layoutResourceId, ArrayList<CoursesOfCurrency> data){
        super(context,layoutResourceId,data);
        this.layoutResourceId = layoutResourceId;
        this.context =context;
        this.data = data;
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent){
        View row = convertView;
        CoursesOfCurrencyHolder holder;
        if(row==null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new CoursesOfCurrencyHolder();
            holder.code = row.findViewById(R.id.code);
            holder.currencyName = row.findViewById(R.id.currencyName);
            holder.currentValue = row.findViewById(R.id.currency_value);
            holder.difValue = row.findViewById(R.id.difference_value);
            holder.nominal = row.findViewById(R.id.nominal_of_currency);
            holder.differenceImage = row.findViewById(R.id.difImage);
            row.setTag(holder);
        }
        else{
            holder = (CoursesOfCurrencyHolder)row.getTag();
        }
        CoursesOfCurrency c = data.get(position);
        holder.code.setText(c.getCharCode());
        holder.currencyName.setText(c.getName());
        String currentValueStr = c.getCourseValue()+"₽";
        holder.currentValue.setText(currentValueStr);
        double dif = c.getDifference();
        if (dif>0){
            holder.difValue.setTextColor(ContextCompat.getColor(context, R.color.courseGREEN));
            holder.differenceImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_up));
        }
        if (dif<0){
            holder.difValue.setTextColor(ContextCompat.getColor(context, R.color.courseRED));
            holder.differenceImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_down));
        }
        if (dif==0.0){
            holder.difValue.setTextColor(ContextCompat.getColor(context, R.color.courseGREY));
        }
        String difValue = new DecimalFormat("#0.0000").format(dif);
        holder.difValue.setText(difValue);
        String nominalStr = context.getString(R.string.nominal)+" "+c.getNominal()+c.getCharCode();
        holder.nominal.setText(nominalStr);
        return row;
    }
    static class CoursesOfCurrencyHolder
    {
        MaterialTextView code;
        MaterialTextView currencyName;
        MaterialTextView currentValue;
        MaterialTextView difValue;
        MaterialTextView nominal;
        ImageView differenceImage;

    }

}
