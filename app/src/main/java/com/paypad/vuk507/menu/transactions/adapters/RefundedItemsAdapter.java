package com.paypad.vuk507.menu.transactions.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.R;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.interfaces.ReturnOrderItemCallback;
import com.paypad.vuk507.model.OrderRefundItem;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.ShapeUtil;

import java.util.ArrayList;
import java.util.List;

public class RefundedItemsAdapter extends RecyclerView.Adapter<RefundedItemsAdapter.RefundItemHolder> {

    private Context context;
    private List<OrderRefundItem> orderRefundItems = new ArrayList<>();

    public RefundedItemsAdapter(Context context, List<OrderRefundItem> orderRefundItems) {
        this.context = context;
        this.orderRefundItems.addAll(orderRefundItems);
    }

    @NonNull
    @Override
    public RefundedItemsAdapter.RefundItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_refunded_item, parent, false);
        return new RefundedItemsAdapter.RefundItemHolder(itemView);
    }

    public class RefundItemHolder extends RecyclerView.ViewHolder {

        private CardView saleItemCv;
        private TextView itemNameTv;
        private TextView itemAmountTv;
        private ImageView refundImgv;

        int position;
        private OrderRefundItem orderRefundItem;

        public RefundItemHolder(View view) {
            super(view);

            itemNameTv = view.findViewById(R.id.itemNameTv);
            itemAmountTv = view.findViewById(R.id.itemAmountTv);
            refundImgv = view.findViewById(R.id.refundImgv);
            saleItemCv = view.findViewById(R.id.saleItemCv);
        }

        public void setData(OrderRefundItem orderRefundItem, int position) {
            this.orderRefundItem = orderRefundItem;
            this.position = position;

            setItemName();
            setItemAmount();
            itemAmountTv.setText(CommonUtils.getAmountTextWithCurrency(orderRefundItem.getAmount()));

            refundImgv.setBackground(ShapeUtil.getShape(context.getResources().getColor(R.color.White, null),
                    context.getResources().getColor(R.color.Black, null), GradientDrawable.OVAL, 20, 3));
        }

        private void setItemName(){
            itemNameTv.setText(orderRefundItem.getName());
        }

        private void setItemAmount(){
            itemAmountTv.setText(CommonUtils.getAmountTextWithCurrency(orderRefundItem.getAmount()));
        }
    }

    @Override
    public void onBindViewHolder(final RefundedItemsAdapter.RefundItemHolder holder, final int position) {
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