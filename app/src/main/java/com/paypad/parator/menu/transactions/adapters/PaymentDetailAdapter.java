package com.paypad.parator.menu.transactions.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.paypad.parator.R;
import com.paypad.parator.enums.PaymentTypeEnum;
import com.paypad.parator.enums.TransactionTypeEnum;
import com.paypad.parator.model.Transaction;
import com.paypad.parator.utils.CommonUtils;
import com.paypad.parator.utils.ShapeUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.paypad.parator.constants.CustomConstants.TYPE_PRICE;


public class PaymentDetailAdapter extends RecyclerView.Adapter {

    private List<Transaction> transactions = new ArrayList<>();
    private Context mContext;

    private static final int PAYMENT_CARD = 0;
    private static final int PAYMENT_CASH = 1;

    public PaymentDetailAdapter(Context context, List<Transaction> transactions) {
        this.transactions.addAll(transactions);
        mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        if(transactions.get(position).getPaymentTypeId() == PaymentTypeEnum.CASH.getId())
            return PAYMENT_CASH;
        else if(transactions.get(position).getPaymentTypeId() == PaymentTypeEnum.CREDIT_CARD.getId())
            return PAYMENT_CARD;

        //TODO - Diger odeme tipleri icin buraya ekleme yapilacak.
        return 0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(viewType == PAYMENT_CASH){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_item_cash_payment_list, parent, false);
            return new PaymentDetailAdapter.CashHolder(itemView);
        }else if(viewType == PAYMENT_CARD){
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.adapter_item_card_payment_list, parent, false);

            return new PaymentDetailAdapter.CardHolder(v);
        }

        //TODO - Diger odeme tipleri icin buraya ekleme yapilacak.
        return null;
    }

    public class CashHolder extends RecyclerView.ViewHolder {

        TextView paymentDateTv;
        TextView cashAmountTv;
        TextView trxSeqDescTv;

        LinearLayout cancelledll;
        ImageView cancelImgv;
        TextView cancelTv;
        TextView cancelDateTv;

        Transaction transaction;
        int position;

        CashHolder(View view) {
            super(view);
            paymentDateTv = view.findViewById(R.id.paymentDateTv);
            cashAmountTv = view.findViewById(R.id.cashAmountTv);
            cancelledll = view.findViewById(R.id.cancelledll);
            cancelImgv = view.findViewById(R.id.cancelImgv);
            cancelTv = view.findViewById(R.id.cancelTv);
            cancelDateTv = view.findViewById(R.id.cancelDateTv);
            trxSeqDescTv = view.findViewById(R.id.trxSeqDescTv);
        }

        void setData(Transaction transaction, int position) {
            this.transaction = transaction;
            this.position = position;

            setTransactionDate();
            setCashAmount();
            checkCancelViews();
            setTrxZnoFno();
        }

        private void setTrxZnoFno() {
            @SuppressLint("DefaultLocale") String desc = mContext.getResources().getString(R.string.z_no_upper)
                    .concat(" : ")
                    .concat(String.format("%06d", transaction.getzNum()))
                    .concat(" / ")
                    .concat(mContext.getResources().getString(R.string.f_no_upper))
                    .concat(" : ")
                    .concat(String.format("%06d", transaction.getfNum()));
            trxSeqDescTv.setText(desc);
        }

        private void setTransactionDate() {
            @SuppressLint("SimpleDateFormat")
            String trxDate = new SimpleDateFormat("dd MM yyyy HH:mm").format(transaction.getCreateDate());
            paymentDateTv.setText(trxDate);
        }

        private void setCashAmount() {
            String amountStr = CommonUtils.getDoubleStrValueForView(transaction.getTransactionAmount(), TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol());
            cashAmountTv.setText(amountStr);
        }

        private void checkCancelViews(){
            if(transaction.getTransactionType() == TransactionTypeEnum.SALE.getId())
                cancelledll.setVisibility(View.GONE);
            else{
                cancelledll.setVisibility(View.VISIBLE);
                setCancelledViews();
            }
        }

        private void setCancelledViews(){
            @SuppressLint("SimpleDateFormat")
            String trxDate = new SimpleDateFormat("dd MM yyyy HH:mm").format(transaction.getCancellationDate());
            cancelDateTv.setText(trxDate);

            if(transaction.getTransactionType() == TransactionTypeEnum.CANCEL.getId()){
                cancelTv.setText(mContext.getResources().getString(R.string.payment_cancelled_upper));
                Glide.with(mContext).load(R.drawable.ic_close_gray_24dp).into(cancelImgv);
                cancelImgv.setBackground(ShapeUtil.getShape(mContext.getResources().getColor(R.color.Red, null),
                        mContext.getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 20, 3));
            }
        }
    }

    public class CardHolder extends RecyclerView.ViewHolder {

        TextView paymentDateTv;
        TextView amountTv;
        TextView tipAmountTv;
        TextView cardAmountTv;
        TextView trxSeqDescTv;

        LinearLayout refundedll;
        ImageView refundImgv;
        TextView refundCancelTv;
        TextView refundDateTv;

        Transaction transaction;
        int position;

        CardHolder(View view) {
            super(view);
            paymentDateTv = view.findViewById(R.id.paymentDateTv);
            amountTv = view.findViewById(R.id.amountTv);
            tipAmountTv = view.findViewById(R.id.tipAmountTv);
            cardAmountTv = view.findViewById(R.id.cardAmountTv);
            refundedll = view.findViewById(R.id.refundedll);
            refundImgv = view.findViewById(R.id.refundImgv);
            refundCancelTv = view.findViewById(R.id.refundCancelTv);
            refundDateTv = view.findViewById(R.id.refundDateTv);
            trxSeqDescTv = view.findViewById(R.id.trxSeqDescTv);
        }

        void setData(Transaction transaction, int position) {
            this.transaction = transaction;
            this.position = position;

            setTransactionDate();
            setAmount();
            setTipAmount();
            setTotalAmount();
            checkCancelViews();
            setTrxZnoFno();
        }

        private void setTrxZnoFno() {
            @SuppressLint("DefaultLocale") String desc = mContext.getResources().getString(R.string.z_no_upper)
                    .concat(" : ")
                    .concat(String.format("%06d", transaction.getzNum()))
                    .concat(" / ")
                    .concat(mContext.getResources().getString(R.string.f_no_upper))
                    .concat(" : ")
                    .concat(String.format("%06d", transaction.getfNum()));
            trxSeqDescTv.setText(desc);
        }

        private void setTransactionDate() {
            @SuppressLint("SimpleDateFormat")
            String trxDate = new SimpleDateFormat("dd MM yyyy HH:mm").format(transaction.getCreateDate());
            paymentDateTv.setText(trxDate);
        }

        private void setAmount() {
            String amountStr = CommonUtils.getDoubleStrValueForView(transaction.getTransactionAmount(), TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol());
            amountTv.setText(amountStr);
        }

        private void setTipAmount() {
            String amountStr = CommonUtils.getDoubleStrValueForView(transaction.getTipAmount(), TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol());
            tipAmountTv.setText(amountStr);
        }

        private void setTotalAmount() {
            String amountStr = CommonUtils.getDoubleStrValueForView(transaction.getTotalAmount(), TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol());
            cardAmountTv.setText(amountStr);
        }

        private void checkCancelViews(){
            if(transaction.getTransactionType() == TransactionTypeEnum.SALE.getId())
                refundedll.setVisibility(View.GONE);
            else{
                refundedll.setVisibility(View.VISIBLE);
                setCancelledViews();
            }
        }

        private void setCancelledViews(){

            @SuppressLint("SimpleDateFormat")
            String trxDate = new SimpleDateFormat("dd MM yyyy HH:mm").format(transaction.getCancellationDate());
            refundDateTv.setText(trxDate);

            if(transaction.getTransactionType() == TransactionTypeEnum.CANCEL.getId()){
                refundCancelTv.setText(mContext.getResources().getString(R.string.payment_cancelled_upper));
                Glide.with(mContext).load(R.drawable.ic_close_gray_24dp).into(refundImgv);
                refundImgv.setBackground(ShapeUtil.getShape(mContext.getResources().getColor(R.color.Red, null),
                        mContext.getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 20, 3));
            }
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        Transaction transaction = transactions.get(position);

        if (holder instanceof CashHolder) {
            ((CashHolder) holder).setData(transaction, position);
        } else {
            ((CardHolder) holder).setData(transaction, position);
        }
    }

    @Override
    public int getItemCount() {
        if(transactions != null)
            return transactions.size();
        else
            return 0;
    }
}
