package com.paypad.parator.charge.payment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.charge.dynamicStruct.adapters.DynamicPaymentSelectAdapter;
import com.paypad.parator.charge.payment.interfaces.PaymentStatusCallback;
import com.paypad.parator.charge.payment.orderpayment.OrderPaymentFragment;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.enums.PaymentTypeEnum;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.charge.payment.cancelpayment.CancellationPaymentFragment;
import com.paypad.parator.model.Transaction;
import com.paypad.parator.model.User;
import com.paypad.parator.utils.ClickableImage.ClickableImageView;
import com.paypad.parator.utils.CommonUtils;
import com.paypad.parator.utils.DataUtils;
import com.paypad.parator.utils.NumberFormatWatcher;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.paypad.parator.constants.CustomConstants.MAX_PRICE_VALUE;
import static com.paypad.parator.constants.CustomConstants.TYPE_CANCEL_PAYMENT;
import static com.paypad.parator.constants.CustomConstants.TYPE_ORDER_PAYMENT;
import static com.paypad.parator.constants.CustomConstants.TYPE_PRICE;

public class CashSelectFragment extends BaseFragment implements PaymentStatusCallback {

    private View mView;

    @BindView(R.id.tenderAmountEt)
    EditText tenderAmountEt;
    @BindView(R.id.btnTender)
    Button btnTender;
    @BindView(R.id.backImgv)
    ClickableImageView backImgv;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;

    private User user;
    private DynamicPaymentSelectAdapter dynamicPaymentSelectAdapter;
    private Transaction mTransaction;
    private double cashAmount;
    private OrderPaymentFragment orderPaymentFragment;
    private CancellationPaymentFragment cancellationPaymentFragment;
    private PaymentStatusCallback paymentStatusCallback;
    private int type;



    public CashSelectFragment(Transaction transaction, int type) {
        mTransaction = transaction;
        this.type = type;
    }

    public void setPaymentStatusCallback(PaymentStatusCallback paymentStatusCallback) {
        this.paymentStatusCallback = paymentStatusCallback;
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
            mView = inflater.inflate(R.layout.fragment_cash_select, container, false);
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

        btnTender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double changeAmount = cashAmount - mTransaction.getTransactionAmount();

                mTransaction.setCashAmount(cashAmount);
                mTransaction.setChangeAmount(changeAmount);

                if(type == TYPE_ORDER_PAYMENT){
                    initPaymentFragment();
                    mFragmentNavigation.pushFragment(orderPaymentFragment);
                }else if(type == TYPE_CANCEL_PAYMENT){
                    initCancellationPaymentFragment();
                    mFragmentNavigation.pushFragment(cancellationPaymentFragment);
                }
            }
        });

        tenderAmountEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && !s.toString().trim().isEmpty()) {

                    double amount = DataUtils.getDoubleValueFromFormattedString(tenderAmountEt.getText().toString());

                    if(amount < mTransaction.getTransactionAmount())
                        CommonUtils.setSaveBtnEnability(false, btnTender, getContext());
                    else{
                        cashAmount = amount;
                        CommonUtils.setSaveBtnEnability(true, btnTender, getContext());
                    }
                } else {
                    CommonUtils.setSaveBtnEnability(true, btnTender, getContext());
                }
            }
        });
    }

    private void initVariables() {
        cashAmount = mTransaction.getTransactionAmount();
        mTransaction.setPaymentTypeId(PaymentTypeEnum.CASH.getId());
        tenderAmountEt.addTextChangedListener(new NumberFormatWatcher(tenderAmountEt, TYPE_PRICE, MAX_PRICE_VALUE));
        String amountStr = CommonUtils.getDoubleStrValueForView(mTransaction.getTransactionAmount(), TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol())
                .concat(" ")
                .concat(getResources().getString(R.string.cash));
        toolbarTitleTv.setText(amountStr);
        tenderAmountEt.setHint(CommonUtils.getDoubleStrValueForView(mTransaction.getTransactionAmount(), TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol()));
        //throw new RuntimeException("Test Crash"); // Force a crash
    }

    private void initPaymentFragment(){
        orderPaymentFragment = new OrderPaymentFragment(mTransaction);
        orderPaymentFragment.setPaymentStatusCallback(this);
    }

    private void initCancellationPaymentFragment(){
        cancellationPaymentFragment = new CancellationPaymentFragment(mTransaction);
        cancellationPaymentFragment.setPaymentStatusCallback(this);
    }

    @Override
    public void OnPaymentReturn(int status) {
        try{
            if(type == TYPE_ORDER_PAYMENT && orderPaymentFragment != null){
                Log.i("Info", "::OnPaymentReturn orderPaymentFragment closed");
                orderPaymentFragment.getActivity().onBackPressed();
            }

            if(type == TYPE_CANCEL_PAYMENT && cancellationPaymentFragment != null){
                Log.i("Info", "::OnPaymentReturn orderPaymentFragment closed");
                cancellationPaymentFragment.getActivity().onBackPressed();
            }

            paymentStatusCallback.OnPaymentReturn(status);
        }catch (Exception e){
            paymentStatusCallback.OnPaymentReturn(status);

            Objects.requireNonNull(getActivity()).onBackPressed();
            Log.i("Info", "Error:" + e);
        }

        //Objects.requireNonNull(getActivity()).onBackPressed();

        Log.i("Info", "::OnPaymentReturn CashSelectFragment paymentStatusCallback triggered");



    }
}