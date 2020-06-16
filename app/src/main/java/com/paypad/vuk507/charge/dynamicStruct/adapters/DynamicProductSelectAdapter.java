package com.paypad.vuk507.charge.dynamicStruct.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.charge.dynamicStruct.DynamicItemSelectFragmant;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.interfaces.ReturnSizeCallback;
import com.paypad.vuk507.menu.product.ProductEditFragment;
import com.paypad.vuk507.menu.product.interfaces.ReturnItemCallback;
import com.paypad.vuk507.model.Product;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.DataUtils;
import com.paypad.vuk507.utils.ShapeUtil;

import java.util.ArrayList;
import java.util.List;

public class DynamicProductSelectAdapter extends RecyclerView.Adapter<DynamicProductSelectAdapter.ProductHolder> {

    private Context context;
    private List<Product> products = new ArrayList<>();
    private List<Product> orgProducts = new ArrayList<>();

    private ReturnItemCallback returnItemCallback;

    public DynamicProductSelectAdapter(Context context, List<Product> products,
                              ReturnItemCallback returnItemCallback) {
        this.context = context;
        this.products.addAll(products);
        this.orgProducts.addAll(products);
        this.returnItemCallback = returnItemCallback;
    }

    @NonNull
    @Override
    public DynamicProductSelectAdapter.ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dynamic_item_list, parent, false);
        return new DynamicProductSelectAdapter.ProductHolder(itemView);
    }

    public class ProductHolder extends RecyclerView.ViewHolder {

        private LinearLayout structItemll;
        private TextView colorfulTv;
        private TextView itemNameTv;

        Product product;
        int position;

        ProductHolder(View view) {
            super(view);

            structItemll = view.findViewById(R.id.structItemll);
            colorfulTv = view.findViewById(R.id.colorfulTv);
            itemNameTv = view.findViewById(R.id.itemNameTv);

            structItemll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    returnItemCallback.OnReturn(product, ItemProcessEnum.SELECTED);
                }
            });
        }

        void setData(Product product, int position) {
            this.product = product;
            this.position = position;
            itemNameTv.setText(product.getName());
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
    public void onBindViewHolder(final ProductHolder holder, final int position) {
        Product product = products.get(position);
        holder.setData(product, position);
    }

    @Override
    public int getItemCount() {
        if(products != null)
            return products.size();
        else
            return 0;
    }

    public void updateAdapter(String searchText, ReturnSizeCallback returnSizeCallback) {
        if (searchText.trim().isEmpty()){
            products = orgProducts;
        } else {

            List<Product> tempProducts = new ArrayList<>();
            for (Product prod : orgProducts) {
                if (prod.getName() != null &&
                        prod.getName().toLowerCase().contains(searchText.toLowerCase()))
                    tempProducts.add(prod);
            }
            products = tempProducts;
        }

        this.notifyDataSetChanged();

        if (products != null)
            returnSizeCallback.OnReturn(products.size());
        else
            returnSizeCallback.OnReturn(0);
    }
}