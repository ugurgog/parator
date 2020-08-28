package com.paypad.parator.charge.sale.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.parator.R;
import com.paypad.parator.charge.interfaces.ReturnSaleItemCallback;
import com.paypad.parator.enums.ItemProcessEnum;
import com.paypad.parator.model.OrderItem;
import com.paypad.parator.model.OrderItemDiscount;
import com.paypad.parator.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import static com.paypad.parator.constants.CustomConstants.TYPE_PRICE;


public class SaleListAdapter extends RecyclerView.Adapter<SaleListAdapter.SaleHolder> {

    private List<OrderItem> orderItems = new ArrayList<>();
    private ReturnSaleItemCallback returnSaleItemCallback;

    public SaleListAdapter(List<OrderItem> orderItems, ReturnSaleItemCallback callback) {
        this.orderItems.addAll(orderItems);
        this.returnSaleItemCallback = callback;
    }

    @NonNull
    @Override
    public SaleListAdapter.SaleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_sale_item, parent, false);
        return new SaleListAdapter.SaleHolder(itemView);
    }

    public class SaleHolder extends RecyclerView.ViewHolder {

        private CardView saleItemCv;
        private TextView saleNameTv;
        private TextView saleNoteTv;
        private ImageView discountImgv;
        private TextView saleAmountTv;

        private OrderItem orderItem;
        private int position;

        public SaleHolder(View view) {
            super(view);

            saleItemCv = view.findViewById(R.id.saleItemCv);
            saleNameTv = view.findViewById(R.id.saleNameTv);
            saleNoteTv = view.findViewById(R.id.saleNoteTv);
            discountImgv = view.findViewById(R.id.discountImgv);
            saleAmountTv = view.findViewById(R.id.saleAmountTv);

            saleItemCv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(orderItem.getId() != null && !orderItem.getId().isEmpty()){
                        //TODO - normal bir sale item
                    }else {
                        //TODO - discounts
                    }
                    returnSaleItemCallback.onReturn(orderItem, ItemProcessEnum.SELECTED);
                }
            });
        }

        public void setData(OrderItem orderItem, int position) {
            this.orderItem = orderItem;
            this.position = position;
            setSaleItemName();
            setSaleItemNoteInfo();
            setSaleItemDiscountImgv();
            setSaleItemAmount();
        }

        private void setSaleItemAmount() {
            if(orderItem != null ){
                String amountStr = "";
                if(orderItem.getId() == null || orderItem.getId().isEmpty()){ //Bu indirimlerdir
                    amountStr = amountStr.concat("- ").concat(CommonUtils.getDoubleStrValueForView(orderItem.getAmount(), TYPE_PRICE)).concat(" ").concat(CommonUtils.getCurrency().getSymbol());
                }else
                    amountStr = CommonUtils.getDoubleStrValueForView(orderItem.getAmount() * (double) orderItem.getQuantity(), TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol());
                saleAmountTv.setText(amountStr);
            }
        }

        private void setSaleItemDiscountImgv() {
            discountImgv.setVisibility(View.GONE);

            if(orderItem != null && orderItem.getDiscounts() != null){

                for(OrderItemDiscount orderItemDiscount : orderItem.getDiscounts()){
                    if(orderItemDiscount.getRate() > 0d){
                        discountImgv.setVisibility(View.VISIBLE);
                        break;
                    }
                }
            }
        }

        private void setSaleItemNoteInfo() {
            if(orderItem != null && orderItem.getNote() != null && !orderItem.getNote().isEmpty()){
                saleNoteTv.setVisibility(View.VISIBLE);
                saleNoteTv.setText(orderItem.getNote());
            }else
                saleNoteTv.setVisibility(View.GONE);
        }

        private void setSaleItemName() {
            if(orderItem != null && orderItem.getName() != null){
                if(orderItem.getQuantity() > 1){
                    saleNameTv.setText(orderItem.getName().concat(" X ").concat(String.valueOf(orderItem.getQuantity())));
                }else
                    saleNameTv.setText(orderItem.getName());
            }

        }
    }

    @Override
    public void onBindViewHolder(final SaleListAdapter.SaleHolder holder, final int position) {
        OrderItem orderItem = orderItems.get(position);
        holder.setData(orderItem, position);
    }

    @Override
    public int getItemCount() {
        if(orderItems != null)
            return orderItems.size();
        else
            return 0;
    }
}