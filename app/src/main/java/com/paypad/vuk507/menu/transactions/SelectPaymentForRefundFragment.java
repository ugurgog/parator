package com.paypad.vuk507.menu.transactions;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.db.RefundDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.interfaces.ReturnAmountCallback;
import com.paypad.vuk507.menu.transactions.adapters.PaymentListForRefundAdapter;
import com.paypad.vuk507.menu.transactions.interfaces.ReturnTransactionCallback;
import com.paypad.vuk507.model.Refund;
import com.paypad.vuk507.model.Transaction;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.model.pojo.SaleModel;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;

import static com.paypad.vuk507.constants.CustomConstants.LANGUAGE_TR;
import static com.paypad.vuk507.constants.CustomConstants.TYPE_CANCEL;
import static com.paypad.vuk507.constants.CustomConstants.TYPE_REFUND;

public class SelectPaymentForRefundFragment extends BaseFragment implements ReturnTransactionCallback {

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

    private User user;
    private PaymentListForRefundAdapter paymentListForRefundAdapter;
    private SaleModel saleModel;
    private int refundCancellationStatus;

    public SelectPaymentForRefundFragment(int refundCancellationStatus, SaleModel saleModel) {
        this.saleModel = saleModel;
        this.refundCancellationStatus = refundCancellationStatus;
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
            //initVariables();
            //initListeners();
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
    }

    private void initVariables() {
        saveBtn.setVisibility(View.GONE);
        setToolbarTitle();
        setRefundDescription();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        itemRv.setLayoutManager(linearLayoutManager);
        updateAdapterWithCurrentList();
    }

    private void setRefundDescription() {

        String desc;
        if (CommonUtils.getLanguage().equals(LANGUAGE_TR))
            desc = refundCancellationStatus == TYPE_REFUND ? "İADE İÇİN İŞLEM SEÇİN" : "İPTAL İÇİN İŞLEM SEÇİN";
         else
            desc = refundCancellationStatus == TYPE_REFUND ? "SELECT TRANSACTION FOR REFUND" : "SELECT TRANSACTION FOR CANCEL";

         refundDescTv.setText(desc);
    }

    private void setToolbarTitle() {
        if(refundCancellationStatus == TYPE_REFUND)
            toolbarTitleTv.setText(Objects.requireNonNull(getContext()).getResources().getString(R.string.select_to_refund));
        else if(refundCancellationStatus == TYPE_CANCEL)
            toolbarTitleTv.setText(Objects.requireNonNull(getContext()).getResources().getString(R.string.select_to_cancel));
    }

    public static class DateComparator implements Comparator<Transaction> {
        @Override
        public int compare(Transaction o1, Transaction o2) {
            return o2.getCreateDate().compareTo(o1.getCreateDate());
        }
    }

    public void updateAdapterWithCurrentList(){
        List<Transaction> trxlist = new ArrayList<>();

        for(Transaction transaction : saleModel.getTransactions()){
            //if(transaction.getPaymentTypeId() == PaymentTypeEnum.CREDIT_CARD.getId() && transaction.getTransactionType() == TransactionTypeEnum.SALE.getId()){
                trxlist.add(transaction);
            //}
        }

        Collections.sort(trxlist, new DateComparator());

        paymentListForRefundAdapter = new PaymentListForRefundAdapter(getContext(), trxlist);
        paymentListForRefundAdapter.setReturnTransactionCallback(this);
        itemRv.setAdapter(paymentListForRefundAdapter);
    }

    @Override
    public void OnTransactionReturn(Transaction transaction, double returnAmount) {

        if(refundCancellationStatus == TYPE_CANCEL)
            mFragmentNavigation.pushFragment(new NFCReadForReturnFragment(transaction, transaction.getTotalAmount(),
                    refundCancellationStatus, false, null, null));
        else if(refundCancellationStatus == TYPE_REFUND){

            RealmResults<Refund> refunds = RefundDBHelper.getAllRefundsOfOrder(transaction.getSaleUuid(), true);

            boolean isExistByAmount = false;
            for(Refund refund : refunds){
                LogUtil.logRefund(refund);
                if(refund.isRefundByAmount()){
                    isExistByAmount = true;
                    break;
                }
            }

            if(isExistByAmount){
                RefundByAmountFragment refundByAmountFragment = new RefundByAmountFragment(transaction, true, returnAmount);
                refundByAmountFragment.setReturnAmountCallback(new ReturnAmountCallback() {
                    @Override
                    public void OnReturnAmount(double amount) {

                    }
                });
                mFragmentNavigation.pushFragment(refundByAmountFragment);
            }else {
                mFragmentNavigation.pushFragment(new RefundFragment(transaction, refundCancellationStatus, returnAmount));
            }
        }
    }
}
