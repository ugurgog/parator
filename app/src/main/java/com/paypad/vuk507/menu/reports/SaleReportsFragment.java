package com.paypad.vuk507.menu.reports;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.adapter.CustomViewPagerAdapter;
import com.paypad.vuk507.enums.ReportSelectionEnum;
import com.paypad.vuk507.menu.reports.saleReport.SalesDetailFragment;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SaleReportsFragment extends BaseFragment{

    private View mView;

    //Toolbar variables
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.backImgv)
    ClickableImageView backImgv;
    @BindView(R.id.sendImgv)
    ClickableImageView sendImgv;
    @BindView(R.id.arrangeDateImgv)
    ClickableImageView arrangeDateImgv;

    @BindView(R.id.customDateTv)
    TextView customDateTv;
    @BindView(R.id.deviceFilterTv)
    TextView deviceFilterTv;

    @BindView(R.id.tablayout)
    TabLayout tablayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    private CustomViewPagerAdapter customViewPagerAdapter;

    public SaleReportsFragment() {

    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_sales_report, container, false);
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
        backImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });

        sendImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        arrangeDateImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void initVariables() {
        setUpPager();
    }

    private void setUpPager() {
        setupViewPager();
        tablayout.setupWithViewPager(viewPager);
        setTabListener();
    }

    private void setupViewPager() {
        customViewPagerAdapter = new CustomViewPagerAdapter(getChildFragmentManager());

        initSaleSummaryReportFragment(ReportSelectionEnum.ONE_D, new Date(), new Date());

        ReportDate oneWeekDates = getOneWeekDates();
        initSaleSummaryReportFragment(ReportSelectionEnum.ONE_W, oneWeekDates.getStartDate(), oneWeekDates.getEndDate());

        ReportDate currentMonthDates = getCurrentMonthDates();
        initSaleSummaryReportFragment(ReportSelectionEnum.ONE_M, currentMonthDates.getStartDate(), currentMonthDates.getEndDate());

        ReportDate threeMonthDates = getThreeMonthsDates();
        initSaleSummaryReportFragment(ReportSelectionEnum.THREE_M, threeMonthDates.getStartDate(), threeMonthDates.getEndDate());

        ReportDate oneYearDates = getOneYearDates();
        initSaleSummaryReportFragment(ReportSelectionEnum.ONE_Y, oneYearDates.getStartDate(), oneYearDates.getEndDate());

        viewPager.setAdapter(customViewPagerAdapter);
    }

    private void setTabListener() {

        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initSaleSummaryReportFragment(ReportSelectionEnum reportSelectionEnum, Date startDate, Date endDate){

        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Log.i("Info", "initSaleSummaryReportFragment  " + reportSelectionEnum.getLabel() + " "
                + simpleDateFormat.format(getStartDate(startDate)) + "  " + simpleDateFormat.format(getEndDate(endDate)) );

        customViewPagerAdapter.addFrag(new SalesDetailFragment(getStartDate(startDate), getEndDate(endDate), false, reportSelectionEnum), reportSelectionEnum.getLabel());
    }

    private Date getStartDate(Date startDate){
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private Date getEndDate(Date endDate){
        Calendar cal = Calendar.getInstance();
        cal.setTime(endDate);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    private ReportDate getOneWeekDates(){
        ReportDate reportDate = new ReportDate();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK,  cal.getActualMinimum(Calendar.DAY_OF_WEEK));
        Date startDate = cal.getTime();

        reportDate.setStartDate(startDate);

        cal.add(Calendar.DATE, 6);

        Date endDate = cal.getTime();
        reportDate.setEndDate(endDate);

        return reportDate;
    }

    private ReportDate getCurrentMonthDates(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, 1);
        Date startdate = cal.getTime();

        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE)); // changed calendar to cal
        Date endDate = cal.getTime();

        ReportDate reportDate = new ReportDate();
        reportDate.setStartDate(startdate);
        reportDate.setEndDate(endDate);
        return reportDate;
    }

    private ReportDate getThreeMonthsDates(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, 1);
        cal.add(Calendar.MONTH, -2);
        Date startdate = cal.getTime();

        cal.add(Calendar.MONTH, 2);
        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE)); // changed calendar to cal
        Date endDate = cal.getTime();

        ReportDate reportDate = new ReportDate();
        reportDate.setStartDate(startdate);
        reportDate.setEndDate(endDate);
        return reportDate;
    }

    private ReportDate getOneYearDates(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_YEAR, 1);
        Date startdate = cal.getTime();

        cal.set(Calendar.MONTH, 12);
        cal.set(Calendar.DAY_OF_MONTH, 31);
        Date endDate = cal.getTime();

        ReportDate reportDate = new ReportDate();
        reportDate.setStartDate(startdate);
        reportDate.setEndDate(endDate);
        return reportDate;
    }

    public class ReportDate{
        private Date startDate;
        private Date endDate;

        public Date getStartDate() {
            return startDate;
        }

        public void setStartDate(Date startDate) {
            this.startDate = startDate;
        }

        public Date getEndDate() {
            return endDate;
        }

        public void setEndDate(Date endDate) {
            this.endDate = endDate;
        }
    }

}