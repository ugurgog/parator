package com.paypad.vuk507.menu.transactions;

import android.app.Activity;
import android.content.Context;
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

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.charge.order.OrderManager;
import com.paypad.vuk507.charge.payment.cancelpayment.ChargePaymentForCancelFragment;
import com.paypad.vuk507.db.TransactionDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.TransactionTypeEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.menu.transactions.adapters.PaymentListForCancelAdapter;
import com.paypad.vuk507.menu.transactions.interfaces.ReturnTransactionCallback;
import com.paypad.vuk507.model.Transaction;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.model.pojo.SaleModel;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.DataUtils;
import com.paypad.vuk507.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import pl.droidsonroids.gif.GifImageView;

public class SelectPaymentForCancelFragment extends BaseFragment implements ReturnTransactionCallback {

    private View mView;

    @BindView(R.id.cancelImgv)
    ClickableImageView cancelImgv;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.saveBtn)
    Button saveBtn;
    @BindView(R.id.itemRv)
    RecyclerView itemRv;
    @BindView(R.id.refundDescTv)
    TextView refundDescTv;
    @BindView(R.id.cancelAllTrxesBtn)
    Button cancelAllTrxesBtn;
    @BindView(R.id.mainrl)
    RelativeLayout mainrl;

    private User user;
    private PaymentListForCancelAdapter paymentListForCancelAdapter;
    private SaleModel saleModel;
    private Realm realm;
    private ChargePaymentForCancelFragment chargePaymentForCancelFragment;
    private List<Transaction> cancelableTrxlist;
    private Context mContext;
    private View completedScreenView;
    private boolean isTrxesAndOrderAmountsEquals;

    public SelectPaymentForCancelFragment(SaleModel saleModel) {
        this.saleModel = saleModel;
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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        CommonUtils.hideNavigationBar((Activity) mContext);
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_select_paym_for_cancel, container, false);
            ButterKnife.bind(this, mView);
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
                checkCancellationAmount();
            }
        });

        cancelAllTrxesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelAllTrxes();
            }
        });
    }

    private void checkCancellationAmount() {
        if(isTrxesAndOrderAmountsEquals)
            mFragmentNavigation.popFragments(1);
        else
            CommonUtils.showCustomToast(mContext,
                    mContext.getResources().getString(R.string.cancel_all_trxes_to_continue));
    }

    private void cancelAllTrxes() {
        boolean success = true;

        double totalCancelAmount = 0d;
        for(Transaction transaction : cancelableTrxlist){
            totalCancelAmount = CommonUtils.round(totalCancelAmount + transaction.getTotalAmount(), 2);
            if(!updateTransactionType(transaction, TransactionTypeEnum.CANCEL.getId())){
                success = false;
                break;
            }
        }

        if(!success){
            CommonUtils.showToastShort(mContext, "Unexpected error occured!");
            for(Transaction transaction : cancelableTrxlist)
                updateTransactionType(transaction, TransactionTypeEnum.SALE.getId());
        }

        if(success)
            completeCancellationProcess(totalCancelAmount);
    }

    private void initVariables() {
        isTrxesAndOrderAmountsEquals = OrderManager.isTrxesAndOrderAmountsEquals(saleModel.getOrder().getId());
        realm = Realm.getDefaultInstance();
        saveBtn.setVisibility(View.GONE);
        setToolbarTitle();
        setItemsRv();
        removeCompletedView();
    }

    private void setItemsRv() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        itemRv.setLayoutManager(linearLayoutManager);
        updatePaymentsAdapter();
    }

    private void setToolbarTitle() {
        toolbarTitleTv.setText(mContext.getResources().getString(R.string.select_to_cancel));
    }

    public static class DateComparator implements Comparator<Transaction> {
        @Override
        public int compare(Transaction o1, Transaction o2) {
            return o2.getCreateDate().compareTo(o1.getCreateDate());
        }
    }

    public void updatePaymentsAdapter(){
        removeCompletedView();
        cancelableTrxlist = new ArrayList<>();

        for(Transaction transaction : saleModel.getTransactions()){
            if(transaction.getTransactionType() != TransactionTypeEnum.CANCEL.getId())
                cancelableTrxlist.add(transaction);
        }

        if(cancelableTrxlist == null || cancelableTrxlist.size() == 0)
            mFragmentNavigation.popFragments(1);

        Collections.sort(cancelableTrxlist, new DateComparator());

        paymentListForCancelAdapter = new PaymentListForCancelAdapter(mContext, cancelableTrxlist);
        paymentListForCancelAdapter.setReturnTransactionCallback(this);
        itemRv.setAdapter(paymentListForCancelAdapter);
    }

    private void removeCompletedView(){
        try{
            if(completedScreenView != null)
                mainrl.removeView(completedScreenView);
        }catch (Exception e){

        }
    }

    @Override
    public void OnTransactionReturn(Transaction transaction, double returnAmount) {

        if(updateTransactionType(transaction, TransactionTypeEnum.CANCEL.getId()))
            completeCancellationProcess(transaction.getTotalAmount());

        //mFragmentNavigation.pushFragment(new NFCReadForReturnFragment(transaction, transaction.getTotalAmount(),
        //            TYPE_CANCEL, false, null, null));
    }

    private boolean updateTransactionType(Transaction transaction, int transactionType){
        realm.beginTransaction();

        Transaction tempTransaction = realm.copyToRealm(transaction);

        tempTransaction.setTransactionType(transactionType);

        if(transactionType == TransactionTypeEnum.CANCEL.getId())
            tempTransaction.setCancellationDate(new Date());

        realm.commitTransaction();

        BaseResponse baseResponse = TransactionDBHelper.createOrUpdateTransaction(tempTransaction);
        DataUtils.showBaseResponseMessage(mContext, baseResponse);

        LogUtil.logTransaction("cancelSingleTransaction", tempTransaction);

        if(baseResponse.isSuccess())
            return true;

        return false;
    }

    private void completeCancellationProcess(double amount){
        completedScreenView = getLayoutInflater().inflate(R.layout.layout_payment_fully_completed, null);

        GifImageView gifImageView = completedScreenView.findViewById(R.id.gifImageView);

        gifImageView.setLayoutParams(new LinearLayout.LayoutParams(
                CommonUtils.getScreenWidth(mContext),
                CommonUtils.getScreenHeight(mContext) + CommonUtils.getNavigationBarHeight(mContext)
        ));

        mainrl.addView(completedScreenView);

        initChargePaymentForCancelFragment(amount);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!isTrxesAndOrderAmountsEquals)
                    updatePaymentsAdapter();
                else
                    mFragmentNavigation.pushFragment(chargePaymentForCancelFragment);

            }
        }, 4000);
    }

    private void initChargePaymentForCancelFragment(double amount){
        chargePaymentForCancelFragment = new ChargePaymentForCancelFragment(saleModel, amount);
    }
}
