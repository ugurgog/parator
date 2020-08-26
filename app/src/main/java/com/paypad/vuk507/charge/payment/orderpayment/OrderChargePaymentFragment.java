package com.paypad.vuk507.charge.payment.orderpayment;

import android.app.Activity;
import android.content.Context;
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

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.charge.dynamicStruct.adapters.DynamicPaymentSelectAdapter;
import com.paypad.vuk507.charge.dynamicStruct.interfaces.ReturnPaymentCallback;
import com.paypad.vuk507.charge.interfaces.SaleCalculateCallback;
import com.paypad.vuk507.charge.order.IOrderManager;
import com.paypad.vuk507.charge.order.OrderManager;
import com.paypad.vuk507.charge.payment.CashSelectFragment;
import com.paypad.vuk507.charge.payment.CreditCardSelectFragment;
import com.paypad.vuk507.charge.payment.interfaces.PaymentStatusCallback;
import com.paypad.vuk507.charge.payment.utils.CancelTransactionManager;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.PaymentTypeEnum;
import com.paypad.vuk507.enums.ProcessDirectionEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.interfaces.CustomDialogListener;
import com.paypad.vuk507.model.Transaction;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.model.pojo.SaleModelInstance;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.CustomDialogBox;
import com.paypad.vuk507.utils.DataUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.paypad.vuk507.constants.CustomConstants.LANGUAGE_EN;
import static com.paypad.vuk507.constants.CustomConstants.LANGUAGE_TR;
import static com.paypad.vuk507.constants.CustomConstants.TYPE_ORDER_PAYMENT;
import static com.paypad.vuk507.constants.CustomConstants.TYPE_PRICE;

public class OrderChargePaymentFragment extends BaseFragment implements PaymentStatusCallback {

    private View mView;

    @BindView(R.id.chargeAmountTv)
    TextView chargeAmountTv;

    @BindView(R.id.paymentsRv)
    RecyclerView paymentsRv;
    @BindView(R.id.cancelImgv)
    ClickableImageView cancelImgv;
    @BindView(R.id.splitBtn)
    Button splitBtn;
    @BindView(R.id.splitInfoTv)
    TextView splitInfoTv;

    private User user;
    private DynamicPaymentSelectAdapter dynamicPaymentSelectAdapter;
    //private double splitAmount = 0d;
    private Transaction mTransaction;
    private CashSelectFragment cashSelectFragment;
    private PaymentStatusCallback paymentStatusCallback;
    private CreditCardSelectFragment creditCardSelectFragment;
    private SaleCalculateCallback saleCalculateCallback;
    private IOrderManager orderManager;

    public OrderChargePaymentFragment() {

    }

    public void setPaymentStatusCallback(PaymentStatusCallback paymentStatusCallback) {
        this.paymentStatusCallback = paymentStatusCallback;
    }

    public void setSaleCalculateCallback(SaleCalculateCallback saleCalculateCallback) {
        this.saleCalculateCallback = saleCalculateCallback;
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

        Objects.requireNonNull(getActivity()).getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE );

        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_select_charge_payment, container, false);
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
                if(OrderManager.isExistPaymentCompletedTransaction(
                        SaleModelInstance.getInstance().getSaleModel().getTransactions()))
                    handleCancelTransaction();
                else{
                    SaleModelInstance.getInstance().getSaleModel().setTransactions(new ArrayList<>());
                    Objects.requireNonNull(getActivity()).onBackPressed();
                }
            }
        });

        splitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mFragmentNavigation.pushFragment(new OrderSplitAmountFragment(mTransaction, new CompleteCallback() {
                    @Override
                    public void onComplete(BaseResponse baseResponse) {

                        for(Transaction transaction : SaleModelInstance.getInstance().getSaleModel().getTransactions()){

                            if(!transaction.isPaymentCompleted()){
                                mTransaction = transaction;
                                setChargeAmount();
                                setSplitInfoTv();
                                break;
                            }
                        }
                    }
                }));
            }
        });
    }

    private void initVariables() {
        orderManager = new OrderManager();
        createInitialTransaction();
        //splitAmount = SaleModelInstance.getInstance().getSaleModel().getOrder().getRemainAmount();
        setChargeAmount();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        paymentsRv.setLayoutManager(linearLayoutManager);
        setPaymentAdapter();
    }

    private void createInitialTransaction() {
        mTransaction = OrderManager.addTransactionToOrder(
                SaleModelInstance.getInstance().getSaleModel().getOrder().getRemainAmount(),
                SaleModelInstance.getInstance().getSaleModel().getTransactions(),
                SaleModelInstance.getInstance().getSaleModel().getOrder().getId()
        );
    }

    private void setPaymentAdapter(){
        PaymentTypeEnum[] paymentTypeEnums = PaymentTypeEnum.values();
        List<PaymentTypeEnum> paymentTypes = new ArrayList<>(Arrays.asList(paymentTypeEnums));

        dynamicPaymentSelectAdapter = new DynamicPaymentSelectAdapter(getContext(), ProcessDirectionEnum.DIRECTION_PAYMENT_SELECT, paymentTypes, new ReturnPaymentCallback() {
            @Override
            public void onReturn(PaymentTypeEnum paymentType) {
                if(paymentType == PaymentTypeEnum.CASH){
                    initCashSelectFragment();
                    mFragmentNavigation.pushFragment(cashSelectFragment);
                }else if(paymentType == PaymentTypeEnum.CREDIT_CARD){
                    initCreditCardSelectFragment();
                    mFragmentNavigation.pushFragment(creditCardSelectFragment);
                }
            }
        });
        paymentsRv.setAdapter(dynamicPaymentSelectAdapter);
    }

    private void setChargeAmount(){
        String amountStr = CommonUtils.getDoubleStrValueForView(mTransaction.getTransactionAmount(), TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol());
        chargeAmountTv.setText(amountStr);
    }

    private void setSplitInfoTv(){
        splitInfoTv.setVisibility(View.VISIBLE);

        String infoText = "";
        if(CommonUtils.getLanguage().equals(LANGUAGE_TR)){
            infoText = CommonUtils.getDoubleStrValueForView(SaleModelInstance.getInstance().getSaleModel().getOrder().getDiscountedAmount(), TYPE_PRICE)
                    .concat(" ").concat(CommonUtils.getCurrency().getSymbol())
                    .concat(" Toplam, Ödeme ")
                    .concat(String.valueOf(mTransaction.getSeqNumber()))
                    .concat("/")
                    .concat(String.valueOf(SaleModelInstance.getInstance().getSaleModel().getTransactions().size()));
        }else if (CommonUtils.getLanguage().equals(LANGUAGE_EN)){
            infoText = "Out of "
                    .concat(CommonUtils.getDoubleStrValueForView(SaleModelInstance.getInstance().getSaleModel().getOrder().getDiscountedAmount(), TYPE_PRICE))
                    .concat(" ").concat(CommonUtils.getCurrency().getSymbol())
                    .concat(" Total, Payment ")
                    .concat(String.valueOf(mTransaction.getSeqNumber()))
                    .concat(" of ")
                    .concat(String.valueOf(SaleModelInstance.getInstance().getSaleModel().getTransactions().size()));
        }
        splitInfoTv.setText(infoText);
    }

    private void initCashSelectFragment(){
        cashSelectFragment = new CashSelectFragment(mTransaction, TYPE_ORDER_PAYMENT);
        cashSelectFragment.setPaymentStatusCallback(this);
    }

    private void initCreditCardSelectFragment(){
        creditCardSelectFragment = new CreditCardSelectFragment(mTransaction, TYPE_ORDER_PAYMENT);
        creditCardSelectFragment.setPaymentStatusCallback(this);
    }

    private void handleCancelTransaction() {
        String title = "";

        if(CommonUtils.getLanguage().equals(LANGUAGE_TR)){
            title = CommonUtils.getDoubleStrValueForView(mTransaction.getTransactionAmount(), TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol())
                    .concat(" İşlemi İptal Et");
        }else if (CommonUtils.getLanguage().equals(LANGUAGE_EN)){
            title = getResources().getString(R.string.cancel)
                    .concat(" ")
                    .concat(CommonUtils.getDoubleStrValueForView(mTransaction.getTransactionAmount(), TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol()))
                    .concat(" Transaction");
        }

        new CustomDialogBox.Builder((Activity) getContext())
                .setTitle(title)
                .setMessage(getContext().getResources().getString(R.string.cancel_transaction_description))
                .setNegativeBtnVisibility(View.VISIBLE)
                .setNegativeBtnText(getContext().getResources().getString(R.string.keep))
                .setNegativeBtnBackground(getContext().getResources().getColor(R.color.Silver, null))
                .setPositiveBtnVisibility(View.VISIBLE)
                .setPositiveBtnText(getContext().getResources().getString(R.string.cancel_transaction))
                .setPositiveBtnBackground(getContext().getResources().getColor(R.color.bg_screen1, null))
                .setDurationTime(0)
                .isCancellable(true)
                .setEdittextVisibility(View.GONE)
                .OnPositiveClicked(new CustomDialogListener() {
                    @Override
                    public void OnClick() {
                        //TODO - odemesi gerceklesmiss transactionlar iptal edilecek mi????

                        BaseResponse baseResponse = CancelTransactionManager.cancelTransactionsOfOrder();

                        DataUtils.showBaseResponseMessage(getContext(), baseResponse);

                        if(baseResponse.isSuccess()){
                            saleCalculateCallback.OnTransactionCancelled();
                            Objects.requireNonNull(getActivity()).onBackPressed();
                        }

                    }
                })
                .OnNegativeClicked(new CustomDialogListener() {
                    @Override
                    public void OnClick() {

                    }
                }).build();
    }

    @Override
    public void OnPaymentReturn(int status) {

        try{
            if(cashSelectFragment != null)
                Objects.requireNonNull(cashSelectFragment.getActivity()).onBackPressed();

        }catch (Exception e){
            Log.i("Info", "Error:" + e);
        }

        try{
            if(creditCardSelectFragment != null)
                Objects.requireNonNull(creditCardSelectFragment.getActivity()).onBackPressed();

        }catch (Exception e){
            Log.i("Info", "Error:" + e);
        }

        if(status == STATUS_NEW_SALE){
            paymentStatusCallback.OnPaymentReturn(status);
            Objects.requireNonNull(getActivity()).onBackPressed();
        }else {
            mTransaction = OrderManager.getTransactionWillBePaid(
                    SaleModelInstance.getInstance().getSaleModel().getTransactions());
            setChargeAmount();
            setSplitInfoTv();
        }
    }
}