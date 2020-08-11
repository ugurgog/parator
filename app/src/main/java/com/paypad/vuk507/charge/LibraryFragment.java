package com.paypad.vuk507.charge;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.charge.interfaces.AmountCallback;
import com.paypad.vuk507.charge.interfaces.OnKeyboardVisibilityListener;
import com.paypad.vuk507.charge.interfaces.SaleCalculateCallback;
import com.paypad.vuk507.charge.sale.DynamicAmountFragment;
import com.paypad.vuk507.db.CategoryDBHelper;
import com.paypad.vuk507.db.DiscountDBHelper;
import com.paypad.vuk507.db.ProductDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.enums.ItemSpinnerEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.interfaces.ReturnSizeCallback;
import com.paypad.vuk507.interfaces.ReturnViewCallback;
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
import com.paypad.vuk507.uiUtils.NDSpinner;
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

public class LibraryFragment extends BaseFragment implements OnKeyboardVisibilityListener,
        ReturnViewCallback {

    private View mView;

    @BindView(R.id.searchEdittext)
    EditText searchEdittext;
    @BindView(R.id.searchCancelImgv)
    ImageView searchCancelImgv;
    @BindView(R.id.searchResultTv)
    TextView searchResultTv;

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
    @BindView(R.id.mainll)
    LinearLayout mainll;

    private Realm realm;
    private ArrayAdapter<String> spinnerAdapter;
    private ItemSpinnerEnum selectedSpinner;

    private ProductListAdapter productListAdapter;
    private DiscountListAdapter discountListAdapter;
    private CategorySelectListAdapter categorySelectListAdapter;

    private User user;
    private SaleCalculateCallback saleCalculateCallback;
    private ReturnViewCallback returnViewCallback;
    private ItemSpinnerEnum spinnerType = ItemSpinnerEnum.PRODUCTS;

    private List<Discount> discountList;
    private List<Product> productList;
    private List<Category> categoryList;

    public LibraryFragment() {

    }

    public void setSaleCalculateCallback(SaleCalculateCallback saleCalculateCallback) {
        this.saleCalculateCallback = saleCalculateCallback;
    }

    public void setReturnViewCallback(ReturnViewCallback returnViewCallback) {
        this.returnViewCallback = returnViewCallback;
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
        setKeyboardVisibilityListener(this);
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

    @SuppressLint("ClickableViewAccessibility")
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
                //Objects.requireNonNull(getActivity()).findViewById(R.id.tablayout).setVisibility(View.VISIBLE);
            }
        });

        searchEdittext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //Objects.requireNonNull(getActivity()).findViewById(R.id.tablayout).setVisibility(View.GONE);
                return false;
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
                spinnerType = ItemSpinnerEnum.getById(position);

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

    void setDiscountAdapter(){
        RealmResults<Discount> discounts = DiscountDBHelper.getAllDiscounts(user.getUsername());
        discountList = new ArrayList(discounts);

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

    void setProductAdapter(long categoryId){

        RealmResults<Product> products;
        if(categoryId == 0)
            products = ProductDBHelper.getAllProducts(user.getUuid());
        else
            products = ProductDBHelper.getProductsByCategoryId(categoryId);

        productList = new ArrayList(products);

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
                            saleCalculateCallback.onProductSelected(product, amount, true);

                        }
                    }));
                }else {
                    saleCalculateCallback.onProductSelected(product, product.getAmount(), false);
                }
            }
        });
        productListAdapter.setReturnViewCallback(this);
        itemListRv.setAdapter(productListAdapter);
    }

    void setCategoryAdapter(){
        categoryNameTv.setVisibility(View.GONE);
        RealmResults<Category> categories = CategoryDBHelper.getAllCategories(user.getUsername());
        categoryList = new ArrayList(categories);

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
                spinnerType = ItemSpinnerEnum.PRODUCTS;
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

    public void updateAdapter(String searchText) {
        if(spinnerType != null && searchText != null){
            if(spinnerType.getId() == ItemSpinnerEnum.DISCOUNTS.getId()){

                if(discountListAdapter != null){
                    discountListAdapter.updateAdapter(searchText, new ReturnSizeCallback() {
                        @Override
                        public void OnReturn(int size) {
                            if(size == 0 && (discountList != null && discountList.size() > 0))
                                searchResultTv.setVisibility(View.VISIBLE);
                            else
                                searchResultTv.setVisibility(View.GONE);
                        }
                    });
                }

            }else if(spinnerType.getId() == ItemSpinnerEnum.PRODUCTS.getId()){

                if(productListAdapter != null){
                    productListAdapter.updateAdapter(searchText, new ReturnSizeCallback() {
                        @Override
                        public void OnReturn(int size) {
                            if(size == 0 && (productList != null && productList.size() > 0))
                                searchResultTv.setVisibility(View.VISIBLE);
                            else
                                searchResultTv.setVisibility(View.GONE);
                        }
                    });
                }
            }else if(spinnerType.getId() == ItemSpinnerEnum.CATEGORIES.getId()){

                if(categorySelectListAdapter != null){
                    categorySelectListAdapter.updateAdapter(searchText, new ReturnSizeCallback() {
                        @Override
                        public void OnReturn(int size) {
                            if(size == 0 && (categoryList != null && categoryList.size() > 0))
                                searchResultTv.setVisibility(View.VISIBLE);
                            else
                                searchResultTv.setVisibility(View.GONE);
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
        if(!visible)
            Objects.requireNonNull(getActivity()).findViewById(R.id.tablayout).setVisibility(View.VISIBLE);
        else
            Objects.requireNonNull(getActivity()).findViewById(R.id.tablayout).setVisibility(View.GONE);
    }


    private void setKeyboardVisibilityListener(final OnKeyboardVisibilityListener onKeyboardVisibilityListener) {
        final View parentView = ((ViewGroup) mView.findViewById(R.id.mainll)).getChildAt(0);
        parentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            private boolean alreadyOpen;
            private final int defaultKeyboardHeightDP = 100;
            private final int EstimatedKeyboardDP = defaultKeyboardHeightDP + 48;
            private final Rect rect = new Rect();

            @Override
            public void onGlobalLayout() {
                int estimatedKeyboardHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, EstimatedKeyboardDP, parentView.getResources().getDisplayMetrics());
                parentView.getWindowVisibleDisplayFrame(rect);
                int heightDiff = parentView.getRootView().getHeight() - (rect.bottom - rect.top);
                boolean isShown = heightDiff >= estimatedKeyboardHeight;

                if (isShown == alreadyOpen) {
                    return;
                }
                alreadyOpen = isShown;
                onKeyboardVisibilityListener.onVisibilityChanged(isShown);
            }
        });
    }

    @Override
    public void OnViewCallback(View view) {
        returnViewCallback.OnViewCallback(view);
    }
}