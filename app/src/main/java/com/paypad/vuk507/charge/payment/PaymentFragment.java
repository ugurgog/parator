package com.paypad.vuk507.charge.payment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.charge.order.IOrderManager;
import com.paypad.vuk507.charge.order.OrderManager;
import com.paypad.vuk507.charge.payment.interfaces.PaymentStatusCallback;
import com.paypad.vuk507.db.AutoIncrementDBHelper;
import com.paypad.vuk507.db.SaleDBHelper;
import com.paypad.vuk507.db.SaleItemDBHelper;
import com.paypad.vuk507.db.TaxDBHelper;
import com.paypad.vuk507.db.TransactionDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.ProcessDirectionEnum;
import com.paypad.vuk507.enums.TransactionTypeEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.model.AutoIncrement;
import com.paypad.vuk507.model.TaxModel;
import com.paypad.vuk507.model.Transaction;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.model.pojo.SaleModelInstance;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.DataUtils;
import com.paypad.vuk507.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import pl.droidsonroids.gif.GifImageView;

public class PaymentFragment extends BaseFragment implements PaymentStatusCallback {

    private View mView;

    @BindView(R.id.mainll)
    LinearLayout mainll;

    private User user;
    private Transaction mTransaction;
    //private ProgressDialog progressDialog;
    private PaymentCompletedFragment paymentCompletedFragment;
    private PaymentStatusCallback paymentStatusCallback;
    private IOrderManager orderManager;
    private AutoIncrement autoIncrement;
    private Realm realm;

    public PaymentFragment(Transaction transaction) {
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
    public void accountHolderUserReceived(UserBus userBus) {
        user = userBus.getUser();
        if (user == null)
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
            mView = inflater.inflate(R.layout.fragment_payment, container, false);
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

    }

    private void initVariables() {
        realm = Realm.getDefaultInstance();
        autoIncrement = AutoIncrementDBHelper.getAutoIncrementByUserId(user.getUuid());
        orderManager = new OrderManager();
        completePayment();
    }

    private void completePayment() {
        saveTransaction();
    }

    public void saveTransaction(){
        mTransaction.setPaymentCompleted(true);
        mTransaction.setCreateDate(new Date());
        mTransaction.setUserUuid(user.getUuid());
        mTransaction.setTotalAmount(mTransaction.getTransactionAmount() + mTransaction.getTipAmount());
        mTransaction.setTransactionType(TransactionTypeEnum.SALE.getId());
        mTransaction.setzNum(autoIncrement.getzNum());
        mTransaction.setfNum(autoIncrement.getfNum());
        mTransaction.setEODProcessed(false);
        mTransaction.setTransferred(false);

        LogUtil.logTransaction("saveTransaction", mTransaction);

        BaseResponse transactionSaveResponse = TransactionDBHelper.createOrUpdateTransaction(mTransaction);

        DataUtils.showBaseResponseMessage(getContext(),transactionSaveResponse);

        if(transactionSaveResponse.isSuccess()){

            BaseResponse baseResponse = AutoIncrementDBHelper.updateFnumByNextValue(autoIncrement);
            DataUtils.showBaseResponseMessage(getContext(), baseResponse);
            autoIncrement = AutoIncrementDBHelper.getAutoIncrementByUserId(user.getUuid());

            LogUtil.logTransactions(SaleModelInstance.getInstance().getSaleModel().getTransactions());

            orderManager.setRemainAmount(orderManager.getRemainAmount() - mTransaction.getTransactionAmount());

            if(orderManager.isExistNotCompletedTransaction()){
                initPaymentCompletedFragment(ProcessDirectionEnum.PAYMENT_PARTIALLY_COMPLETED);
                mFragmentNavigation.pushFragment(paymentCompletedFragment);
            }else {
                saveSaleAndItems();
            }
        }
    }

    private void saveSaleAndItems(){
        SaleModelInstance.getInstance().getSaleModel().getSale().setCreateDate(new Date());
        SaleModelInstance.getInstance().getSaleModel().getSale().setPaymentCompleted(true);
        //SaleModelInstance.getInstance().getSaleModel().getSale().setEndOfDayProcessed(false);
        SaleModelInstance.getInstance().getSaleModel().getSale().setOrderNum(DataUtils.getOrderRetrefNum(user.getUuid()));
        SaleModelInstance.getInstance().getSaleModel().getSale().setzNum(autoIncrement.getzNum());
        SaleModelInstance.getInstance().getSaleModel().getSale().setTransferred(false);

        LogUtil.logSale(SaleModelInstance.getInstance().getSaleModel().getSale());

        BaseResponse saleBaseResponse = SaleDBHelper.createOrUpdateSale(SaleModelInstance.getInstance().getSaleModel().getSale());
        DataUtils.showBaseResponseMessage(getContext(), saleBaseResponse);

        if(saleBaseResponse.isSuccess()){

            LogUtil.logAutoIncrement(autoIncrement);

            BaseResponse baseResponseAI = AutoIncrementDBHelper.updateOrderNumByNextValue(autoIncrement);
            DataUtils.showBaseResponseMessage(getContext(), baseResponseAI);
            autoIncrement = AutoIncrementDBHelper.getAutoIncrementByUserId(user.getUuid());

            BaseResponse baseResponse = SaleItemDBHelper.saveSaleItems(SaleModelInstance.getInstance().getSaleModel());
            DataUtils.showBaseResponseMessage(getContext(), baseResponse);

            if(baseResponse.isSuccess())
                showCompletedScreen();
        }
    }

    private void initPaymentCompletedFragment(ProcessDirectionEnum processType){
        paymentCompletedFragment = new PaymentCompletedFragment(mTransaction, processType);
        paymentCompletedFragment.setPaymentStatusCallback(this);
    }

    private void showCompletedScreen(){

        View child = getLayoutInflater().inflate(R.layout.layout_payment_fully_completed, null);

        GifImageView gifImageView = child.findViewById(R.id.gifImageView);

        gifImageView.setLayoutParams(new LinearLayout.LayoutParams(
                CommonUtils.getScreenWidth(getContext()),
                CommonUtils.getScreenHeight(getContext()) + CommonUtils.getNavigationBarHeight(getContext())
        ));

        mainll.addView(child);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //progressDialog.dismiss();
                initPaymentCompletedFragment(ProcessDirectionEnum.PAYMENT_FULLY_COMPLETED);
                mFragmentNavigation.pushFragment(paymentCompletedFragment);

            }
        }, 4000);
    }

    @Override
    public void OnPaymentReturn(int status) {
        if(paymentCompletedFragment != null){
            Log.i("Info", "::OnPaymentReturn paymentCompletedFragment closed");
            Objects.requireNonNull(paymentCompletedFragment.getActivity()).onBackPressed();
        }

        Log.i("Info", "::OnPaymentReturn PaymentFragment paymentStatusCallback triggered");
        paymentStatusCallback.OnPaymentReturn(status);
    }
}
