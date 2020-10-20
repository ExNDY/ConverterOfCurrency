package ru.mellman.conv3rter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textview.MaterialTextView;

import java.text.DecimalFormat;
import java.util.Objects;

import ru.mellman.conv3rter.data_adapters.CoursesOfCurrency;

public class BottomSheetDialog extends BottomSheetDialogFragment {
    public static final String TAG = BottomSheetDialog.class.getSimpleName();;
    private TextView bs_code;
    private TextView bs_name;
    private TextView bs_current_value;
    private TextView bs_previous_value;
    private TextView bs_last_update_date;
    private TextView bs_previous_date;
    private ImageView bs_difImgUp;
    private ImageView bs_difImgDown;
    private MaterialTextView bs_diff_value;
    private Context context;

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        Objects.requireNonNull(getDialog()).getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;
    }
    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        this.context = context;

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.buttomsheet_layout, container, false);
        init(v);
        Bundle bundle = getArguments();
        assert bundle != null;
        CoursesOfCurrency coursesOfCurrency = bundle.getParcelable("currency");
        if (coursesOfCurrency!=null){
            bs_code.setText(coursesOfCurrency.getCharCode());
            bs_code.setSelected(true);
            String title = coursesOfCurrency.getNominal()+" "+ coursesOfCurrency.getName();
            bs_name.setText(title);
            bs_current_value.setText(Function.getDecimalToFormat(coursesOfCurrency.getCourseValue()));
            bs_previous_value.setText(Function.getDecimalToFormat(coursesOfCurrency.getPreviousValue()));
            bs_last_update_date.setText(Function.getFormatDate(bundle.getString("date_last_update")));
            bs_previous_date.setText(Function.getFormatDate(bundle.getString("date_previous_update")));

            double dif = coursesOfCurrency.getDifference();
            if (dif>0){
                bs_diff_value.setTextColor(ContextCompat.getColor(context, R.color.courseGREEN));
                //bs_difImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_up));
            }
            if (dif<0){
                bs_diff_value.setTextColor(ContextCompat.getColor(context, R.color.courseRED));
                //bs_difImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_down));
            }
            if (dif==0.0){
                bs_diff_value.setTextColor(ContextCompat.getColor(context, R.color.courseGREY));
            }
            bs_diff_value.setText(Function.getDecimalToFormat(coursesOfCurrency.getDifference()));
        }

        return v;
    }
    private void init(View v){
        bs_code = v.findViewById(R.id.bs_code_currency);
        bs_name = v.findViewById(R.id.bs_name_currency);
        bs_current_value = v.findViewById(R.id.bs_current_value);
        bs_previous_value = v.findViewById(R.id.bs_previous_value);
        bs_last_update_date = v.findViewById(R.id.bs_current_value_date);
        bs_previous_date = v.findViewById(R.id.bs_previous_value_date);
        bs_diff_value = v.findViewById(R.id.bs_difference_value);
        bs_difImgUp = v.findViewById(R.id.bs_difImageUp);
        bs_difImgDown = v.findViewById(R.id.bs_difImageDown);
    }

}
