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
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.db.DiscountDBHelper;
import com.paypad.vuk507.db.ProductDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.enums.ItemSpinnerEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.menu.discount.DiscountEditFragment;
import com.paypad.vuk507.menu.discount.adapters.DiscountListAdapter;
import com.paypad.vuk507.menu.discount.interfaces.ReturnDiscountCallback;
import com.paypad.vuk507.menu.product.ProductEditFragment;
import com.paypad.vuk507.menu.product.adapters.ProductListAdapter;
import com.paypad.vuk507.menu.product.interfaces.ReturnItemCallback;
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
    Spinner spinner;
    @BindView(R.id.addItemBtn)
    Button addItemBtn;
    @BindView(R.id.itemListRv)
    RecyclerView itemListRv;

    private Realm realm;
    private ArrayAdapter<String> spinnerAdapter;
    private ItemSpinnerEnum selectedSpinner;

    private ProductListAdapter productListAdapter;
    private DiscountListAdapter discountListAdapter;

    private User user;

    public LibraryFragment() {

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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
                            setProductAdapter();
                        }
                    }));
                }else if(selectedSpinner.getId() == ItemSpinnerEnum.DISCOUNTS.getId()){
                    mFragmentNavigation.pushFragment(new DiscountEditFragment(null, new ReturnDiscountCallback() {
                        @Override
                        public void OnReturn(Discount discount) {
                            setDiscountAdapter();
                        }
                    }));
                }
            }
        });

        /*addItemImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getContext(), addItemImgv);
                popupMenu.inflate(R.menu.menu_add_item);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.createItem:
                                mFragmentNavigation.pushFragment(new ProductEditFragment(null, new ReturnItemCallback() {
                                    @Override
                                    public void OnReturn(Product product, ItemProcessEnum processEnum) {

                                    }
                                }));
                                break;
                            case R.id.createDiscount:
                                mFragmentNavigation.pushFragment(new DiscountEditFragment(null, new ReturnDiscountCallback() {
                                    @Override
                                    public void OnReturn(Discount discount) {

                                    }
                                }));
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });*/
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

                Log.i("Info", "spinner_position:".concat(String.valueOf(position)).concat("  id:").concat(String.valueOf(id)));


                if(selectedSpinner.getId() == ItemSpinnerEnum.DISCOUNTS.getId()){
                    setDiscountAdapter();
                }else if(selectedSpinner.getId() == ItemSpinnerEnum.PRODUCTS.getId()){
                    setProductAdapter();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
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
        discountListAdapter = new DiscountListAdapter(getContext(), discountList, mFragmentNavigation, new ReturnDiscountCallback() {
            @Override
            public void OnReturn(Discount discount) {
                setDiscountAdapter();
            }
        });
        itemListRv.setAdapter(discountListAdapter);
    }

    public void setProductAdapter(){
        RealmResults<Product> products = ProductDBHelper.getAllProducts(user.getUuid());
        List<Product> productList = new ArrayList(products);
        productListAdapter = new ProductListAdapter(getContext(), productList, mFragmentNavigation, new ReturnItemCallback() {
            @Override
            public void OnReturn(Product product, ItemProcessEnum processEnum) {
                setProductAdapter();
            }
        });
        itemListRv.setAdapter(productListAdapter);
    }



}