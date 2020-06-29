package com.paypad.vuk507.charge.sale.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.model.SaleItem;
import com.paypad.vuk507.model.pojo.SaleModel;
import com.paypad.vuk507.model.pojo.SaleModelInstance;
import com.paypad.vuk507.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;

import static com.paypad.vuk507.constants.CustomConstants.TYPE_PRICE;
import static com.paypad.vuk507.constants.CustomConstants.TYPE_RATE;

public class SaleItemDiscountListAdapter extends RecyclerView.Adapter<SaleItemDiscountListAdapter.DiscountHolder> {

    private List<Discount> discounts = new ArrayList<>();
    private SaleItem saleItem;

    public SaleItemDiscountListAdapter(List<Discount> discounts, SaleItem saleItem) {
        this.discounts.addAll(discounts);
        this.saleItem = saleItem;
    }

    @NonNull
    @Override
    public SaleItemDiscountListAdapter.DiscountHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_sale_item_discount, parent, false);
        return new SaleItemDiscountListAdapter.DiscountHolder(itemView);
    }

    public class DiscountHolder extends RecyclerView.ViewHolder {

        private TextView discountNameTv;
        private TextView discountRateTv;
        private Switch discSwitch;

        private Discount discount;
        private int position;

        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    if(saleItem.getDiscounts() == null)
                        saleItem.setDiscounts(new RealmList<>());

                    SaleModelInstance.getInstance().getSaleModel().addDiscountToSaleItem(saleItem.getUuid(), discount);

                }else {

                    if(saleItem.getDiscounts() == null)
                        saleItem.setDiscounts(new RealmList<>());

                    SaleModelInstance.getInstance().getSaleModel().removeDiscountFromSaleItem(saleItem.getUuid(), discount);
                }
            }
        };

        public DiscountHolder(View view) {
            super(view);

            discountNameTv = view.findViewById(R.id.discountNameTv);
            discountRateTv = view.findViewById(R.id.discountRateTv);
            discSwitch = view.findViewById(R.id.discSwitch);
            discSwitch.setOnCheckedChangeListener(listener);
        }

        public void setData(Discount discount, int position) {
            this.discount = discount;
            this.position = position;
            discountNameTv.setText(discount.getName());
            discountRateTv.setText(CommonUtils.getDoubleStrValueForView(discount.getRate(), TYPE_RATE).concat(" %"));

            discSwitch.setOnCheckedChangeListener(null);
            if(isDiscountExist()){
                discSwitch.setChecked(true);
            }else
                discSwitch.setChecked(false);
            discSwitch.setOnCheckedChangeListener(listener);
        }

        public boolean isDiscountExist(){

            Log.i("Info", "discounts:" + discounts);
            Log.i("Info", "saleItem.getDiscounts():" + saleItem.getDiscounts());

            if(saleItem.getDiscounts() != null && saleItem.getDiscounts().size() > 0){
                for (Discount discount1 : saleItem.getDiscounts()) {

                    if (discount1.getId() == discount.getId())
                        return true;
                }
            }

            return false;
        }
    }

    @Override
    public void onBindViewHolder(final SaleItemDiscountListAdapter.DiscountHolder holder, final int position) {
        Discount discount = discounts.get(position);
        holder.setData(discount, position);
    }

    @Override
    public int getItemCount() {
        if(discounts != null)
            return discounts.size();
        else
            return 0;
    }
}