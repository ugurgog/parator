package com.paypad.parator.menu.reports.saleReport.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.paypad.parator.R;
import com.paypad.parator.menu.reports.saleReport.TopCategory;
import com.paypad.parator.model.pojo.ReportOrderItem;
import com.paypad.parator.utils.CommonUtils;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import java.util.List;

public class TopCategoryAdapter extends ExpandableRecyclerViewAdapter<TopCategoryAdapter.CategoryViewHolder, TopCategoryAdapter.ItemViewHolder> {

    private Context context;
    private boolean isGrossSelected;

    public TopCategoryAdapter(Context context, List<? extends ExpandableGroup> groups, boolean isGrossSelected) {
        super(groups);
        this.context = context;
        this.isGrossSelected = isGrossSelected;
    }

    @Override
    public CategoryViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_custom_with_name_value_leftside_arrow, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public ItemViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_custom_with_name_value_margin_start, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(ItemViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        final ReportOrderItem reportOrderItem = ((TopCategory) group).getItems().get(childIndex);
        holder.setData(reportOrderItem);
    }

    @Override
    public void onBindGroupViewHolder(CategoryViewHolder holder, int flatPosition, ExpandableGroup group) {

        final TopCategory topCategory = ((TopCategory) group);
        holder.setData(topCategory);
    }

    public class CategoryViewHolder extends GroupViewHolder {

        private TextView nameTv;
        private TextView valueTv;
        private ImageView arrowImgv;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.nameTv);
            valueTv = itemView.findViewById(R.id.valueTv);
            arrowImgv = itemView.findViewById(R.id.arrowImgv);

            nameTv.setTypeface(Typeface.DEFAULT_BOLD);
            valueTv.setTypeface(Typeface.DEFAULT_BOLD);
        }

        public void setData(TopCategory topCategory) {
            nameTv.setText(topCategory.getTitle());

            if(isGrossSelected)
                valueTv.setText(CommonUtils.getAmountTextWithCurrency(topCategory.getTotalGrossAmount()));
            else
                valueTv.setText(String.valueOf(topCategory.getTotalSaleCount()));
        }

        @Override
        public void expand() {
            animateExpand();
        }

        @Override
        public void collapse() {
            animateCollapse();
        }

        private void animateExpand() {
            ObjectAnimator.ofFloat(arrowImgv, View.ROTATION, 0f, 90f).setDuration(300).start();
        }

        private void animateCollapse() {
            ObjectAnimator.ofFloat(arrowImgv, View.ROTATION, 90f, 0f).setDuration(300).start();
        }
    }

    public class ItemViewHolder extends ChildViewHolder {

        private TextView nameTv;
        private TextView valueTv;

        public ItemViewHolder(View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.nameTv);
            valueTv = itemView.findViewById(R.id.valueTv);

            nameTv.setTextColor(context.getResources().getColor(R.color.Black, null));
            valueTv.setTextColor(context.getResources().getColor(R.color.Black, null));
        }

        public void setData(ReportOrderItem reportOrderItem) {
            nameTv.setText(reportOrderItem.getItemName());

            if(isGrossSelected)
                valueTv.setText(CommonUtils.getAmountTextWithCurrency(reportOrderItem.getGrossAmount()));
            else
                valueTv.setText(String.valueOf(reportOrderItem.getSaleCount()));
        }
    }
}