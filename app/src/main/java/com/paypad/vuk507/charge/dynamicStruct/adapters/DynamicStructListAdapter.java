package com.paypad.vuk507.charge.dynamicStruct.adapters;

import android.content.Context;
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
import com.paypad.vuk507.charge.dynamicStruct.interfaces.ReturnDynamicBoxListener;
import com.paypad.vuk507.db.CategoryDBHelper;
import com.paypad.vuk507.db.DiscountDBHelper;
import com.paypad.vuk507.db.ProductDBHelper;
import com.paypad.vuk507.enums.DynamicStructEnum;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.model.Category;
import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.model.DynamicBoxModel;
import com.paypad.vuk507.model.Product;

import java.util.ArrayList;
import java.util.List;

public class DynamicStructListAdapter extends RecyclerView.Adapter<DynamicStructListAdapter.StructListHolder> {

    private Context context;
    private List<DynamicBoxModel> boxModels = new ArrayList<>();
    private BaseFragment.FragmentNavigation fragmentNavigation;
    private ReturnDynamicBoxListener dynamicBoxListener;

    public DynamicStructListAdapter(Context context, List<DynamicBoxModel> boxModels,
                                    BaseFragment.FragmentNavigation fragmentNavigation,
                                    ReturnDynamicBoxListener listener) {
        this.context = context;
        this.boxModels.addAll(boxModels);
        this.fragmentNavigation = fragmentNavigation;
        this.dynamicBoxListener = listener;
    }

    @NonNull
    @Override
    public StructListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dynamic_box_layout, parent, false);
        return new StructListHolder(itemView);
    }

    public class StructListHolder extends RecyclerView.ViewHolder {

        private CardView itemCv;
        private TextView itemNameTv;
        private ImageView deleteItemImgv;

        DynamicBoxModel dynamicBoxModel;
        int position;

        StructListHolder(View view) {
            super(view);

            itemCv = view.findViewById(R.id.itemCv);
            itemNameTv = view.findViewById(R.id.itemNameTv);
            deleteItemImgv = view.findViewById(R.id.deleteItemImgv);

            itemCv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dynamicBoxListener.onReturn(dynamicBoxModel, ItemProcessEnum.SELECTED);
                }
            });

            deleteItemImgv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            itemCv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(dynamicBoxModel.getStructId() != 0)
                        dynamicBoxListener.onReturn(dynamicBoxModel, ItemProcessEnum.DELETED);
                    return false;
                }
            });
        }

        void setData(DynamicBoxModel dynamicBoxModel, int position) {
            this.dynamicBoxModel = dynamicBoxModel;
            this.position = position;

            deleteItemImgv.setVisibility(View.GONE);
            setItemName();
        }

        private void setItemName() {
            if(dynamicBoxModel.getStructId() == 0){
                itemNameTv.setText("+");
            }else {
                if(dynamicBoxModel.getItemName() != null && !dynamicBoxModel.getItemName().isEmpty()){
                    itemNameTv.setText(dynamicBoxModel.getItemName());
                }else {
                    if(dynamicBoxModel.getStructId() == DynamicStructEnum.DISCOUNT_SET.getId()){
                        Discount discount = DiscountDBHelper.getDiscount(dynamicBoxModel.getItemId());
                        itemNameTv.setText(discount.getName());
                    }else if(dynamicBoxModel.getStructId() == DynamicStructEnum.PRODUCT_SET.getId()){
                        Product product = ProductDBHelper.getProduct(dynamicBoxModel.getItemId());
                        itemNameTv.setText(product.getName());
                    }else if(dynamicBoxModel.getStructId() == DynamicStructEnum.CATEGORY_SET.getId()){
                        Category category = CategoryDBHelper.getCategory(dynamicBoxModel.getItemId());
                        itemNameTv.setText(category.getName());
                    }else if(dynamicBoxModel.getStructId() == DynamicStructEnum.PAYMENT_SET.getId()){
                        // TODO - function tanimi nasil yapiliyor
                    }
                }
            }
        }
    }

    public void dynamicModelRemoveResult(int position){
        boxModels.remove(position);
        this.notifyItemRemoved(position);
        this.notifyItemRangeChanged(position, getItemCount());
    }

    public void dynamicModelChangedResult(int position){
        this.notifyItemChanged(position);
    }

    public void addDynamicModel(DynamicBoxModel dynamicBoxModel){
        if(boxModels != null && dynamicBoxModel != null){
            boxModels.add(dynamicBoxModel);
            this.notifyItemInserted(boxModels.size() - 1);
        }
    }

    @Override
    public void onBindViewHolder(final StructListHolder holder, final int position) {
        DynamicBoxModel dynamicBoxModel = boxModels.get(position);
        holder.setData(dynamicBoxModel, position);
    }

    @Override
    public int getItemCount() {
        if(boxModels != null)
            return boxModels.size();
        else
            return 0;
    }
}