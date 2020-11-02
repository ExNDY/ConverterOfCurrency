package ru.mellman.conv3rter.adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

import ru.mellman.conv3rter.R;
import ru.mellman.conv3rter.lists.CurrencyRate;

public class RatesDataAdapter extends ArrayAdapter<CurrencyRate> {

    public RatesDataAdapter(Context context, ArrayList<CurrencyRate> rateList) {
        super(context, 0, rateList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView,parent);
    }
    private View initView(int position, View convertView, ViewGroup parent){
        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.spinner_item, parent, false
            );
        }
        MaterialTextView code = convertView.findViewById(R.id.code_text);

        CurrencyRate cr = getItem(position);
        if(cr!=null){
            code.setText(cr.getCharCode());
        }

        return convertView;
    }
}