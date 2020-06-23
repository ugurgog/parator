package com.paypad.vuk507.menu.product.adapters;

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
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.interfaces.ReturnSizeCallback;
import com.paypad.vuk507.menu.product.ProductEditFragment;
import com.paypad.vuk507.menu.product.interfaces.ReturnItemCallback;
import com.paypad.vuk507.menu.tax.TaxEditFragment;
import com.paypad.vuk507.menu.tax.interfaces.ReturnTaxCallback;
import com.paypad.vuk507.model.Product;
import com.paypad.vuk507.model.TaxModel;
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

        Product product;
        int position;

        ProductHolder(View view) {
            super(view);

            productItemCv = view.findViewById(R.id.productItemCv);
            productNameTv = view.findViewById(R.id.productNameTv);
            itemDescTv = view.findViewById(R.id.itemDescTv);
            itemImgv = view.findViewById(R.id.itemImgv);
            shortNameTv = view.findViewById(R.id.shortNameTv);

            productItemCv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // If process comes from Library step, just product will be selected
                    if(processType != null && (processType == ItemProcessEnum.SELECTED)){
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
            }
        }

        private void setItemDescription(){
            if(product.getAmount() == 0){
                itemDescTv.setText(context.getResources().getString(R.string.variable).concat("/").concat(product.getUnitType()));
            }else {
                itemDescTv.setText(String.valueOf(product.getAmount()).concat(" ")
                    .concat(CommonUtils.getCurrency().getSymbol())
                    .concat("/").concat(product.getUnitType()));
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