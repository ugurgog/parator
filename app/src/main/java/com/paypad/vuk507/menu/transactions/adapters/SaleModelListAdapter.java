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
import com.paypad.vuk507.charge.order.OrderManager;
import com.paypad.vuk507.enums.PaymentTypeEnum;
import com.paypad.vuk507.interfaces.ReturnSizeCallback;
import com.paypad.vuk507.model.SaleItem;
import com.paypad.vuk507.model.Transaction;
import com.paypad.vuk507.model.pojo.SaleModel;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.DataUtils;

import java.util.ArrayList;
import java.util.List;

import static com.paypad.vuk507.constants.CustomConstants.TYPE_PRICE;

public class SaleModelListAdapter extends RecyclerView.Adapter {

    private List<SaleModel> saleModels = new ArrayList<>();
    private List<SaleModel> orgSaleModels = new ArrayList<>();

    private Context mContext;
    private ReturnSaleModelCallback returnSaleModelCallback;

    private static final int VIEW_ITEM = 0;
    private static final int VIEW_NONE = 1;

    private static final int PAYMENT_CARD = 0;
    private static final int PAYMENT_CASH = 1;
    private static final int PAYMENT_MULTI_CARD = 2;
    private static final int PAYMENT_MULTI_CASH = 3;

    public interface ReturnSaleModelCallback{
        void OnReturnSaleModel(SaleModel saleModel);
    }

    public SaleModelListAdapter(Context context, List<SaleModel> saleModels) {
        this.saleModels.addAll(saleModels);
        this.orgSaleModels.addAll(saleModels);
        mContext = context;
    }

    public void setReturnSaleModelCallback(ReturnSaleModelCallback returnSaleModelCallback) {
        this.returnSaleModelCallback = returnSaleModelCallback;
    }

    @Override
    public int getItemViewType(int position) {
        if(saleModels.get(position).getSale().getSaleUuid() != null)
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
        TextView amountTv;
        TextView saleItemDescTv;
        TextView hourTv;

        SaleModel saleModel;
        int position;

        TransactionHolder(View view) {
            super(view);

            transactionCv = view.findViewById(R.id.transactionCv);
            paymentIconImgv = view.findViewById(R.id.paymentIconImgv);
            amountTv = view.findViewById(R.id.amountTv);
            saleItemDescTv = view.findViewById(R.id.saleItemDescTv);
            hourTv = view.findViewById(R.id.hourTv);

            transactionCv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    returnSaleModelCallback.OnReturnSaleModel(saleModel);
                }
            });
        }

        void setData(SaleModel saleModel, int position) {
            this.saleModel = saleModel;
            this.position = position;

            setSaleAmount();
            setSaleItemsDesc();
            setSaleHour();
            setIcon();
        }

        private void setSaleItemsDesc() {

            String desc = "";
            for(SaleItem saleItem: saleModel.getSaleItems()){

                if(saleItem.getQuantity() == 1)
                    desc = desc.concat(saleItem.getName()).concat(", ");
                else
                    desc = desc.concat(saleItem.getName().concat(" x ").concat(String.valueOf(saleItem.getQuantity()))).concat(", ");

                if(desc.length() > 40)
                    break;
            }

            String desc1 = desc.substring(0, desc.length() - 2);

            if(desc1.length() > 40)
                desc1 = desc1.substring(0, 40).concat("...");

            saleItemDescTv.setText(desc1);
        }

        private void setSaleAmount() {
            double amount = CommonUtils.round((saleModel.getSale().getSubTotalAmount() + OrderManager.getTotalTipAmountOfSale(saleModel)), 2);
            String amountStr = CommonUtils.getDoubleStrValueForView(amount, TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol());
            amountTv.setText(amountStr);
        }

        private void setSaleHour() {
            hourTv.setText(DataUtils.getHourOfOrder(saleModel.getSale().getCreateDate()));
        }

        private void setIcon() {
            long paymentType = 0;

            if(saleModel.getTransactions() != null){
                if(saleModel.getTransactions().size() == 1){
                    Transaction transaction = saleModel.getTransactions().get(0);

                    if(transaction.getPaymentTypeId() == PaymentTypeEnum.CASH.getId())
                        paymentType = PAYMENT_CASH;
                    else  if(transaction.getPaymentTypeId() == PaymentTypeEnum.CREDIT_CARD.getId())
                        paymentType = PAYMENT_CARD;
                }else {
                    paymentType = PAYMENT_MULTI_CARD;
                    for(Transaction transaction : saleModel.getTransactions()){
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
    }

    public class NoTransactionHolder extends RecyclerView.ViewHolder {

        private TextView titleTv;

        NoTransactionHolder(View view) {
            super(view);
            titleTv = view.findViewById(R.id.titleTv);
        }

        void setData(SaleModel saleModel, int position) {
            titleTv.setText(DataUtils.getTransactionFullDateName(saleModel.getSale().getCreateDate()));
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        SaleModel saleModel = saleModels.get(position);

        if (holder instanceof TransactionHolder) {
            ((TransactionHolder) holder).setData(saleModel, position);
        } else {
            ((NoTransactionHolder) holder).setData(saleModel, position);
        }
    }

    @Override
    public int getItemCount() {
        if(saleModels != null)
            return saleModels.size();
        else
            return 0;
    }

    public void updateAdapter(String searchText, ReturnSizeCallback returnSizeCallback) {
        /*if (searchText.trim().isEmpty()){
            taxModels = orgTaxModels;
        } else {

            List<TaxModel> tempTaxList = new ArrayList<>();
            for (TaxModel tax : orgTaxModels) {
                if (tax.getName() != null &&
                        tax.getName().toLowerCase().contains(searchText.toLowerCase()))
                    tempTaxList.add(tax);
            }
            taxModels = tempTaxList;
        }

        this.notifyDataSetChanged();

        if (taxModels != null)
            returnSizeCallback.OnReturn(taxModels.size());
        else
            returnSizeCallback.OnReturn(0);*/
    }
}
