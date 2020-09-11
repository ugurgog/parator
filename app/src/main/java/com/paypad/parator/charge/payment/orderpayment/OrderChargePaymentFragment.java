package com.paypad.parator.charge.payment.orderpayment;

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
import com.paypad.parator.charge.payment.utils.CancelTransactionManager;
import com.paypad.parator.db.PasscodeDBHelper;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.enums.PaymentTypeEnum;
import com.paypad.parator.enums.ProcessDirectionEnum;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.interfaces.CompleteCallback;
import com.paypad.parator.interfaces.CustomDialogListener;
import com.paypad.parator.menu.settings.checkoutoptions.PaymentTypesEditFragment;
import com.paypad.parator.menu.settings.passcode.PasscodeEditFragment;
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

public class OrderChargePaymentFragment extends BaseFragment implements PaymentStatusCallback, WalkthroughCallback {

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
    @BindView(R.id.prevPaymTypesTv)
    TextView prevPaymTypesTv;

    private User user;
    private DynamicPaymentSelectAdapter dynamicPaymentSelectAdapter;
    //private double splitAmount = 0d;
    private Transaction mTransaction;
    private CashSelectFragment cashSelectFragment;
    private PaymentStatusCallback paymentStatusCallback;
    private CreditCardSelectFragment creditCardSelectFragment;
    private SaleCalculateCallback saleCalculateCallback;
    private IOrderManager orderManager;
    private Context mContext;
    private int walkthrough;
    private WalkthroughCallback walkthroughCallback;
    private Tutorial tutorial;
    private SharedPreferences loginPreferences;

    public OrderChargePaymentFragment(int walkthrough) {
        this.walkthrough = walkthrough;
    }

    public void setPaymentStatusCallback(PaymentStatusCallback paymentStatusCallback) {
        this.paymentStatusCallback = paymentStatusCallback;
    }

    public void setSaleCalculateCallback(SaleCalculateCallback saleCalculateCallback) {
        this.saleCalculateCallback = saleCalculateCallback;
    }

    public void setWalkthroughCallback(WalkthroughCallback walkthroughCallback) {
        this.walkthroughCallback = walkthroughCallback;
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

    @Override
    public void onResume() {
        super.onResume();
        setPaymentAdapter();
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
                    Passcode passcode = PasscodeDBHelper.getPasscodeByUserId(user.getId());

                    if(passcode != null && passcode.isEnabled() && passcode.isBackOutOfSaleEnabled() &&
                            passcode.getPasscodeVal() != null && !passcode.getPasscodeVal().isEmpty()){
                        Intent intent = new Intent(mContext, PasscodeTypeActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("toolbarVisible", true);
                        bundle.putString("passcodeVal", passcode.getPasscodeVal());
                        intent.putExtras(bundle);
                        startActivityForResult(intent, 1);
                    }else
                        returnWithoutCancellation();
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

        prevPaymTypesTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFragmentNavigation.pushFragment(new PaymentTypesEditFragment());
            }
        });
    }

    private void returnWithoutCancellation(){
        SaleModelInstance.getInstance().getSaleModel().setTransactions(new ArrayList<>());
        Objects.requireNonNull(getActivity()).onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            boolean passcodeTypeSucceed = data.getBooleanExtra("isSucceed", true);

            if(passcodeTypeSucceed)
                returnWithoutCancellation();
        }else if (resultCode == Activity.RESULT_OK && requestCode == 2) {
            boolean passcodeTypeSucceed = data.getBooleanExtra("isSucceed", true);

            if(passcodeTypeSucceed)
                returnWithCancellation();
        }
    }

    private void initVariables() {
        orderManager = new OrderManager();
        createInitialTransaction();
        setChargeAmount();
        loginPreferences = mContext.getSharedPreferences("disabledPaymentTypes", MODE_PRIVATE);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        paymentsRv.setLayoutManager(linearLayoutManager);
        initTutorial();
    }

    private void initTutorial() {
        tutorial = mView.findViewById(R.id.tutorial);
        tutorial.setWalkthroughCallback(this);

        if(walkthrough == WALK_THROUGH_CONTINUE) {
            tutorial.setLayoutVisibility(View.VISIBLE);
            tutorial.setTutorialMessage(mContext.getResources().getString(R.string.tap_the_button_cash));
        }
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
        List<PaymentTypeEnum> paymentTypes = new ArrayList<>();

        for(PaymentTypeEnum paymentType : paymentTypeEnums){
            if(loginPreferences.getBoolean(String.valueOf(paymentType.getId()), false))
                paymentTypes.add(paymentType);
        }

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
        cashSelectFragment = new CashSelectFragment(mTransaction, TYPE_ORDER_PAYMENT, walkthrough);
        cashSelectFragment.setPaymentStatusCallback(this);
        cashSelectFragment.setWalkthroughCallback(this);
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
                .setPositiveBtnText(getContext().getResources().getString(R.string.cancel))
                .setPositiveBtnBackground(getContext().getResources().getColor(R.color.bg_screen1, null))
                .setDurationTime(0)
                .isCancellable(true)
                .setEdittextVisibility(View.GONE)
                .OnPositiveClicked(new CustomDialogListener() {
                    @Override
                    public void OnClick() {
                        checkPasscodeForCancellation();
                    }
                })
                .OnNegativeClicked(new CustomDialogListener() {
                    @Override
                    public void OnClick() {

                    }
                }).build();
    }

    private void checkPasscodeForCancellation(){
        Passcode passcode = PasscodeDBHelper.getPasscodeByUserId(user.getId());

        if(passcode != null && passcode.isEnabled() && passcode.isBackOutOfSaleEnabled() &&
                passcode.getPasscodeVal() != null && !passcode.getPasscodeVal().isEmpty()){
            Intent intent = new Intent(mContext, PasscodeTypeActivity.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean("toolbarVisible", true);
            bundle.putString("passcodeVal", passcode.getPasscodeVal());
            intent.putExtras(bundle);
            startActivityForResult(intent, 2);
        }else
            returnWithCancellation();
    }

    private void returnWithCancellation(){
        //TODO - odemesi gerceklesmiss transactionlar iptal edilecek mi????
        BaseResponse baseResponse = CancelTransactionManager.cancelTransactionsOfOrder();
        DataUtils.showBaseResponseMessage(mContext, baseResponse);

        if(baseResponse.isSuccess()){
            saleCalculateCallback.OnTransactionCancelled();
            ((Activity) mContext).onBackPressed();
        }
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

    @Override
    public void OnWalkthroughResult(int result) {
        walkthrough = result;
        walkthroughCallback.OnWalkthroughResult(result);
        if(walkthrough == WALK_THROUGH_END)
            tutorial.setLayoutVisibility(View.GONE);
    }
}