package com.paypad.vuk507.charge.payment;

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

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.charge.dynamicStruct.adapters.DynamicPaymentSelectAdapter;
import com.paypad.vuk507.charge.payment.interfaces.PaymentStatusCallback;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.PaymentTypeEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.model.Transaction;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.model.pojo.SaleModelInstance;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.DataUtils;
import com.paypad.vuk507.utils.NumberFormatWatcher;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.paypad.vuk507.constants.CustomConstants.LANGUAGE_EN;
import static com.paypad.vuk507.constants.CustomConstants.LANGUAGE_TR;
import static com.paypad.vuk507.constants.CustomConstants.MAX_PRICE_VALUE;
import static com.paypad.vuk507.constants.CustomConstants.TYPE_PRICE;

public class CreditCardSelectFragment extends BaseFragment implements PaymentStatusCallback {

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
    private Transaction mTransaction;
    private double totalAmount;
    private PaymentFragment paymentFragment;
    private PaymentStatusCallback paymentStatusCallback;

    public CreditCardSelectFragment(Transaction transaction) {
        mTransaction = transaction;
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
            mView = inflater.inflate(R.layout.fragment_credit_card_select, container, false);
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
                double tipAmount = totalAmount - mTransaction.getTransactionAmount();

                mTransaction.setTotalAmount(totalAmount);
                mTransaction.setTipAmount(tipAmount);

                initPaymentFragment();
                mFragmentNavigation.pushFragment(paymentFragment);
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
                    setToolbarDynamicTitle(amount);

                    if(amount < mTransaction.getTransactionAmount())
                        CommonUtils.setSaveBtnEnability(false, btnTender, getContext());
                    else{
                        totalAmount = amount;
                        CommonUtils.setSaveBtnEnability(true, btnTender, getContext());
                    }
                } else {
                    CommonUtils.setSaveBtnEnability(true, btnTender, getContext());
                }
            }
        });
    }

    private void initVariables() {
        totalAmount = mTransaction.getTransactionAmount();
        mTransaction.setPaymentTypeId(PaymentTypeEnum.CREDIT_CARD.getId());
        tenderAmountEt.addTextChangedListener(new NumberFormatWatcher(tenderAmountEt, TYPE_PRICE, MAX_PRICE_VALUE));
        setToolbarTitleInitValue();
        tenderAmountEt.setHint(CommonUtils.getDoubleStrValueForView(mTransaction.getTransactionAmount(), TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol()));
    }

    private void initPaymentFragment(){
        paymentFragment = new PaymentFragment(mTransaction);
        paymentFragment.setPaymentStatusCallback(this);
    }

    private void setToolbarTitleInitValue(){
        String amountStr = CommonUtils.getDoubleStrValueForView(mTransaction.getTransactionAmount(), TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol());
        toolbarTitleTv.setText(amountStr);
    }

    private void setToolbarDynamicTitle(double amount){
        if (amount > mTransaction.getTransactionAmount()) {
            double tipAmount = amount - mTransaction.getTransactionAmount();

            String title = CommonUtils.getDoubleStrValueForView(mTransaction.getTransactionAmount(), TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol())
                    .concat(" + ")
                    .concat(CommonUtils.getDoubleStrValueForView(tipAmount, TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol()))
                    .concat(" ").concat(getResources().getString(R.string.tip));
            toolbarTitleTv.setText(title);
        } else
            setToolbarTitleInitValue();
    }

    @Override
    public void OnPaymentReturn(int status) {
        try{
            if(paymentFragment != null){
                Log.i("Info", "::OnPaymentReturn paymentFragment closed");
                paymentFragment.getActivity().onBackPressed();
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