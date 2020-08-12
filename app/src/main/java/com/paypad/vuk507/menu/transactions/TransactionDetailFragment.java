package com.paypad.vuk507.menu.transactions;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.charge.order.OrderManager;
import com.paypad.vuk507.charge.payment.utils.PrintOrderManager;
import com.paypad.vuk507.db.RefundDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.PaymentTypeEnum;
import com.paypad.vuk507.enums.TransactionTypeEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.menu.transactions.adapters.ItemsServicesAdapter;
import com.paypad.vuk507.menu.transactions.adapters.PaymentDetailAdapter;
import com.paypad.vuk507.menu.transactions.adapters.PaymentTotalAdapter;
import com.paypad.vuk507.menu.transactions.adapters.RefundedTransactionAdapter;
import com.paypad.vuk507.menu.transactions.util.PaymentTotalManager;
import com.paypad.vuk507.model.DynamicBoxModel;
import com.paypad.vuk507.model.Refund;
import com.paypad.vuk507.model.Transaction;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.model.pojo.PaymentDetailModel;
import com.paypad.vuk507.model.pojo.SaleModel;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CommonUtils;
import com.sunmi.peripheral.printer.InnerResultCallbcak;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.paypad.vuk507.constants.CustomConstants.TYPE_CANCEL;
import static com.paypad.vuk507.constants.CustomConstants.TYPE_PRICE;
import static com.paypad.vuk507.constants.CustomConstants.TYPE_REFUND;

public class TransactionDetailFragment extends BaseFragment {

    private View mView;

    @BindView(R.id.backImgv)
    ClickableImageView backImgv;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;


    @BindView(R.id.newReceiptBtn)
    Button newReceiptBtn;
    @BindView(R.id.issueRefundBtn)
    Button issueRefundBtn;
    @BindView(R.id.paymentsRv)
    RecyclerView paymentsRv;
    @BindView(R.id.saleItemsRv)
    RecyclerView saleItemsRv;
    @BindView(R.id.totalRv)
    RecyclerView totalRv;
    @BindView(R.id.refundsRv)
    RecyclerView refundsRv;
    @BindView(R.id.printReceiptBtn)
    Button printReceiptBtn;
    @BindView(R.id.mainll)
    LinearLayout mainll;
    @BindView(R.id.refundsll)
    LinearLayout refundsll;
    @BindView(R.id.orderNumTv)
    TextView orderNumTv;


    private User user;

    private PaymentDetailAdapter paymentDetailAdapter;
    private RefundedTransactionAdapter refundedTransactionAdapter;
    private ItemsServicesAdapter itemsServicesAdapter;
    private PaymentTotalAdapter paymentTotalAdapter;

    private SaleModel saleModel;
    private OrderManager orderManager;
    private PrintOrderManager printOrderManager;
    private SelectPaymentForRefundFragment selectPaymentForRefundFragment;

    private int refundCancellationStatus = TYPE_REFUND;

    public TransactionDetailFragment(SaleModel saleModel) {
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
            mView = inflater.inflate(R.layout.fragment_transaction_detail, container, false);
            ButterKnife.bind(this, mView);
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    private void initListeners() {
        backImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });

        newReceiptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFragmentNavigation.pushFragment(new SelectNewReceiptFragment(saleModel.getTransactions()));
            }
        });

        printReceiptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                printOrderManager.printCustomerReceipt();
            }
        });

        issueRefundBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processRefundOrCancel();
            }
        });
    }

    private void initVariables() {
        orderManager = new OrderManager();
        orderNumTv.setText(saleModel.getSale().getOrderNum());
        refundsll.setVisibility(View.VISIBLE);
        setToolbarTitle();
        initPrinter();
        setRefundStatus();

        setPaymentDetailAdapter();
        setItemsServicesAdapter();
        setRefundsAdapter();
        setPaymentTotalAdapter();
    }

    private void processRefundOrCancel(){
        boolean cancellationStatus = checkRefundAndCancellation();

        if(!cancellationStatus){
            if(refundCancellationStatus == TYPE_REFUND){
                CommonUtils.snackbarDisplay(mainll, getContext(), getResources().getString(R.string.no_item_for_refund_found));
            }else if(refundCancellationStatus == TYPE_CANCEL){
                CommonUtils.snackbarDisplay(mainll, getContext(), getResources().getString(R.string.no_item_for_cancel_found));
            }
        }else {
            initSelectPaymentForRefundFragment();
            mFragmentNavigation.pushFragment(selectPaymentForRefundFragment);
        }
    }

    private void initSelectPaymentForRefundFragment(){
        selectPaymentForRefundFragment = new SelectPaymentForRefundFragment(refundCancellationStatus, saleModel);
    }

    private boolean checkRefundAndCancellation(){
        for(Transaction transaction : saleModel.getTransactions()){
            if(transaction.getTransactionType() == TransactionTypeEnum.SALE.getId()){

                RealmResults<Refund> refunds = RefundDBHelper.getAllRefundsOfTransaction(transaction.getTransactionId(), true);

                if(refunds != null){
                    double totalRefundAmount = 0d;
                    for(Refund refund : refunds)
                        totalRefundAmount = CommonUtils.round(totalRefundAmount + refund.getRefundAmount(), 2);

                    if(totalRefundAmount < transaction.getTotalAmount())
                        return true;

                }else
                    return true;
            }
        }
        return false;
    }

    private void initPrinter() {
        printOrderManager = new PrintOrderManager(getContext(), saleModel,false);
        printOrderManager.setCallback(mCallback);
    }

    private void setRefundStatus(){
        Transaction transaction = saleModel.getTransactions().get(0);

        if(transaction.isEODProcessed()){
            issueRefundBtn.setText(getResources().getString(R.string.order_refund));
            refundCancellationStatus = TYPE_REFUND;
        } else {
            issueRefundBtn.setText(getResources().getString(R.string.order_cancel));
            refundCancellationStatus = TYPE_CANCEL;
        }
    }

    private void setToolbarTitle(){
        double amount = CommonUtils.round((saleModel.getSale().getDiscountedAmount() + OrderManager.getTotalTipAmountOfSale(saleModel)), 2);
        String amountStr = CommonUtils.getDoubleStrValueForView(amount, TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol()).concat(" ")
                .concat(getResources().getString(R.string.sale));
        toolbarTitleTv.setText(amountStr);
    }

    public static class TrxSeqNumComparator implements Comparator<Transaction> {
        @Override
        public int compare(Transaction o1, Transaction o2) {
            return (int) (o1.getSeqNumber() - o2.getSeqNumber());
        }
    }

    public void setPaymentDetailAdapter(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        paymentsRv.setLayoutManager(linearLayoutManager);

        Realm realm = Realm.getDefaultInstance();
        List<Transaction> trxlist = realm.copyFromRealm(saleModel.getTransactions());

        Collections.sort(trxlist, new TrxSeqNumComparator());

        paymentDetailAdapter = new PaymentDetailAdapter(getContext(), trxlist);
        paymentsRv.setAdapter(paymentDetailAdapter);
    }

    private void setItemsServicesAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        saleItemsRv.setLayoutManager(linearLayoutManager);

        itemsServicesAdapter = new ItemsServicesAdapter(getContext(), saleModel.getSaleItems());
        saleItemsRv.setAdapter(itemsServicesAdapter);
    }

    private void setRefundsAdapter() {
        RealmResults<Refund> refunds = RefundDBHelper.getAllRefundsOfOrder(saleModel.getSale().getSaleUuid(), true);

        if(refunds == null || refunds.size() == 0){
            refundsll.setVisibility(View.GONE);
            return;
        }

        List<Refund> refundList = new ArrayList(refunds);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        refundsRv.setLayoutManager(linearLayoutManager);

        refundedTransactionAdapter = new RefundedTransactionAdapter(getContext(), refundList, saleModel.getSale().getSaleUuid());
        refundsRv.setAdapter(refundedTransactionAdapter);
    }

    private void setPaymentTotalAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        totalRv.setLayoutManager(linearLayoutManager);

        PaymentTotalManager paymentTotalManager = new PaymentTotalManager(getContext(), saleModel);
        List<PaymentDetailModel> paymentDetailModels = paymentTotalManager.getPaymentDetails();

        paymentTotalAdapter = new PaymentTotalAdapter(getContext(), paymentDetailModels);
        totalRv.setAdapter(paymentTotalAdapter);
    }

    InnerResultCallbcak mCallback = new InnerResultCallbcak() {
        @Override
        public void onRunResult(boolean isSuccess) throws RemoteException {

            Log.i("Info", "::onReturnString isSuccess:" + isSuccess);
        }

        @Override
        public void onReturnString(String result) throws RemoteException {

            Log.i("Info", "::onReturnString result:" + result);
        }

        @Override
        public void onRaiseException(int code, String msg) throws RemoteException {

        }

        @Override
        public void onPrintResult(int code, String msg) throws RemoteException {
            final int res = code;
            ((Activity) Objects.requireNonNull(getContext())).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(res == 0){
                        CommonUtils.showToastShort(getContext(), "Print successful");
                    }else{
                        CommonUtils.showToastShort(getContext(), "Print failed");
                    }
                }
            });
        }
    };
}
