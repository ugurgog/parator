package com.paypad.vuk507.menu.reports;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.enums.ReportSelectionEnum;
import com.paypad.vuk507.menu.reports.interfaces.ReturnReportDateCallback;
import com.paypad.vuk507.model.pojo.ReportDate;
import com.paypad.vuk507.square.DayViewAdapter;
import com.paypad.vuk507.square.SampleDecorator;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CommonUtils;
import com.squareup.timessquare.CalendarCellDecorator;
import com.squareup.timessquare.CalendarPickerView;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.wdullaer.materialdatetimepicker.time.Timepoint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.squareup.timessquare.CalendarPickerView.SelectionMode.RANGE;

public class CustomizeReportFragment extends BaseFragment implements TimePickerDialog.OnTimeSetListener {

    private View mView;

    //Toolbar variables
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.cancelImgv)
    ClickableImageView cancelImgv;
    @BindView(R.id.saveBtn)
    Button saveBtn;
    @BindView(R.id.calendar_view)
    CalendarPickerView calendar;

    @BindView(R.id.startTimeTv)
    TextView startTimeTv;
    @BindView(R.id.endTimeTv)
    TextView endTimeTv;

    private TimePickerDialog timePickerDialog;
    private boolean isStartDateSelected = true;

    private String startTime;
    private String endTime;

    private ReturnReportDateCallback returnReportDateCallback;

    public CustomizeReportFragment() {

    }

    public void setReturnReportDateCallback(ReturnReportDateCallback returnReportDateCallback) {
        this.returnReportDateCallback = returnReportDateCallback;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        //TimePickerDialog tpd = (TimePickerDialog) requireFragmentManager().findFragmentByTag("Timepickerdialog");
        //if(tpd != null) tpd.setOnTimeSetListener(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_customize_report, container, false);
            ButterKnife.bind(this, mView);
            initVariables();
            initListeners();
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    private void initListeners() {
        cancelImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNewDateSelection();
            }
        });

        startTimeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isStartDateSelected = true;
                timePickerDialog.show(getActivity().getSupportFragmentManager(), "Timepickerdialog");
            }
        });

        endTimeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isStartDateSelected = false;
                timePickerDialog.show(getActivity().getSupportFragmentManager(), "Timepickerdialog");
            }
        });
    }

    private void initVariables() {
        toolbarTitleTv.setText(getContext().getResources().getString(R.string.customize_report));
        initCalendar();
        initTimePickerDialog();
        startTime = "00:00";
        endTime = "23:59";
        startTimeTv.setText(startTime);
        endTimeTv.setText(endTime);
    }

    private void initTimePickerDialog() {
        Calendar now = Calendar.getInstance();

        if (timePickerDialog == null) {
            timePickerDialog = TimePickerDialog.newInstance(
                    this,
                    now.get(Calendar.HOUR_OF_DAY),
                    now.get(Calendar.MINUTE),
                    true
            );
        } else {
            timePickerDialog.initialize(
                    this,
                    now.get(Calendar.HOUR_OF_DAY),
                    now.get(Calendar.MINUTE),
                    now.get(Calendar.SECOND),
                    true
            );
        }

        //timePickerDialog.setThemeDark(true);
        timePickerDialog.vibrate(true);
        timePickerDialog.dismissOnPause(true);
        //timePickerDialog.enableSeconds(true);
        timePickerDialog.setVersion(TimePickerDialog.Version.VERSION_2);

        timePickerDialog.setAccentColor(Color.parseColor("#9C27B0"));

        //timePickerDialog.setTitle("TimePicker Title");

        //Enable seconds
        timePickerDialog.setTimeInterval(1, 1, 10);
        //timePickerDialog.setTimeInterval(3, 5, 60);


        //disableSpecificTimes.isChecked()
        Timepoint[] disabledTimes = {
                new Timepoint(10),
                new Timepoint(10, 30),
                new Timepoint(11),
                new Timepoint(12, 30)
        };
        timePickerDialog.setDisabledTimes(disabledTimes);


        //timePickerDialog.setOnCancelListener(dialogInterface -> {
        //    timePickerDialog = null;
        //});
    }

    private void initCalendar() {
        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);

        final Calendar lastYear = Calendar.getInstance();
        lastYear.add(Calendar.YEAR, -1);

        calendar.setCustomDayView(new DayViewAdapter());

        Calendar today = Calendar.getInstance();
        ArrayList<Date> dates = new ArrayList<Date>();

        //today.add(Calendar.DATE, 3);

        dates.add(today.getTime());

        //today.add(Calendar.DATE, 5);

        dates.add(today.getTime());

        //calendar.setDecorators(Collections.<CalendarCellDecorator>emptyList());

        calendar.setDecorators(Arrays.<CalendarCellDecorator>asList(new SampleDecorator()));


        calendar.init(lastYear.getTime(), nextYear.getTime()) //
                .inMode(CalendarPickerView.SelectionMode.RANGE) //
                .withSelectedDates(dates);
    }

    private void saveNewDateSelection(){

        int startTimeInt = Integer.parseInt(startTime.replaceAll(":", ""));
        int endTimeInt = Integer.parseInt(endTime.replaceAll(":", ""));

        if(endTimeInt <= startTimeInt){
            CommonUtils.showToastShort(getContext(), getContext().getString(R.string.end_time_must_be_bigger_than_start_time));
            return;
        }

        List<Date> selectedDates = calendar.getSelectedDates();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");

        Log.i("Info", "saveNewDateSelection  startDate:" + simpleDateFormat.format(selectedDates.get(0)));
        Log.i("Info", "saveNewDateSelection  endDate  :" + simpleDateFormat.format(selectedDates.get(selectedDates.size() - 1)));


        finalizeReportDate(selectedDates);
    }

    private void finalizeReportDate(List<Date> selectedDates) {
        String[] startTimeX = startTime.split(":");
        String[] endTimeX = endTime.split(":");


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedDates.get(0));
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, Integer.parseInt(startTimeX[1]));
        calendar.set(Calendar.HOUR, Integer.parseInt(startTimeX[0]));

        ReportDate reportDate = new ReportDate();
        reportDate.setStartDate(calendar.getTime());

        calendar.setTime(selectedDates.get(selectedDates.size() - 1));
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, Integer.parseInt(endTimeX[1]));
        calendar.set(Calendar.HOUR, Integer.parseInt(endTimeX[0]));

        reportDate.setEndDate(calendar.getTime());

        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Log.i("Info", "saveNewDateSelection  repoertDate_S:" + simpleDateFormat.format(reportDate.getStartDate()));
        Log.i("Info", "saveNewDateSelection  repoertDate_E:" + simpleDateFormat.format(reportDate.getEndDate()));

        returnReportDateCallback.OnReturnDates(reportDate);
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String hourString = hourOfDay < 10 ? "0"+hourOfDay : ""+hourOfDay;
        String minuteString = minute < 10 ? "0"+minute : ""+minute;

        String time = hourString.concat(":").concat(minuteString);

        if(isStartDateSelected){
            startTime = time;
            startTimeTv.setText(time);
        } else{
            endTime = time;
            endTimeTv.setText(time);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timePickerDialog = null;
    }
}