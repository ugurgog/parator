package com.paypad.vuk507.charge.sale.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.R;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.menu.discount.interfaces.ReturnDiscountCallback;
import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.model.SaleItem;
import com.paypad.vuk507.model.pojo.SaleModelInstance;
import com.paypad.vuk507.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;

import static com.paypad.vuk507.constants.CustomConstants.TYPE_PRICE;
import static com.paypad.vuk507.constants.CustomConstants.TYPE_RATE;

public class SaleDiscountListAdapter extends RecyclerView.Adapter<SaleDiscountListAdapter.DiscountHolder> {

    private Context context;
    private List<Discount> discounts = new ArrayList<>();
    private ReturnDiscountCallback discountCallback;

    public SaleDiscountListAdapter(Context context, List<Discount> discounts) {
        this.context = context;
        this.discounts.addAll(discounts);
    }

    public void setDiscountCallback(ReturnDiscountCallback discountCallback) {
        this.discountCallback = discountCallback;
    }

    @NonNull
    @Override
    public SaleDiscountListAdapter.DiscountHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_sale_discount, parent, false);
        return new SaleDiscountListAdapter.DiscountHolder(itemView);
    }

    public class DiscountHolder extends RecyclerView.ViewHolder {

        private TextView discNameTv;
        private TextView discRateTv;
        private ImageButton deleteDiscImgBtn;

        private Discount discount;
        private int position;


        public DiscountHolder(View view) {
            super(view);

            discNameTv = view.findViewById(R.id.discNameTv);
            discRateTv = view.findViewById(R.id.discRateTv);
            deleteDiscImgBtn = view.findViewById(R.id.deleteDiscImgBtn);

            deleteDiscImgBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    discountCallback.OnReturn(discount, ItemProcessEnum.DELETED);
                    discounts.remove(discount);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, getItemCount());
                }
            });
        }

        public void setData(Discount discount, int position) {
            this.discount = discount;
            this.position = position;
            setDiscountNameTv();
            setDiscountRateOrAmountTv();
        }

        private void setDiscountRateOrAmountTv() {
            String rateOrAmount = "";
            if(discount.getRate() > 0d){
                rateOrAmount = "% ".concat(CommonUtils.getDoubleStrValueForView(discount.getRate(), TYPE_RATE));
            }else if(discount.getAmount() > 0d){
                rateOrAmount = CommonUtils.getDoubleStrValueForView(discount.getAmount(), TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol());
            }
            discRateTv.setText(rateOrAmount);
        }

        private void setDiscountNameTv() {
            int saleItemCount = 0;
            int itemCount = 0;

            if(discount.getAmount() > 0d){
                discNameTv.setText(discount.getName().concat("(").concat(context.getResources().getString(R.string.all_items)).concat(")"));

            }else {
                for(SaleItem saleItem: SaleModelInstance.getInstance().getSaleModel().getSaleItems()){
                    saleItemCount++;

                    if(saleItem.getDiscounts() != null){
                        for(Discount discount1 : saleItem.getDiscounts()){
                            if(discount1.getId() == discount.getId()){
                                itemCount++;
                                break;
                            }
                        }
                    }
                }

                if(saleItemCount == itemCount){
                    discNameTv.setText(discount.getName().concat("(").concat(context.getResources().getString(R.string.all_items)).concat(")"));
                }else {
                    discNameTv.setText(discount.getName().concat("(").concat(String.valueOf(itemCount)).concat(" ").concat(context.getResources().getString(R.string.items)).concat(")"));
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(final SaleDiscountListAdapter.DiscountHolder holder, final int position) {
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