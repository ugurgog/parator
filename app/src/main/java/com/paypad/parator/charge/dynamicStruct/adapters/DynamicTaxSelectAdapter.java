package com.paypad.parator.charge.dynamicStruct.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.parator.R;
import com.paypad.parator.enums.ItemProcessEnum;
import com.paypad.parator.interfaces.ReturnSizeCallback;
import com.paypad.parator.menu.tax.interfaces.ReturnTaxCallback;
import com.paypad.parator.model.TaxModel;
import com.paypad.parator.utils.CommonUtils;
import com.paypad.parator.utils.ShapeUtil;

import java.util.ArrayList;
import java.util.List;

public class DynamicTaxSelectAdapter extends RecyclerView.Adapter<DynamicTaxSelectAdapter.TaxHolder> {

    private Context context;
    private List<TaxModel> taxModels = new ArrayList<>();
    private List<TaxModel> orgTaxModels = new ArrayList<>();

    private ReturnTaxCallback returnTaxCallback;

    public DynamicTaxSelectAdapter(Context context, List<TaxModel> taxModels,
                                   ReturnTaxCallback callback) {
        this.context = context;
        this.taxModels.addAll(taxModels);
        this.orgTaxModels.addAll(taxModels);
        this.returnTaxCallback = callback;
    }

    @NonNull
    @Override
    public DynamicTaxSelectAdapter.TaxHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dynamic_item_list, parent, false);
        return new DynamicTaxSelectAdapter.TaxHolder(itemView);
    }

    public class TaxHolder extends RecyclerView.ViewHolder {

        private LinearLayout structItemll;
        private TextView colorfulTv;
        private TextView itemNameTv;

        private TaxModel taxModel;
        private int position;

        TaxHolder(View view) {
            super(view);

            structItemll = view.findViewById(R.id.structItemll);
            colorfulTv = view.findViewById(R.id.colorfulTv);
            itemNameTv = view.findViewById(R.id.itemNameTv);

            structItemll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    returnTaxCallback.OnReturn(taxModel, ItemProcessEnum.SELECTED);
                }
            });
        }

        void setData(TaxModel taxModel, int position) {
            this.taxModel = taxModel;
            this.position = position;
            itemNameTv.setText(taxModel.getName());
            setItemColor();
        }

        private void setItemColor() {
            int colorCode = CommonUtils.getDarkRandomColor(context);
            GradientDrawable imageShape = ShapeUtil.getShape(context.getResources().getColor(colorCode, null),
                    context.getResources().getColor(colorCode, null),
                    GradientDrawable.OVAL, 50, 0);
            colorfulTv.setBackground(imageShape);
        }
    }

    @Override
    public void onBindViewHolder(final TaxHolder holder, final int position) {
        TaxModel taxModel = taxModels.get(position);
        holder.setData(taxModel, position);
    }

    @Override
    public int getItemCount() {
        if(taxModels != null)
            return taxModels.size();
        else
            return 0;
    }

    public void updateAdapter(String searchText, ReturnSizeCallback returnSizeCallback) {
        if (searchText.trim().isEmpty()){
            taxModels = orgTaxModels;
        } else {

            List<TaxModel> tempTaxModels = new ArrayList<>();
            for (TaxModel taxModel : orgTaxModels) {
                if (taxModel.getName() != null &&
                        taxModel.getName().toLowerCase().contains(searchText.toLowerCase()))
                    tempTaxModels.add(taxModel);
            }
            taxModels = tempTaxModels;
        }

        this.notifyDataSetChanged();

        if (taxModels != null)
            returnSizeCallback.OnReturn(taxModels.size());
        else
            returnSizeCallback.OnReturn(0);
    }
}