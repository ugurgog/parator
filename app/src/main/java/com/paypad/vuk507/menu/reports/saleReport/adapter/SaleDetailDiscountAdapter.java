package com.paypad.vuk507.menu.reports.saleReport.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.R;
import com.paypad.vuk507.model.pojo.ReportDiscountModel;
import com.paypad.vuk507.utils.CommonUtils;

import java.util.Map;

public class SaleDetailDiscountAdapter extends RecyclerView.Adapter<SaleDetailDiscountAdapter.DiscountHolder> {

    private Map<Long, ReportDiscountModel> discounts;

    public SaleDetailDiscountAdapter(Map<Long, ReportDiscountModel> discounts) {
        this.discounts = discounts;
    }

    @NonNull
    @Override
    public SaleDetailDiscountAdapter.DiscountHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_custom_with_name_value_margin_start, parent, false);
        return new SaleDetailDiscountAdapter.DiscountHolder(itemView);
    }

    public class DiscountHolder extends RecyclerView.ViewHolder {

        private TextView nameTv;
        private TextView valueTv;

        private ReportDiscountModel reportDiscountModel;
        private int position;

        DiscountHolder(View view) {
            super(view);
            nameTv = view.findViewById(R.id.nameTv);
            valueTv = view.findViewById(R.id.valueTv);
        }

        public void setData(ReportDiscountModel reportDiscountModel, int position) {
            this.reportDiscountModel = reportDiscountModel;
            this.position = position;
            nameTv.setText(reportDiscountModel.getDiscountName());
            valueTv.setText("(".concat(CommonUtils.getAmountTextWithCurrency(reportDiscountModel.getTotalDiscountAmount())).concat(")"));
        }
    }

    @Override
    public void onBindViewHolder(final SaleDetailDiscountAdapter.DiscountHolder holder, final int position) {

        long discountId = (long)discounts.keySet().toArray()[position];
        ReportDiscountModel reportDiscountModel = discounts.get(discountId);
        holder.setData(reportDiscountModel, position);
    }

    @Override
    public int getItemCount() {
        if(discounts != null)
            return discounts.size();
        else
            return 0;
    }
}