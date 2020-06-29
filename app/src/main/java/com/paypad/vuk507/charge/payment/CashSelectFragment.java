package com.paypad.vuk507.charge.payment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.charge.dynamicStruct.adapters.DynamicPaymentSelectAdapter;
import com.paypad.vuk507.charge.dynamicStruct.interfaces.ReturnPaymentCallback;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.PaymentTypeEnum;
import com.paypad.vuk507.enums.ProcessDirectionEnum;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.paypad.vuk507.constants.CustomConstants.TYPE_PRICE;

public class CashSelectFragment extends BaseFragment {

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

    public CashSelectFragment(Transaction transaction) {
        mTransaction = transaction;
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
                mFragmentNavigation.pushFragment(new PaymentFragment(mTransaction));
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
        tenderAmountEt.addTextChangedListener(new NumberFormatWatcher(tenderAmountEt, TYPE_PRICE));
        String amountStr = CommonUtils.getDoubleStrValueForView(mTransaction.getTransactionAmount(), TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol())
                .concat(" ")
                .concat(getResources().getString(R.string.cash));
        toolbarTitleTv.setText(amountStr);
        tenderAmountEt.setHint(CommonUtils.getDoubleStrValueForView(mTransaction.getTransactionAmount(), TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol()));
    }

}