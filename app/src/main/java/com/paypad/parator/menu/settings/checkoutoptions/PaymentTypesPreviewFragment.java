package com.paypad.parator.menu.settings.checkoutoptions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.charge.dynamicStruct.adapters.DynamicPaymentSelectAdapter;
import com.paypad.parator.charge.dynamicStruct.interfaces.ReturnPaymentCallback;
import com.paypad.parator.charge.interfaces.SaleCalculateCallback;
import com.paypad.parator.charge.order.IOrderManager;
import com.paypad.parator.charge.order.OrderManager;
import com.paypad.parator.charge.payment.CashSelectFragment;
import com.paypad.parator.charge.payment.CreditCardSelectFragment;
import com.paypad.parator.charge.payment.interfaces.PaymentStatusCallback;
import com.paypad.parator.charge.payment.orderpayment.OrderSplitAmountFragment;
import com.paypad.parator.charge.payment.utils.CancelTransactionManager;
import com.paypad.parator.db.PasscodeDBHelper;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.enums.PaymentTypeEnum;
import com.paypad.parator.enums.ProcessDirectionEnum;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.interfaces.CompleteCallback;
import com.paypad.parator.interfaces.CustomDialogListener;
import com.paypad.parator.menu.settings.passcode.PasscodeTypeActivity;
import com.paypad.parator.model.Passcode;
import com.paypad.parator.model.Transaction;
import com.paypad.parator.model.User;
import com.paypad.parator.model.pojo.BaseResponse;
import com.paypad.parator.model.pojo.SaleModelInstance;
import com.paypad.parator.uiUtils.tutorial.Tutorial;
import com.paypad.parator.uiUtils.tutorial.WalkthroughCallback;
import com.paypad.parator.utils.ClickableImage.ClickableImageView;
import com.paypad.parator.utils.CommonUtils;
import com.paypad.parator.utils.CustomDialogBox;
import com.paypad.parator.utils.DataUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;
import static com.paypad.parator.constants.CustomConstants.LANGUAGE_EN;
import static com.paypad.parator.constants.CustomConstants.LANGUAGE_TR;
import static com.paypad.parator.constants.CustomConstants.TYPE_ORDER_PAYMENT;
import static com.paypad.parator.constants.CustomConstants.TYPE_PRICE;
import static com.paypad.parator.constants.CustomConstants.WALK_THROUGH_CONTINUE;
import static com.paypad.parator.constants.CustomConstants.WALK_THROUGH_END;

public class PaymentTypesPreviewFragment extends BaseFragment {

    private View mView;

    @BindView(R.id.chargeAmountTv)
    TextView chargeAmountTv;

    @BindView(R.id.paymentsRv)
    RecyclerView paymentsRv;
    @BindView(R.id.cancelImgv)
    ClickableImageView cancelImgv;
    @BindView(R.id.splitBtn)
    Button splitBtn;

    private DynamicPaymentSelectAdapter dynamicPaymentSelectAdapter;
    private Context mContext;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;

    public PaymentTypesPreviewFragment() {

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Objects.requireNonNull(getActivity()).getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE );

        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_payment_types_preview, container, false);
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
        cancelImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity) mContext).onBackPressed();
            }
        });
    }

    private void initVariables() {
        loginPreferences = mContext.getSharedPreferences("disabledPaymentTypes", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();
        splitBtn.setTextColor(mContext.getResources().getColor(R.color.DarkGray, null));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        paymentsRv.setLayoutManager(linearLayoutManager);
        setPaymentAdapter();
        setChargeAmount();
    }

    private void setChargeAmount(){
        String amountStr = CommonUtils.getDoubleStrValueForView(25d, TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol());
        chargeAmountTv.setText(amountStr);
    }

    private void setPaymentAdapter(){
        PaymentTypeEnum[] paymentTypeEnums = PaymentTypeEnum.values();
        List<PaymentTypeEnum> paymentTypes = new ArrayList<>();

        for(PaymentTypeEnum paymentType : paymentTypeEnums){
            if(loginPreferences.getBoolean(String.valueOf(paymentType.getId()), false))
                paymentTypes.add(paymentType);
        }

        dynamicPaymentSelectAdapter = new DynamicPaymentSelectAdapter(getContext(), ProcessDirectionEnum.DIRECTION_PAYMENT_SELECT, paymentTypes, new ReturnPaymentCallback() {
            @Override
            public void onReturn(PaymentTypeEnum paymentType) {

            }
        });
        paymentsRv.setAdapter(dynamicPaymentSelectAdapter);
    }
}