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
import com.paypad.vuk507.db.SaleDBHelper;
import com.paypad.vuk507.db.TransactionDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.TransactionTypeEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.interfaces.ReturnAmountCallback;
import com.paypad.vuk507.menu.transactions.adapters.ItemsServicesAdapter;
import com.paypad.vuk507.menu.transactions.adapters.PaymentDetailAdapter;
import com.paypad.vuk507.menu.transactions.adapters.PaymentTotalAdapter;
import com.paypad.vuk507.menu.transactions.adapters.RefundedTransactionAdapter;
import com.paypad.vuk507.menu.transactions.model.RefundedTrxModel;
import com.paypad.vuk507.menu.transactions.util.PaymentTotalManager;
import com.paypad.vuk507.model.Refund;
import com.paypad.vuk507.model.Transaction;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.model.pojo.PaymentDetailModel;
import com.paypad.vuk507.model.pojo.SaleModel;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.DeviceUtil;
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
import io.realm.RealmList;
import io.realm.RealmResults;

import static com.paypad.vuk507.constants.CustomConstants.TYPE_CANCEL;
import static com.paypad.vuk507.constants.CustomConstants.TYPE_REFUND;

public class TransactionDetailFragment extends BaseFragment implements ReturnAmountCallback {

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

    private TransactionsFragment.TransactionItem transactionItem;
    private PrintOrderManager printOrderManager;
    private SelectPaymentForCancelFragment selectPaymentForCancelFragment;
    private Context mContext;

    private int refundCancellationStatus = TYPE_REFUND;

    public TransactionDetailFragment(TransactionsFragment.TransactionItem transactionItem) {
        this.transactionItem = transactionItem;
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
            user = UserDBHelper.getUserFromCache(getContext());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        CommonUtils.showNavigationBar((Activity) mContext);
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
                mFragmentNavigation.pushFragment(new SelectNewReceiptFragment(transactionItem.getSaleModel().getTransactions()));
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
        orderNumTv.setText(transactionItem.getSaleModel().getOrder().getOrderNum());
        refundsll.setVisibility(View.VISIBLE);
        setToolbarTitle();
        initPrinter();
        setRefundStatus();

        setPaymentDetailAdapter();
        setItemsServicesAdapter();
        setRefundsAdapter();
        setPaymentTotalAdapter();

        //String deviceName = DeviceUtil.getDeviceName();
        //Log.i("Info", "deviceName:" + deviceName);
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
            if(refundCancellationStatus == TYPE_CANCEL){
                initSelectPaymentForRefundFragment();
                mFragmentNavigation.pushFragment(selectPaymentForCancelFragment);
            }else {

                if(OrderManager.isExistRefundByAmount(transactionItem.getSaleModel().getOrder().getId()) ||
                        !OrderManager.isTrxesAndOrderAmountsEquals(transactionItem.getSaleModel().getOrder().getId()))
                    startRefundByAmountFragment();
                else
                    mFragmentNavigation.pushFragment(new RefundFragment(transactionItem.getSaleModel(), refundCancellationStatus));
            }
        }
    }

    private void startRefundByAmountFragment(){
        double availableRefundAmount = OrderManager.getAvailableRefundAmount(transactionItem.getSaleModel());
        RefundByAmountFragment refundByAmountFragment = new RefundByAmountFragment(transactionItem.getSaleModel(), true, availableRefundAmount);
        refundByAmountFragment.setReturnAmountCallback(this);
        mFragmentNavigation.pushFragment(refundByAmountFragment);
    }

    private void initSelectPaymentForRefundFragment(){
        selectPaymentForCancelFragment = new SelectPaymentForCancelFragment(transactionItem.getSaleModel());
    }

    private boolean checkRefundAndCancellation(){
        for(Transaction transaction : transactionItem.getSaleModel().getTransactions()){
            if(transaction.getTransactionType() == TransactionTypeEnum.SALE.getId()){

                RealmResults<Refund> refunds = RefundDBHelper.getAllRefundsOfTransaction(transaction.getId(), true);

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
        printOrderManager = new PrintOrderManager(getContext(), transactionItem.getSaleModel(),false);
        printOrderManager.setCallback(mCallback);
    }

    private void setRefundStatus(){
        Transaction transaction = transactionItem.getSaleModel().getTransactions().get(0);

        if(transaction.isEODProcessed()){
            issueRefundBtn.setText(getResources().getString(R.string.order_refund));
            refundCancellationStatus = TYPE_REFUND;
        } else {
            issueRefundBtn.setText(getResources().getString(R.string.order_cancel));
            refundCancellationStatus = TYPE_CANCEL;
        }
    }

    private void setToolbarTitle(){
        String title;
        if(transactionItem.getTransaction() != null){
            title = CommonUtils.getAmountTextWithCurrency(transactionItem.getTransaction().getTotalAmount()).concat(" ")
                    .concat(getResources().getString(R.string.cancel));
        }else if(transactionItem.getRefunds() != null && transactionItem.getRefunds().size() > 0){
            double totalRefundAmount = 0d;

            for(Refund refund : transactionItem.getRefunds())
                totalRefundAmount = CommonUtils.round(totalRefundAmount + refund.getRefundAmount(), 2);

            title = CommonUtils.getAmountTextWithCurrency(totalRefundAmount).concat(" ").concat(getResources().getString(R.string.refund));
        }else {
            double amount = CommonUtils.round((transactionItem.getSaleModel().getOrder().getDiscountedAmount() + OrderManager.getTotalTipAmountOfSale(transactionItem.getSaleModel())), 2);
            title = CommonUtils.getAmountTextWithCurrency(amount).concat(" ")
                    .concat(getResources().getString(R.string.order));
        }

        toolbarTitleTv.setText(title);
    }

    @Override
    public void OnReturnAmount(double amount) {

    }

    public static class TrxSeqNumComparator implements Comparator<Transaction> {
        @Override
        public int compare(Transaction o1, Transaction o2) {
            return (int) (o1.getSeqNumber() - o2.getSeqNumber());
        }
    }

    public void setPaymentDetailAdapter(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        paymentsRv.setLayoutManager(linearLayoutManager);

        Realm realm = Realm.getDefaultInstance();
        List<Transaction> trxlist = realm.copyFromRealm(transactionItem.getSaleModel().getTransactions());

        Collections.sort(trxlist, new TrxSeqNumComparator());

        paymentDetailAdapter = new PaymentDetailAdapter(mContext, trxlist);
        paymentsRv.setAdapter(paymentDetailAdapter);
    }

    private void setItemsServicesAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        saleItemsRv.setLayoutManager(linearLayoutManager);

        itemsServicesAdapter = new ItemsServicesAdapter(mContext, transactionItem.getSaleModel().getOrderItems());
        saleItemsRv.setAdapter(itemsServicesAdapter);
    }

    public static class RefundGroupIdComparator implements Comparator<Refund> {
        @Override
        public int compare(Refund o1, Refund o2) {
            return o2.getRefundGroupId().compareTo(o1.getRefundGroupId());
        }
    }

    private void setRefundsAdapter() {
        RealmResults<Refund> refunds = RefundDBHelper.getAllRefundsOfOrder(transactionItem.getSaleModel().getOrder().getId(), true);

        if(refunds == null || refunds.size() == 0){
            refundsll.setVisibility(View.GONE);
            return;
        }

        List<Refund> refundList = new ArrayList(refunds);
        Collections.sort(refundList, new RefundGroupIdComparator());

        List<RefundedTrxModel> refundedTrxModels = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        refundsRv.setLayoutManager(linearLayoutManager);

        String prevRefundGroupId = "";
        RefundedTrxModel refundedTrxModel = null;
        for(Refund refund : refundList){

            if(!refund.getRefundGroupId().equals(prevRefundGroupId)){
                if(refundedTrxModel != null) {
                    refundedTrxModels.add(refundedTrxModel);
                    refundedTrxModel = null;
                }

                refundedTrxModel = new RefundedTrxModel();
                refundedTrxModel.setRefunds(new RealmList<>());
                refundedTrxModel.setRefundGroupId(refund.getRefundGroupId());
                refundedTrxModel.getRefunds().add(refund);
            }else {
                refundedTrxModel.getRefunds().add(refund);
            }
            prevRefundGroupId = refund.getRefundGroupId();
        }

        if(refundedTrxModel != null){
            refundedTrxModels.add(refundedTrxModel);
        }


        refundedTransactionAdapter = new RefundedTransactionAdapter(mContext, refundedTrxModels, transactionItem.getSaleModel().getOrder().getId());
        refundsRv.setAdapter(refundedTransactionAdapter);
    }

    private void setPaymentTotalAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        totalRv.setLayoutManager(linearLayoutManager);

        PaymentTotalManager paymentTotalManager = new PaymentTotalManager(mContext, transactionItem.getSaleModel());
        List<PaymentDetailModel> paymentDetailModels = paymentTotalManager.getPaymentDetails();

        paymentTotalAdapter = new PaymentTotalAdapter(mContext, paymentDetailModels);
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
            ((Activity) Objects.requireNonNull(mContext)).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(res == 0){
                        CommonUtils.showToastShort(mContext, "Print successful");
                    }else{
                        CommonUtils.showToastShort(mContext, "Print failed");
                    }
                }
            });
        }
    };
}
