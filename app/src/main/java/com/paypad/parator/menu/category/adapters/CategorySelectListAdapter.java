package com.paypad.parator.menu.category.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.interfaces.ReturnSizeCallback;
import com.paypad.parator.menu.category.interfaces.ReturnCategoryCallback;
import com.paypad.parator.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategorySelectListAdapter extends RecyclerView.Adapter<CategorySelectListAdapter.CategoryHolder> {

    private Context context;
    private List<Category> categories = new ArrayList<>();
    private List<Category> orgCategories = new ArrayList<>();

    private BaseFragment.FragmentNavigation fragmentNavigation;
    private ReturnCategoryCallback returnCategoryCallback;

    public CategorySelectListAdapter(Context context, List<Category> categories,
                                     BaseFragment.FragmentNavigation fragmentNavigation,
                                     ReturnCategoryCallback returnCategoryCallback) {
        this.context = context;
        this.categories.addAll(categories);
        this.orgCategories.addAll(categories);
        this.fragmentNavigation = fragmentNavigation;
        this.returnCategoryCallback = returnCategoryCallback;
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_list, parent, false);
        return new CategoryHolder(itemView);
    }

    public class CategoryHolder extends RecyclerView.ViewHolder {

        CardView categoryItemCv;
        TextView categoryTv;
        Category category;

        int position;

        public CategoryHolder(View view) {
            super(view);

            categoryTv = view.findViewById(R.id.categoryTv);
            categoryItemCv = view.findViewById(R.id.categoryItemCv);

            categoryItemCv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    returnCategoryCallback.OnReturn(category);
                }
            });
        }

        public void setData(Category category, int position) {
            this.category = category;
            this.position = position;
            categoryTv.setText(category.getName());
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

            List<Category> tempCategoryList = new ArrayList<>();
            for (Category category : orgCategories) {
                if (category.getName() != null &&
                        category.getName().toLowerCase().contains(searchText.toLowerCase()))
                    tempCategoryList.add(category);
            }
            categories = tempCategoryList;
        }

        this.notifyDataSetChanged();

        if (categories != null)
            returnSizeCallback.OnReturn(categories.size());
        else
            returnSizeCallback.OnReturn(0);
    }
}