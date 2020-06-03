package com.paypad.vuk507.menu.category;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.db.CategoryDBHelper;
import com.paypad.vuk507.interfaces.CustomDialogListener;
import com.paypad.vuk507.menu.category.interfaces.ReturnCategoryCallback;
import com.paypad.vuk507.model.Category;
import com.paypad.vuk507.utils.CustomDialogBox;

import java.util.ArrayList;
import java.util.List;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.CategoryHolder> {

    private Context context;
    private List<Category> categories = new ArrayList<>();
    private BaseFragment.FragmentNavigation fragmentNavigation;

    public CategoryListAdapter(Context context, List<Category> categories,
                               BaseFragment.FragmentNavigation fragmentNavigation) {
        this.context = context;
        this.categories.addAll(categories);
        this.fragmentNavigation = fragmentNavigation;
    }

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
        ImageView deleteImgv;
        Category category;

        int position;

        public CategoryHolder(View view) {
            super(view);

            categoryTv = view.findViewById(R.id.categoryTv);
            categoryItemCv = view.findViewById(R.id.categoryItemCv);
            deleteImgv = view.findViewById(R.id.deleteImgv);

            categoryItemCv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fragmentNavigation.pushFragment(new CategoryEditFragment(category, new ReturnCategoryCallback() {
                        @Override
                        public void OnReturn(Category category) {
                            categories.set(position, category);
                            notifyItemChanged(position);
                        }
                    }));
                }
            });

            deleteImgv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new CustomDialogBox.Builder((Activity) context)
                            .setMessage(context.getResources().getString(R.string.sure_to_delete_category))
                            .setNegativeBtnVisibility(View.VISIBLE)
                            .setNegativeBtnText(context.getResources().getString(R.string.cancel))
                            .setNegativeBtnBackground(context.getResources().getColor(R.color.Silver, null))
                            .setPositiveBtnVisibility(View.VISIBLE)
                            .setPositiveBtnText(context.getResources().getString(R.string.yes))
                            .setPositiveBtnBackground(context.getResources().getColor(R.color.bg_screen1, null))
                            .setDurationTime(0)
                            .isCancellable(true)
                            .setEditTextVisibility(View.GONE)
                            .OnPositiveClicked(new CustomDialogListener() {
                                @Override
                                public void OnClick() {
                                    CategoryDBHelper.deleteCategory(position);
                                    categories.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, getItemCount());
                                }
                            })
                            .OnNegativeClicked(new CustomDialogListener() {
                                @Override
                                public void OnClick() {

                                }
                            }).build();
                }
            });
        }

        public void setData(Category category, int position) {
            this.category = category;
            this.position = position;

            categoryTv.setText(category.getName());
        }
    }

    public void addCategory(Category category){
        //TODO - categories listesi sorunlu bakacagiz
        if(categories != null && category != null){
            categories.add(category);
            notifyItemInserted(categories.size() - 1);
        }
    }

    @Override
    public void onBindViewHolder(final CategoryHolder holder, final int position) {
        Category category = categories.get(position);
        holder.setData(category, position);
    }

    @Override
    public int getItemCount() {
        int size;
        size = categories.size();
        return size;
    }
}