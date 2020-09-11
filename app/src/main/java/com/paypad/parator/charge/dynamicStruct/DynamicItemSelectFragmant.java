package com.paypad.parator.charge.dynamicStruct;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.charge.dynamicStruct.adapters.DynamicCategorySelectAdapter;
import com.paypad.parator.charge.dynamicStruct.adapters.DynamicDiscountSelectAdapter;
import com.paypad.parator.charge.dynamicStruct.adapters.DynamicPaymentSelectAdapter;
import com.paypad.parator.charge.dynamicStruct.adapters.DynamicProductSelectAdapter;
import com.paypad.parator.charge.dynamicStruct.adapters.DynamicTaxSelectAdapter;
import com.paypad.parator.charge.dynamicStruct.interfaces.ReturnPaymentCallback;
import com.paypad.parator.db.CategoryDBHelper;
import com.paypad.parator.db.DiscountDBHelper;
import com.paypad.parator.db.DynamicBoxModelDBHelper;
import com.paypad.parator.db.ProductDBHelper;
import com.paypad.parator.db.TaxDBHelper;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.enums.DynamicStructEnum;
import com.paypad.parator.enums.ItemProcessEnum;
import com.paypad.parator.enums.PaymentTypeEnum;
import com.paypad.parator.enums.ProcessDirectionEnum;
import com.paypad.parator.enums.TaxRateEnum;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.interfaces.CustomDialogListener;
import com.paypad.parator.interfaces.ReturnSizeCallback;
import com.paypad.parator.menu.category.interfaces.ReturnCategoryCallback;
import com.paypad.parator.menu.discount.interfaces.ReturnDiscountCallback;
import com.paypad.parator.menu.product.interfaces.ReturnItemCallback;
import com.paypad.parator.menu.settings.checkoutoptions.PaymentTypesEditFragment;
import com.paypad.parator.menu.tax.interfaces.ReturnTaxCallback;
import com.paypad.parator.model.Category;
import com.paypad.parator.model.Discount;
import com.paypad.parator.model.DynamicBoxModel;
import com.paypad.parator.model.Product;
import com.paypad.parator.model.TaxModel;
import com.paypad.parator.model.User;
import com.paypad.parator.utils.CommonUtils;
import com.paypad.parator.utils.CustomDialogBoxVert;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import io.realm.RealmResults;

import static android.content.Context.MODE_PRIVATE;

public class DynamicItemSelectFragmant extends BottomSheetDialogFragment {

    RecyclerView structRv;
    EditText searchEdittext;
    TextView searchResultTv;

    private DynamicStructEnum dynamicStructEnum;

    private DynamicItemSelectListener dynamicItemSelectListener;

    private User user;
    private long categoryId;

    private DynamicProductSelectAdapter dynamicProductSelectAdapter;
    private DynamicDiscountSelectAdapter dynamicDiscountSelectAdapter;
    private DynamicCategorySelectAdapter dynamicCategorySelectAdapter;
    private DynamicPaymentSelectAdapter dynamicPaymentSelectAdapter;
    private DynamicTaxSelectAdapter dynamicTaxSelectAdapter;

    private List<Product> productList;
    private List<Discount> discountList;
    private List<Category> categoryList;
    private List<TaxModel> taxModelList;
    private List<PaymentTypeEnum> paymentTypes;

    private SharedPreferences loginPreferences;
    private Context mContext;
    private BaseFragment.FragmentNavigation fragmentNavigation;

    public DynamicItemSelectFragmant(DynamicStructEnum dynamicStructEnum, BaseFragment.FragmentNavigation fragmentNavigation) {
        this.dynamicStructEnum = dynamicStructEnum;
        this.fragmentNavigation = fragmentNavigation;
    }

    public interface DynamicItemSelectListener {
        void onProductClick(Product product);
        void onDiscountClick(Discount discount);
        void onCategoryClick(Category category);
        void onTaxClick(TaxModel taxModel);
        void onPaymentClick(PaymentTypeEnum paymentType);
        void onCategoryProductSelected(Product product);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
        mContext = null;
    }

    @Subscribe(sticky = true)
    public void accountHolderUserReceived(UserBus userBus){
        user = userBus.getUser();
        if(user == null)
            user = UserDBHelper.getUserFromCache(mContext);
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
        View contentView = View.inflate(mContext, R.layout.fragment_dynamic_item_select, null);

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

        loginPreferences = mContext.getSharedPreferences("disabledPaymentTypes", MODE_PRIVATE);


        closeImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        if(dynamicStructEnum == DynamicStructEnum.PRODUCT_SET){
            if(categoryId > 0)
                setCategoryProductsAdapter();
            else
                setProductAdapter();
        }else if(dynamicStructEnum == DynamicStructEnum.DISCOUNT_SET){
            setDiscountAdapter();
        }else if(dynamicStructEnum == DynamicStructEnum.CATEGORY_SET){
            setCategoryAdapter();
        }else if(dynamicStructEnum == DynamicStructEnum.PAYMENT_SET){
            setPaymentAdapter();
        }else if(dynamicStructEnum == DynamicStructEnum.TAX_SET){
            setTaxAdapter();
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
                CommonUtils.showKeyboard(mContext,false, searchEdittext);
            }
        });
    }

    public void updateAdapter(String searchText) {

        if (searchText != null) {
            if(dynamicStructEnum == DynamicStructEnum.PRODUCT_SET && dynamicProductSelectAdapter != null){
                dynamicProductSelectAdapter.updateAdapter(searchText, new ReturnSizeCallback() {
                    @Override
                    public void OnReturn(int size) {
                        if (size == 0 && productList != null && productList.size() > 0)
                            searchResultTv.setVisibility(View.VISIBLE);
                        else
                            searchResultTv.setVisibility(View.GONE);
                    }
                });
            }else if(dynamicStructEnum == DynamicStructEnum.DISCOUNT_SET && dynamicDiscountSelectAdapter != null){
                dynamicDiscountSelectAdapter.updateAdapter(searchText, new ReturnSizeCallback() {
                    @Override
                    public void OnReturn(int size) {
                        if (size == 0 && discountList != null && discountList.size() > 0)
                            searchResultTv.setVisibility(View.VISIBLE);
                        else
                            searchResultTv.setVisibility(View.GONE);
                    }
                });
            }else if(dynamicStructEnum == DynamicStructEnum.CATEGORY_SET && dynamicCategorySelectAdapter != null){
                dynamicCategorySelectAdapter.updateAdapter(searchText, new ReturnSizeCallback() {
                    @Override
                    public void OnReturn(int size) {
                        if (size == 0 && categoryList != null && categoryList.size() > 0)
                            searchResultTv.setVisibility(View.VISIBLE);
                        else
                            searchResultTv.setVisibility(View.GONE);
                    }
                });
            }else if(dynamicStructEnum == DynamicStructEnum.TAX_SET ){

                dynamicTaxSelectAdapter.updateAdapter(searchText, new ReturnSizeCallback() {
                    @Override
                    public void OnReturn(int size) {
                        if (size == 0 && taxModelList != null && taxModelList.size() > 0)
                            searchResultTv.setVisibility(View.VISIBLE);
                        else
                            searchResultTv.setVisibility(View.GONE);
                    }
                });

            } else if(dynamicStructEnum == DynamicStructEnum.PAYMENT_SET ){

                dynamicPaymentSelectAdapter.updateAdapter(searchText, new ReturnSizeCallback() {
                    @Override
                    public void OnReturn(int size) {
                        if (size == 0 && paymentTypes != null && paymentTypes.size() > 0)
                            searchResultTv.setVisibility(View.VISIBLE);
                        else
                            searchResultTv.setVisibility(View.GONE);
                    }
                });

            }
        }
    }

    private void setProductAdapter(){
        RealmResults<Product> products;
        products = ProductDBHelper.getAllProducts(user.getId());
        productList = new ArrayList(products);

        boolean dataExist = productList.size() > 0;

        for(Iterator<Product> it = productList.iterator(); it.hasNext();) {
            Product product = it.next();
            DynamicBoxModel dynamicBoxModel = DynamicBoxModelDBHelper.getDynamicBoxModel(DynamicStructEnum.PRODUCT_SET.getId(), product.getId(), user.getId());

            if(dynamicBoxModel != null)
                it.remove();
        }

        if(dataExist){
            if(productList.size() == 0){
                CommonUtils.showToastShort(mContext, getResources().getString(R.string.all_products_added_to_dynamic_boxes));
                dismiss();
                return;
            }
        }else {
            CommonUtils.showToastShort(mContext, getResources().getString(R.string.there_is_no_item_defined));
            dismiss();
            return;
        }

        dynamicProductSelectAdapter = new DynamicProductSelectAdapter(mContext, productList, new ReturnItemCallback() {
            @Override
            public void OnReturn(Product product, ItemProcessEnum processEnum) {
                dynamicItemSelectListener.onProductClick(product);
                //productSelectListener.onPClick(product);
            }
        });
        structRv.setAdapter(dynamicProductSelectAdapter);
    }

    private void setCategoryProductsAdapter(){
        RealmResults<Product> products;
        products = ProductDBHelper.getProductsByCategoryId(categoryId);
        productList = new ArrayList(products);

        if(productList.size() == 0){
            CommonUtils.showToastShort(mContext, getResources().getString(R.string.there_is_no_item_belongs_this_category));
            dismiss();
            return;
        }

        dynamicProductSelectAdapter = new DynamicProductSelectAdapter(mContext, productList, new ReturnItemCallback() {
            @Override
            public void OnReturn(Product product, ItemProcessEnum processEnum) {
                dynamicItemSelectListener.onCategoryProductSelected(product);
            }
        });
        structRv.setAdapter(dynamicProductSelectAdapter);
    }

    public void setCategoryId(long categoryId){
        this.categoryId = categoryId;
    }

    private void setDiscountAdapter(){
        RealmResults<Discount> discounts = DiscountDBHelper.getAllDiscounts(user.getId());
        discountList = new ArrayList(discounts);

        boolean dataExist = discountList.size() > 0;

        for(Iterator<Discount> it = discountList.iterator(); it.hasNext();) {
            Discount discount = it.next();
            DynamicBoxModel dynamicBoxModel = DynamicBoxModelDBHelper.getDynamicBoxModel(DynamicStructEnum.DISCOUNT_SET.getId(), discount.getId(), user.getId());

            if(dynamicBoxModel != null)
                it.remove();
        }

        if(dataExist){
            if(discountList.size() == 0){
                CommonUtils.showToastShort(mContext, getResources().getString(R.string.all_discounts_added_to_dynamic_boxes));
                dismiss();
                return;
            }
        }else {
            CommonUtils.showToastShort(mContext, getResources().getString(R.string.there_is_no_discount_defined));
            dismiss();
            return;
        }

        dynamicDiscountSelectAdapter = new DynamicDiscountSelectAdapter(mContext, discountList, new ReturnDiscountCallback() {
            @Override
            public void OnReturn(Discount discount, ItemProcessEnum processType) {
                dynamicItemSelectListener.onDiscountClick(discount);
                //discountSelectListener.onDClick(discount);
            }
        });
        structRv.setAdapter(dynamicDiscountSelectAdapter);
    }

    private void setCategoryAdapter(){
        RealmResults<Category> categories = CategoryDBHelper.getAllCategories(user.getId());
        categoryList = new ArrayList(categories);

        boolean dataExist = categoryList.size() > 0;

        for(Iterator<Category> it = categoryList.iterator(); it.hasNext();) {
            Category category = it.next();
            DynamicBoxModel dynamicBoxModel = DynamicBoxModelDBHelper.getDynamicBoxModel(DynamicStructEnum.CATEGORY_SET.getId(), category.getId(), user.getId());

            if(dynamicBoxModel != null)
                it.remove();
        }

        if(dataExist){
            if(categoryList.size() == 0){
                CommonUtils.showToastShort(mContext, getResources().getString(R.string.all_categories_added_to_dynamic_boxes));
                dismiss();
                return;
            }
        }else {
            CommonUtils.showToastShort(mContext, getResources().getString(R.string.there_is_no_category_defined));
            dismiss();
            return;
        }

        dynamicCategorySelectAdapter = new DynamicCategorySelectAdapter(mContext, categoryList, new ReturnCategoryCallback() {
            @Override
            public void OnReturn(Category category) {
                dynamicItemSelectListener.onCategoryClick(category);
            }
        });
        structRv.setAdapter(dynamicCategorySelectAdapter);
    }

    private void setTaxAdapter(){
        RealmResults<TaxModel> taxModels = TaxDBHelper.getAllTaxes(user.getId());
        taxModelList = new ArrayList(taxModels);

        for(TaxRateEnum taxRateEnum : TaxRateEnum.values()){
            TaxModel taxModel = new TaxModel();
            taxModel.setId(taxRateEnum.getId());
            taxModel.setName(taxRateEnum.getLabel());
            taxModel.setTaxRate(taxRateEnum.getRateValue());
            taxModelList.add(taxModel);
        }

        boolean dataExist = taxModelList.size() > 0;

        for(Iterator<TaxModel> it = taxModelList.iterator(); it.hasNext();) {
            TaxModel taxModel = it.next();
            DynamicBoxModel dynamicBoxModel = DynamicBoxModelDBHelper.getDynamicBoxModel(DynamicStructEnum.TAX_SET.getId(), taxModel.getId(), user.getId());

            if(dynamicBoxModel != null)
                it.remove();
        }

        if(dataExist){
            if(taxModelList.size() == 0){
                CommonUtils.showToastShort(mContext, getResources().getString(R.string.all_taxes_added_to_dynamic_boxes));
                dismiss();
                return;
            }
        }else {
            CommonUtils.showToastShort(mContext, getResources().getString(R.string.there_is_no_tax_defined));
            dismiss();
            return;
        }

        dynamicTaxSelectAdapter = new DynamicTaxSelectAdapter(mContext, taxModelList, new ReturnTaxCallback() {
            @Override
            public void OnReturn(TaxModel taxModel, ItemProcessEnum processEnum) {
                dynamicItemSelectListener.onTaxClick(taxModel);
            }
        });
        structRv.setAdapter(dynamicTaxSelectAdapter);
    }

    private void setPaymentAdapter(){
        PaymentTypeEnum[] paymentTypeEnums = PaymentTypeEnum.values();
        paymentTypes = new ArrayList<>(Arrays.asList(paymentTypeEnums));

        for(Iterator<PaymentTypeEnum> it = paymentTypes.iterator(); it.hasNext();) {
            PaymentTypeEnum paymentTypeEnum = it.next();
            DynamicBoxModel dynamicBoxModel = DynamicBoxModelDBHelper.getDynamicBoxModel(DynamicStructEnum.PAYMENT_SET.getId(),
                    paymentTypeEnum.getId(), user.getId());

            if(dynamicBoxModel != null)
                it.remove();
        }

        if(paymentTypes.size() == 0){
            CommonUtils.showToastShort(mContext, getResources().getString(R.string.all_payments_added_to_dynamic_boxes));
            dismiss();
            return;
        }

        dynamicPaymentSelectAdapter = new DynamicPaymentSelectAdapter(mContext, ProcessDirectionEnum.DIRECTION_FAST_MENU, paymentTypes, new ReturnPaymentCallback() {
            @Override
            public void onReturn(PaymentTypeEnum paymentType) {
                if(!loginPreferences.getBoolean(String.valueOf(paymentType.getId()), false)){


                    new CustomDialogBoxVert.Builder((Activity) mContext)
                            .setMessage(mContext.getResources().getString(R.string.preview_payment_types_desc))
                            .setNegativeBtnVisibility(View.VISIBLE)
                            .setPositiveBtnVisibility(View.VISIBLE)
                            .setPositiveBtnText(mContext.getResources().getString(R.string.preview_payment_types))
                            .setNegativeBtnText(mContext.getResources().getString(R.string.cancel))
                            .setPositiveBtnBackground(mContext.getResources().getColor(R.color.DodgerBlue, null))
                            .setNegativeBtnBackground(mContext.getResources().getColor(R.color.custom_btn_bg_color, null))
                            .setDurationTime(0)
                            .isCancellable(false)
                            .setEdittextVisibility(View.GONE)
                            .setpBtnTextColor(mContext.getResources().getColor(R.color.White, null))
                            .setnBtnTextColor(mContext.getResources().getColor(R.color.DodgerBlue, null))
                            .OnPositiveClicked(new CustomDialogListener() {
                                @Override
                                public void OnClick() {
                                    fragmentNavigation.pushFragment(new PaymentTypesEditFragment());
                                    dismiss();
                                }
                            }).OnNegativeClicked(new CustomDialogListener() {
                                @Override
                                public void OnClick() {
                                    dismiss();
                                }
                            }).build();
                    return;
                }

                dynamicItemSelectListener.onPaymentClick(paymentType);
            }
        });
        dynamicPaymentSelectAdapter.setOrgPaymentTypes(paymentTypes);
        structRv.setAdapter(dynamicPaymentSelectAdapter);
    }

    public void setDynamicItemSelectListener(DynamicItemSelectListener dynamicItemSelectListener) {
        this.dynamicItemSelectListener = dynamicItemSelectListener;
    }
}