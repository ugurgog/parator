package com.paypad.parator.menu.reports.saleReport;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.menu.reports.saleReport.adapter.SaleDetailDiscountAdapter;
import com.paypad.parator.model.pojo.ReportModel;
import com.paypad.parator.utils.CommonUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SalesSummaryDetailFragment extends BaseFragment {

    private View mView;

    @BindView(R.id.grossSalesTv)
    TextView grossSalesTv;
    @BindView(R.id.refundsAmountTv)
    TextView refundsAmountTv;
    @BindView(R.id.discountsAmountTv)
    TextView discountsAmountTv;
    @BindView(R.id.discountsRv)
    RecyclerView discountsRv;
    @BindView(R.id.netSalesTv)
    TextView netSalesTv;
    @BindView(R.id.taxAmountTv)
    TextView taxAmountTv;
    @BindView(R.id.tipsAmountTv)
    TextView tipsAmountTv;
    @BindView(R.id.cancelsAmountTv)
    TextView cancelsAmountTv;
    @BindView(R.id.totalAmountTv)
    TextView totalAmountTv;

    private ReportModel reportModel;
    private SaleDetailDiscountAdapter saleDetailDiscountAdapter;

    public SalesSummaryDetailFragment(ReportModel reportModel) {
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
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_sale_details, container, false);
            ButterKnife.bind(this, mView);
            initVariables();
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    private void initVariables() {
        grossSalesTv.setText(CommonUtils.getAmountTextWithCurrency(reportModel.getGrossSalesAmount()));
        refundsAmountTv.setText("(".concat(CommonUtils.getAmountTextWithCurrency(reportModel.getRefundsAmount())).concat(")"));
        cancelsAmountTv.setText("(".concat(CommonUtils.getAmountTextWithCurrency(reportModel.getCancelAmount())).concat(")"));
        discountsAmountTv.setText("(".concat(CommonUtils.getAmountTextWithCurrency(reportModel.getDiscountAmount())).concat(")"));
        netSalesTv.setText(CommonUtils.getAmountTextWithCurrency(reportModel.getNetSalesAmount()));
        taxAmountTv.setText(CommonUtils.getAmountTextWithCurrency(reportModel.getTaxAmount()));
        tipsAmountTv.setText(CommonUtils.getAmountTextWithCurrency(reportModel.getTipsAmount()));
        totalAmountTv.setText(CommonUtils.getAmountTextWithCurrency(reportModel.getTotalAmount()));
        setAdapter();
    }

    private void setAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        discountsRv.setLayoutManager(linearLayoutManager);

        saleDetailDiscountAdapter = new SaleDetailDiscountAdapter(reportModel.getDiscounts());
        discountsRv.setAdapter(saleDetailDiscountAdapter);
    }
}