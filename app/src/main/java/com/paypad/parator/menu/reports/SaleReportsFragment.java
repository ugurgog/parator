package com.paypad.parator.menu.reports;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.adapter.CustomViewPagerAdapter;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.enums.MonthEnum;
import com.paypad.parator.enums.ReportSelectionEnum;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.menu.reports.interfaces.ReturnReportDateCallback;
import com.paypad.parator.menu.reports.saleReport.SaleReportEmailFragment;
import com.paypad.parator.menu.reports.saleReport.SalesDetailFragment;
import com.paypad.parator.model.User;
import com.paypad.parator.model.pojo.ReportDate;
import com.paypad.parator.model.pojo.ReportModel;
import com.paypad.parator.utils.ClickableImage.ClickableImageView;
import com.paypad.parator.utils.CommonUtils;
import com.paypad.parator.utils.DataUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.paypad.parator.constants.CustomConstants.LANGUAGE_TR;


public class SaleReportsFragment extends BaseFragment implements ReturnReportDateCallback,
        SalesDetailFragment.ReturnReportModelCallback {

    private View mView;

    //Toolbar variables
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.backImgv)
    ClickableImageView backImgv;
    @BindView(R.id.sendImgv)
    ClickableImageView sendImgv;
    @BindView(R.id.printImgv)
    ClickableImageView printImgv;

    @BindView(R.id.customDateTv)
    TextView customDateTv;

    @BindView(R.id.tablayout)
    TabLayout tablayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.viewPagerRl)
    RelativeLayout viewPagerRl;

    @BindView(R.id.selectPeriodLL)
    LinearLayout selectPeriodLL;

    private CustomViewPagerAdapter customViewPagerAdapter;

    private CustomizeReportFragment customizeReportFragment;

    private SalesDetailFragment undefinedSalesDetailFragment;

    private User user;
    private String dateTitle;
    private ReportModel reportModel;

    public SaleReportsFragment() {

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(sticky = true)
    public void accountHolderUserReceived(UserBus userBus){
        user = userBus.getUser();
        if(user == null)
            user = UserDBHelper.getUserFromCache(getContext());
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
                mFragmentNavigation.pushFragment(new SaleReportEmailFragment(dateTitle, reportModel));
            }
        });

        printImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        selectPeriodLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initCustomizeReportFragment();
                mFragmentNavigation.pushFragment(customizeReportFragment);
            }
        });
    }

    private void initCustomizeReportFragment(){
        customizeReportFragment = new CustomizeReportFragment();
        customizeReportFragment.setReturnReportDateCallback(this);
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
        setDateTitle(new ReportDate(new Date(), new Date()), ReportSelectionEnum.ONE_D );

        ReportDate oneWeekDates = getOneWeekDates();
        initSaleSummaryReportFragment(ReportSelectionEnum.ONE_W, oneWeekDates.getStartDate(), oneWeekDates.getEndDate());

        ReportDate currentMonthDates = getCurrentMonthDates();
        initSaleSummaryReportFragment(ReportSelectionEnum.ONE_M, currentMonthDates.getStartDate(), currentMonthDates.getEndDate());

        /*ReportDate threeMonthDates = getThreeMonthsDates();
        initSaleSummaryReportFragment(ReportSelectionEnum.THREE_M, threeMonthDates.getStartDate(), threeMonthDates.getEndDate());

        ReportDate oneYearDates = getOneYearDates();
        initSaleSummaryReportFragment(ReportSelectionEnum.ONE_Y, oneYearDates.getStartDate(), oneYearDates.getEndDate());*/

        viewPager.setAdapter(customViewPagerAdapter);
    }

    private void setTabListener() {

        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                removeUndefinedSaleFragment();
                tablayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.Black, null));
                SalesDetailFragment salesDetailFragment = (SalesDetailFragment)customViewPagerAdapter.getItem(tab.getPosition());
                setDateTitle(new ReportDate(salesDetailFragment.getStartDate(), salesDetailFragment.getEndDate()), salesDetailFragment.getReportSelectionType());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                removeUndefinedSaleFragment();
                tablayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.Black, null));
            }
        });
    }

    private void removeUndefinedSaleFragment(){
        viewPager.setVisibility(View.VISIBLE);
        try{
            if(undefinedSalesDetailFragment != null){
                FragmentTransaction fragmentTransaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
                fragmentTransaction.remove(undefinedSalesDetailFragment);
                fragmentTransaction.commit();
            }
        }catch (Exception ignored){

        }
    }

    private void initSaleSummaryReportFragment(ReportSelectionEnum reportSelectionEnum, Date startDate, Date endDate){

        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Log.i("Info", "initSaleSummaryReportFragment  " + reportSelectionEnum.getLabel() + " "
                + simpleDateFormat.format(DataUtils.getStartTimeOfDate(startDate)) + "  " + simpleDateFormat.format(DataUtils.getEndTimeOfDate(endDate)) );

        SalesDetailFragment salesDetailFragment = new SalesDetailFragment(DataUtils.getStartTimeOfDate(startDate), DataUtils.getEndTimeOfDate(endDate), false, reportSelectionEnum);
        salesDetailFragment.setReturnReportModelCallback(this);
        customViewPagerAdapter.addFrag(salesDetailFragment, reportSelectionEnum.getLabel());
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

        int monthNum = DataUtils.getMonthNumFromDate(new Date());
        Date startDate, endDate;

        if(monthNum < 3){
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DATE, 1);
            cal.set(Calendar.MONTH, 1);
            startDate = cal.getTime();

            cal.add(Calendar.MONTH, 2);
            cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE)); // changed calendar to cal
            endDate = cal.getTime();
        }else {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DATE, 1);
            cal.add(Calendar.MONTH, -2);
            startDate = cal.getTime();

            cal.add(Calendar.MONTH, 2);
            cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE)); // changed calendar to cal
            endDate = cal.getTime();
        }

        ReportDate reportDate = new ReportDate();
        reportDate.setStartDate(startDate);
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

    @Override
    public void OnReturnDates(ReportDate reportDate) {
        viewPager.setVisibility(View.GONE);

        tablayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.transparent, null));

        if (customizeReportFragment != null)
            Objects.requireNonNull(customizeReportFragment.getActivity()).onBackPressed();

        undefinedSalesDetailFragment = new SalesDetailFragment(DataUtils.getStartTimeOfDate(reportDate.getStartDate()), DataUtils.getEndTimeOfDate(reportDate.getEndDate()),
                false, ReportSelectionEnum.NOT_DEFINED);
        undefinedSalesDetailFragment.setReturnReportModelCallback(this);

        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                .replace(R.id.viewPagerRl, undefinedSalesDetailFragment, SalesDetailFragment.class.getName())
                .addToBackStack(null)
                .commit();
        setDateTitle(reportDate, ReportSelectionEnum.NOT_DEFINED);
    }

    private void setDateTitle(ReportDate reportDate, ReportSelectionEnum reportSelectionType){

        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");

        Log.i("Info", "::setDateTitle  reportDate+++++++++++++++: " + reportSelectionType.getLabel());
        Log.i("Info", "::setDateTitle  reportDate_S:" + simpleDateFormat.format(reportDate.getStartDate()));
        Log.i("Info", "::setDateTitle  reportDate_E:" + simpleDateFormat.format(reportDate.getEndDate()));

        String fullName = "";
        if(reportSelectionType == ReportSelectionEnum.ONE_D){
            fullName = getResources().getString(R.string.today)
                    .concat(", ")
                    .concat(getMonthNameLabel(reportDate.getStartDate()))
                    .concat(" ")
                    .concat(getDayNumLabel(reportDate.getStartDate()))
                    .concat(", ")
                    .concat(getYearNumLabel(reportDate.getStartDate()));
        }else if(reportSelectionType == ReportSelectionEnum.ONE_W){
            fullName = getResources().getString(R.string.this_week)
                    .concat(", ")
                    .concat(getMonthNameLabel(reportDate.getStartDate()))
                    .concat(" ")
                    .concat(getDayNumLabel(reportDate.getStartDate()))
                    .concat(" - ")
                    .concat(getMonthNameLabel(reportDate.getEndDate()))
                    .concat(" ")
                    .concat(getDayNumLabel(reportDate.getEndDate()))
                    .concat(", ")
                    .concat(getYearNumLabel(reportDate.getStartDate()));
        }else if(reportSelectionType == ReportSelectionEnum.ONE_M){
            fullName = getResources().getString(R.string.this_month)
                    .concat(", ")
                    .concat(getMonthNameLabel(reportDate.getStartDate()))
                    .concat(", ")
                    .concat(getYearNumLabel(reportDate.getStartDate()));
        }else if(reportSelectionType == ReportSelectionEnum.THREE_M){
            fullName = getResources().getString(R.string.current_three_month)
                    .concat(", ")
                    .concat(getMonthNameLabel(reportDate.getStartDate()))
                    .concat(" - ")
                    .concat(getMonthNameLabel(reportDate.getEndDate()))
                    .concat(", ")
                    .concat(getYearNumLabel(reportDate.getStartDate()));
        }else if(reportSelectionType == ReportSelectionEnum.ONE_Y){
            fullName = getResources().getString(R.string.this_year)
                    .concat(", ")
                    .concat(getYearNumLabel(reportDate.getStartDate()));
        }else if(reportSelectionType == ReportSelectionEnum.NOT_DEFINED){

            fullName = getMonthNameLabel(reportDate.getStartDate())
                    .concat(" ")
                    .concat(getDayNumLabel(reportDate.getStartDate()))
                    .concat(", ")
                    .concat(getYearNumLabel(reportDate.getStartDate()))
                    .concat(" - ")
                    .concat(getMonthNameLabel(reportDate.getEndDate()))
                    .concat(" ")
                    .concat(getDayNumLabel(reportDate.getEndDate()))
                    .concat(", ")
                    .concat(getYearNumLabel(reportDate.getEndDate()));
        }
        dateTitle = fullName;
        customDateTv.setText(fullName);
    }

    private String getMonthNameLabel(Date date){
        return CommonUtils.getLanguage().equals(LANGUAGE_TR) ?
                Objects.requireNonNull(MonthEnum.getById(DataUtils.getMonthNumFromDate(date))).getTrCode()
                : Objects.requireNonNull(MonthEnum.getById(DataUtils.getMonthNumFromDate(date))).getEngCode();
    }

    private String getDayNumLabel(Date date){
        return String.valueOf(DataUtils.getDateNumFromDate(date));
    }

    private String getYearNumLabel(Date date){
        return String.valueOf(DataUtils.getYearFromDate(date));
    }

    @Override
    public void OnReturnReportModel(ReportModel reportModel) {
        this.reportModel = reportModel;

        if(reportModel == null || reportModel.getSaleCount() == 0){
            sendImgv.setColorFilter(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.Silver), android.graphics.PorterDuff.Mode.SRC_IN);
            sendImgv.setEnabled(false);
        }else {
            sendImgv.setColorFilter(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.Black), android.graphics.PorterDuff.Mode.SRC_IN);
            sendImgv.setEnabled(true);
        }
    }
}