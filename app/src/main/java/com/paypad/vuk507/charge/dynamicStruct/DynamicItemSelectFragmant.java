package com.paypad.vuk507.charge.dynamicStruct;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.charge.dynamicStruct.adapters.DynamicCategorySelectAdapter;
import com.paypad.vuk507.charge.dynamicStruct.adapters.DynamicDiscountSelectAdapter;
import com.paypad.vuk507.charge.dynamicStruct.adapters.DynamicProductSelectAdapter;
import com.paypad.vuk507.db.CategoryDBHelper;
import com.paypad.vuk507.db.DiscountDBHelper;
import com.paypad.vuk507.db.DynamicBoxModelDBHelper;
import com.paypad.vuk507.db.ProductDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.DynamicStructEnum;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.interfaces.ReturnSizeCallback;
import com.paypad.vuk507.menu.category.interfaces.ReturnCategoryCallback;
import com.paypad.vuk507.menu.discount.interfaces.ReturnDiscountCallback;
import com.paypad.vuk507.menu.product.adapters.ProductListAdapter;
import com.paypad.vuk507.menu.product.interfaces.ReturnItemCallback;
import com.paypad.vuk507.model.Category;
import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.model.DynamicBoxModel;
import com.paypad.vuk507.model.Product;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.utils.CommonUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.realm.RealmResults;

public class DynamicItemSelectFragmant extends BottomSheetDialogFragment {

    RecyclerView structRv;
    EditText searchEdittext;
    TextView searchResultTv;

    private DynamicStructEnum dynamicStructEnum;

    private ProductSelectListener productSelectListener;
    private DiscountSelectListener discountSelectListener;
    private CategorySelectListener categorySelectListener;
    private User user;
    private long categoryId;

    private DynamicProductSelectAdapter dynamicProductSelectAdapter;
    private DynamicDiscountSelectAdapter dynamicDiscountSelectAdapter;
    private DynamicCategorySelectAdapter dynamicCategorySelectAdapter;

    private List<Product> productList;
    private List<Discount> discountList;
    private List<Category> categoryList;

    public DynamicItemSelectFragmant(DynamicStructEnum dynamicStructEnum) {
        this.dynamicStructEnum = dynamicStructEnum;
    }

    public interface ProductSelectListener {
        void onPClick(Product product);
    }

    public interface DiscountSelectListener{
        void onDClick(Discount discount);
    }

    public interface CategorySelectListener{
        void onCClick(Category category);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(sticky = true)
    public void accountHolderUserReceived(UserBus userBus){
        user = userBus.getUser();
        if(user == null)
            user = UserDBHelper.getUserFromCache(getContext());
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_dynamic_item_select, null);

        dialog.setContentView(contentView);

        View parent = ((View) contentView.getParent());

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) parent.getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }

        parent.setBackgroundColor(getResources().getColor(android.R.color.transparent, null));

        structRv = contentView.findViewById(R.id.structRv);
        searchEdittext = contentView.findViewById(R.id.searchEdittext);
        ImageView addItemImgv = contentView.findViewById(R.id.addItemImgv);
        addItemImgv.setVisibility(View.GONE);
        ImageView searchCancelImgv = contentView.findViewById(R.id.searchCancelImgv);
        searchResultTv = contentView.findViewById(R.id.searchResultTv);

        ImageButton closeImgBtn = contentView.findViewById(R.id.closeImgBtn);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        structRv.setLayoutManager(linearLayoutManager);


        closeImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        if(dynamicStructEnum == DynamicStructEnum.PRODUCT_SET){
            setProductAdapter();
        }else if(dynamicStructEnum == DynamicStructEnum.DISCOUNT_SET){
            setDiscountAdapter();
        }else if(dynamicStructEnum == DynamicStructEnum.CATEGORY_SET){
            setCategoryAdapter();
        }

        searchEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && !s.toString().trim().isEmpty()) {
                    updateAdapter(s.toString());
                    searchCancelImgv.setVisibility(View.VISIBLE);
                } else {
                    updateAdapter("");
                    searchCancelImgv.setVisibility(View.GONE);
                }
            }
        });

        searchCancelImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEdittext.setText("");
                searchCancelImgv.setVisibility(View.GONE);
                CommonUtils.showKeyboard(getContext(),false, searchEdittext);
            }
        });
    }

    public void updateAdapter(String searchText) {

        if (searchText != null) {
            if(dynamicStructEnum == DynamicStructEnum.PRODUCT_SET && dynamicProductSelectAdapter != null){
                dynamicProductSelectAdapter.updateAdapter(searchText, new ReturnSizeCallback() {
                    @Override
                    public void OnReturn(int size) {
                        if (size == 0 && productList.size() > 0)
                            searchResultTv.setVisibility(View.VISIBLE);
                        else
                            searchResultTv.setVisibility(View.GONE);
                    }
                });
            }else if(dynamicStructEnum == DynamicStructEnum.DISCOUNT_SET && dynamicDiscountSelectAdapter != null){
                dynamicDiscountSelectAdapter.updateAdapter(searchText, new ReturnSizeCallback() {
                    @Override
                    public void OnReturn(int size) {
                        if (size == 0 && discountList.size() > 0)
                            searchResultTv.setVisibility(View.VISIBLE);
                        else
                            searchResultTv.setVisibility(View.GONE);
                    }
                });
            }else if(dynamicStructEnum == DynamicStructEnum.CATEGORY_SET && dynamicCategorySelectAdapter != null){
                dynamicCategorySelectAdapter.updateAdapter(searchText, new ReturnSizeCallback() {
                    @Override
                    public void OnReturn(int size) {
                        if (size == 0 && categoryList.size() > 0)
                            searchResultTv.setVisibility(View.VISIBLE);
                        else
                            searchResultTv.setVisibility(View.GONE);
                    }
                });
            }else if(dynamicStructEnum == DynamicStructEnum.FUNCTION_SET ){
                // TODO - Odeme icin search kontrolleri burada olacak
            }
        }
    }

    private void setProductAdapter(){
        RealmResults<Product> products;
        if(categoryId == 0)
            products = ProductDBHelper.getAllProducts(user.getUuid());
        else
            products = ProductDBHelper.getProductsByCategoryId(categoryId);

        productList = new ArrayList(products);

        boolean dataExist = productList.size() > 0;

        for(Iterator<Product> it = productList.iterator(); it.hasNext();) {
            Product product = it.next();
            DynamicBoxModel dynamicBoxModel = DynamicBoxModelDBHelper.getDynamicBoxModel(DynamicStructEnum.PRODUCT_SET.getId(), product.getId(), user.getUuid());

            if(dynamicBoxModel != null)
                it.remove();
        }

        if(dataExist){
            if(productList.size() == 0){
                CommonUtils.showToastShort(getContext(), getResources().getString(R.string.all_products_added_to_dynamic_boxes));
                dismiss();
                return;
            }
        }else {
            if(categoryId == 0){
                CommonUtils.showToastShort(getContext(), getResources().getString(R.string.there_is_no_item_defined));
                dismiss();
                return;
            }else {
                CommonUtils.showToastShort(getContext(), getResources().getString(R.string.there_is_no_item_belongs_this_category));
                dismiss();
                return;
            }
        }

        dynamicProductSelectAdapter = new DynamicProductSelectAdapter(getContext(), productList, new ReturnItemCallback() {
            @Override
            public void OnReturn(Product product, ItemProcessEnum processEnum) {
                productSelectListener.onPClick(product);
            }
        });
        structRv.setAdapter(dynamicProductSelectAdapter);
    }

    public void setCategoryId(long categoryId){
        this.categoryId = categoryId;
    }

    private void setDiscountAdapter(){
        RealmResults<Discount> discounts = DiscountDBHelper.getAllDiscounts(user.getUsername());
        discountList = new ArrayList(discounts);

        boolean dataExist = discountList.size() > 0;

        for(Iterator<Discount> it = discountList.iterator(); it.hasNext();) {
            Discount discount = it.next();
            DynamicBoxModel dynamicBoxModel = DynamicBoxModelDBHelper.getDynamicBoxModel(DynamicStructEnum.DISCOUNT_SET.getId(), discount.getId(), user.getUuid());

            if(dynamicBoxModel != null)
                it.remove();
        }

        if(dataExist){
            if(discountList.size() == 0){
                CommonUtils.showToastShort(getContext(), getResources().getString(R.string.all_discounts_added_to_dynamic_boxes));
                dismiss();
                return;
            }
        }else {
            CommonUtils.showToastShort(getContext(), getResources().getString(R.string.there_is_no_discount_defined));
            dismiss();
            return;
        }

        dynamicDiscountSelectAdapter = new DynamicDiscountSelectAdapter(getContext(), discountList, new ReturnDiscountCallback() {
            @Override
            public void OnReturn(Discount discount) {
                discountSelectListener.onDClick(discount);
            }
        });
        structRv.setAdapter(dynamicDiscountSelectAdapter);
    }

    private void setCategoryAdapter(){
        RealmResults<Category> categories = CategoryDBHelper.getAllCategories(user.getUsername());
        categoryList = new ArrayList(categories);

        if(categoryList.size() == 0){
            CommonUtils.showToastShort(getContext(), getResources().getString(R.string.there_is_no_category_defined));
            dismiss();
            return;
        }

        dynamicCategorySelectAdapter = new DynamicCategorySelectAdapter(getContext(), categoryList, new ReturnCategoryCallback() {
            @Override
            public void OnReturn(Category category) {
                categorySelectListener.onCClick(category);
            }
        });
        structRv.setAdapter(dynamicCategorySelectAdapter);
    }

    public void setProductSelectListener(ProductSelectListener listener){ productSelectListener = listener;}

    public void setDiscountSelectListener(DiscountSelectListener listener){ discountSelectListener = listener;}

    public void setCategorySelectListener(CategorySelectListener listener){ categorySelectListener = listener;}
}