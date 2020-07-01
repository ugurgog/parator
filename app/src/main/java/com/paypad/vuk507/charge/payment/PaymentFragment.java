package com.paypad.vuk507.charge.payment;

import android.app.ProgressDialog;
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
import com.paypad.vuk507.charge.payment.interfaces.PaymentStatusCallback;
import com.paypad.vuk507.db.SaleDBHelper;
import com.paypad.vuk507.db.SaleItemDBHelper;
import com.paypad.vuk507.db.TransactionDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.PaymentTypeEnum;
import com.paypad.vuk507.enums.ProcessDirectionEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.interfaces.CompleteCallback;
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
        //progressDialog = new ProgressDialog(getActivity());
        //progressDialog.setMessage("Please wait until payment completed...");
        //progressDialog.show();

        //thread.start();

        completePayment();
    }

    private void completePayment() {

        if(SaleDBHelper.getSaleById(SaleModelInstance.getInstance().getSaleModel().getSale().getSaleUuid()) != null){
            saveTransaction();
        }else {
            SaleModelInstance.getInstance().getSaleModel().getSale().setCreateDate(new Date());

            LogUtil.logSale(SaleModelInstance.getInstance().getSaleModel().getSale());

            BaseResponse saleBaseResponse = SaleDBHelper.createOrUpdateSale(SaleModelInstance.getInstance().getSaleModel());
            DataUtils.showBaseResponseMessage(getContext(), saleBaseResponse);

            if(saleBaseResponse.isSuccess()){

                BaseResponse baseResponse = SaleItemDBHelper.saveSaleItems(SaleModelInstance.getInstance().getSaleModel());
                DataUtils.showBaseResponseMessage(getContext(), baseResponse);

                if(baseResponse.isSuccess()){
                    saveTransaction();
                }
                //else
                //    progressDialog.dismiss();
            }
            //else
            //    progressDialog.dismiss();
        }
    }

    public void saveTransaction(){
        mTransaction.setPaymentCompleted(true);
        mTransaction.setCreateDate(new Date());
        mTransaction.setUserUuid(user.getUuid());
        mTransaction.setTotalAmount(mTransaction.getTransactionAmount() + mTransaction.getTipAmount());

        LogUtil.logTransaction(mTransaction);

        BaseResponse transactionSaveResponse = TransactionDBHelper.createOrUpdateTransaction(mTransaction);

        DataUtils.showBaseResponseMessage(getContext(),transactionSaveResponse);

        if(transactionSaveResponse.isSuccess()){

            LogUtil.logTransactions(SaleModelInstance.getInstance().getSaleModel().getTransactions());

            SaleModelInstance.getInstance().getSaleModel().getSale().setRemainAmount(
                    SaleModelInstance.getInstance().getSaleModel().getSale().getRemainAmount() - mTransaction.getTransactionAmount());

            if(SaleModelInstance.getInstance().getSaleModel().isExistNotCompletedTransaction()){
                initPaymentCompletedFragment(ProcessDirectionEnum.PAYMENT_PARTIALLY_COMPLETED);
                mFragmentNavigation.pushFragment(paymentCompletedFragment);
                //progressDialog.dismiss();
            }else {
                showCompletedScreen();
            }
        }else {
            //progressDialog.dismiss();
        }
    }

    Thread thread = new Thread() {
        @Override
        public void run() {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    private void initPaymentCompletedFragment(ProcessDirectionEnum processType){
        paymentCompletedFragment = new PaymentCompletedFragment(mTransaction, processType);
        paymentCompletedFragment.setPaymentStatusCallback(this);
    }

    private void showCompletedScreen(){

        //BaseResponse baseResponse = SaleDBHelper.createOrUpdateSale(SaleModelInstance.getInstance().getSaleModel());


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
