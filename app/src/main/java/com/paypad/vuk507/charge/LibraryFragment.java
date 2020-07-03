package com.paypad.vuk507.charge;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.charge.interfaces.AmountCallback;
import com.paypad.vuk507.charge.interfaces.SaleCalculateCallback;
import com.paypad.vuk507.charge.sale.DynamicAmountFragment;
import com.paypad.vuk507.uiUtils.NDSpinner;
import com.paypad.vuk507.db.CategoryDBHelper;
import com.paypad.vuk507.db.DiscountDBHelper;
import com.paypad.vuk507.db.ProductDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.enums.ItemSpinnerEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.menu.category.CategoryEditFragment;
import com.paypad.vuk507.menu.category.adapters.CategorySelectListAdapter;
import com.paypad.vuk507.menu.category.interfaces.ReturnCategoryCallback;
import com.paypad.vuk507.menu.discount.DiscountEditFragment;
import com.paypad.vuk507.menu.discount.adapters.DiscountListAdapter;
import com.paypad.vuk507.menu.discount.interfaces.ReturnDiscountCallback;
import com.paypad.vuk507.menu.product.ProductEditFragment;
import com.paypad.vuk507.menu.product.adapters.ProductListAdapter;
import com.paypad.vuk507.menu.product.interfaces.ReturnItemCallback;
import com.paypad.vuk507.model.Category;
import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.model.Product;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.utils.CommonUtils;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.paypad.vuk507.constants.CustomConstants.LANGUAGE_TR;

public class LibraryFragment extends BaseFragment {

    private View mView;

    @BindView(R.id.searchEdittext)
    EditText searchEdittext;
    @BindView(R.id.searchCancelImgv)
    ImageView searchCancelImgv;

    @BindView(R.id.spinner)
    NDSpinner spinner;
    @BindView(R.id.addItemBtn)
    Button addItemBtn;
    @BindView(R.id.itemListRv)
    RecyclerView itemListRv;
    @BindView(R.id.categoryNameTv)
    AppCompatTextView categoryNameTv;
    @BindView(R.id.itemExistInfoTv)
    AppCompatTextView itemExistInfoTv;

    private Realm realm;
    private ArrayAdapter<String> spinnerAdapter;
    private ItemSpinnerEnum selectedSpinner;

    private ProductListAdapter productListAdapter;
    private DiscountListAdapter discountListAdapter;
    private CategorySelectListAdapter categorySelectListAdapter;

    private User user;
    private SaleCalculateCallback saleCalculateCallback;

    public LibraryFragment() {

    }

    public void setSaleCalculateCallback(SaleCalculateCallback saleCalculateCallback) {
        this.saleCalculateCallback = saleCalculateCallback;
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_library, container, false);
            ButterKnife.bind(this, mView);
            initVariables();
            initListeners();
            setSpinnerAdapter();
        }
        return mView;
    }

    private void initVariables() {
        realm = Realm.getDefaultInstance();
        initRecyclerView();
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

    private void initListeners() {

        addItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedSpinner.getId() == ItemSpinnerEnum.PRODUCTS.getId()){
                    mFragmentNavigation.pushFragment(new ProductEditFragment(null, new ReturnItemCallback() {
                        @Override
                        public void OnReturn(Product product, ItemProcessEnum processEnum) {
                            setProductAdapter(0);
                        }
                    }));
                }else if(selectedSpinner.getId() == ItemSpinnerEnum.DISCOUNTS.getId()){
                    mFragmentNavigation.pushFragment(new DiscountEditFragment(null, new ReturnDiscountCallback() {
                        @Override
                        public void OnReturn(Discount discount, ItemProcessEnum processType) {
                            setDiscountAdapter();
                        }
                    }));
                }else if(selectedSpinner.getId() == ItemSpinnerEnum.CATEGORIES.getId()){
                    mFragmentNavigation.pushFragment(new CategoryEditFragment(null, new ReturnCategoryCallback() {
                        @Override
                        public void OnReturn(Category category) {
                            setCategoryAdapter();
                        }
                    }));
                }
            }
        });
    }

    private void setSpinnerAdapter() {
        List<String> spinnerList = getDefaultSpinnerList();

        spinnerAdapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), android.R.layout.simple_spinner_item, spinnerList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSpinner = ItemSpinnerEnum.getById(position);

                if(selectedSpinner != null){
                    if(selectedSpinner.getId() == ItemSpinnerEnum.DISCOUNTS.getId()){
                        categoryNameTv.setVisibility(View.GONE);
                        setDiscountAdapter();
                    }else if(selectedSpinner.getId() == ItemSpinnerEnum.PRODUCTS.getId()){
                        categoryNameTv.setVisibility(View.GONE);
                        setProductAdapter(0);
                    }else if(selectedSpinner.getId() == ItemSpinnerEnum.CATEGORIES.getId()){
                        categoryNameTv.setVisibility(View.GONE);
                        setCategoryAdapter();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i("Info", "spinner_position:");

            }
        });
    }

    private List<String> getDefaultSpinnerList() {
        List<String> spinnerList = new ArrayList<>();

        ItemSpinnerEnum[] values = ItemSpinnerEnum.values();
        if(CommonUtils.getLanguage().equals(LANGUAGE_TR)){
            for(ItemSpinnerEnum item : values)
                spinnerList.add(item.getLabelTr());
        }else{
            for(ItemSpinnerEnum item : values)
                spinnerList.add(item.getLabelEn());
        }
        return spinnerList;
    }

    private void initRecyclerView(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        itemListRv.setLayoutManager(linearLayoutManager);
        itemListRv.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getContext()), LinearLayoutManager.VERTICAL));
    }

    public void setDiscountAdapter(){
        RealmResults<Discount> discounts = DiscountDBHelper.getAllDiscounts(user.getUsername());
        List<Discount> discountList = new ArrayList(discounts);

        if(discountList.size() == 0){
            itemExistInfoTv.setVisibility(View.VISIBLE);
            itemExistInfoTv.setText(getResources().getString(R.string.there_is_no_discount_defined));
        }else
            itemExistInfoTv.setVisibility(View.GONE);

        discountListAdapter = new DiscountListAdapter(getContext(), discountList, mFragmentNavigation, new ReturnDiscountCallback() {
            @Override
            public void OnReturn(Discount discount, ItemProcessEnum processType) {
                //setDiscountAdapter();
                saleCalculateCallback.onDiscountSelected(discount);
            }
        }, ItemProcessEnum.SELECTED);
        itemListRv.setAdapter(discountListAdapter);
    }

    private void setProductAdapter(long categoryId){

        RealmResults<Product> products;
        if(categoryId == 0)
            products = ProductDBHelper.getAllProducts(user.getUuid());
        else
            products = ProductDBHelper.getProductsByCategoryId(categoryId);

        List<Product> productList = new ArrayList(products);

        if(productList.size() == 0){
            itemExistInfoTv.setVisibility(View.VISIBLE);
            if(categoryId > 0){
                itemExistInfoTv.setText(getResources().getString(R.string.there_is_no_item_belongs_this_category));
            }else
                itemExistInfoTv.setText(getResources().getString(R.string.there_is_no_item_defined));
        }else
            itemExistInfoTv.setVisibility(View.GONE);

        productListAdapter = new ProductListAdapter(getContext(), productList, mFragmentNavigation, ItemProcessEnum.SELECTED, new ReturnItemCallback() {
            @Override
            public void OnReturn(Product product, ItemProcessEnum processEnum) {

                if(product.getAmount() == 0){

                    mFragmentNavigation.pushFragment(new DynamicAmountFragment(product, new AmountCallback() {
                        @Override
                        public void OnDynamicAmountReturn(double amount) {

                            Log.i("Info", "dynamic_amount:" + amount);

                            saleCalculateCallback.onProductSelected(product, amount, true);

                        }
                    }));


                }else {
                    saleCalculateCallback.onProductSelected(product, product.getAmount(), false);
                }

            }
        });
        itemListRv.setAdapter(productListAdapter);
    }

    private void setCategoryAdapter(){
        categoryNameTv.setVisibility(View.GONE);
        RealmResults<Category> categories = CategoryDBHelper.getAllCategories(user.getUsername());
        List<Category> categoryList = new ArrayList(categories);

        if(categoryList.size() == 0){
            itemExistInfoTv.setVisibility(View.VISIBLE);
            itemExistInfoTv.setText(getResources().getString(R.string.there_is_no_category_defined));
        }else
            itemExistInfoTv.setVisibility(View.GONE);

        categorySelectListAdapter = new CategorySelectListAdapter(getContext(), categoryList, mFragmentNavigation, new ReturnCategoryCallback() {
            @Override
            public void OnReturn(Category category) {
                categoryNameTv.setVisibility(View.VISIBLE);
                categoryNameTv.setText(Objects.requireNonNull(getContext()).getResources().getString(R.string.category)
                        .concat(" : ")
                        .concat(category.getName()));
                setProductAdapter(category.getId());
            }
        });
        itemListRv.setAdapter(categorySelectListAdapter);
    }

    public ProductListAdapter getProductListAdapter() {
        return productListAdapter;
    }

    public DiscountListAdapter getDiscountListAdapter() {
        return discountListAdapter;
    }

    public CategorySelectListAdapter getCategorySelectListAdapter() {
        return categorySelectListAdapter;
    }
}