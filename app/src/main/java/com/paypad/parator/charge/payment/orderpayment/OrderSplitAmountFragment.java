package com.paypad.parator.charge.payment.orderpayment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.charge.order.IOrderManager;
import com.paypad.parator.charge.order.OrderManager;
import com.paypad.parator.charge.payment.CustomSplitFragment;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.interfaces.CompleteCallback;
import com.paypad.parator.model.Transaction;
import com.paypad.parator.model.User;
import com.paypad.parator.model.pojo.SaleModelInstance;
import com.paypad.parator.utils.ClickableImage.ClickableImageView;
import com.paypad.parator.utils.CommonUtils;
import com.paypad.parator.utils.DataUtils;
import com.paypad.parator.utils.LogUtil;
import com.paypad.parator.utils.NumberFormatWatcher;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Iterator;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.paypad.parator.constants.CustomConstants.LANGUAGE_EN;
import static com.paypad.parator.constants.CustomConstants.LANGUAGE_TR;
import static com.paypad.parator.constants.CustomConstants.TYPE_PRICE;


public class OrderSplitAmountFragment extends BaseFragment implements NumberFormatWatcher.ReturnEtTextCallback {

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
    private Context mContext;

    public OrderSplitAmountFragment(Transaction transaction, CompleteCallback completeCallback) {
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
        mContext = context;
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(sticky = true)
    public void accountHolderUserReceived(UserBus userBus){
        user = userBus.getUser();
        if(user == null)
            user = UserDBHelper.getUserFromCache(mContext);
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
                    OrderManager.addTransactionToOrder(firstAmount,
                            SaleModelInstance.getInstance().getSaleModel().getTransactions(),
                            SaleModelInstance.getInstance().getSaleModel().getOrder().getId());
                    //decideSplitCountByAmount(firstAmount);

                    double secondAmount = mTransaction.getTransactionAmount() - firstAmount;

                    if(secondAmount > 0)
                        OrderManager.addTransactionToOrder(secondAmount,
                                SaleModelInstance.getInstance().getSaleModel().getTransactions(),
                                SaleModelInstance.getInstance().getSaleModel().getOrder().getId());

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
        String amountStr = CommonUtils.getDoubleStrValueForView(SaleModelInstance.getInstance().getSaleModel().getOrder().getRemainAmount(), TYPE_PRICE)
                .concat(" ")
                .concat(CommonUtils.getCurrency().getSymbol());
        toolbarTitleTv.setText(amountStr);
        setSplitInfoTv(mTransaction.getTransactionAmount());
    }

    private void initNumberFormatWatcher(){
        numberFormatWatcher = new NumberFormatWatcher(amountEt, TYPE_PRICE, SaleModelInstance.getInstance().getSaleModel().getOrder().getRemainAmount());
        numberFormatWatcher.setReturnEtTextCallback(this);
    }

    private void setSplitInfoTv(double amount) {
        String infoText = "";

        double remainAmount = SaleModelInstance.getInstance().getSaleModel().getOrder().getRemainAmount() - amount;

        if(CommonUtils.getLanguage().equals(LANGUAGE_TR)){
            infoText = CommonUtils.getDoubleStrValueForView(SaleModelInstance.getInstance().getSaleModel().getOrder().getDiscountedAmount(), TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol())
                    .concat(" toplam tutardan, ödeme sonrası kalacak tutar ")
                    .concat(CommonUtils.getDoubleStrValueForView(remainAmount, TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol()))
                    .concat(".");
        }else if (CommonUtils.getLanguage().equals(LANGUAGE_EN)){
            infoText = CommonUtils.getDoubleStrValueForView(remainAmount, TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol())
                    .concat(" of ")
                    .concat(CommonUtils.getDoubleStrValueForView(SaleModelInstance.getInstance().getSaleModel().getOrder().getDiscountedAmount(), TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol()))
                    .concat(" will remain after this transaction.");
        }
        splitInfoTv.setText(infoText);
    }

    private void initCustomSplitFragment(){
        customSplitFragment = new CustomSplitFragment(mTransaction.getTransactionAmount(), new CustomSplitFragment.ReturnSplitCountCallback() {
            @Override
            public void OnReturnSplitCount(int splitCount) {
                removeNotPayedSplits();

                if(customSplitFragment != null)
                    Objects.requireNonNull(customSplitFragment.getActivity()).onBackPressed();

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
        double totalSplitAmount = 0d;
        double splitAmount = CommonUtils.round(SaleModelInstance.getInstance().getSaleModel().getOrder().getRemainAmount() / (double) splitCount, 2);

        for(int count = 0; count < splitCount - 1; count ++){
            totalSplitAmount = CommonUtils.round(totalSplitAmount + splitAmount, 2);
            OrderManager.addTransactionToOrder(splitAmount,
                    SaleModelInstance.getInstance().getSaleModel().getTransactions(),
                    SaleModelInstance.getInstance().getSaleModel().getOrder().getId());
        }

        double lastAmount = CommonUtils.round(SaleModelInstance.getInstance().getSaleModel().getOrder().getRemainAmount() - totalSplitAmount, 2);

        OrderManager.addTransactionToOrder(lastAmount,
                SaleModelInstance.getInstance().getSaleModel().getTransactions(),
                SaleModelInstance.getInstance().getSaleModel().getOrder().getId());


        LogUtil.logTransactions(SaleModelInstance.getInstance().getSaleModel().getTransactions());

        completeCallback.onComplete(null);
        Objects.requireNonNull(getActivity()).onBackPressed();
    }

    @Override
    public void OnReturnEtValue(String text) {
        if(text == null || text.isEmpty()){
            CommonUtils.setSaveBtnEnability(false, continueBtn, mContext);
            setSplitInfoTv(0d);
        } else{
            CommonUtils.setSaveBtnEnability(true, continueBtn, mContext);
            double amount = DataUtils.getDoubleValueFromFormattedString(text);
            setSplitInfoTv(amount);
        }

    }
}