package com.paypad.vuk507.charge.sale.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.charge.interfaces.ReturnSaleItemCallback;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.interfaces.ReturnSizeCallback;
import com.paypad.vuk507.menu.category.CategoryEditFragment;
import com.paypad.vuk507.menu.category.interfaces.ReturnCategoryCallback;
import com.paypad.vuk507.model.Category;
import com.paypad.vuk507.model.SaleItem;
import com.paypad.vuk507.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import static com.paypad.vuk507.constants.CustomConstants.TYPE_PRICE;


public class SaleListAdapter extends RecyclerView.Adapter<SaleListAdapter.SaleHolder> {

    private List<SaleItem> saleItems = new ArrayList<>();
    private ReturnSaleItemCallback returnSaleItemCallback;

    public SaleListAdapter(List<SaleItem> saleItems, ReturnSaleItemCallback callback) {
        this.saleItems.addAll(saleItems);
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

        private SaleItem saleItem;
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
                    if(saleItem.getUuid() != null && !saleItem.getUuid().isEmpty()){
                        //TODO - normal bir sale item
                    }else {
                        //TODO - discounts
                    }
                    returnSaleItemCallback.onReturn(saleItem, ItemProcessEnum.SELECTED);
                }
            });
        }

        public void setData(SaleItem saleItem, int position) {
            this.saleItem = saleItem;
            this.position = position;
            setSaleItemName();
            setSaleItemNoteInfo();
            setSaleItemDiscountImgv();
            setSaleItemAmount();
        }

        private void setSaleItemAmount() {
            if(saleItem != null ){
                String amountStr = "";
                if(saleItem.getUuid() == null || saleItem.getUuid().isEmpty()){ //Bu indirimlerdir
                    amountStr = amountStr.concat("- ").concat(CommonUtils.getDoubleStrValueForView(saleItem.getAmount(), TYPE_PRICE)).concat(" ").concat(CommonUtils.getCurrency().getSymbol());
                }else
                    amountStr = CommonUtils.getDoubleStrValueForView(saleItem.getAmountIncludingTax() * (double) saleItem.getQuantity(), TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol());
                saleAmountTv.setText(amountStr);
            }
        }

        private void setSaleItemDiscountImgv() {
            if(saleItem != null && saleItem.getDiscounts() != null
                && saleItem.getDiscounts().size() > 0){
                discountImgv.setVisibility(View.VISIBLE);
            }else
                discountImgv.setVisibility(View.GONE);
        }

        private void setSaleItemNoteInfo() {
            if(saleItem != null && saleItem.getNote() != null && !saleItem.getNote().isEmpty()){
                saleNoteTv.setVisibility(View.VISIBLE);
                saleNoteTv.setText(saleItem.getNote());
            }else
                saleNoteTv.setVisibility(View.GONE);
        }

        private void setSaleItemName() {
            if(saleItem != null && saleItem.getName() != null){
                if(saleItem.getQuantity() > 1){
                    saleNameTv.setText(saleItem.getName().concat(" X ").concat(String.valueOf(saleItem.getQuantity())));
                }else
                    saleNameTv.setText(saleItem.getName());
            }

        }
    }

    @Override
    public void onBindViewHolder(final SaleListAdapter.SaleHolder holder, final int position) {
        SaleItem saleItem = saleItems.get(position);
        holder.setData(saleItem, position);
    }

    @Override
    public int getItemCount() {
        if(saleItems != null)
            return saleItems.size();
        else
            return 0;
    }
}