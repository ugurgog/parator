package com.paypad.vuk507.charge.dynamicStruct;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.paypad.vuk507.charge.dynamicStruct.interfaces.ReturnDynamicBoxListener;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.interfaces.ReturnSizeCallback;
import com.paypad.vuk507.menu.discount.interfaces.ReturnDiscountCallback;
import com.paypad.vuk507.menu.product.ProductEditFragment;
import com.paypad.vuk507.menu.product.interfaces.ReturnItemCallback;
import com.paypad.vuk507.model.DynamicBoxModel;
import com.paypad.vuk507.model.Product;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.DataUtils;

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
        private TextView plusTv;
        private ImageView deleteItemImgv;

        DynamicBoxModel dynamicBoxModel;
        int position;

        StructListHolder(View view) {
            super(view);

            itemCv = view.findViewById(R.id.itemCv);
            itemNameTv = view.findViewById(R.id.itemNameTv);
            plusTv = view.findViewById(R.id.plusTv);
            deleteItemImgv = view.findViewById(R.id.deleteItemImgv);

            itemCv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }

        void setData(DynamicBoxModel dynamicBoxModel, int position) {
            this.dynamicBoxModel = dynamicBoxModel;
            this.position = position;

            if(dynamicBoxModel.getStructId() == 0){
                itemNameTv.setText("");
                plusTv.setText("");
                deleteItemImgv.setVisibility(View.GONE);
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