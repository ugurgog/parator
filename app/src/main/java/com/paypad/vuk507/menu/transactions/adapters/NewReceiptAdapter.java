package com.paypad.vuk507.menu.transactions.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.paypad.vuk507.R;
import com.paypad.vuk507.enums.PaymentTypeEnum;
import com.paypad.vuk507.menu.transactions.interfaces.ReturnTransactionCallback;
import com.paypad.vuk507.model.Transaction;
import com.paypad.vuk507.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import static com.paypad.vuk507.constants.CustomConstants.TYPE_PRICE;


public class NewReceiptAdapter extends RecyclerView.Adapter<NewReceiptAdapter.ReceiptHolder> {

    private Context context;
    private List<Transaction> transactions = new ArrayList<>();
    private ReturnTransactionCallback returnTransactionCallback;

    public NewReceiptAdapter(Context context, List<Transaction> transactions) {
        this.context = context;
        this.transactions.addAll(transactions);
    }

    public void setReturnTransactionCallback(ReturnTransactionCallback returnTransactionCallback) {
        this.returnTransactionCallback = returnTransactionCallback;
    }

    @NonNull
    @Override
    public NewReceiptAdapter.ReceiptHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_item_new_receipt, parent, false);
        return new NewReceiptAdapter.ReceiptHolder(itemView);
    }

    public class ReceiptHolder extends RecyclerView.ViewHolder {

        private CardView transactionCv;
        private ImageView paymentIconImgv;
        private TextView paymentTypeTv;
        private TextView amountTv;

        int position;
        private Transaction transaction;

        public ReceiptHolder(View view) {
            super(view);

            transactionCv = view.findViewById(R.id.transactionCv);
            paymentIconImgv = view.findViewById(R.id.paymentIconImgv);
            paymentTypeTv = view.findViewById(R.id.paymentTypeTv);
            amountTv = view.findViewById(R.id.amountTv);

            transactionCv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    returnTransactionCallback.OnTransactionReturn(transaction, 0d);
                }
            });
        }

        public void setData(Transaction transaction, int position) {
            this.transaction = transaction;
            this.position = position;

            setIconAndPaymentTypeText();
            setAmountTv();
        }

        private void setAmountTv() {
            double amount = CommonUtils.round(transaction.getTransactionAmount() + transaction.getTipAmount(), 2);
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
    }

    @Override
    public void onBindViewHolder(final NewReceiptAdapter.ReceiptHolder holder, final int position) {
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