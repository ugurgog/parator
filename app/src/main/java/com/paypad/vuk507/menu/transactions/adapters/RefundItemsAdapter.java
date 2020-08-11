package com.paypad.vuk507.menu.transactions.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.R;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.interfaces.ReturnOrderItemCallback;
import com.paypad.vuk507.model.OrderRefundItem;
import com.paypad.vuk507.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

public class RefundItemsAdapter extends RecyclerView.Adapter<RefundItemsAdapter.RefundItemHolder> {

    private Context context;
    private List<OrderRefundItem> orderRefundItems = new ArrayList<>();
    private ReturnOrderItemCallback returnOrderItemCallback;

    public RefundItemsAdapter(Context context, List<OrderRefundItem> orderRefundItems,
                              ReturnOrderItemCallback returnOrderItemCallback) {
        this.context = context;
        this.orderRefundItems.addAll(orderRefundItems);
        this.returnOrderItemCallback = returnOrderItemCallback;
    }

    @NonNull
    @Override
    public RefundItemsAdapter.RefundItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_refund_item, parent, false);
        return new RefundItemsAdapter.RefundItemHolder(itemView);
    }

    public class RefundItemHolder extends RecyclerView.ViewHolder {

        private CardView saleItemCv;
        private TextView itemNameTv;
        private TextView itemAmountTv;
        private CheckBox checkb;

        int position;
        private OrderRefundItem orderRefundItem;

        public RefundItemHolder(View view) {
            super(view);

            itemNameTv = view.findViewById(R.id.itemNameTv);
            itemAmountTv = view.findViewById(R.id.itemAmountTv);
            checkb = view.findViewById(R.id.checkb);
            saleItemCv = view.findViewById(R.id.saleItemCv);

            saleItemCv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(checkb.isChecked())
                        checkb.setChecked(false);
                    else
                        checkb.setChecked(true);

                    if(checkb.isChecked())
                        returnOrderItemCallback.onReturn(orderRefundItem, ItemProcessEnum.SELECTED);
                    else
                        returnOrderItemCallback.onReturn(orderRefundItem, ItemProcessEnum.UNSELECTED);
                }
            });

            checkb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(checkb.isChecked())
                        returnOrderItemCallback.onReturn(orderRefundItem, ItemProcessEnum.SELECTED);
                    else
                        returnOrderItemCallback.onReturn(orderRefundItem, ItemProcessEnum.UNSELECTED);
                }
            });
        }

        public void setData(OrderRefundItem orderRefundItem, int position) {
            this.orderRefundItem = orderRefundItem;
            this.position = position;

            setItemName();
            setItemAmount();
            itemAmountTv.setText(CommonUtils.getAmountTextWithCurrency(orderRefundItem.getAmount()));
        }

        private void setItemName(){
            if(orderRefundItem.getQuantity() > 1){
                itemNameTv.setText(String.valueOf(orderRefundItem.getQuantity()).concat(" X ").concat(orderRefundItem.getName()));
            }else
                itemNameTv.setText(orderRefundItem.getName());
        }

        private void setItemAmount(){
            itemAmountTv.setText(CommonUtils.getAmountTextWithCurrency(orderRefundItem.getAmount() * orderRefundItem.getQuantity()));
        }
    }

    @Override
    public void onBindViewHolder(final RefundItemsAdapter.RefundItemHolder holder, final int position) {
        OrderRefundItem orderRefundItem = orderRefundItems.get(position);
        holder.setData(orderRefundItem, position);
    }

    @Override
    public int getItemCount() {
        if(orderRefundItems != null)
            return orderRefundItems.size();
        else
            return 0;
    }
}