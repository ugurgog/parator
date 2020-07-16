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
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.mikephil.charting.charts.BarChart;
import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.db.SaleDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.ChartSaleSelectionEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.menu.reports.ChartManager;
import com.paypad.vuk507.menu.reports.SaleReportManager;
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

public class OneDaySaleFragment extends BaseFragment {

    private View mView;

    //Sales Summary
    @BindView(R.id.salesSummaryTv)
    TextView salesSummaryTv;
    @BindView(R.id.salesSummaryMenuImgv)
    ImageView salesSummaryMenuImgv;
    @BindView(R.id.oneDaySalesSummaryRl)
    RelativeLayout oneDaySalesSummaryRl;

    @BindView(R.id.graphicSummaryMenuImgv)
    ImageView graphicSummaryMenuImgv;
    @BindView(R.id.graphicSummaryChart)
    BarChart graphicSummaryChart;
    @BindView(R.id.graphicSummaryTv)
    TextView graphicSummaryTv;

    @BindView(R.id.OneDayPaymentTypeRl)
    RelativeLayout OneDayPaymentTypeRl;



    private User user;

    private Date startDate;
    private Date endDate;
    private boolean isThisDevice;

    private boolean isSaleOverviewDisplayed = true;

    private ChartManager chartManager;

    private List<SaleModel> saleModels = new ArrayList<>();
    private ReportModel reportModel;


    public OneDaySaleFragment(Date startDate, Date endDate, boolean isThisDevice) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.isThisDevice = isThisDevice;
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
        mView = inflater.inflate(R.layout.fragment_sale_summary_report_one_day, container, false);
        ButterKnife.bind(this, mView);
        //initVariables();
        //initListeners();
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
                                graphicSummaryTv.setText(getResources().getString(R.string.daily_gross_sales));
                                chartManager.fillOneDayChartVariables(ChartSaleSelectionEnum.GROSS_SALES);
                                setChartData();
                                break;
                            case R.id.netSales:
                                graphicSummaryTv.setText(getResources().getString(R.string.daily_net_sales));
                                chartManager.fillOneDayChartVariables(ChartSaleSelectionEnum.NET_SALES);
                                setChartData();
                                break;

                            case R.id.salesCount:
                                graphicSummaryTv.setText(getResources().getString(R.string.daily_sales_count));
                                chartManager.fillOneDayChartVariables(ChartSaleSelectionEnum.SALES_COUNT);
                                setChartData();
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
        getSaleModels();
        addSaleOverviewFragment();
        setInitialChartData();
        addSalesPaymTypeFragment();
        addTopItemsFragment();
    }

    private void setInitialChartData(){
        graphicSummaryTv.setText(getResources().getString(R.string.daily_gross_sales));
        chartManager = new ChartManager(getContext(), saleModels);
        chartManager.fillOneDayChartVariables(ChartSaleSelectionEnum.GROSS_SALES);
        setChartData();
    }

    private void setChartData(){
        graphicSummaryChart.setData(chartManager.getBARDATA());
        graphicSummaryChart.animateY(1500);
    }

    @SuppressLint("HardwareIds")
    private void getSaleModels(){
        saleModels = SaleDBHelper.getSaleModelsForReport(user.getUuid(),
                isThisDevice ? Settings.Secure.getString(Objects.requireNonNull(getContext()).getContentResolver(), Settings.Secure.ANDROID_ID) : null,
                startDate, endDate);

        SaleReportManager saleReportManager = new SaleReportManager(saleModels,
                startDate, endDate);
        reportModel = saleReportManager.getReportModel();

        LogUtil.logReportModel(reportModel);
    }

    private void addSaleOverviewFragment(){
        if(isSaleOverviewDisplayed){
            ReportUIHelper.addSaleOverviewFragment(Objects.requireNonNull(getActivity()), reportModel, R.id.oneDaySalesSummaryRl);
            salesSummaryTv.setText(getResources().getString(R.string.sales_summary_overview));
        }else {
            ReportUIHelper.addSaleDetailsFragment(Objects.requireNonNull(getActivity()), reportModel, R.id.oneDaySalesSummaryRl);
            salesSummaryTv.setText(getResources().getString(R.string.sales_summary_details));
        }
    }

    private void addSalesPaymTypeFragment(){
        SalesByPaymentTypeFragment salesByPaymentTypeFragment = new SalesByPaymentTypeFragment(reportModel);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.OneDayPaymentTypeRl, salesByPaymentTypeFragment, SalesByPaymentTypeFragment.class.getName())
                .addToBackStack(null)
                .commit();
    }

    private void addTopItemsFragment(){
        TopItemsFragment topItemsFragment = new TopItemsFragment(reportModel);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.oneDayTopItemsRl, topItemsFragment, TopItemsFragment.class.getName())
                .addToBackStack(null)
                .commit();
    }


}