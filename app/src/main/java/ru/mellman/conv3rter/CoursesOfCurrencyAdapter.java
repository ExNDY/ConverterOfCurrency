package ru.mellman.conv3rter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class CoursesOfCurrencyAdapter extends ArrayAdapter<CoursesOfCurrency> {
    Context context;
    int layoutResourceId;
    ArrayList<CoursesOfCurrency> data;

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
            holder.currentValue = row.findViewById(R.id.valuevalute);
            holder.previousValue = row.findViewById(R.id.previousvalue);
            row.setTag(holder);
        }
        else{
            holder = (CoursesOfCurrencyHolder)row.getTag();
        }
        CoursesOfCurrency c = data.get(position);
        holder.code.setText(c.getCharCode());
        holder.currentValue.setText(String.valueOf(c.getCourseValue()));
        holder.previousValue.setText(String.valueOf(c.getPrevious()));

        return row;
    }
    static class CoursesOfCurrencyHolder
    {
        MaterialTextView code;
        MaterialTextView currentValue;
        MaterialTextView previousValue;
    }
}
