package com.paypad.vuk507.menu.reports;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.enums.ReportsEnum;
import com.paypad.vuk507.menu.reports.adapters.ReportAdapter;
import com.paypad.vuk507.menu.reports.interfaces.ReturnReportDateCallback;
import com.paypad.vuk507.menu.reports.interfaces.ReturnReportItemCallback;
import com.paypad.vuk507.model.pojo.ReportDate;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;

import java.util.Objects;

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
