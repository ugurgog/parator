package com.paypad.parator.charge.payment.cancelpayment;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.adapter.LocationTrackerAdapter;
import com.paypad.parator.charge.order.CancellationManager;
import com.paypad.parator.charge.payment.interfaces.PaymentStatusCallback;
import com.paypad.parator.db.AutoIncrementDBHelper;
import com.paypad.parator.db.TransactionDBHelper;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.enums.ProcessDirectionEnum;
import com.paypad.parator.enums.TransactionTypeEnum;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.interfaces.LocationCallback;
import com.paypad.parator.model.AutoIncrement;
import com.paypad.parator.model.Transaction;
import com.paypad.parator.model.User;
import com.paypad.parator.model.pojo.BaseResponse;
import com.paypad.parator.model.pojo.CancelPaymentModelInstance;
import com.paypad.parator.utils.CommonUtils;
import com.paypad.parator.utils.DataUtils;
import com.paypad.parator.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import pl.droidsonroids.gif.GifImageView;

public class CancellationPaymentFragment extends BaseFragment implements PaymentStatusCallback {

    private View mView;

    @BindView(R.id.mainll)
    LinearLayout mainll;

    private User user;
    private Transaction mTransaction;
    //private ProgressDialog progressDialog;
    private CancellationPaymentCompletedFragment cancellationPaymentCompletedFragment;
    private PaymentStatusCallback paymentStatusCallback;
    private AutoIncrement autoIncrement;
    private Realm realm;
    private LocationTrackerAdapter locationTrackObj;

    public CancellationPaymentFragment(Transaction transaction) {
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
        initLocationTracker();
        autoIncrement = AutoIncrementDBHelper.getAutoIncrementByUserId(user.getId());
        completePayment();
    }

    private void initLocationTracker() {
        locationTrackObj = new LocationTrackerAdapter(getContext(), new LocationCallback() {
            @Override
            public void onLocationChanged(Location location) {

            }
        });
    }

    private void completePayment() {
        saveTransaction();
    }

    public void saveTransaction(){
        mTransaction.setPaymentCompleted(true);
        mTransaction.setCreateDate(new Date());
        mTransaction.setUserId(user.getId());
        mTransaction.setTotalAmount(mTransaction.getTransactionAmount() + mTransaction.getTipAmount());
        mTransaction.setTransactionType(TransactionTypeEnum.SALE.getId());
        mTransaction.setzNum(autoIncrement.getzNum());
        mTransaction.setfNum(autoIncrement.getfNum());
        mTransaction.setEODProcessed(false);
        mTransaction.setTransferred(false);

        LogUtil.logTransaction(mTransaction);

        BaseResponse transactionSaveResponse = TransactionDBHelper.createOrUpdateTransaction(mTransaction);

        DataUtils.showBaseResponseMessage(getContext(),transactionSaveResponse);

        if(transactionSaveResponse.isSuccess()){

            BaseResponse baseResponse = AutoIncrementDBHelper.updateFnumByNextValue(autoIncrement);
            DataUtils.showBaseResponseMessage(getContext(), baseResponse);
            autoIncrement = AutoIncrementDBHelper.getAutoIncrementByUserId(user.getId());

            CancelPaymentModelInstance.getInstance().getCancelPaymentModel().setRemainAmount(
                    CancelPaymentModelInstance.getInstance().getCancelPaymentModel().getRemainAmount()- mTransaction.getTransactionAmount());

            if(CancellationManager.isExistNotCompletedTransaction(CancelPaymentModelInstance.getInstance().getCancelPaymentModel().getTransactions())){
                initPaymentCompletedFragment(ProcessDirectionEnum.PAYMENT_PARTIALLY_COMPLETED);
                mFragmentNavigation.pushFragment(cancellationPaymentCompletedFragment);
            }else
                showCompletedScreen();
        }
    }

    private void initPaymentCompletedFragment(ProcessDirectionEnum processType){
        cancellationPaymentCompletedFragment = new CancellationPaymentCompletedFragment(mTransaction, processType);
        cancellationPaymentCompletedFragment.setPaymentStatusCallback(this);
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
                mFragmentNavigation.pushFragment(cancellationPaymentCompletedFragment);

            }
        }, 4000);
    }

    @Override
    public void OnPaymentReturn(int status) {
        if(cancellationPaymentCompletedFragment != null){
            Log.i("Info", "::OnPaymentReturn paymentCompletedFragment closed");
            Objects.requireNonNull(cancellationPaymentCompletedFragment.getActivity()).onBackPressed();
        }

        Log.i("Info", "::OnPaymentReturn OrderPaymentFragment paymentStatusCallback triggered");
        paymentStatusCallback.OnPaymentReturn(status);
    }

    @Override
    public void onStop() {
        super.onStop();
        locationTrackObj.removeUpdates();
    }

    @Override
    public void onPause() {
        super.onPause();
        locationTrackObj.removeUpdates();
    }
}
