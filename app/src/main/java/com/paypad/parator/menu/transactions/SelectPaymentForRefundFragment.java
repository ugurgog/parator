package com.paypad.parator.menu.transactions;

import android.app.Activity;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.adapter.LocationTrackerAdapter;
import com.paypad.parator.db.AutoIncrementDBHelper;
import com.paypad.parator.db.OrderRefundItemDBHelper;
import com.paypad.parator.db.RefundDBHelper;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.enums.RefundReasonEnum;
import com.paypad.parator.enums.TransactionTypeEnum;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.interfaces.CustomDialogBoxTextListener;
import com.paypad.parator.interfaces.CustomDialogListener;
import com.paypad.parator.interfaces.LocationCallback;
import com.paypad.parator.interfaces.RefundReasonTypeCallback;
import com.paypad.parator.menu.transactions.adapters.PaymentListForRefundAdapter;
import com.paypad.parator.menu.transactions.adapters.RefundReasonsAdapter;
import com.paypad.parator.menu.transactions.interfaces.RefundableTrxModelCallback;
import com.paypad.parator.menu.transactions.model.RefundableTrxModel;
import com.paypad.parator.model.AutoIncrement;
import com.paypad.parator.model.OrderRefundItem;
import com.paypad.parator.model.Refund;
import com.paypad.parator.model.Transaction;
import com.paypad.parator.model.User;
import com.paypad.parator.model.pojo.BaseResponse;
import com.paypad.parator.model.pojo.SaleModel;
import com.paypad.parator.utils.ClickableImage.ClickableImageView;
import com.paypad.parator.utils.CommonUtils;
import com.paypad.parator.utils.CustomDialogBox;
import com.paypad.parator.utils.DataUtils;
import com.paypad.parator.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import pl.droidsonroids.gif.GifImageView;

import static com.paypad.parator.constants.CustomConstants.LANGUAGE_TR;
import static com.paypad.parator.constants.CustomConstants.TYPE_CANCEL;

public class SelectPaymentForRefundFragment extends BaseFragment
            implements RefundReasonTypeCallback, RefundableTrxModelCallback {

    private View mView;

    @BindView(R.id.cancelImgv)
    ClickableImageView cancelImgv;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.saveBtn)
    Button saveBtn;
    @BindView(R.id.itemRv)
    RecyclerView itemRv;
    @BindView(R.id.refundReasonRv)
    RecyclerView refundReasonRv;
    @BindView(R.id.remainingAmountTv)
    TextView remainingAmountTv;
    @BindView(R.id.refundReasonll)
    LinearLayout refundReasonll;
    @BindView(R.id.mainrl)
    RelativeLayout mainrl;

    private User user;
    private PaymentListForRefundAdapter paymentListForRefundAdapter;
    private RefundReasonsAdapter refundReasonsAdapter;
    private SaleModel saleModel;
    private int refundCancellationStatus;
    private String refundReasonText = null;
    private boolean isRefundByAmount;
    private double refundAmount;
    private double remainingAmount;
    private double returnAmount = 0d;
    private List<RefundableTrxModel> mRefundableTrxModels;
    private List<OrderRefundItem> orderRefundItems;
    private Realm realm;
    private LocationTrackerAdapter locationTrackObj;
    private AutoIncrement autoIncrement;

    public SelectPaymentForRefundFragment(int refundCancellationStatus, SaleModel saleModel, boolean isRefundByAmount,
                                          double refundAmount, List<OrderRefundItem> orderRefundItems) {
        this.saleModel = saleModel;
        this.refundCancellationStatus = refundCancellationStatus;
        this.isRefundByAmount = isRefundByAmount;
        this.refundAmount = refundAmount;
        this.orderRefundItems = orderRefundItems;
        remainingAmount = refundAmount;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        initVariables();
        initListeners();
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
            mView = inflater.inflate(R.layout.fragment_select_paym_for_refund, container, false);
            ButterKnife.bind(this, mView);
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    private void initLocationTracker() {
        locationTrackObj = new LocationTrackerAdapter(getContext(), new LocationCallback() {
            @Override
            public void onLocationChanged(Location location) {

            }
        });
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

                String refundGroupId = UUID.randomUUID().toString();

                boolean refundSuccess = true;
                for(RefundableTrxModel refundableTrxModel : mRefundableTrxModels){

                    if(refundableTrxModel.getRefundAmount() > 0d){
                        refundSuccess = refundTransaction(refundableTrxModel, refundGroupId);
                        if(!refundSuccess)
                            break;
                    }
                }

                if(refundSuccess)
                    refundSuccess = saveRefundItems(refundGroupId);

                if(refundSuccess)
                    refundCompletedProcess();
            }
        });
    }

    private void refundCompletedProcess(){
        View child = getLayoutInflater().inflate(R.layout.layout_payment_fully_completed, null);

        GifImageView gifImageView = child.findViewById(R.id.gifImageView);

        gifImageView.setLayoutParams(new LinearLayout.LayoutParams(
                CommonUtils.getScreenWidth(getContext()),
                CommonUtils.getScreenHeight(getContext()) + CommonUtils.getNavigationBarHeight(getContext())
        ));

        mainrl.addView(child);

        //initSendNewReceiptFragment();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //mFragmentNavigation.pushFragment(sendNewReceiptFragment);
                mFragmentNavigation.popFragments(3);

            }
        }, 4000);
    }

    private void initVariables() {
        realm = Realm.getDefaultInstance();
        initLocationTracker();
        saveBtn.setText(getResources().getString(R.string.continue_text));
        CommonUtils.setSaveBtnEnability(false, saveBtn, getContext());
        mRefundableTrxModels = new ArrayList<>();
        autoIncrement = AutoIncrementDBHelper.getAutoIncrementByUserId(user.getId());

        if(refundCancellationStatus == TYPE_CANCEL)
            refundReasonll.setVisibility(View.GONE);

        setToolbarTitle();
        setItemsRv();
        setRefundReasonsRv();
        remainingAmountTv.setText(CommonUtils.getAmountTextWithCurrency(refundAmount).concat(" ")
                .concat(getResources().getString(R.string.remaining)));
    }

    private void setItemsRv() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        itemRv.setLayoutManager(linearLayoutManager);
        updatePaymentsAdapter();
    }

    private void setRefundReasonsRv(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        refundReasonRv.setLayoutManager(linearLayoutManager);
        updateReasonsAdapter();
    }

    private void setToolbarTitle() {
        toolbarTitleTv.setText(getResources().getString(R.string.refund)
            .concat(" ")
            .concat(CommonUtils.getAmountTextWithCurrency(refundAmount)));
    }

    @Override
    public void OnRefundReasonReturn(RefundReasonEnum refundReasonType) {
        CommonUtils.hideKeyBoard(getActivity());
        refundReasonText = null;
        if(refundReasonType == RefundReasonEnum.OTHER){
            new CustomDialogBox.Builder((Activity) getContext())
                    .setTitle(getContext().getResources().getString(R.string.other))
                    .setMessage(getContext().getResources().getString(R.string.please_select_refund_reason))
                    .setEdittextVisibility(View.VISIBLE)
                    .setNegativeBtnVisibility(View.VISIBLE)
                    .setNegativeBtnText(getContext().getResources().getString(R.string.cancel))
                    .setNegativeBtnBackground(getContext().getResources().getColor(R.color.Silver, null))
                    .setPositiveBtnVisibility(View.VISIBLE)
                    .setPositiveBtnText(getContext().getResources().getString(R.string.yes))
                    .setPositiveBtnBackground(getContext().getResources().getColor(R.color.bg_screen1, null))
                    .setDurationTime(0)
                    .isCancellable(true)
                    .OnPositiveClicked(new CustomDialogListener() {
                        @Override
                        public void OnClick() {

                        }
                    })
                    .OnNegativeClicked(new CustomDialogListener() {
                        @Override
                        public void OnClick() {

                        }
                    })
                    .OnTextReturn(new CustomDialogBoxTextListener() {
                        @Override
                        public void OnTextReturn(String text) {
                            refundReasonText = text;
                        }
                    }).build();
        }else {
            refundReasonText = CommonUtils.getLanguage().equals(LANGUAGE_TR) ? refundReasonType.getLabelTr() : refundReasonType.getLabelEn();
        }
        checkSaveBtnEnability();
    }

    @Override
    public void OnReturnTrxList(List<RefundableTrxModel> refundableTrxModels) {
        mRefundableTrxModels = refundableTrxModels;
        double returnAmount = 0d;
        for(RefundableTrxModel refundableTrxModel : refundableTrxModels){
            returnAmount = CommonUtils.round(returnAmount + refundableTrxModel.getRefundAmount(), 2);
        }
        remainingAmount = CommonUtils.round(refundAmount - returnAmount, 2);
        remainingAmountTv.setText(CommonUtils.getAmountTextWithCurrency(remainingAmount).concat(" ")
                .concat(getResources().getString(R.string.remaining)));

        checkSaveBtnEnability();
    }

    private void checkSaveBtnEnability(){
        if(remainingAmount > 0d || refundReasonText == null)
            CommonUtils.setSaveBtnEnability(false, saveBtn, getContext());
        else
            CommonUtils.setSaveBtnEnability(true, saveBtn, getContext());
    }

    public void updatePaymentsAdapter(){
        List<RefundableTrxModel> refundableTrxModels = new ArrayList<>();

        for(Transaction transaction : saleModel.getTransactions()){

            if(transaction.getTransactionType() == TransactionTypeEnum.CANCEL.getId())
                continue;

            RealmResults<Refund> refunds = RefundDBHelper.getAllRefundsOfTransaction(transaction.getId(), true);

            double totalRefundAmount = 0d;
            for(Refund refund : refunds){
                totalRefundAmount = CommonUtils.round(totalRefundAmount + refund.getRefundAmount(), 2);
            }

            if(totalRefundAmount >= transaction.getTransactionAmount())
                continue;

            RefundableTrxModel refundableTrxModel = new RefundableTrxModel();
            refundableTrxModel.setTransaction(transaction);
            refundableTrxModel.setRefundAmount(0d);

            double availableRefundAmount = CommonUtils.round(transaction.getTransactionAmount() - totalRefundAmount, 2);

            if(availableRefundAmount > refundAmount)
                availableRefundAmount = refundAmount;

            refundableTrxModel.setMaxRefundAmount(CommonUtils.round(availableRefundAmount, 2));
            refundableTrxModels.add(refundableTrxModel);
        }

        paymentListForRefundAdapter = new PaymentListForRefundAdapter(getContext(), refundableTrxModels, refundAmount);
        paymentListForRefundAdapter.setRefundableTrxModelCallback(this);
        itemRv.setAdapter(paymentListForRefundAdapter);
    }

    private void updateReasonsAdapter(){
        if(refundCancellationStatus == TYPE_CANCEL)
            return;

        refundReasonsAdapter = new RefundReasonsAdapter(getContext());
        refundReasonsAdapter.setRefundReasonTypeCallback(this);
        refundReasonRv.setAdapter(refundReasonsAdapter);
    }

    private boolean refundTransaction(RefundableTrxModel refundableTrxModel, String refundGroupId){
        realm.beginTransaction();

        Refund refund = new Refund();
        refund.setId(UUID.randomUUID().toString());
        refund.setTransactionId(refundableTrxModel.getTransaction().getId());
        refund.setOrderId(refundableTrxModel.getTransaction().getOrderId());
        refund.setRefundByAmount(isRefundByAmount);
        refund.setRefundAmount(refundableTrxModel.getRefundAmount());
        refund.setRefundGroupId(refundGroupId);

        if (locationTrackObj.canGetLocation()){
            Location location = locationTrackObj.getLocation();
            if(location != null){
                refund.setLatitude(location.getLatitude());
                refund.setLongitude(location.getLongitude());
            }
        }

        refund.setRefundReason(refundReasonText);
        refund.setSuccessful(true);
        refund.setCreateDate(new Date());
        refund.setzNum(autoIncrement.getzNum());
        refund.setfNum(autoIncrement.getfNum());
        refund.setEODProcessed(false);

        //tempRefund = realm.copyToRealm(refund);

        LogUtil.logRefund(refund);

        realm.commitTransaction();

        BaseResponse baseResponse = RefundDBHelper.createOrUpdateRefund(refund);
        DataUtils.showBaseResponseMessage(getContext(), baseResponse);

        if(baseResponse.isSuccess()){
            AutoIncrementDBHelper.updateFnumByNextValue(autoIncrement);
            return true;
        }else
            return false;
    }

    private boolean saveRefundItems(String refundGroupId){

        if(orderRefundItems != null && orderRefundItems.size() > 0){
            for(OrderRefundItem orderRefundItem : orderRefundItems){
                LogUtil.logOrderRefundItem(orderRefundItem);
                orderRefundItem.setRefundGroupId(refundGroupId);
                BaseResponse baseResponse = OrderRefundItemDBHelper.createOrUpdateRefundItem(orderRefundItem);

                if(!baseResponse.isSuccess())
                    return false;
            }
        }
        return true;
    }
}
