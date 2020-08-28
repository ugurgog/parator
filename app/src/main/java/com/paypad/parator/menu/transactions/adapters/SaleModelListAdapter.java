package com.paypad.parator.menu.transactions.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.paypad.parator.R;
import com.paypad.parator.charge.order.OrderManager;
import com.paypad.parator.db.OrderRefundItemDBHelper;
import com.paypad.parator.enums.PaymentTypeEnum;
import com.paypad.parator.enums.TransactionTypeEnum;
import com.paypad.parator.interfaces.ReturnSizeCallback;
import com.paypad.parator.menu.transactions.TransactionsFragment;
import com.paypad.parator.menu.transactions.interfaces.TransactionItemCallback;
import com.paypad.parator.model.OrderItem;
import com.paypad.parator.model.OrderRefundItem;
import com.paypad.parator.model.Refund;
import com.paypad.parator.model.Transaction;
import com.paypad.parator.utils.CommonUtils;
import com.paypad.parator.utils.DataUtils;
import com.paypad.parator.utils.ShapeUtil;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;

import static com.paypad.parator.constants.CustomConstants.TYPE_PRICE;

public class SaleModelListAdapter extends RecyclerView.Adapter {

    private List<TransactionsFragment.TransactionItem> transactionItems = new ArrayList<>();
    private List<TransactionsFragment.TransactionItem> orgTransactionItems = new ArrayList<>();

    private Context mContext;
    private TransactionItemCallback transactionItemCallback;

    private static final int VIEW_ITEM = 0;
    private static final int VIEW_NONE = 1;

    private static final int PAYMENT_CARD = 0;
    private static final int PAYMENT_CASH = 1;
    private static final int PAYMENT_MULTI_CARD = 2;
    private static final int PAYMENT_MULTI_CASH = 3;


    public SaleModelListAdapter(Context context, List<TransactionsFragment.TransactionItem> transactionItems) {
        this.transactionItems.addAll(transactionItems);
        this.orgTransactionItems.addAll(transactionItems);
        mContext = context;
    }

    public void setTransactionItemCallback(TransactionItemCallback transactionItemCallback) {
        this.transactionItemCallback = transactionItemCallback;
    }

    @Override
    public int getItemViewType(int position) {
        if(!transactionItems.get(position).isNoItem())
            return VIEW_ITEM;
        else
            return VIEW_NONE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(viewType == VIEW_ITEM){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_item_transaction, parent, false);
            return new SaleModelListAdapter.TransactionHolder(itemView);
        }else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.adapter_no_transaction_title, parent, false);

            return new SaleModelListAdapter.NoTransactionHolder(v);
        }
    }

    public class TransactionHolder extends RecyclerView.ViewHolder {

        CardView transactionCv;
        ImageView paymentIconImgv;
        ImageView cancelImgv;
        TextView amountTv;
        TextView saleItemDescTv;
        TextView hourTv;
        TextView orderNumTv;

        TransactionsFragment.TransactionItem transactionItem;
        int position;

        TransactionHolder(View view) {
            super(view);

            transactionCv = view.findViewById(R.id.transactionCv);
            paymentIconImgv = view.findViewById(R.id.paymentIconImgv);
            amountTv = view.findViewById(R.id.amountTv);
            saleItemDescTv = view.findViewById(R.id.saleItemDescTv);
            hourTv = view.findViewById(R.id.hourTv);
            cancelImgv = view.findViewById(R.id.cancelImgv);
            orderNumTv = view.findViewById(R.id.orderNumTv);

            transactionCv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    transactionItemCallback.onReturnTransactionItem(transactionItem);
                }
            });
        }

        void setData(TransactionsFragment.TransactionItem transactionItem, int position) {
            this.transactionItem = transactionItem;
            this.position = position;

            setSaleAmount();
            setSaleItemsDesc();
            setSaleHour();
            setIcon();
            setOrderNumTv();
        }

        @SuppressLint("DefaultLocale")
        private void setOrderNumTv() {
            String orderNumText;

            orderNumText = transactionItem.getSaleModel().getOrder().getOrderNum();

            if(transactionItem.getTransaction() != null){
                orderNumText = orderNumText.concat("/").concat(String.format("%06d", transactionItem.getTransaction().getzNum()).concat("/"))
                        .concat(String.format("%06d", transactionItem.getTransaction().getfNum()));
            }/*else if(transactionItem.getRefund() != null){
                orderNumText = orderNumText.concat("/").concat(String.format("%06d", transactionItem.getRefund().getzNum()).concat("/"))
                        .concat(String.format("%06d", transactionItem.getRefund().getfNum()));
            }*/
            orderNumTv.setText(orderNumText);
        }

        private void setSaleItemsDesc() {

            if(transactionItem.getTransaction() == null && transactionItem.getRefunds() == null){
                saleItemDescTv.setVisibility(View.VISIBLE);
                String desc = "";
                for(OrderItem orderItem : transactionItem.getSaleModel().getOrderItems()){

                    if(orderItem.getQuantity() == 1)
                        desc = desc.concat(orderItem.getName()).concat(", ");
                    else
                        desc = desc.concat(orderItem.getName().concat(" x ").concat(String.valueOf(orderItem.getQuantity()))).concat(", ");

                    if(desc.length() > 40)
                        break;
                }

                String desc1 = "";

                try{
                    desc1 = desc.substring(0, desc.length() - 2);

                    if(desc1.length() > 40)
                        desc1 = desc1.substring(0, 40).concat("...");
                }catch (Exception e){

                }

                saleItemDescTv.setText(desc1);
            }else if(transactionItem.getRefunds() != null && transactionItem.getRefunds().size() > 0){
                saleItemDescTv.setVisibility(View.VISIBLE);

                String refundGroupId = transactionItem.getRefunds().get(0).getRefundGroupId();
                RealmResults<OrderRefundItem> orderRefundItems = OrderRefundItemDBHelper.getRefundItemsByRefundGroupId(refundGroupId);

                if(orderRefundItems == null || orderRefundItems.size() == 0){
                    saleItemDescTv.setVisibility(View.GONE);
                }else {
                    String desc = "";
                    for(OrderRefundItem orderRefundItem: orderRefundItems){
                        desc = desc.concat(orderRefundItem.getName()).concat(", ");
                        if(desc.length() > 40)
                            break;
                    }

                    String desc1 = "";

                    try{
                        desc1 = desc.substring(0, desc.length() - 2);

                        if(desc1.length() > 40)
                            desc1 = desc1.substring(0, 40).concat("...");
                    }catch (Exception e){

                    }
                    saleItemDescTv.setText(desc1);
                }
            }else
                saleItemDescTv.setVisibility(View.GONE);
        }

        private void setSaleAmount() {
            double amount = 0d;

            if(transactionItem.getTransaction() != null)
                amount = CommonUtils.round((transactionItem.getTransaction().getTotalAmount()), 2);
            else if(transactionItem.getRefunds() != null){

                for(Refund refund : transactionItem.getRefunds())
                    amount = CommonUtils.round(amount + refund.getRefundAmount(), 2);

                amount = CommonUtils.round(amount, 2);
            } else
                amount = CommonUtils.round((transactionItem.getSaleModel().getOrder().getDiscountedAmount()
                        + OrderManager.getTotalTipAmountOfSale(transactionItem.getSaleModel())), 2);

            String amountStr = ((transactionItem.getTransaction() != null || transactionItem.getRefunds() != null) ? "- " : "") +
                    CommonUtils.getDoubleStrValueForView(amount, TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol());
            amountTv.setText(amountStr);
        }

        private void setSaleHour() {
            hourTv.setText(DataUtils.getHourOfOrder(transactionItem.getTrxDate()));
        }

        private void setIcon() {
            setIconForSaleModel();

            if(transactionItem.getTransaction() == null && transactionItem.getRefunds() == null){
                cancelImgv.setVisibility(View.GONE);
            } else {
                cancelImgv.setVisibility(View.VISIBLE);
                setIconForTransaction();
            }
        }

        private void setIconForSaleModel(){
            long paymentType = 0;

            if(transactionItem.getSaleModel().getTransactions() != null){
                if(transactionItem.getSaleModel().getTransactions().size() == 1){
                    Transaction transaction = transactionItem.getSaleModel().getTransactions().get(0);

                    if(transaction.getPaymentTypeId() == PaymentTypeEnum.CASH.getId())
                        paymentType = PAYMENT_CASH;
                    else  if(transaction.getPaymentTypeId() == PaymentTypeEnum.CREDIT_CARD.getId())
                        paymentType = PAYMENT_CARD;
                }else {
                    paymentType = PAYMENT_MULTI_CARD;
                    for(Transaction transaction : transactionItem.getSaleModel().getTransactions()){
                        if(transaction.getPaymentTypeId() == PaymentTypeEnum.CASH.getId()){
                            paymentType = PAYMENT_MULTI_CASH;
                            break;
                        }
                    }
                }
            }

            if(paymentType == PAYMENT_CASH)
                Glide.with(mContext).load(R.drawable.icon_payment_cash_type).into(paymentIconImgv);
            else if(paymentType == PAYMENT_CARD)
                Glide.with(mContext).load(R.drawable.icon_payment_card_type).into(paymentIconImgv);
            if(paymentType == PAYMENT_MULTI_CASH)
                Glide.with(mContext).load(R.drawable.icon_payment_multi_type).into(paymentIconImgv);
            if(paymentType == PAYMENT_MULTI_CARD)
                Glide.with(mContext).load(R.drawable.icon_payment_multi_card_type).into(paymentIconImgv);
        }

        private void setIconForTransaction(){
            if(transactionItem.getRefunds() != null){

                Glide.with(mContext).load(R.drawable.ic_arrow_back_white_24dp).into(cancelImgv);

                cancelImgv.setBackground(ShapeUtil.getShape(mContext.getResources().getColor(R.color.Orange, null),
                        mContext.getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 20, 3));
            }else if(transactionItem.getTransaction().getTransactionType() == TransactionTypeEnum.CANCEL.getId()){

                Glide.with(mContext).load(R.drawable.ic_close_gray_24dp).into(cancelImgv);

                cancelImgv.setBackground(ShapeUtil.getShape(mContext.getResources().getColor(R.color.Red, null),
                        mContext.getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 20, 3));
            }
        }
    }

    public class NoTransactionHolder extends RecyclerView.ViewHolder {

        private TextView titleTv;

        NoTransactionHolder(View view) {
            super(view);
            titleTv = view.findViewById(R.id.titleTv);
        }

        void setData(TransactionsFragment.TransactionItem transactionItem) {
            titleTv.setText(DataUtils.getTransactionFullDateName(transactionItem.getTrxDate()));
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        TransactionsFragment.TransactionItem transactionItem = transactionItems.get(position);

        if (holder instanceof TransactionHolder) {
            ((TransactionHolder) holder).setData(transactionItem, position);
        } else {
            ((NoTransactionHolder) holder).setData(transactionItem);
        }
    }

    @Override
    public int getItemCount() {
        if(transactionItems != null)
            return transactionItems.size();
        else
            return 0;
    }

    @SuppressLint("DefaultLocale")
    public void updateAdapter(String searchText, ReturnSizeCallback returnSizeCallback) {
        if (searchText.trim().isEmpty()){
            transactionItems = orgTransactionItems;
        } else {

            List<TransactionsFragment.TransactionItem> tempTransactionItems = new ArrayList<>();

            for(TransactionsFragment.TransactionItem transactionItem : orgTransactionItems){
                if(transactionItem.getSaleModel() != null && transactionItem.getSaleModel().getOrder() != null){
                    String orderNum = transactionItem.getSaleModel().getOrder().getOrderNum();
                    String zNum = "", fNum = "";

                    if(transactionItem.getRefunds() != null){
                        for(Refund refund : transactionItem.getRefunds()){
                            zNum = zNum.concat(",").concat(String.format("%06d", refund.getzNum()));
                            fNum = fNum.concat(",").concat(String.format("%06d", refund.getfNum()));
                        }
                    }else if(transactionItem.getTransaction() != null){
                        zNum = String.format("%06d", transactionItem.getTransaction().getzNum());
                        fNum = String.format("%06d", transactionItem.getTransaction().getfNum());
                    }

                    if(orderNum.contains(searchText) || zNum.contains(searchText) || fNum.contains(searchText))
                        tempTransactionItems.add(transactionItem);
                }
            }
            transactionItems = tempTransactionItems;
        }

        this.notifyDataSetChanged();

        if (transactionItems != null)
            returnSizeCallback.OnReturn(transactionItems.size());
        else
            returnSizeCallback.OnReturn(0);
    }
}
