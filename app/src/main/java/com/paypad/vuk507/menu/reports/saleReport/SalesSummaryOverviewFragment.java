package com.paypad.vuk507.menu.reports.saleReport;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.model.pojo.ReportModel;
import com.paypad.vuk507.utils.CommonUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SalesSummaryOverviewFragment extends BaseFragment {

    private View mView;

    @BindView(R.id.grossSalesTv)
    TextView grossSalesTv;
    @BindView(R.id.netSalesTv)
    TextView netSalesTv;
    @BindView(R.id.salesCountTv)
    TextView salesCountTv;
    @BindView(R.id.averageSaleTv)
    TextView averageSaleTv;
    @BindView(R.id.refundsTv)
    TextView refundsTv;
    @BindView(R.id.discountsAmountTv)
    TextView discountsAmountTv;


    private ReportModel reportModel;

    public SalesSummaryOverviewFragment(ReportModel reportModel) {
        this.reportModel = reportModel;
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
        mView = inflater.inflate(R.layout.fragment_sale_overview, container, false);
        ButterKnife.bind(this, mView);
        initVariables();
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    private void initVariables() {
        grossSalesTv.setText(CommonUtils.getAmountTextWithCurrency(reportModel.getGrossSalesAmount()));
        netSalesTv.setText(CommonUtils.getAmountTextWithCurrency(reportModel.getNetSalesAmount()));
        salesCountTv.setText(String.valueOf(reportModel.getSaleCount()));
        averageSaleTv.setText(CommonUtils.getAmountTextWithCurrency(reportModel.getAverageSaleAmount()));
        refundsTv.setText(CommonUtils.getAmountTextWithCurrency(
                CommonUtils.round(reportModel.getRefundsAmount() + reportModel.getCancelAmount(), 2)));
        discountsAmountTv.setText(CommonUtils.getAmountTextWithCurrency(reportModel.getDiscountAmount()));
    }

}