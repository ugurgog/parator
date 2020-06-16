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
import com.paypad.vuk507.interfaces.ReturnSizeCallback;
import com.paypad.vuk507.menu.category.interfaces.ReturnCategoryCallback;
import com.paypad.vuk507.menu.discount.interfaces.ReturnDiscountCallback;
import com.paypad.vuk507.model.Category;
import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.ShapeUtil;

import java.util.ArrayList;
import java.util.List;

public class DynamicCategorySelectAdapter extends RecyclerView.Adapter<DynamicCategorySelectAdapter.CategoryHolder> {

    private Context context;
    private List<Category> categories = new ArrayList<>();
    private List<Category> orgCategories = new ArrayList<>();

    private ReturnCategoryCallback returnCategoryCallback;

    public DynamicCategorySelectAdapter(Context context, List<Category> categories,
                                        ReturnCategoryCallback returnCategoryCallback) {
        this.context = context;
        this.categories.addAll(categories);
        this.orgCategories.addAll(categories);
        this.returnCategoryCallback = returnCategoryCallback;
    }

    @NonNull
    @Override
    public DynamicCategorySelectAdapter.CategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dynamic_item_list, parent, false);
        return new DynamicCategorySelectAdapter.CategoryHolder(itemView);
    }

    public class CategoryHolder extends RecyclerView.ViewHolder {

        private LinearLayout structItemll;
        private TextView colorfulTv;
        private TextView itemNameTv;

        private Category category;
        private int position;

        CategoryHolder(View view) {
            super(view);

            structItemll = view.findViewById(R.id.structItemll);
            colorfulTv = view.findViewById(R.id.colorfulTv);
            itemNameTv = view.findViewById(R.id.itemNameTv);

            structItemll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    returnCategoryCallback.OnReturn(category);
                }
            });
        }

        void setData(Category category, int position) {
            this.category = category;
            this.position = position;
            itemNameTv.setText(category.getName());
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
    public void onBindViewHolder(final CategoryHolder holder, final int position) {
        Category category = categories.get(position);
        holder.setData(category, position);
    }

    @Override
    public int getItemCount() {
        if(categories != null)
            return categories.size();
        else
            return 0;
    }

    public void updateAdapter(String searchText, ReturnSizeCallback returnSizeCallback) {
        if (searchText.trim().isEmpty()){
            categories = orgCategories;
        } else {

            List<Category> tempCategories= new ArrayList<>();
            for (Category cat : orgCategories) {
                if (cat.getName() != null &&
                        cat.getName().toLowerCase().contains(searchText.toLowerCase()))
                    tempCategories.add(cat);
            }
            categories = tempCategories;
        }

        this.notifyDataSetChanged();

        if (categories != null)
            returnSizeCallback.OnReturn(categories.size());
        else
            returnSizeCallback.OnReturn(0);
    }
}