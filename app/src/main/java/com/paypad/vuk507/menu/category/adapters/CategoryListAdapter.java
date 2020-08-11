package com.paypad.vuk507.menu.category.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.interfaces.ReturnSizeCallback;
import com.paypad.vuk507.menu.category.CategoryEditFragment;
import com.paypad.vuk507.menu.category.interfaces.ReturnCategoryCallback;
import com.paypad.vuk507.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.CategoryHolder> {

    private Context context;
    private List<Category> categories = new ArrayList<>();
    private List<Category> orgCategories = new ArrayList<>();

    private BaseFragment.FragmentNavigation fragmentNavigation;
    private ReturnCategoryCallback returnCategoryCallback;

    public CategoryListAdapter(Context context, List<Category> categories,
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
                    fragmentNavigation.pushFragment(new CategoryEditFragment(category, new ReturnCategoryCallback() {
                        @Override
                        public void OnReturn(Category category) {
                            returnCategoryCallback.OnReturn(category);
                        }
                    }));
                }
            });
        }

        public void setData(Category category, int position) {
            this.category = category;
            this.position = position;
            categoryTv.setText(category.getName());

            Log.i("Info", "Category_position:" + position);
            try{

                Log.i("Info", "Category_name:" + category.getName());
            }catch (Exception e){

            }
        }
    }

    public void categoryRemoveResult(int position){
        categories.remove(position);
        //this.notifyItemRemoved(position);
        //this.notifyItemRangeChanged(position, getItemCount());
        this.notifyDataSetChanged();
    }

    public void categoryChangedResult(int position){
        this.notifyItemChanged(position);
        //notifyDataSetChanged();
    }

    public void addCategory(Category category){
        //TODO - categories listesi sorunlu bakacagiz
        if(categories != null && category != null){
            categories.add(category);
            //this.notifyItemInserted(categories.size() - 1);
            this.notifyDataSetChanged();
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