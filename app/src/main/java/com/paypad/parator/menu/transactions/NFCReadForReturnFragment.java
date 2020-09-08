package com.paypad.parator.menu.transactions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.adapter.LocationTrackerAdapter;
import com.paypad.parator.db.AutoIncrementDBHelper;
import com.paypad.parator.db.RefundDBHelper;
import com.paypad.parator.db.TransactionDBHelper;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.enums.PaymentTypeEnum;
import com.paypad.parator.enums.TransactionTypeEnum;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.interfaces.LocationCallback;
import com.paypad.parator.model.AutoIncrement;
import com.paypad.parator.model.OrderRefundItem;
import com.paypad.parator.model.Refund;
import com.paypad.parator.model.Transaction;
import com.paypad.parator.model.User;
import com.paypad.parator.model.pojo.BaseResponse;
import com.paypad.parator.utils.ClickableImage.ClickableImageView;
import com.paypad.parator.utils.CommonUtils;
import com.paypad.parator.utils.DataUtils;
import com.paypad.parator.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import pl.droidsonroids.gif.GifImageView;

import static com.paypad.parator.constants.CustomConstants.TYPE_CANCEL;
import static com.paypad.parator.constants.CustomConstants.TYPE_REFUND;

public class NFCReadForReturnFragment extends BaseFragment {

    private View mView;

    @BindView(R.id.cancelImgv)
    ClickableImageView cancelImgv;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.saveBtn)
    Button saveBtn;
    @BindView(R.id.receiptInfoll)
    LinearLayout receiptInfoll;
    @BindView(R.id.cardInfoll)
    LinearLayout cardInfoll;
    @BindView(R.id.nfcInfoll)
    LinearLayout nfcInfoll;
    @BindView(R.id.zNumTv)
    TextView zNumTv;
    @BindView(R.id.fNumTv)
    TextView fNumTv;
    @BindView(R.id.mainrl)
    RelativeLayout mainrl;

    private User user;
    private Transaction transaction;
    private double totalAmount;
    private Realm realm;
    private int refundCancelStatus;
    private SendNewReceiptFragment sendNewReceiptFragment;
    private double returnAmount;
    private boolean isRefundByAmount;
    private List<OrderRefundItem> refundedItems;
    private String refundReason;
    private AutoIncrement autoIncrement;
    private Refund tempRefund;
    private LocationTrackerAdapter locationTrackObj;

    public NFCReadForReturnFragment(Transaction transaction, double returnAmount, int refundCancelStatus, boolean isRefundByAmount,
                                    List<OrderRefundItem> refundedItems, String refundReason) {
        this.returnAmount = returnAmount;
        this.transaction = transaction;
        this.refundCancelStatus = refundCancelStatus;
        this.isRefundByAmount = isRefundByAmount;
        this.refundedItems = refundedItems;
        this.refundReason = refundReason;
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
            mView = inflater.inflate(R.layout.fragment_nfc_read_for_return, container, false);
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

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processRefund();
            }
        });
    }

    private void initVariables() {
        realm = Realm.getDefaultInstance();
        autoIncrement = AutoIncrementDBHelper.getAutoIncrementByUserId(user.getId());
        saveBtn.setText(getResources().getString(R.string.continue_text));
        CommonUtils.setSaveBtnEnability(false, saveBtn, getContext());
        setToolbarTitle(returnAmount);
        setZNumFNum();
        initLocationTracker();

        if(transaction.getPaymentTypeId() == PaymentTypeEnum.CASH.getId()){

            nfcInfoll.setVisibility(View.GONE);
            receiptInfoll.setVisibility(View.VISIBLE);
            CommonUtils.setSaveBtnEnability(true, saveBtn, getContext());

        }else if(transaction.getPaymentTypeId() == PaymentTypeEnum.CREDIT_CARD.getId()){

            nfcInfoll.setVisibility(View.VISIBLE);
            cardInfoll.setVisibility(View.GONE);

            //Card gosterildi ve nfc basarili kabul edelim
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try{
                        nfcInfoll.setVisibility(View.VISIBLE);
                        receiptInfoll.setVisibility(View.VISIBLE);
                        cardInfoll.setVisibility(View.VISIBLE);
                        CommonUtils.setSaveBtnEnability(true, saveBtn, getActivity());
                    }catch (Exception e){

                    }
                }
            }, 5000);

        }
    }

    private void initLocationTracker() {
        locationTrackObj = new LocationTrackerAdapter(getContext(), new LocationCallback() {
            @Override
            public void onLocationChanged(Location location) {

            }
        });
    }

    @SuppressLint("DefaultLocale")
    private void setZNumFNum() {
        fNumTv.setText(String.format("%06d", transaction.getfNum()));
        zNumTv.setText(String.format("%06d", transaction.getzNum()));
    }

    private void setToolbarTitle(double amount){
        totalAmount = amount;
        toolbarTitleTv.setText(CommonUtils.getAmountTextWithCurrency(amount)
                .concat(" ")
                .concat(transaction.getPaymentTypeId() == PaymentTypeEnum.CASH.getId() ? getResources().getString(R.string.cash) :
                        getResources().getString(R.string.card))
                .concat(" ")
                .concat(refundCancelStatus == TYPE_REFUND ? getContext().getResources().getString(R.string.refund)
                        : getContext().getResources().getString(R.string.cancel)));
    }

    private void processRefund(){
        if(refundCancelStatus == TYPE_REFUND)
            refundTransaction();
        else if(refundCancelStatus == TYPE_CANCEL)
            cancelTransaction();
    }

    private void refundTransaction(){
        realm.beginTransaction();

        RealmList<OrderRefundItem> saleItems = new RealmList<>();

        Refund refund = new Refund();
        refund.setId(UUID.randomUUID().toString());
        refund.setTransactionId(transaction.getId());
        refund.setOrderId(transaction.getOrderId());
        refund.setRefundByAmount(isRefundByAmount);
        refund.setRefundAmount(returnAmount);

        if (locationTrackObj.canGetLocation()){
            Location location = locationTrackObj.getLocation();
            if(location != null){
                refund.setLatitude(location.getLatitude());
                refund.setLongitude(location.getLongitude());
            }
        }

        if(refundedItems != null && refundedItems.size() > 0)
            saleItems.addAll(refundedItems);

        refund.setRefundReason(refundReason);
        refund.setSuccessful(true);
        refund.setCreateDate(new Date());
        refund.setzNum(autoIncrement.getzNum());
        refund.setfNum(autoIncrement.getfNum());
        refund.setEODProcessed(false);

        tempRefund = realm.copyToRealm(refund);

        LogUtil.logRefund(tempRefund);

        realm.commitTransaction();

        BaseResponse baseResponse = RefundDBHelper.createOrUpdateRefund(tempRefund);
        DataUtils.showBaseResponseMessage(getContext(), baseResponse);

        if(baseResponse.isSuccess()){
            AutoIncrementDBHelper.updateFnumByNextValue(autoIncrement);
            refundCompletedProcess();
        }
    }

    private void cancelTransaction(){
        realm.beginTransaction();

        Transaction tempTransaction = realm.copyToRealm(transaction);

        tempTransaction.setTransactionType(TransactionTypeEnum.CANCEL.getId());
        tempTransaction.setCancellationDate(new Date());

        realm.commitTransaction();

        BaseResponse baseResponse = TransactionDBHelper.createOrUpdateTransaction(tempTransaction);
        DataUtils.showBaseResponseMessage(getContext(), baseResponse);

        LogUtil.logTransaction(tempTransaction);

        if(baseResponse.isSuccess()){
            refundCompletedProcess();
        }
    }

    private void refundCompletedProcess(){
        View child = getLayoutInflater().inflate(R.layout.layout_payment_fully_completed, null);

        GifImageView gifImageView = child.findViewById(R.id.gifImageView);

        gifImageView.setLayoutParams(new LinearLayout.LayoutParams(
                CommonUtils.getScreenWidth(getContext()),
                CommonUtils.getScreenHeight(getContext()) + CommonUtils.getNavigationBarHeight(getContext())
        ));

        mainrl.addView(child);

        initSendNewReceiptFragment();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mFragmentNavigation.pushFragment(sendNewReceiptFragment);

            }
        }, 4000);
    }

    private void initSendNewReceiptFragment(){
        sendNewReceiptFragment = new SendNewReceiptFragment(transaction, tempRefund, refundCancelStatus, totalAmount);
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