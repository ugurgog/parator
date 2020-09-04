package com.paypad.parator.charge.payment.cancelpayment;

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

import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.charge.dynamicStruct.adapters.DynamicPaymentSelectAdapter;
import com.paypad.parator.charge.dynamicStruct.interfaces.ReturnPaymentCallback;
import com.paypad.parator.charge.order.CancellationManager;
import com.paypad.parator.charge.payment.CashSelectFragment;
import com.paypad.parator.charge.payment.CreditCardSelectFragment;
import com.paypad.parator.charge.payment.interfaces.PaymentStatusCallback;
import com.paypad.parator.charge.payment.utils.CancelTransactionManager;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.enums.PaymentTypeEnum;
import com.paypad.parator.enums.ProcessDirectionEnum;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.interfaces.CompleteCallback;
import com.paypad.parator.interfaces.CustomDialogListener;
import com.paypad.parator.model.Transaction;
import com.paypad.parator.model.User;
import com.paypad.parator.model.pojo.BaseResponse;
import com.paypad.parator.model.pojo.CancelPaymentModelInstance;
import com.paypad.parator.model.pojo.SaleModel;
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
import io.realm.RealmList;

import static com.paypad.parator.constants.CustomConstants.LANGUAGE_EN;
import static com.paypad.parator.constants.CustomConstants.LANGUAGE_TR;
import static com.paypad.parator.constants.CustomConstants.TYPE_CANCEL_PAYMENT;
import static com.paypad.parator.constants.CustomConstants.TYPE_PRICE;
import static com.paypad.parator.constants.CustomConstants.WALK_THROUGH_END;

public class ChargePaymentForCancelFragment extends BaseFragment implements PaymentStatusCallback {

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
    private CreditCardSelectFragment creditCardSelectFragment;
    private double chargeAmount;
    private SaleModel saleModel;
    private Context mContext;

    public ChargePaymentForCancelFragment(SaleModel saleModel, double chargeAmount) {
        this.saleModel = saleModel;
        this.chargeAmount = chargeAmount;
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
            user = UserDBHelper.getUserFromCache(getContext());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        CommonUtils.showNavigationBar((Activity) mContext);
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
                if(CancellationManager.isExistPaymentCompletedTransaction(
                        CancelPaymentModelInstance.getInstance().getCancelPaymentModel().getTransactions()))
                    handleCancelTransaction();
                else{
                    CancelPaymentModelInstance.getInstance().getCancelPaymentModel().setTransactions(new ArrayList<>());
                    mFragmentNavigation.popFragments(1);
                }
            }
        });

        splitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mFragmentNavigation.pushFragment(new CancellationSplitAmountFragment(mTransaction, chargeAmount, new CompleteCallback() {
                    @Override
                    public void onComplete(BaseResponse baseResponse) {
                        for(Transaction transaction :  CancelPaymentModelInstance.getInstance().getCancelPaymentModel().getTransactions()){
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
        CancelPaymentModelInstance.getInstance().getCancelPaymentModel().setOrder(saleModel.getOrder());
        CancelPaymentModelInstance.getInstance().getCancelPaymentModel().setRemainAmount(chargeAmount);
        createInitialTransaction();
        setChargeAmount();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        paymentsRv.setLayoutManager(linearLayoutManager);
        setPaymentAdapter();
    }

    private void createInitialTransaction() {
        mTransaction = CancellationManager.addTransactionToOrder(
                CancelPaymentModelInstance.getInstance().getCancelPaymentModel().getRemainAmount(),
                CancelPaymentModelInstance.getInstance().getCancelPaymentModel().getTransactions(),
                CancelPaymentModelInstance.getInstance().getCancelPaymentModel().getOrder().getId());
    }

    private void setPaymentAdapter(){
        PaymentTypeEnum[] paymentTypeEnums = PaymentTypeEnum.values();
        List<PaymentTypeEnum> paymentTypes = new ArrayList<>(Arrays.asList(paymentTypeEnums));

        dynamicPaymentSelectAdapter = new DynamicPaymentSelectAdapter(mContext, ProcessDirectionEnum.DIRECTION_PAYMENT_SELECT, paymentTypes, new ReturnPaymentCallback() {
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
            infoText = CommonUtils.getDoubleStrValueForView(chargeAmount, TYPE_PRICE)
                    .concat(" ").concat(CommonUtils.getCurrency().getSymbol())
                    .concat(" Toplam, Ödeme ")
                    .concat(String.valueOf(mTransaction.getSeqNumber()))
                    .concat("/")
                    .concat(String.valueOf(CancelPaymentModelInstance.getInstance().getCancelPaymentModel().getTransactions().size()));
        }else if (CommonUtils.getLanguage().equals(LANGUAGE_EN)){
            infoText = "Out of "
                    .concat(CommonUtils.getDoubleStrValueForView(chargeAmount, TYPE_PRICE))
                    .concat(" ").concat(CommonUtils.getCurrency().getSymbol())
                    .concat(" Total, Payment ")
                    .concat(String.valueOf(mTransaction.getSeqNumber()))
                    .concat(" of ")
                    .concat(String.valueOf(CancelPaymentModelInstance.getInstance().getCancelPaymentModel().getTransactions().size()));
        }
        splitInfoTv.setText(infoText);
    }

    private void initCashSelectFragment(){
        cashSelectFragment = new CashSelectFragment(mTransaction, TYPE_CANCEL_PAYMENT, WALK_THROUGH_END);
        cashSelectFragment.setPaymentStatusCallback(this);
    }

    private void initCreditCardSelectFragment(){
        creditCardSelectFragment = new CreditCardSelectFragment(mTransaction, TYPE_CANCEL_PAYMENT);
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

        new CustomDialogBox.Builder((Activity) mContext)
                .setTitle(title)
                .setMessage(mContext.getResources().getString(R.string.cancel_transaction_description))
                .setNegativeBtnVisibility(View.VISIBLE)
                .setNegativeBtnText(mContext.getResources().getString(R.string.keep))
                .setNegativeBtnBackground(mContext.getResources().getColor(R.color.Silver, null))
                .setPositiveBtnVisibility(View.VISIBLE)
                .setPositiveBtnText(mContext.getResources().getString(R.string.cancel_transaction))
                .setPositiveBtnBackground(mContext.getResources().getColor(R.color.bg_screen1, null))
                .setDurationTime(0)
                .isCancellable(true)
                .setEdittextVisibility(View.GONE)
                .OnPositiveClicked(new CustomDialogListener() {
                    @Override
                    public void OnClick() {
                        //TODO - odemesi gerceklesmiss transactionlar iptal edilecek mi????

                        RealmList<Transaction> transactions = new RealmList<>();
                        for(Transaction transaction : CancelPaymentModelInstance.getInstance().getCancelPaymentModel().getTransactions())
                            transactions.add(transaction);

                        BaseResponse baseResponse = CancelTransactionManager.cancelTransactionsOfCancelProcess(transactions);
                        DataUtils.showBaseResponseMessage(mContext, baseResponse);

                        if(baseResponse.isSuccess())
                            mFragmentNavigation.popFragments(2);
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
            ((Activity)mContext).onBackPressed();
        }else {
            mTransaction = CancellationManager.getTransactionWillBePaid(CancelPaymentModelInstance.getInstance().getCancelPaymentModel().getTransactions());
            setChargeAmount();
            setSplitInfoTv();
        }

    }
}