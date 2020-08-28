package com.paypad.parator.menu.reports;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.menu.reports.interfaces.ReturnReportDateCallback;
import com.paypad.parator.model.pojo.ReportDate;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NoSalesFragment extends BaseFragment implements ReturnReportDateCallback {

    private View mView;

    @BindView(R.id.btnSelectTimeFrame)
    Button btnSelectTimeFrame;

    private CustomizeReportFragment customizeReportFragment;

    public NoSalesFragment() {

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
            mView = inflater.inflate(R.layout.fragment_no_sales, container, false);
            ButterKnife.bind(this, mView);
            initListeners();
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    private void initListeners() {
        btnSelectTimeFrame.setOnClickListener(view -> {
            initCustomizeReportFragment();
            mFragmentNavigation.pushFragment(customizeReportFragment);
        });
    }

    private void initCustomizeReportFragment(){
        customizeReportFragment = new CustomizeReportFragment();
        customizeReportFragment.setReturnReportDateCallback(this);
    }

    @Override
    public void OnReturnDates(ReportDate reportDate) {

    }
}
