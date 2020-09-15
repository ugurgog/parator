package com.paypad.parator.charge.sale.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;
import com.paypad.parator.R;
import com.paypad.parator.charge.order.IOrderManager;
import com.paypad.parator.charge.order.OrderManager;
import com.paypad.parator.model.Discount;
import com.paypad.parator.model.OrderItem;
import com.paypad.parator.model.OrderItemDiscount;
import com.paypad.parator.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;

import static com.paypad.parator.constants.CustomConstants.TYPE_RATE;

public class SaleItemDiscountListAdapter extends RecyclerView.Adapter<SaleItemDiscountListAdapter.DiscountHolder> {

    private List<Discount> discounts = new ArrayList<>();
    private OrderItem orderItem;
    private IOrderManager orderManager;

    public SaleItemDiscountListAdapter(List<Discount> discounts, OrderItem orderItem) {
        this.discounts.addAll(discounts);
        this.orderItem = orderItem;
        orderManager = new OrderManager();
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
        private LabeledSwitch discSwitch;

        private Discount discount;
        private int position;


        OnToggledListener listener1 = new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if(isOn){
                    if(orderItem.getDiscounts() == null)
                        orderItem.setDiscounts(new RealmList<>());

                    orderManager.addDiscountToSaleItem(orderItem, discount);

                }else {

                    if(orderItem.getDiscounts() == null)
                        orderItem.setDiscounts(new RealmList<>());

                    orderManager.removeDiscountFromSaleItem(orderItem, discount);
                }
            }
        };

        public DiscountHolder(View view) {
            super(view);

            discountNameTv = view.findViewById(R.id.discountNameTv);
            discountRateTv = view.findViewById(R.id.discountRateTv);
            discSwitch = view.findViewById(R.id.discSwitch);
            discSwitch.setOnToggledListener(listener1);
        }

        public void setData(Discount discount, int position) {
            this.discount = discount;
            this.position = position;
            discountNameTv.setText(discount.getName());
            discountRateTv.setText(CommonUtils.getDoubleStrValueForView(discount.getRate(), TYPE_RATE).concat(" %"));

            discSwitch.setOnToggledListener(null);
            if(isDiscountExist()){
                discSwitch.setOn(true);
            }else
                discSwitch.setOn(false);
            discSwitch.setOnToggledListener(listener1);
        }

        public boolean isDiscountExist(){
            if(orderItem.getDiscounts() != null && orderItem.getDiscounts().size() > 0){
                for (OrderItemDiscount discount1 : orderItem.getDiscounts()) {

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