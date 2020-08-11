package com.paypad.vuk507.menu.reports.saleReport;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.db.SaleDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.ChartSaleSelectionEnum;
import com.paypad.vuk507.enums.ReportSelectionEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.menu.reports.NoSalesFragment;
import com.paypad.vuk507.menu.reports.util.ChartManager3;
import com.paypad.vuk507.menu.reports.util.SaleReportManager;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.model.pojo.ReportModel;
import com.paypad.vuk507.model.pojo.SaleModel;
import com.paypad.vuk507.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SalesDetailFragment extends BaseFragment {

    private View mView;

    //Sales Summary
    @BindView(R.id.mainll)
    LinearLayout mainll;
    @BindView(R.id.salesSummaryTv)
    TextView salesSummaryTv;
    @BindView(R.id.salesSummaryMenuImgv)
    ImageView salesSummaryMenuImgv;

    @BindView(R.id.graphicSummaryMenuImgv)
    ImageView graphicSummaryMenuImgv;
    @BindView(R.id.graphicSummaryTv)
    TextView graphicSummaryTv;

    BarChart graphicSummaryChart;
    LineChart graphicSummaryLineChart;

    private User user;

    private Date startDate;
    private Date endDate;
    private boolean isThisDevice;

    private boolean isSaleOverviewDisplayed = true;

    //private ChartManager chartManager;
    private ChartManager3 chartManager;

    private List<SaleModel> saleModels = new ArrayList<>();
    private ReportModel reportModel;
    private ReportSelectionEnum reportSelectionType;
    private ChartSaleSelectionEnum chartSaleSelectionType = ChartSaleSelectionEnum.GROSS_SALES;

    private int saleSummaryViewId;
    private int salePaymentTypeId;
    private int saleTopItemsId;
    private int saleTopCategoriesId;
    private int saleMainRelLayoutId;
    private ReturnReportModelCallback returnReportModelCallback;

    public interface ReturnReportModelCallback{
        void OnReturnReportModel(ReportModel reportModel);
    }

    public SalesDetailFragment(Date startDate, Date endDate, boolean isThisDevice, ReportSelectionEnum reportSelectionType) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.isThisDevice = isThisDevice;
        this.reportSelectionType = reportSelectionType;
    }

    public void setReturnReportModelCallback(ReturnReportModelCallback returnReportModelCallback) {
        this.returnReportModelCallback = returnReportModelCallback;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        initVariables();
        initListeners();
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

        if(reportSelectionType == ReportSelectionEnum.ONE_D){

            mView = inflater.inflate(R.layout.fragment_sale_summary_report_one_day, container, false);
            graphicSummaryChart = mView.findViewById(R.id.graphicSummaryChart);

        }else if(reportSelectionType == ReportSelectionEnum.ONE_W){

            mView = inflater.inflate(R.layout.fragment_sale_summary_report_one_week, container, false);
            graphicSummaryChart = mView.findViewById(R.id.graphicSummaryChart);

        }else if(reportSelectionType == ReportSelectionEnum.ONE_M){

            mView = inflater.inflate(R.layout.fragment_sale_summary_report_one_month, container, false);
            graphicSummaryChart = mView.findViewById(R.id.graphicSummaryChart);

        }else if(reportSelectionType == ReportSelectionEnum.THREE_M){

            mView = inflater.inflate(R.layout.fragment_sale_summary_report_three_month, container, false);
            graphicSummaryChart = mView.findViewById(R.id.graphicSummaryChart);

        }else if(reportSelectionType == ReportSelectionEnum.ONE_Y){
            mView = inflater.inflate(R.layout.fragment_sale_summary_report_one_year, container, false);
            graphicSummaryChart = mView.findViewById(R.id.graphicSummaryChart);

        }else if(reportSelectionType == ReportSelectionEnum.NOT_DEFINED){
            mView = inflater.inflate(R.layout.fragment_sale_summary_report_not_defined, container, false);
            graphicSummaryChart = mView.findViewById(R.id.graphicSummaryChart);

        }

        ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    private void initListeners() {

        salesSummaryMenuImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getContext(), salesSummaryMenuImgv);

                popupMenu.inflate(R.menu.menu_sales_summary);

                if(isSaleOverviewDisplayed){
                    popupMenu.getMenu().findItem(R.id.showOverview).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.showDetail).setVisible(true);
                }else {
                    popupMenu.getMenu().findItem(R.id.showOverview).setVisible(true);
                    popupMenu.getMenu().findItem(R.id.showDetail).setVisible(false);
                }

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {

                            case R.id.showOverview:
                                isSaleOverviewDisplayed = true;
                                addSaleOverviewFragment();
                                break;
                            case R.id.showDetail:
                                isSaleOverviewDisplayed = false;
                                addSaleOverviewFragment();
                                break;

                            case R.id.dismiss:
                                popupMenu.dismiss();
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        graphicSummaryMenuImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getContext(), graphicSummaryMenuImgv);

                popupMenu.inflate(R.menu.menu_sales_graphic);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {

                            case R.id.grossSales:
                                chartSaleSelectionType = ChartSaleSelectionEnum.GROSS_SALES;
                                chartRoute();
                                break;
                            case R.id.netSales:
                                chartSaleSelectionType = ChartSaleSelectionEnum.NET_SALES;
                                chartRoute();
                                break;

                            case R.id.salesCount:
                                chartSaleSelectionType = ChartSaleSelectionEnum.SALES_COUNT;
                                chartRoute();
                                break;

                            case R.id.dismiss:
                                popupMenu.dismiss();
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

    }

    private void initVariables() {
        setViewIds();
        getSaleModels();
        checkDataExistance();
    }

    private void checkDataExistance(){
        if(reportModel.getSaleCount() == 0){
            mainll.setVisibility(View.GONE);
            addNoSalesFragment();
        }else {
            mainll.setVisibility(View.VISIBLE);
            addSaleOverviewFragment();
            setInitialChartData();
            addSalesPaymTypeFragment();
            addTopItemsFragment();
            addTopCategoriesFragment();
        }
    }

    private void setViewIds() {

        if(reportSelectionType == ReportSelectionEnum.ONE_D){

            saleSummaryViewId = R.id.oneDaySalesSummaryRl;
            salePaymentTypeId = R.id.oneDayPaymentTypeRl;
            saleTopItemsId = R.id.oneDayTopItemsRl;
            saleTopCategoriesId = R.id.oneDayTopCategoriesRl;
            saleMainRelLayoutId = R.id.oneDayMainRl;

        }else if(reportSelectionType == ReportSelectionEnum.ONE_W){

            saleSummaryViewId = R.id.oneWeekSalesSummaryRl;
            salePaymentTypeId = R.id.oneWeekPaymentTypeRl;
            saleTopItemsId = R.id.oneWeekTopItemsRl;
            saleTopCategoriesId = R.id.oneWeekTopCategoriesRl;
            saleMainRelLayoutId = R.id.oneWeekMainRl;

        }else if(reportSelectionType == ReportSelectionEnum.ONE_M){

            saleSummaryViewId = R.id.oneMonthSalesSummaryRl;
            salePaymentTypeId = R.id.oneMonthPaymentTypeRl;
            saleTopItemsId = R.id.oneMonthTopItemsRl;
            saleTopCategoriesId = R.id.oneMonthTopCategoriesRl;
            saleMainRelLayoutId = R.id.oneMonthMainRl;

        }else if(reportSelectionType == ReportSelectionEnum.THREE_M){

            saleSummaryViewId = R.id.threeMonthSalesSummaryRl;
            salePaymentTypeId = R.id.threeMonthPaymentTypeRl;
            saleTopItemsId = R.id.threeMonthTopItemsRl;
            saleTopCategoriesId = R.id.threeMonthTopCategoriesRl;
            saleMainRelLayoutId = R.id.threeMonthMainRl;

        }else if(reportSelectionType == ReportSelectionEnum.ONE_Y){

            saleSummaryViewId = R.id.oneYearSalesSummaryRl;
            salePaymentTypeId = R.id.oneYearPaymentTypeRl;
            saleTopItemsId = R.id.oneYearTopItemsRl;
            saleTopCategoriesId = R.id.oneYearTopCategoriesRl;
            saleMainRelLayoutId = R.id.oneYearMainRl;

        }else if(reportSelectionType == ReportSelectionEnum.NOT_DEFINED){

            saleSummaryViewId = R.id.notDefSalesSummaryRl;
            salePaymentTypeId = R.id.notDefPaymentTypeRl;
            saleTopItemsId = R.id.notDefTopItemsRl;
            saleTopCategoriesId = R.id.notDefTopCategoriesRl;
            saleMainRelLayoutId = R.id.notDefMainRl;
        }
    }

    private void setInitialChartData(){
        chartManager = new ChartManager3(getContext(), saleModels, chartSaleSelectionType);
        chartManager.setBarChart(graphicSummaryChart);
        //chartManager.setLineChart(graphicSummaryLineChart);
        chartRoute();
    }


    private void chartRoute(){
        chartManager.setChartSaleSelectionEnum(chartSaleSelectionType);

        if(reportSelectionType == ReportSelectionEnum.ONE_D){
            chartManager.fillOneDayChartVariables();
            setBarChartData();
        }else if(reportSelectionType == ReportSelectionEnum.ONE_W){
            chartManager.fillOneWeekChartVariables();
            setBarChartData();
        }else if(reportSelectionType == ReportSelectionEnum.ONE_M){
            chartManager.setReportSelectionEnum(reportSelectionType);
            chartManager.fillOneMonthChartVariables();
            setBarChartData();
        }else if(reportSelectionType == ReportSelectionEnum.THREE_M){

            chartManager.setStartDate(startDate);
            chartManager.setReportSelectionEnum(reportSelectionType);
            chartManager.fillThreeMonthChartVariables();
            setBarChartData();

        }else if(reportSelectionType == ReportSelectionEnum.ONE_Y){

            chartManager.setReportSelectionEnum(reportSelectionType);
            chartManager.fillOneYearChartVariables();
            setBarChartData();

        }else if(reportSelectionType == ReportSelectionEnum.NOT_DEFINED){

            chartManager.setStartDate(startDate);
            chartManager.setEndDate(endDate);
            chartManager.setReportSelectionEnum(reportSelectionType);
            chartManager.fillUndefinedSalesList();
            setBarChartData();

        }
        setChartTitle();
    }

    private void setBarChartData(){
        graphicSummaryChart.setData(chartManager.getBARDATA());
        graphicSummaryChart.animateY(2500);
        graphicSummaryChart.notifyDataSetChanged();
        graphicSummaryChart.invalidate();
    }

    private void setlineChartData(){
        /*graphicSummaryLineChart.setData(chartManager.getLineData());
        graphicSummaryLineChart.animateY(2500);
        graphicSummaryLineChart.notifyDataSetChanged();
        graphicSummaryLineChart.invalidate();*/
    }

    private void setChartTitle(){
        if(reportSelectionType == ReportSelectionEnum.ONE_D){

            if(chartSaleSelectionType == ChartSaleSelectionEnum.GROSS_SALES)
                graphicSummaryTv.setText(getResources().getString(R.string.daily_gross_sales));
            else if(chartSaleSelectionType == ChartSaleSelectionEnum.NET_SALES)
                graphicSummaryTv.setText(getResources().getString(R.string.daily_net_sales));
            else if(chartSaleSelectionType == ChartSaleSelectionEnum.SALES_COUNT)
                graphicSummaryTv.setText(getResources().getString(R.string.daily_sales_count));

        }else if(reportSelectionType == ReportSelectionEnum.ONE_W){

            if(chartSaleSelectionType == ChartSaleSelectionEnum.GROSS_SALES)
                graphicSummaryTv.setText(getResources().getString(R.string.weekly_gross_sales));
            else if(chartSaleSelectionType == ChartSaleSelectionEnum.NET_SALES)
                graphicSummaryTv.setText(getResources().getString(R.string.weekly_net_sales));
            else if(chartSaleSelectionType == ChartSaleSelectionEnum.SALES_COUNT)
                graphicSummaryTv.setText(getResources().getString(R.string.weekly_sales_count));

        }else if(reportSelectionType == ReportSelectionEnum.ONE_M){

            if(chartSaleSelectionType == ChartSaleSelectionEnum.GROSS_SALES)
                graphicSummaryTv.setText(getResources().getString(R.string.monthly_gross_sales));
            else if(chartSaleSelectionType == ChartSaleSelectionEnum.NET_SALES)
                graphicSummaryTv.setText(getResources().getString(R.string.monthly_net_sales));
            else if(chartSaleSelectionType == ChartSaleSelectionEnum.SALES_COUNT)
                graphicSummaryTv.setText(getResources().getString(R.string.monthly_sales_count));

        }else if(reportSelectionType == ReportSelectionEnum.THREE_M){

            if(chartSaleSelectionType == ChartSaleSelectionEnum.GROSS_SALES)
                graphicSummaryTv.setText(getResources().getString(R.string.three_monthly_gross_sales));
            else if(chartSaleSelectionType == ChartSaleSelectionEnum.NET_SALES)
                graphicSummaryTv.setText(getResources().getString(R.string.three_monthly_net_sales));
            else if(chartSaleSelectionType == ChartSaleSelectionEnum.SALES_COUNT)
                graphicSummaryTv.setText(getResources().getString(R.string.three_monthly_sales_count));

        }else if(reportSelectionType == ReportSelectionEnum.ONE_Y){

            if(chartSaleSelectionType == ChartSaleSelectionEnum.GROSS_SALES)
                graphicSummaryTv.setText(getResources().getString(R.string.yearly_gross_sales));
            else if(chartSaleSelectionType == ChartSaleSelectionEnum.NET_SALES)
                graphicSummaryTv.setText(getResources().getString(R.string.yearly_net_sales));
            else if(chartSaleSelectionType == ChartSaleSelectionEnum.SALES_COUNT)
                graphicSummaryTv.setText(getResources().getString(R.string.yearly_sales_count));

        }else if(reportSelectionType == ReportSelectionEnum.NOT_DEFINED){

            if(chartSaleSelectionType == ChartSaleSelectionEnum.GROSS_SALES)
                graphicSummaryTv.setText(getResources().getString(R.string.gross_sales));
            else if(chartSaleSelectionType == ChartSaleSelectionEnum.NET_SALES)
                graphicSummaryTv.setText(getResources().getString(R.string.net_sales));
            else if(chartSaleSelectionType == ChartSaleSelectionEnum.SALES_COUNT)
                graphicSummaryTv.setText(getResources().getString(R.string.sales_count));
        }
    }

    @SuppressLint("HardwareIds")
    private void getSaleModels(){
        saleModels = SaleDBHelper.getSaleModelsForReport(user.getUuid(),
                isThisDevice ? Settings.Secure.getString(Objects.requireNonNull(getContext()).getContentResolver(), Settings.Secure.ANDROID_ID) : null,
                startDate, endDate);

        SaleReportManager saleReportManager = new SaleReportManager(saleModels,
                startDate, endDate);
        reportModel = saleReportManager.getReportModel();

        returnReportModelCallback.OnReturnReportModel(reportModel);

        LogUtil.logReportModel(reportModel);
    }

    private void addSaleOverviewFragment(){

        if(isSaleOverviewDisplayed){
            ReportUIHelper.addSaleOverviewFragment(Objects.requireNonNull(getActivity()), reportModel, saleSummaryViewId);
            salesSummaryTv.setText(getResources().getString(R.string.sales_summary_overview));
        }else {
            ReportUIHelper.addSaleDetailsFragment(Objects.requireNonNull(getActivity()), reportModel, saleSummaryViewId);
            salesSummaryTv.setText(getResources().getString(R.string.sales_summary_details));
        }
    }

    private void addSalesPaymTypeFragment(){
        SalesByPaymentTypeFragment salesByPaymentTypeFragment = new SalesByPaymentTypeFragment(reportModel);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(salePaymentTypeId, salesByPaymentTypeFragment, SalesByPaymentTypeFragment.class.getName())
                .addToBackStack(null)
                .commit();
    }

    private void addTopItemsFragment(){
        SalesTopItemsFragment salesTopItemsFragment = new SalesTopItemsFragment(reportModel);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(saleTopItemsId, salesTopItemsFragment, SalesTopItemsFragment.class.getName())
                .addToBackStack(null)
                .commit();
    }

    private void addTopCategoriesFragment() {
        SalesTopCategoriesFragment salesTopCategoriesFragment = new SalesTopCategoriesFragment(reportModel);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(saleTopCategoriesId, salesTopCategoriesFragment, SalesTopCategoriesFragment.class.getName())
                .addToBackStack(null)
                .commit();
    }

    private void addNoSalesFragment(){
        NoSalesFragment noSalesFragment = new NoSalesFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(saleMainRelLayoutId, noSalesFragment, NoSalesFragment.class.getName())
                .addToBackStack(null)
                .commit();
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public ReportSelectionEnum getReportSelectionType() {
        return reportSelectionType;
    }
}