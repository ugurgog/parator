package com.paypad.parator.menu.transactions;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.interfaces.ReturnAmountCallback;
import com.paypad.parator.model.User;
import com.paypad.parator.model.pojo.SaleModel;
import com.paypad.parator.utils.ClickableImage.ClickableImageView;
import com.paypad.parator.utils.CommonUtils;
import com.paypad.parator.utils.DataUtils;
import com.paypad.parator.utils.NumberFormatWatcher;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.paypad.parator.constants.CustomConstants.TYPE_PRICE;
import static com.paypad.parator.constants.CustomConstants.TYPE_REFUND;

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
    @BindView(R.id.availableRefundAmountTv)
    TextView availableRefundAmountTv;

    private User user;

    private boolean isToolbarVisible;
    private double refundAmount;
    private SaleModel saleModel;
    private ReturnAmountCallback returnAmountCallback;
    private double availableRefundAmount;

    public RefundByAmountFragment(SaleModel saleModel, boolean isToolbarVisible, double availableRefundAmount) {
        this.saleModel = saleModel;
        this.isToolbarVisible = isToolbarVisible;
        this.availableRefundAmount = availableRefundAmount;
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
                mFragmentNavigation.pushFragment(new SelectPaymentForRefundFragment(TYPE_REFUND, saleModel, true, refundAmount, null));
            }
        });
    }

    private void initVariables() {
        setToolbarVisibility();
        refundAmountEt.addTextChangedListener(new NumberFormatWatcher(refundAmountEt, TYPE_PRICE, availableRefundAmount));
        refundAmountEt.setHint(CommonUtils.getDoubleStrValueForView(0d, TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol()));
        toolbarTitleTv.setText(getResources().getString(R.string.refund));
        CommonUtils.setSaveBtnEnability(false, saveBtn, getContext());
        setAvailableRefundAmount();
    }

    private void setAvailableRefundAmount() {
        availableRefundAmountTv.setText(getResources().getString(R.string.available_refund_amount)
            .concat(" ")
            .concat(CommonUtils.getAmountTextWithCurrency(availableRefundAmount)));
    }

    private void setToolbarVisibility() {
        if(isToolbarVisible)
            toolbarWithClose.setVisibility(View.VISIBLE);
        else
            toolbarWithClose.setVisibility(View.GONE);
    }
}
