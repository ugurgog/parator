package com.paypad.vuk507.menu.reports.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.R;
import com.paypad.vuk507.enums.ReportsEnum;
import com.paypad.vuk507.menu.reports.interfaces.ReturnReportItemCallback;
import com.paypad.vuk507.utils.CommonUtils;

import static com.paypad.vuk507.constants.CustomConstants.LANGUAGE_TR;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ItemHolder> {

    private ReportsEnum[] values;
    private ReturnReportItemCallback returnReportItemCallback;

    public ReportAdapter() {
        this.values = ReportsEnum.values();
    }

    public void setReturnReportItemCallback(ReturnReportItemCallback returnReportItemCallback) {
        this.returnReportItemCallback = returnReportItemCallback;
    }

    @NonNull
    @Override
    public ReportAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_simple_list_item, parent, false);
        return new ReportAdapter.ItemHolder(itemView);
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        private CardView itemCv;
        private TextView nameTv;
        private ReportsEnum reportsEnum;
        private int position;

        public ItemHolder(View view) {
            super(view);
            itemCv = view.findViewById(R.id.itemCv);
            nameTv = view.findViewById(R.id.nameTv);

            itemCv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    returnReportItemCallback.OnReturnReportItem(reportsEnum);
                }
            });
        }

        public void setData(ReportsEnum reportsEnum, int position) {
            this.reportsEnum = reportsEnum;
            this.position = position;

            if(CommonUtils.getLanguage().equals(LANGUAGE_TR))
                nameTv.setText(reportsEnum.getLabelTr());
            else
                nameTv.setText(reportsEnum.getLabelEn());
        }
    }

    @Override
    public void onBindViewHolder(final ReportAdapter.ItemHolder holder, final int position) {
        ReportsEnum reportsEnum = values[position];
        holder.setData(reportsEnum, position);
    }

    @Override
    public int getItemCount() {
        if(values != null)
            return values.length;
        else
            return 0;
    }
}