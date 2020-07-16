package com.paypad.vuk507.menu.reports;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.paypad.vuk507.menu.reports.saleReport.OneDaySaleFragment;
import com.paypad.vuk507.menu.reports.saleReport.OneMonthSaleFragment;
import com.paypad.vuk507.menu.reports.saleReport.OneWeekSaleFragment;
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

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        customViewPagerAdapter = new CustomViewPagerAdapter(getChildFragmentManager());

        initSaleSummaryReportFragment(ReportSelectionEnum.ONE_D, new Date(), new Date());
        initSaleSummaryReportFragment(ReportSelectionEnum.ONE_W, new Date(), new Date());
        initSaleSummaryReportFragment(ReportSelectionEnum.ONE_M, new Date(), new Date());



        customViewPagerAdapter.addFrag(new SaleSummaryReportFragment(new Date(), new Date(), false), ReportSelectionEnum.THREE_M.getLabel());
        customViewPagerAdapter.addFrag(new SaleSummaryReportFragment(new Date(), new Date(), false), ReportSelectionEnum.ONE_Y.getLabel());

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

        if(reportSelectionEnum == ReportSelectionEnum.ONE_D)
            customViewPagerAdapter.addFrag(new OneDaySaleFragment(getStartDate(startDate), getEndDate(endDate), false), reportSelectionEnum.getLabel());
        else if(reportSelectionEnum == ReportSelectionEnum.ONE_W)
            customViewPagerAdapter.addFrag(new OneWeekSaleFragment(getStartDate(startDate), getEndDate(endDate), false), reportSelectionEnum.getLabel());
        else if(reportSelectionEnum == ReportSelectionEnum.ONE_M)
            customViewPagerAdapter.addFrag(new OneMonthSaleFragment(getStartDate(startDate), getEndDate(endDate), false), reportSelectionEnum.getLabel());
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

}