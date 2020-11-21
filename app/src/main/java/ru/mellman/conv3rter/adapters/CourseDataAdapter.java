package ru.mellman.conv3rter.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import java.text.DecimalFormat;
import java.util.ArrayList;

import ru.mellman.conv3rter.R;
import ru.mellman.conv3rter.lists.Courses;

public class CourseDataAdapter extends RecyclerView.Adapter<CourseDataAdapter.ViewHolder> {
    private final LayoutInflater inflater;
    private final ArrayList<Courses> courses;
    private final Context context;
    private final String baseCurrency;

    private static CourseListInterface courseListInterface;

    public CourseDataAdapter(Context context, ArrayList<Courses> courses, String baseCurrency, CourseListInterface interFace) {
        this.inflater = LayoutInflater.from(context);
        this.courses = courses;
        this.context = context;
        this.baseCurrency = baseCurrency;
        courseListInterface = interFace;
    }

    @NonNull
    @Override
    public CourseDataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CourseDataAdapter.ViewHolder holder, int position) {
        Courses course = courses.get(position);
        holder.code.setText(course.getSymbolCurrency());
        holder.currencyName.setText(course.getName());

        String value = new DecimalFormat("#0.0000").format(course.getCourseValue());
        holder.currentValue.setText(value);

        String ratio = 1 + baseCurrency + " = " + value + course.getCharCode();
        holder.ratio.setText(ratio);

        double dif = course.getDifference();
        if (dif > 0) {
            holder.difValue.setTextColor(ContextCompat.getColor(context, R.color.courseGREEN));
        }
        if (dif < 0) {
            holder.difValue.setTextColor(ContextCompat.getColor(context, R.color.courseRED));
        }
        if (dif == 0.0) {
            holder.difValue.setTextColor(ContextCompat.getColor(context, R.color.courseGREY));
        }

        double difPercent = course.getPercentOfDifference();
        String difPercentValue = new DecimalFormat("#0.00").format(difPercent);
        String different;
        if (difPercent > 0) {
            different = "(+" + difPercentValue + "%)";
        } else {
            different = "(" + difPercentValue + "%)";
        }
        holder.difValue.setText(different);
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView code;
        final TextView currencyName;
        final TextView currentValue;
        final TextView difValue;
        final TextView ratio;

        public ViewHolder(View view) {
            super(view);

            code = view.findViewById(R.id.symbol);
            currencyName = view.findViewById(R.id.currencyName);
            currentValue = view.findViewById(R.id.currency_value);
            difValue = view.findViewById(R.id.difference_value);
            ratio = view.findViewById(R.id.ratio_of_currency);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        courseListInterface.onItemClick(position);
                    }
                }
            });
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        courseListInterface.onItemClick(position);
                    }
                    return true;
                }
            });
        }
    }
    public interface CourseListInterface{
        void onItemClick(int position);
        void onLongItemClick(int position);
    }
}

