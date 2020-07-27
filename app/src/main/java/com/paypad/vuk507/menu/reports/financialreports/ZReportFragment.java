package com.paypad.vuk507.menu.reports.financialreports;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.enums.FinancialReportsEnum;
import com.paypad.vuk507.menu.reports.adapters.FinancialReportAdapter;
import com.paypad.vuk507.menu.reports.interfaces.ReturnFinancialReportItemCallback;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CommonUtils;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.paypad.vuk507.constants.CustomConstants.LANGUAGE_TR;

public class ZReportFragment extends BaseFragment implements ReturnFinancialReportItemCallback {

    private View mView;

    @BindView(R.id.reportsRv)
    RecyclerView reportsRv;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.backImgv)
    ClickableImageView backImgv;

    public ZReportFragment() {

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
            mView = inflater.inflate(R.layout.fragment_reports, container, false);
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
    }

    private void initVariables() {
        toolbarTitleTv.setText(CommonUtils.getLanguage().equals(LANGUAGE_TR) ? FinancialReportsEnum.Z_REPORT.getLabelTr() : FinancialReportsEnum.Z_REPORT.getLabelEn());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        reportsRv.setLayoutManager(linearLayoutManager);
        setAdapter();
    }

    private void setAdapter() {

        FinancialReportsEnum[] financialReportsEnums = new FinancialReportsEnum[]{
                FinancialReportsEnum.Z_REPORT,
                FinancialReportsEnum.Z_TERM_REPORT
        };

        FinancialReportAdapter reportAdapter = new FinancialReportAdapter(financialReportsEnums);
        reportAdapter.setCallback(this);
        reportsRv.setAdapter(reportAdapter);
    }

    @Override
    public void OnReturnReportItem(FinancialReportsEnum reportsEnum) {

        if(reportsEnum == FinancialReportsEnum.Z_REPORT){

        }else if(reportsEnum == FinancialReportsEnum.Z_TERM_REPORT){
            mFragmentNavigation.pushFragment(new ZTermReportFragment());
        }
    }
}
