package com.paypad.parator.menu.transactions.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.paypad.parator.R;
import com.paypad.parator.db.TransactionDBHelper;
import com.paypad.parator.enums.PaymentTypeEnum;
import com.paypad.parator.model.Refund;
import com.paypad.parator.model.Transaction;
import com.paypad.parator.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import static com.paypad.parator.constants.CustomConstants.TYPE_PRICE;

public class RefundedPaymentsAdapter extends RecyclerView.Adapter<RefundedPaymentsAdapter.SaleItemHolder> {

    private Context context;
    private List<Refund> refunds = new ArrayList<>();

    public RefundedPaymentsAdapter(Context context, List<Refund> refunds) {
        this.context = context;
        this.refunds.addAll(refunds);
    }

    @NonNull
    @Override
    public RefundedPaymentsAdapter.SaleItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_paym_refund_list, parent, false);
        return new RefundedPaymentsAdapter.SaleItemHolder(itemView);
    }

    public class SaleItemHolder extends RecyclerView.ViewHolder {

        private ImageView paymentTypeImgv;
        private TextView paymentTypeTv;
        private TextView paymTypeAmountTv;

        int position;
        private Refund refund;

        public SaleItemHolder(View view) {
            super(view);

            paymentTypeImgv = view.findViewById(R.id.paymentTypeImgv);
            paymentTypeTv = view.findViewById(R.id.paymentTypeTv);
            paymTypeAmountTv = view.findViewById(R.id.paymTypeAmountTv);
        }

        public void setData(Refund refund, int position) {
            this.refund = refund;
            this.position = position;
            setItemName();
            setItemAmount();
        }

        private void setItemName(){
            Transaction transaction = TransactionDBHelper.getTransactionById(refund.getTransactionId());

            if(transaction.getPaymentTypeId() == PaymentTypeEnum.CASH.getId()){
                Glide.with(context).load(R.drawable.icon_payment_cash_type).into(paymentTypeImgv);
                paymentTypeTv.setText(context.getResources().getString(R.string.cash));
            } else if(transaction.getPaymentTypeId() == PaymentTypeEnum.CREDIT_CARD.getId()) {
                Glide.with(context).load(R.drawable.icon_payment_card_type).into(paymentTypeImgv);
                paymentTypeTv.setText(context.getResources().getString(R.string.card));
            }
        }

        private void setItemAmount(){
            double itemAmount = CommonUtils.round(refund.getRefundAmount() , 2);
            String amountStr = CommonUtils.getDoubleStrValueForView(itemAmount, TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol());
            paymTypeAmountTv.setText(amountStr);
        }
    }

    @Override
    public void onBindViewHolder(final RefundedPaymentsAdapter.SaleItemHolder holder, final int position) {
        Refund refund = refunds.get(position);
        holder.setData(refund, position);
    }

    @Override
    public int getItemCount() {
        if(refunds != null)
            return refunds.size();
        else
            return 0;
    }
}