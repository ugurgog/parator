package com.paypad.vuk507.charge.dynamicStruct.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.R;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.interfaces.ReturnSizeCallback;
import com.paypad.vuk507.menu.discount.interfaces.ReturnDiscountCallback;
import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.ShapeUtil;

import java.util.ArrayList;
import java.util.List;

public class DynamicDiscountSelectAdapter extends RecyclerView.Adapter<DynamicDiscountSelectAdapter.DiscountHolder> {

    private Context context;
    private List<Discount> discounts = new ArrayList<>();
    private List<Discount> orgDiscounts = new ArrayList<>();

    private ReturnDiscountCallback returnDiscountCallback;

    public DynamicDiscountSelectAdapter(Context context, List<Discount> discounts,
                                        ReturnDiscountCallback returnDiscountCallback) {
        this.context = context;
        this.discounts.addAll(discounts);
        this.orgDiscounts.addAll(discounts);
        this.returnDiscountCallback = returnDiscountCallback;
    }

    @NonNull
    @Override
    public DynamicDiscountSelectAdapter.DiscountHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dynamic_item_list, parent, false);
        return new DynamicDiscountSelectAdapter.DiscountHolder(itemView);
    }

    public class DiscountHolder extends RecyclerView.ViewHolder {

        private LinearLayout structItemll;
        private TextView colorfulTv;
        private TextView itemNameTv;

        private Discount discount;
        private int position;

        DiscountHolder(View view) {
            super(view);

            structItemll = view.findViewById(R.id.structItemll);
            colorfulTv = view.findViewById(R.id.colorfulTv);
            itemNameTv = view.findViewById(R.id.itemNameTv);

            structItemll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    returnDiscountCallback.OnReturn(discount, ItemProcessEnum.SELECTED);
                }
            });
        }

        void setData(Discount discount, int position) {
            this.discount = discount;
            this.position = position;
            itemNameTv.setText(discount.getName());
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
    public void onBindViewHolder(final DiscountHolder holder, final int position) {
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

    public void updateAdapter(String searchText, ReturnSizeCallback returnSizeCallback) {
        if (searchText.trim().isEmpty()){
            discounts = orgDiscounts;
        } else {

            List<Discount> tempDiscounts = new ArrayList<>();
            for (Discount disc : orgDiscounts) {
                if (disc.getName() != null &&
                        disc.getName().toLowerCase().contains(searchText.toLowerCase()))
                    tempDiscounts.add(disc);
            }
            discounts = tempDiscounts;
        }

        this.notifyDataSetChanged();

        if (discounts != null)
            returnSizeCallback.OnReturn(discounts.size());
        else
            returnSizeCallback.OnReturn(0);
    }
}