package com.paypad.vuk507.menu.product.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.db.TaxDBHelper;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.enums.TaxRateEnum;
import com.paypad.vuk507.interfaces.ReturnSizeCallback;
import com.paypad.vuk507.menu.product.ProductEditFragment;
import com.paypad.vuk507.menu.product.interfaces.ReturnItemCallback;
import com.paypad.vuk507.model.Product;
import com.paypad.vuk507.model.TaxModel;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.DataUtils;

import java.util.ArrayList;
import java.util.List;

import static com.paypad.vuk507.constants.CustomConstants.TYPE_PRICE;
import static com.paypad.vuk507.constants.CustomConstants.TYPE_RATE;

public class ProductTaxListAdapter extends RecyclerView.Adapter<ProductTaxListAdapter.ProductHolder> {

    private Context context;
    private List<Product> products = new ArrayList<>();
    private List<Product> orgProducts = new ArrayList<>();

    private ReturnItemCallback returnItemCallback;

    public ProductTaxListAdapter(Context context, List<Product> products, ReturnItemCallback returnItemCallback) {
        this.context = context;
        this.products.addAll(products);
        this.orgProducts.addAll(products);
        this.returnItemCallback = returnItemCallback;
    }

    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_tax_item, parent, false);
        return new ProductHolder(itemView);
    }

    public class ProductHolder extends RecyclerView.ViewHolder {

        CardView productItemCv;
        TextView taxRateTv;
        TextView productNameTv;

        Product product;
        int position;

        ProductHolder(View view) {
            super(view);

            productItemCv = view.findViewById(R.id.productItemCv);
            taxRateTv = view.findViewById(R.id.taxRateTv);
            productNameTv = view.findViewById(R.id.productNameTv);

            productItemCv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    returnItemCallback.OnReturn(product, ItemProcessEnum.SELECTED);
                }
            });
        }

        void setData(Product product, int position) {
            this.product = product;
            this.position = position;
            setProductTaxRate();
            setProductName();
        }

        private void setProductTaxRate(){
            if(product.getTaxId() != 0){

                TaxModel taxModel;
                if(product.getTaxId() < 0){
                    taxModel = new TaxModel();
                    TaxRateEnum taxRateEnum = TaxRateEnum.getById(product.getTaxId());
                    taxModel.setId(taxRateEnum.getId());
                    taxModel.setTaxRate(taxRateEnum.getRateValue());
                    taxModel.setName(taxRateEnum.getLabel());
                }else {
                    taxModel = TaxDBHelper.getTax(product.getTaxId());
                }

                taxRateTv.setText("% ".concat(CommonUtils.getDoubleStrValueForView(taxModel.getTaxRate(), TYPE_RATE)));
            }else
                taxRateTv.setText("");
        }

        private void setProductName(){
            if(product.getName() != null && !product.getName().isEmpty()){
                if(product.getName().length() > 10)
                    productNameTv.setText(product.getName().substring(0, 7).concat("..."));
                else
                    productNameTv.setText(product.getName());
            }else
                productNameTv.setText("");
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
}