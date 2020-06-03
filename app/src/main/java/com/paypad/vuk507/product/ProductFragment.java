package com.paypad.vuk507.product;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.model.City;
import com.paypad.vuk507.model.Product;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.NumberTextWatcher;

import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class ProductFragment extends BaseFragment {

    private View mView;

    @BindView(R.id.amountEt)
    EditText amountEt;
    @BindView(R.id.productNameEt)
    EditText productNameEt;
    @BindView(R.id.productDescEt)
    EditText productDescEt;
    @BindView(R.id.cancelImgv)
    ClickableImageView cancelImgv;
    @BindView(R.id.saveBtn)
    Button saveBtn;
    @BindView(R.id.productMainll)
    LinearLayout productMainll;
    @BindView(R.id.doitAmountEt)
    EditText doitAmountEt;

    private Realm realm;
    private Product product;

    private RealmResults<Product> products;

    public ProductFragment(@Nullable Product product) {
        this.product = product;
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_product, container, false);
            ButterKnife.bind(this, mView);
            initVariables();
            initListeners();
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    private void initListeners() {
        amountEt.addTextChangedListener(new NumberTextWatcher(amountEt));

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidProduct();
            }
        });

        cancelImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });
    }

    private void checkValidProduct() {
        if(amountEt.getText() == null || amountEt.getText().toString().isEmpty()){
            CommonUtils.snackbarDisplay(productMainll,
                    Objects.requireNonNull(getContext()), getContext().getResources().getString(R.string.amount_can_not_be_empty));
            return;
        }

        if(productNameEt.getText() == null || productNameEt.getText().toString().isEmpty()){
            CommonUtils.snackbarDisplay(productMainll,
                    Objects.requireNonNull(getContext()), getContext().getResources().getString(R.string.product_name_can_not_be_empty));
            return;
        }

        updateProduct();
    }

    private void updateProduct() {
        realm.executeTransaction(new Realm.Transaction(){

            @Override
            public void execute(Realm realm) {
                Number currentIdNum = realm.where(Product.class).max("id");
                int nextId;
                if(currentIdNum == null) {
                    nextId = 1;
                } else {
                    nextId = currentIdNum.intValue() + 1;
                }

                String amountStr = amountEt.getText().toString()
                        .concat(".")
                        .concat(!doitAmountEt.getText().toString().isEmpty() ? doitAmountEt.getText().toString() : "00");
                double amount = Double.valueOf(amountStr);

                product.setId(nextId);
                product.setAmount(amount);
                product.setTitle(productNameEt.getText().toString());
                product.setCreateDate(new Date());

                realm.insertOrUpdate(product); // using insert API
            }
        });
    }

    private void initVariables() {
        realm = Realm.getDefaultInstance();
        if(product == null)
            product = new Product();

        products = realm.where(Product.class).findAllAsync();
        products.addChangeListener(realmChangeListener);
    }


    private RealmChangeListener<RealmResults<Product>> realmChangeListener = (products) -> {
        System.out.println(products.toString());
    };


}