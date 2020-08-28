package com.paypad.parator.menu.transactions.adapters;

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
import com.paypad.parator.db.RefundDBHelper;
import com.paypad.parator.enums.PaymentTypeEnum;
import com.paypad.parator.enums.TransactionTypeEnum;
import com.paypad.parator.menu.transactions.interfaces.ReturnTransactionCallback;
import com.paypad.parator.model.Refund;
import com.paypad.parator.model.Transaction;
import com.paypad.parator.model.pojo.BaseResponse;
import com.paypad.parator.utils.CommonUtils;
import com.paypad.parator.utils.DataUtils;
import com.paypad.parator.utils.ShapeUtil;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;

import static com.paypad.parator.constants.CustomConstants.LANGUAGE_TR;
import static com.paypad.parator.constants.CustomConstants.TYPE_PRICE;

public class PaymentListForCancelAdapter extends RecyclerView.Adapter<PaymentListForCancelAdapter.TransactionHolder> {

    private Context context;
    private List<Transaction> transactions = new ArrayList<>();
    private ReturnTransactionCallback returnTransactionCallback;

    public PaymentListForCancelAdapter(Context context, List<Transaction> transactions) {
        this.context = context;
        this.transactions.addAll(transactions);
    }

    public void setReturnTransactionCallback(ReturnTransactionCallback returnTransactionCallback) {
        this.returnTransactionCallback = returnTransactionCallback;
    }

    @NonNull
    @Override
    public PaymentListForCancelAdapter.TransactionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_item_transaction_cancel, parent, false);
        return new PaymentListForCancelAdapter.TransactionHolder(itemView);
    }

    public class TransactionHolder extends RecyclerView.ViewHolder {

        private CardView transactionCv;
        private ImageView paymentIconImgv;
        private TextView paymentTypeTv;
        private TextView amountTv;
        private ImageView cancelImgv;

        int position;
        private Transaction transaction;
        private double refundAmount = 0d;

        public TransactionHolder(View view) {
            super(view);

            transactionCv = view.findViewById(R.id.transactionCv);
            paymentIconImgv = view.findViewById(R.id.paymentIconImgv);
            paymentTypeTv = view.findViewById(R.id.paymentTypeTv);
            amountTv = view.findViewById(R.id.amountTv);
            cancelImgv = view.findViewById(R.id.cancelImgv);

            transactionCv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    BaseResponse baseResponse = getRefundDescriptionResult();

                    if(!baseResponse.isSuccess()){
                        DataUtils.showBaseResponseMessage(context, baseResponse);
                        return;
                    }

                    returnTransactionCallback.OnTransactionReturn(transaction, refundAmount);
                }
            });
        }

        public void setData(Transaction transaction, int position) {
            this.transaction = transaction;
            this.position = position;

            setIconAndPaymentTypeText();
            setCancelIcon();
            setAmountTv();
            getRefundDescriptionResult();
        }

        private void setAmountTv() {
            double amount = CommonUtils.round(transaction.getTotalAmount(), 2);
            String amountStr = CommonUtils.getDoubleStrValueForView(amount, TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol());
            amountTv.setText(amountStr);
        }

        private void setIconAndPaymentTypeText() {
            if(transaction.getPaymentTypeId() == PaymentTypeEnum.CASH.getId()){
                Glide.with(context)
                        .load(R.drawable.icon_payment_cash_type)
                        .into(paymentIconImgv);
                paymentTypeTv.setText(context.getResources().getString(R.string.cash));
            }else if(transaction.getPaymentTypeId() == PaymentTypeEnum.CREDIT_CARD.getId()){
                Glide.with(context)
                        .load(R.drawable.icon_payment_card_type)
                        .into(paymentIconImgv);
                paymentTypeTv.setText(context.getResources().getString(R.string.card));
            }else {
                //TODO - Diger odeme tipleri icin burada aksiyon alinacak
            }
        }

        private void setCancelIcon(){
            RealmResults<Refund> refunds = RefundDBHelper.getAllRefundsOfTransaction(transaction.getId(), true);

            cancelImgv.setVisibility(View.VISIBLE);

            if(transaction.getTransactionType() == TransactionTypeEnum.CANCEL.getId()){
                Glide.with(context).load(R.drawable.ic_close_gray_24dp).into(cancelImgv);

                cancelImgv.setBackground(ShapeUtil.getShape(context.getResources().getColor(R.color.Red, null),
                        context.getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 20, 3));
            }else {

                if(refunds != null && refunds.size() > 0){
                    Glide.with(context).load(R.drawable.ic_arrow_back_white_24dp).into(cancelImgv);

                    cancelImgv.setBackground(ShapeUtil.getShape(context.getResources().getColor(R.color.Orange, null),
                            context.getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 20, 3));
                }else {
                    cancelImgv.setVisibility(View.GONE);
                }
            }
        }

        private BaseResponse getRefundDescriptionResult(){
            RealmResults<Refund> refunds = RefundDBHelper.getAllRefundsOfTransaction(transaction.getId(), true);

            BaseResponse baseResponse = new BaseResponse();
            baseResponse.setSuccess(true);

            refundAmount = 0d;

            if(transaction.getTransactionType() == TransactionTypeEnum.CANCEL.getId()){
                //refundDescTv.setText(context.getResources().getString(R.string.transaction_cancelled_desc));
                baseResponse.setSuccess(false);
                baseResponse.setMessage(context.getResources().getString(R.string.transaction_cancelled_desc));
            } else {

                double totalRefundAmount = 0d;

                if (refunds != null && refunds.size() > 0) {
                    for (Refund refund : refunds) {
                        totalRefundAmount = CommonUtils.round(totalRefundAmount + refund.getRefundAmount(), 2);
                    }
                }

                if (totalRefundAmount >= transaction.getTotalAmount()) {
                    //refundDescTv.setText(context.getResources().getString(R.string.transaction_fully_refunded));
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage(context.getResources().getString(R.string.transaction_fully_refunded));
                } else {
                    String refundDesc;
                    refundAmount = CommonUtils.round(transaction.getTotalAmount() - totalRefundAmount, 2);

                    if (CommonUtils.getLanguage().equals(LANGUAGE_TR)) {
                        refundDesc = "Yapılabilecek maximum iade veya iptal tutarı ".concat(CommonUtils.getAmountTextWithCurrency(refundAmount));
                    } else {
                        refundDesc = "Max ".concat(CommonUtils.getAmountTextWithCurrency(refundAmount))
                                .concat(" available for refund/cancel");
                    }
                    //refundDescTv.setText(refundDesc);
                }
            }
            return baseResponse;
        }
    }

    @Override
    public void onBindViewHolder(final PaymentListForCancelAdapter.TransactionHolder holder, final int position) {
        Transaction transaction = transactions.get(position);
        holder.setData(transaction, position);
    }

    @Override
    public int getItemCount() {
        if(transactions != null)
            return transactions.size();
        else
            return 0;
    }
}