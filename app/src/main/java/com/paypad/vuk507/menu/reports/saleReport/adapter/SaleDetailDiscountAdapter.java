package com.paypad.vuk507.menu.reports.saleReport.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.charge.order.IOrderManager;
import com.paypad.vuk507.charge.order.OrderManager;
import com.paypad.vuk507.enums.CurrencyEnum;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.interfaces.ReturnSizeCallback;
import com.paypad.vuk507.menu.discount.interfaces.ReturnDiscountCallback;
import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.model.pojo.ReportDiscountModel;
import com.paypad.vuk507.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;
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
                .inflate(R.layout.adapter_item_sale_detail_discount, parent, false);
        return new SaleDetailDiscountAdapter.DiscountHolder(itemView);
    }

    public class DiscountHolder extends RecyclerView.ViewHolder {

        private TextView discountNameTv;
        private TextView discountAmountTv;

        private ReportDiscountModel reportDiscountModel;
        private int position;

        DiscountHolder(View view) {
            super(view);
            discountNameTv = view.findViewById(R.id.discountNameTv);
            discountAmountTv = view.findViewById(R.id.discountAmountTv);
        }

        public void setData(ReportDiscountModel reportDiscountModel, int position) {
            this.reportDiscountModel = reportDiscountModel;
            this.position = position;
            discountNameTv.setText(reportDiscountModel.getDiscountName());
            discountAmountTv.setText("(".concat(CommonUtils.getAmountTextWithCurrency(reportDiscountModel.getTotalDiscountAmount())).concat(")"));
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