package com.ydbm.dialogs;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialog;


import com.archit.calendardaterangepicker.customviews.DateRangeCalendarView;
import com.ydbm.R;

import java.util.Calendar;

public class DatePickerCustomDiaog extends AppCompatDialog {

    private Context mContext;
    Button btn_ok,btn_reset;
    private DateRangeCalendarView calendar;

    CalendarListener calendarListener;

    public DatePickerCustomDiaog(Context context,CalendarListener calendarListener1) {

        super(context);
        this.mContext = context;
        this.calendarListener=calendarListener1;
    }

    @Override
    protected void onStart() {
        super.onStart();
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        //  getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.datepicker_custom_dialog);
        findView();


    }


    private void findView() {
        btn_reset = (Button) this.findViewById(R.id.btnReset);
        btn_ok=this.findViewById(R.id.btnOk);
        calendar = this.findViewById(R.id.calendar);
        calendar.setCalendarListener(new DateRangeCalendarView.CalendarListener() {
            @Override
            public void onFirstDateSelected(Calendar startDate) {
                //  Toast.makeText(mContext, "Start Date: " + startDate.getTime().toString(), Toast.LENGTH_SHORT).show();
                calendarListener.onFirstDateSelected(startDate);
            }

            @Override
            public void onDateRangeSelected(Calendar startDate, Calendar endDate) {
               calendarListener.onDateRangeSelected(startDate,endDate);

                //Toast.makeText(mContext, "Start Date: " + startDate.getTime().toString() + " End date: " + endDate.getTime().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             dismiss();
            }
        });
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.resetAllSelectedViews();
                calendarListener.onResetBtnClicked();
            }
        });
    }

    public interface CalendarListener {
        void onFirstDateSelected(Calendar startDate);

        void onDateRangeSelected(Calendar startDate, Calendar endDate);
        void onResetBtnClicked();
    }
}
