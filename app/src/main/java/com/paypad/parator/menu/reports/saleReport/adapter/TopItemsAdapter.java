package com.paypad.parator.menu.reports.saleReport.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.parator.R;
import com.paypad.parator.model.pojo.ReportOrderItem;
import com.paypad.parator.utils.CommonUtils;

import java.util.List;

public class TopItemsAdapter extends RecyclerView.Adapter<TopItemsAdapter.ItemHolder> {

    private List<ReportOrderItem> reportOrderItems;
    private boolean isGrossSelected;

    public TopItemsAdapter(List<ReportOrderItem> reportOrderItems, boolean isGrossSelected) {
        this.reportOrderItems = reportOrderItems;
        this.isGrossSelected = isGrossSelected;
    }

    @NonNull
    @Override
    public TopItemsAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_custom_with_name_and_value, parent, false);
        return new TopItemsAdapter.ItemHolder(itemView);
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        private TextView nameTv;
        private TextView valueTv;

        private ReportOrderItem reportOrderItem;
        private int position;

        ItemHolder(View view) {
            super(view);
            nameTv = view.findViewById(R.id.nameTv);
            valueTv = view.findViewById(R.id.valueTv);
        }

        public void setData(ReportOrderItem reportOrderItem , int position) {
            this.reportOrderItem = reportOrderItem;
            this.position = position;
            nameTv.setText(reportOrderItem.getItemName());

            if(isGrossSelected)
                valueTv.setText(CommonUtils.getAmountTextWithCurrency(reportOrderItem.getGrossAmount()));
            else
                valueTv.setText(String.valueOf(reportOrderItem.getSaleCount()));
        }
    }

    @Override
    public void onBindViewHolder(final TopItemsAdapter.ItemHolder holder, final int position) {
        ReportOrderItem reportOrderItem = reportOrderItems.get(position);
        holder.setData(reportOrderItem, position);
    }

    @Override
    public int getItemCount() {
        if(reportOrderItems != null)
            return reportOrderItems.size();
        else
            return 0;
    }
}