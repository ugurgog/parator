package com.paypad.vuk507.charge.payment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
import com.paypad.vuk507.charge.order.IOrderManager;
import com.paypad.vuk507.charge.order.OrderManager;
import com.paypad.vuk507.db.DynamicBoxModelDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.DynamicStructEnum;
import com.paypad.vuk507.enums.PaymentTypeEnum;
import com.paypad.vuk507.enums.ProcessDirectionEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.model.DynamicBoxModel;
import com.paypad.vuk507.model.Product;
import com.paypad.vuk507.model.Split;
import com.paypad.vuk507.model.Transaction;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.model.pojo.SaleModelInstance;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.DataUtils;
import com.paypad.vuk507.utils.LogUtil;
import com.paypad.vuk507.utils.NumberFormatWatcher;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.paypad.vuk507.constants.CustomConstants.LANGUAGE_EN;
import static com.paypad.vuk507.constants.CustomConstants.LANGUAGE_TR;
import static com.paypad.vuk507.constants.CustomConstants.TYPE_PRICE;


public class SplitAmountFragment extends BaseFragment implements NumberFormatWatcher.ReturnEtTextCallback {

    private View mView;

    @BindView(R.id.cancelImgv)
    ClickableImageView cancelImgv;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.saveBtn)
    Button saveBtn;

    @BindView(R.id.remainInfoTv)
    TextView remainInfoTv;
    @BindView(R.id.amountEt)
    EditText amountEt;
    @BindView(R.id.continueBtn)
    Button continueBtn;
    @BindView(R.id.splitInfoTv)
    TextView splitInfoTv;

    @BindView(R.id.splitTwoBtn)
    Button splitTwoBtn;
    @BindView(R.id.splitThreeBtn)
    Button splitThreeBtn;
    @BindView(R.id.splitFourBtn)
    Button splitFourBtn;
    @BindView(R.id.splitCustomBtn)
    Button splitCustomBtn;

    private User user;
    //private double amount;
    private Transaction mTransaction;
    private CompleteCallback completeCallback;
    private CustomSplitFragment customSplitFragment;
    private NumberFormatWatcher numberFormatWatcher;
    private IOrderManager orderManager;

    public SplitAmountFragment(Transaction transaction, CompleteCallback completeCallback) {
        this.mTransaction = transaction;
        this.completeCallback = completeCallback;
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
            mView = inflater.inflate(R.layout.fragment_split_amount, container, false);
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
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });

        splitTwoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeNotPayedSplits();
                decideSplitCount(2);
            }
        });

        splitThreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeNotPayedSplits();
                decideSplitCount(3);
            }
        });

        splitFourBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeNotPayedSplits();
                decideSplitCount(4);
            }
        });

        splitCustomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initCustomSplitFragment();
                mFragmentNavigation.pushFragment(customSplitFragment);
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double firstAmount = DataUtils.getDoubleValueFromFormattedString(amountEt.getText().toString());

                if(firstAmount == mTransaction.getTransactionAmount()){
                    Objects.requireNonNull(getActivity()).onBackPressed();
                }else {
                    removeNotPayedSplits();
                    orderManager.addTransactionToOrder(firstAmount);
                    //decideSplitCountByAmount(firstAmount);

                    double secondAmount = mTransaction.getTransactionAmount() - firstAmount;

                    if(secondAmount > 0)
                        orderManager.addTransactionToOrder(secondAmount);

                    LogUtil.logTransactions(SaleModelInstance.getInstance().getSaleModel().getTransactions());

                    completeCallback.onComplete(null);
                    Objects.requireNonNull(getActivity()).onBackPressed();
                }
            }
        });
    }

    private void initVariables() {
        orderManager = new OrderManager();
        initNumberFormatWatcher();
        amountEt.addTextChangedListener(numberFormatWatcher);
        amountEt.setHint("0.00 ".concat(CommonUtils.getCurrency().getSymbol()));
        CommonUtils.setAmountToView(mTransaction.getTransactionAmount(), amountEt, TYPE_PRICE);
        saveBtn.setVisibility(View.GONE);
        String amountStr = CommonUtils.getDoubleStrValueForView(SaleModelInstance.getInstance().getSaleModel().getSale().getRemainAmount(), TYPE_PRICE)
                .concat(" ")
                .concat(CommonUtils.getCurrency().getSymbol());
        toolbarTitleTv.setText(amountStr);
        setSplitInfoTv(mTransaction.getTransactionAmount());
    }

    private void initNumberFormatWatcher(){
        numberFormatWatcher = new NumberFormatWatcher(amountEt, TYPE_PRICE, SaleModelInstance.getInstance().getSaleModel().getSale().getRemainAmount());
        numberFormatWatcher.setReturnEtTextCallback(this);
    }

    private void setSplitInfoTv(double amount) {
        String infoText = "";

        double remainAmount = SaleModelInstance.getInstance().getSaleModel().getSale().getRemainAmount() - amount;

        if(CommonUtils.getLanguage().equals(LANGUAGE_TR)){
            infoText = CommonUtils.getDoubleStrValueForView(SaleModelInstance.getInstance().getSaleModel().getSale().getDiscountedAmount(), TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol())
                    .concat(" toplam tutardan, ödeme sonrası kalacak tutar ")
                    .concat(CommonUtils.getDoubleStrValueForView(remainAmount, TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol()))
                    .concat(".");
        }else if (CommonUtils.getLanguage().equals(LANGUAGE_EN)){
            infoText = CommonUtils.getDoubleStrValueForView(remainAmount, TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol())
                    .concat(" of ")
                    .concat(CommonUtils.getDoubleStrValueForView(SaleModelInstance.getInstance().getSaleModel().getSale().getDiscountedAmount(), TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol()))
                    .concat(" will remain after this transaction.");
        }
        splitInfoTv.setText(infoText);
    }

    private void initCustomSplitFragment(){
        customSplitFragment = new CustomSplitFragment(mTransaction.getTransactionAmount(), new CustomSplitFragment.ReturnSplitCountCallback() {
            @Override
            public void OnReturnSplitCount(int splitCount) {
                removeNotPayedSplits();
                decideSplitCount(splitCount);
            }
        });
    }

    private void removeNotPayedSplits(){
        if(SaleModelInstance.getInstance().getSaleModel().getTransactions() != null ){
            for(Iterator<Transaction> it = SaleModelInstance.getInstance().getSaleModel().getTransactions().iterator(); it.hasNext();) {
               Transaction transaction = it.next();

                if(!transaction.isPaymentCompleted())
                    it.remove();
            }
        }
    }

    private void decideSplitCount(int splitCount){
        double splitAmount = SaleModelInstance.getInstance().getSaleModel().getSale().getRemainAmount() / (double) splitCount;

        for(int count = 0; count < splitCount; count ++){
            orderManager.addTransactionToOrder(splitAmount);
        }

        LogUtil.logTransactions(SaleModelInstance.getInstance().getSaleModel().getTransactions());

        completeCallback.onComplete(null);

        if(customSplitFragment != null)
            Objects.requireNonNull(customSplitFragment.getActivity()).onBackPressed();

        Objects.requireNonNull(getActivity()).onBackPressed();
    }

    /*private void decideSplitCountByAmount(double amountx){
        Transaction transaction = new Transaction();
        transaction.setTransactionUuid(UUID.randomUUID().toString());
        transaction.setSaleUuid(SaleModelInstance.getInstance().getSaleModel().getSale().getSaleUuid());
        transaction.setTransactionAmount(amountx);
        transaction.setSeqNumber(SaleModelInstance.getInstance().getSaleModel().getMaxSplitId() + 1);
        SaleModelInstance.getInstance().getSaleModel().getTransactions().add(transaction);
    }*/

    @Override
    public void OnReturnEtValue(String text) {
        if(text == null || text.isEmpty()){
            CommonUtils.setSaveBtnEnability(false, continueBtn, getContext());
            setSplitInfoTv(0d);
        } else{
            CommonUtils.setSaveBtnEnability(true, continueBtn, getContext());
            double amount = DataUtils.getDoubleValueFromFormattedString(text);
            setSplitInfoTv(amount);
        }

    }
}