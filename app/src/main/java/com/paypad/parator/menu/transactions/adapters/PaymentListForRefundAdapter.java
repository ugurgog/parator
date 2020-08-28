package com.paypad.parator.menu.transactions.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.paypad.parator.R;
import com.paypad.parator.db.RefundDBHelper;
import com.paypad.parator.enums.PaymentTypeEnum;
import com.paypad.parator.enums.TransactionTypeEnum;
import com.paypad.parator.menu.transactions.interfaces.RefundableTrxModelCallback;
import com.paypad.parator.menu.transactions.model.RefundableTrxModel;
import com.paypad.parator.model.Refund;
import com.paypad.parator.model.pojo.BaseResponse;
import com.paypad.parator.utils.CommonUtils;
import com.paypad.parator.utils.DataUtils;
import com.paypad.parator.utils.NumberFormatWatcher;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;

import static com.paypad.parator.constants.CustomConstants.LANGUAGE_TR;
import static com.paypad.parator.constants.CustomConstants.TYPE_PRICE;

public class PaymentListForRefundAdapter extends RecyclerView.Adapter<PaymentListForRefundAdapter.TransactionHolder> {

    private Context context;
    private List<RefundableTrxModel> refundableTrxModels = new ArrayList<>();
    private RefundableTrxModelCallback refundableTrxModelCallback;
    private double availableRefundAmount;
    private double totalRefundAmount = 0d;

    public PaymentListForRefundAdapter(Context context, List<RefundableTrxModel> refundableTrxModels, double availableRefundAmount) {
        this.context = context;
        this.refundableTrxModels.addAll(refundableTrxModels);
        this.availableRefundAmount = availableRefundAmount;
    }

    public void setRefundableTrxModelCallback(RefundableTrxModelCallback refundableTrxModelCallback) {
        this.refundableTrxModelCallback = refundableTrxModelCallback;
    }

    @NonNull
    @Override
    public PaymentListForRefundAdapter.TransactionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_item_transaction_refund, parent, false);
        return new PaymentListForRefundAdapter.TransactionHolder(itemView);
    }

    public class TransactionHolder extends RecyclerView.ViewHolder {

        private ImageView paymentIconImgv;
        private TextView paymentTypeTv;
        private TextView refundDescTv;
        private EditText amountEt;

        int position;
        private RefundableTrxModel refundableTrxModel;
        private TextWatcher textWatcher;

        public TransactionHolder(View view) {
            super(view);

            paymentIconImgv = view.findViewById(R.id.paymentIconImgv);
            paymentTypeTv = view.findViewById(R.id.paymentTypeTv);
            refundDescTv = view.findViewById(R.id.refundDescTv);
            amountEt = view.findViewById(R.id.amountEt);
        }

        public void setData(RefundableTrxModel refundableTrxModel, int position) {
            this.refundableTrxModel = refundableTrxModel;
            this.position = position;

            setIconAndPaymentTypeText();
            getRefundDescriptionResult();

            amountEt.setHint("0.00 ".concat(CommonUtils.getCurrency().getSymbol()));
            amountEt.addTextChangedListener(new NumberFormatWatcher(amountEt, TYPE_PRICE, refundableTrxModel.getMaxRefundAmount()));

            amountEt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable != null && !editable.toString().trim().isEmpty()) {
                        double refundAmount = DataUtils.getDoubleValueFromFormattedString(amountEt.getText().toString());

                        refundableTrxModel.setRefundAmount(refundAmount);

                        double amount = 0d;
                        for(RefundableTrxModel refundableTrxModel1 : refundableTrxModels)
                            amount = CommonUtils.round(amount + refundableTrxModel1.getRefundAmount(), 2);

                        if(amount > availableRefundAmount){

                            double amountX = 0d;
                            for(RefundableTrxModel refundableTrxModel1 : refundableTrxModels){
                                if(!refundableTrxModel1.getTransaction().getId().equals(refundableTrxModel.getTransaction().getId()))
                                    amountX = CommonUtils.round(amountX + refundableTrxModel1.getRefundAmount(), 2);
                            }

                            refundAmount = CommonUtils.round(availableRefundAmount - amountX, 2);
                            refundableTrxModel.setRefundAmount(refundAmount);
                            amountEt.setText(CommonUtils.getAmountText(refundAmount));
                        }

                        refundableTrxModelCallback.OnReturnTrxList(refundableTrxModels);
                    }
                }
            });
        }

        private void setIconAndPaymentTypeText() {
            if(refundableTrxModel.getTransaction().getPaymentTypeId() == PaymentTypeEnum.CASH.getId()){
                Glide.with(context)
                        .load(R.drawable.icon_payment_cash_type)
                        .into(paymentIconImgv);
                paymentTypeTv.setText(context.getResources().getString(R.string.cash));
            }else if(refundableTrxModel.getTransaction().getPaymentTypeId() == PaymentTypeEnum.CREDIT_CARD.getId()){
                Glide.with(context)
                        .load(R.drawable.icon_payment_card_type)
                        .into(paymentIconImgv);
                paymentTypeTv.setText(context.getResources().getString(R.string.card));
            }else {
                //TODO - Diger odeme tipleri icin burada aksiyon alinacak
            }
        }

        private BaseResponse getRefundDescriptionResult(){
            RealmResults<Refund> refunds = RefundDBHelper.getAllRefundsOfTransaction(refundableTrxModel.getTransaction().getId(), true);

            BaseResponse baseResponse = new BaseResponse();
            baseResponse.setSuccess(true);

            double refundAmount = 0d;

            if(refundableTrxModel.getTransaction().getTransactionType() == TransactionTypeEnum.CANCEL.getId()){
                refundDescTv.setText(context.getResources().getString(R.string.transaction_cancelled_desc));
                baseResponse.setSuccess(false);
                baseResponse.setMessage(context.getResources().getString(R.string.transaction_cancelled_desc));
            } else {

                double totalRefundAmount = 0d;

                if (refunds != null && refunds.size() > 0) {
                    for (Refund refund : refunds) {
                        totalRefundAmount = CommonUtils.round(totalRefundAmount + refund.getRefundAmount(), 2);
                    }
                }

                if (totalRefundAmount >= refundableTrxModel.getTransaction().getTotalAmount()) {
                    refundDescTv.setText(context.getResources().getString(R.string.transaction_fully_refunded));
                    baseResponse.setSuccess(false);
                    baseResponse.setMessage(context.getResources().getString(R.string.transaction_fully_refunded));
                } else {
                    String refundDesc;

                    if (CommonUtils.getLanguage().equals(LANGUAGE_TR)) {
                        refundDesc = "Yapılabilecek maximum iade tutarı ".concat(CommonUtils.getAmountTextWithCurrency(refundableTrxModel.getMaxRefundAmount()));
                    } else {
                        refundDesc = "Max ".concat(CommonUtils.getAmountTextWithCurrency(refundableTrxModel.getMaxRefundAmount()))
                                .concat(" available for refund");
                    }
                    refundDescTv.setText(refundDesc);
                }
            }
            return baseResponse;
        }


    }

    @Override
    public void onBindViewHolder(final PaymentListForRefundAdapter.TransactionHolder holder, final int position) {
        RefundableTrxModel refundableTrxModel = refundableTrxModels.get(position);
        holder.setData(refundableTrxModel, position);
    }

    @Override
    public int getItemCount() {
        if(refundableTrxModels != null)
            return refundableTrxModels.size();
        else
            return 0;
    }
}