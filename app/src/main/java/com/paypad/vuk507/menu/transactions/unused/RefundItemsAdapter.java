package com.paypad.vuk507.menu.transactions.unused;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.R;

import java.util.ArrayList;
import java.util.List;

public class RefundItemsAdapter extends RecyclerView.Adapter<RefundItemsAdapter.RefundItemHolder> {

    private Context context;
    private List<RefundItem> refundItems = new ArrayList<>();

    public RefundItemsAdapter(Context context, List<RefundItem> refundItems) {
        this.context = context;
        this.refundItems.addAll(refundItems);
    }

    @NonNull
    @Override
    public RefundItemsAdapter.RefundItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_not_refunded_item, parent, false);
        return new RefundItemsAdapter.RefundItemHolder(itemView);
    }

    public class RefundItemHolder extends RecyclerView.ViewHolder {

        private TextView itemNameTv;
        private TextView itemAmountTv;

        int position;
        private RefundItem refundItem;

        public RefundItemHolder(View view) {
            super(view);

            itemNameTv = view.findViewById(R.id.itemNameTv);
            itemAmountTv = view.findViewById(R.id.itemAmountTv);
        }

        public void setData(RefundItem refundItem, int position) {
            this.refundItem = refundItem;
            this.position = position;
        }
    }

    @Override
    public void onBindViewHolder(final RefundItemsAdapter.RefundItemHolder holder, final int position) {
        RefundItem refundItem = refundItems.get(position);
        holder.setData(refundItem, position);
    }

    @Override
    public int getItemCount() {
        if(refundItems != null)
            return refundItems.size();
        else
            return 0;
    }
}