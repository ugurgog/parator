package com.paypad.vuk507.menu.transactions;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.db.TransactionDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.interfaces.ReturnAmountCallback;
import com.paypad.vuk507.model.Transaction;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.DataUtils;
import com.paypad.vuk507.utils.NumberFormatWatcher;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.paypad.vuk507.constants.CustomConstants.MAX_PRICE_VALUE;
import static com.paypad.vuk507.constants.CustomConstants.TYPE_PRICE;
import static com.paypad.vuk507.constants.CustomConstants.TYPE_REFUND;

public class RefundByAmountFragment extends BaseFragment {

    private View mView;

    @BindView(R.id.cancelImgv)
    ClickableImageView cancelImgv;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.saveBtn)
    Button saveBtn;

    @BindView(R.id.toolbarWithClose)
    LinearLayout toolbarWithClose;
    @BindView(R.id.refundAmountEt)
    EditText refundAmountEt;

    private User user;

    private boolean isToolbarVisible;
    private double returnAmount;
    private double refundAmount;
    private Transaction transaction;
    private ReturnAmountCallback returnAmountCallback;

    public RefundByAmountFragment(Transaction transaction, boolean isToolbarVisible, double returnAmount) {
        this.transaction = transaction;
        this.isToolbarVisible = isToolbarVisible;
        this.returnAmount = returnAmount;
    }

    public void setReturnAmountCallback(ReturnAmountCallback returnAmountCallback) {
        this.returnAmountCallback = returnAmountCallback;
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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_refund_by_amount, container, false);
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
        refundAmountEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && !s.toString().trim().isEmpty()) {
                    refundAmount = DataUtils.getDoubleValueFromFormattedString(refundAmountEt.getText().toString());
                    returnAmountCallback.OnReturnAmount(refundAmount);
                    setToolbarTitle(refundAmount);

                    if(refundAmount > 0d)
                        CommonUtils.setSaveBtnEnability(true, saveBtn, getContext());
                    else
                        CommonUtils.setSaveBtnEnability(false, saveBtn, getContext());
                }
            }
        });

        cancelImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFragmentNavigation.popFragments(1);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFragmentNavigation.pushFragment(new NFCReadForReturnFragment(transaction, refundAmount,
                        TYPE_REFUND, true, null, "XXXXX"));
            }
        });
    }

    private void initVariables() {
        setToolbarVisibility();
        refundAmountEt.addTextChangedListener(new NumberFormatWatcher(refundAmountEt, TYPE_PRICE, returnAmount));
        refundAmountEt.setHint(CommonUtils.getDoubleStrValueForView(0d, TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol()));
        setToolbarTitle(returnAmount);
        CommonUtils.setSaveBtnEnability(false, saveBtn, getContext());
    }

    private void setToolbarTitle(double amount){
        toolbarTitleTv.setText(CommonUtils.getAmountTextWithCurrency(amount)
                .concat(" ")
                .concat(getResources().getString(R.string.refund)));
    }

    private void setToolbarVisibility() {
        if(isToolbarVisible)
            toolbarWithClose.setVisibility(View.VISIBLE);
        else
            toolbarWithClose.setVisibility(View.GONE);
    }
}
