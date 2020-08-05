package com.paypad.vuk507.menu.transactions.adapters;

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
import com.paypad.vuk507.R;
import com.paypad.vuk507.charge.order.OrderManager;
import com.paypad.vuk507.enums.PaymentTypeEnum;
import com.paypad.vuk507.enums.TransactionTypeEnum;
import com.paypad.vuk507.interfaces.ReturnSizeCallback;
import com.paypad.vuk507.menu.transactions.TransactionsFragment;
import com.paypad.vuk507.model.SaleItem;
import com.paypad.vuk507.model.Transaction;
import com.paypad.vuk507.model.pojo.SaleModel;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.DataUtils;
import com.paypad.vuk507.utils.ShapeUtil;

import java.util.ArrayList;
import java.util.List;

import static com.paypad.vuk507.constants.CustomConstants.TYPE_PRICE;

public class SaleModelListAdapter extends RecyclerView.Adapter {

    private List<TransactionsFragment.TransactionItem> transactionItems = new ArrayList<>();
    private List<TransactionsFragment.TransactionItem> orgTransactionItems = new ArrayList<>();

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

    public SaleModelListAdapter(Context context, List<TransactionsFragment.TransactionItem> transactionItems) {
        this.transactionItems.addAll(transactionItems);
        this.orgTransactionItems.addAll(transactionItems);
        mContext = context;
    }

    public void setReturnSaleModelCallback(ReturnSaleModelCallback returnSaleModelCallback) {
        this.returnSaleModelCallback = returnSaleModelCallback;
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

            transactionCv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    returnSaleModelCallback.OnReturnSaleModel(transactionItem.getSaleModel());
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
        }

        private void setSaleItemsDesc() {

            if(transactionItem.getTransaction() == null){
                saleItemDescTv.setVisibility(View.VISIBLE);
                String desc = "";
                for(SaleItem saleItem: transactionItem.getSaleModel().getSaleItems()){

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
            }else
                saleItemDescTv.setVisibility(View.GONE);
        }

        private void setSaleAmount() {
            double amount;

            if(transactionItem.getTransaction() != null)
                amount = CommonUtils.round((transactionItem.getTransaction().getTotalAmount()), 2);
            else
                amount = CommonUtils.round((transactionItem.getSaleModel().getSale().getSubTotalAmount()
                        + OrderManager.getTotalTipAmountOfSale(transactionItem.getSaleModel())), 2);

            String amountStr = (transactionItem.getTransaction() != null ? "- " : "") +
                    CommonUtils.getDoubleStrValueForView(amount, TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol());
            amountTv.setText(amountStr);
        }

        private void setSaleHour() {
            hourTv.setText(DataUtils.getHourOfOrder(transactionItem.getTrxDate()));
        }

        private void setIcon() {
            setIconForSaleModel();

            if(transactionItem.getTransaction() == null){
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
            if(transactionItem.getTransaction().getTransactionType() == TransactionTypeEnum.REFUND.getId()){

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
