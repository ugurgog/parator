package com.paypad.vuk507.menu.reports.saleReport;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.model.pojo.ReportModel;
import com.paypad.vuk507.utils.CommonUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SalesByPaymentTypeFragment extends BaseFragment {

    private View mView;

    @BindView(R.id.totalCollectedAmountTv)
    TextView totalCollectedAmountTv;
    @BindView(R.id.cashAmountTv)
    TextView cashAmountTv;
    @BindView(R.id.cashSeekBar)
    SeekBar cashSeekBar;
    @BindView(R.id.cardAmountTv)
    TextView cardAmountTv;
    @BindView(R.id.cardSeekBar)
    SeekBar cardSeekBar;
    @BindView(R.id.feeAmountTv)
    TextView feeAmountTv;
    @BindView(R.id.netTotalAmountTv)
    TextView netTotalAmountTv;

    private ReportModel reportModel;

    public SalesByPaymentTypeFragment(ReportModel reportModel) {
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
        mView = inflater.inflate(R.layout.fragment_sales_by_paym_type, container, false);
        ButterKnife.bind(this, mView);
        initVariables();
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    private void initVariables() {
        double totalCollected = reportModel.getCashAmount() + reportModel.getCardAmount();
        totalCollectedAmountTv.setText(CommonUtils.getAmountTextWithCurrency(CommonUtils.round(totalCollected, 2)));
        cashAmountTv.setText(CommonUtils.getAmountTextWithCurrency(reportModel.getCashAmount()));
        cardAmountTv.setText(CommonUtils.getAmountTextWithCurrency(reportModel.getCardAmount()));
        feeAmountTv.setText(CommonUtils.getAmountTextWithCurrency(reportModel.getFeeAmount()));
        netTotalAmountTv.setText(CommonUtils.getAmountTextWithCurrency(reportModel.getTotalAmount()));
        setSeekbars(totalCollected);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setSeekbars(double totalCollected){
        cashSeekBar.setMax((int) totalCollected);
        cardSeekBar.setMax((int) totalCollected);

        cashSeekBar.getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
        cashSeekBar.getThumb().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);

        cardSeekBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        cardSeekBar.getThumb().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);

        cashSeekBar.setProgress((int) reportModel.getCashAmount());
        cardSeekBar.setProgress((int) reportModel.getCardAmount());

        cashSeekBar.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        cardSeekBar.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }
}