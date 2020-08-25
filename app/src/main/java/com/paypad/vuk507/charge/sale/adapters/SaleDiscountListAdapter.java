package com.paypad.vuk507.charge.sale.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.R;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.interfaces.ReturnOrderItemDiscountCallback;
import com.paypad.vuk507.model.OrderItemDiscount;
import com.paypad.vuk507.model.OrderItem;
import com.paypad.vuk507.model.pojo.SaleModelInstance;
import com.paypad.vuk507.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import static com.paypad.vuk507.constants.CustomConstants.TYPE_PRICE;
import static com.paypad.vuk507.constants.CustomConstants.TYPE_RATE;

public class SaleDiscountListAdapter extends RecyclerView.Adapter<SaleDiscountListAdapter.DiscountHolder> {

    private Context context;
    private List<OrderItemDiscount> discounts = new ArrayList<>();
    private ReturnOrderItemDiscountCallback returnOrderItemDiscountCallback;

    public SaleDiscountListAdapter(Context context, List<OrderItemDiscount> discounts) {
        this.context = context;
        this.discounts.addAll(discounts);
    }

    public void setReturnOrderItemDiscountCallback(ReturnOrderItemDiscountCallback returnOrderItemDiscountCallback) {
        this.returnOrderItemDiscountCallback = returnOrderItemDiscountCallback;
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

        private OrderItemDiscount discount;
        private int position;


        public DiscountHolder(View view) {
            super(view);

            discNameTv = view.findViewById(R.id.discNameTv);
            discRateTv = view.findViewById(R.id.discRateTv);
            deleteDiscImgBtn = view.findViewById(R.id.deleteDiscImgBtn);

            deleteDiscImgBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    returnOrderItemDiscountCallback.OnReturn(discount, ItemProcessEnum.DELETED);
                    discounts.remove(discount);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, getItemCount());
                }
            });
        }

        public void setData(OrderItemDiscount discount, int position) {
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
                for(OrderItem orderItem : SaleModelInstance.getInstance().getSaleModel().getOrderItems()){
                    saleItemCount++;

                    if(orderItem.getDiscounts() != null){
                        for(OrderItemDiscount discount1 : orderItem.getDiscounts()){
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
        OrderItemDiscount discount = discounts.get(position);
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