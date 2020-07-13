package com.paypad.vuk507.menu.product.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.interfaces.ReturnSizeCallback;
import com.paypad.vuk507.interfaces.ReturnViewCallback;
import com.paypad.vuk507.menu.product.ProductEditFragment;
import com.paypad.vuk507.menu.product.interfaces.ReturnItemCallback;
import com.paypad.vuk507.menu.tax.TaxEditFragment;
import com.paypad.vuk507.menu.tax.interfaces.ReturnTaxCallback;
import com.paypad.vuk507.model.Product;
import com.paypad.vuk507.model.TaxModel;
import com.paypad.vuk507.model.UnitModel;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.DataUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductHolder> {

    private Context context;
    private List<Product> products = new ArrayList<>();
    private List<Product> orgProducts = new ArrayList<>();

    private BaseFragment.FragmentNavigation fragmentNavigation;
    private ReturnItemCallback returnItemCallback;
    private ItemProcessEnum processType;
    private ReturnViewCallback returnViewCallback;

    public ProductListAdapter(Context context, List<Product> products,
                   BaseFragment.FragmentNavigation fragmentNavigation, ItemProcessEnum processType,
                   ReturnItemCallback returnItemCallback) {
        this.context = context;
        this.products.addAll(products);
        this.orgProducts.addAll(products);
        this.fragmentNavigation = fragmentNavigation;
        this.processType = processType;
        this.returnItemCallback = returnItemCallback;
    }

    public void setReturnViewCallback(ReturnViewCallback returnViewCallback) {
        this.returnViewCallback = returnViewCallback;
    }

    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_list, parent, false);
        return new ProductHolder(itemView);
    }

    public class ProductHolder extends RecyclerView.ViewHolder {

        CardView productItemCv;
        TextView productNameTv;
        TextView itemDescTv;
        ImageView itemImgv;
        TextView shortNameTv;
        FrameLayout content_frame;

        Product product;
        int position;

        ProductHolder(View view) {
            super(view);

            productItemCv = view.findViewById(R.id.productItemCv);
            productNameTv = view.findViewById(R.id.productNameTv);
            itemDescTv = view.findViewById(R.id.itemDescTv);
            itemImgv = view.findViewById(R.id.itemImgv);
            shortNameTv = view.findViewById(R.id.shortNameTv);
            content_frame = view.findViewById(R.id.content_frame);

            productItemCv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // If process comes from Library step, just product will be selected
                    if(processType != null && (processType == ItemProcessEnum.SELECTED)){

                        if(product.getAmount() > 0d)
                            returnViewCallback.OnViewCallback(content_frame);

                        returnItemCallback.OnReturn(product, processType);
                    }else{
                        fragmentNavigation.pushFragment(new ProductEditFragment(product, new ReturnItemCallback() {
                            @Override
                            public void OnReturn(Product product, ItemProcessEnum processEnum) {
                                returnItemCallback.OnReturn(product, processEnum);
                                Log.i("Info", "ProductListAdapter callback.");
                            }
                        }));
                    }
                }
            });
        }

        void setData(Product product, int position) {
            this.product = product;
            this.position = position;

            productNameTv.setText(product.getName());
            setItemDescription();
            setItemPicture();
        }

        private void setItemPicture() {
            if(product.getProductImage() != null){
                shortNameTv.setText("");

                itemImgv.post(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bmp = BitmapFactory.decodeByteArray(product.getProductImage(), 0, product.getProductImage().length);

                        itemImgv.setImageBitmap(Bitmap.createScaledBitmap(bmp, itemImgv.getWidth(),
                                itemImgv.getHeight(), false));
                    }
                });
            }else {
                shortNameTv.setText(DataUtils.getProductShortenName(product));
                itemImgv.setImageDrawable(null);

                if(product.getColorId() != 0)
                    itemImgv.setBackgroundColor(context.getResources().getColor(product.getColorId(), null));
            }
        }

        private void setItemDescription(){

            UnitModel unitModel = null;
            if(product.getUnitId() != 0)
                unitModel = DataUtils.getUnitModelById(product.getUnitId());

            if(product.getAmount() == 0){
                itemDescTv.setText(context.getResources().getString(R.string.variable)
                        .concat("/")
                        .concat(unitModel != null ? unitModel.getName() : context.getResources().getString(R.string.unit_undefined)));
            }else {
                itemDescTv.setText(String.valueOf(product.getAmount()).concat(" ")
                    .concat(CommonUtils.getCurrency().getSymbol())
                    .concat("/")
                        .concat(unitModel != null ? unitModel.getName() : context.getResources().getString(R.string.unit_undefined)));
            }
        }
    }

    public void productRemoveResult(int position){
        products.remove(position);
        this.notifyItemRemoved(position);
        this.notifyItemRangeChanged(position, getItemCount());
        //this.notifyDataSetChanged();
    }

    public void productChangedResult(int position){
        this.notifyItemChanged(position);
    }

    public void addProduct(Product product){
        if(products != null && product != null){
            products.add(product);
            this.notifyItemInserted(products.size() - 1);
            //this.notifyDataSetChanged();
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