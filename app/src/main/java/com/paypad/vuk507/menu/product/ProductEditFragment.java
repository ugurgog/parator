package com.paypad.vuk507.menu.product;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.db.ProductDBHelper;
import com.paypad.vuk507.db.TaxDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.menu.category.CategoryFragment;
import com.paypad.vuk507.menu.category.CategorySelectFragment;
import com.paypad.vuk507.menu.category.interfaces.ReturnCategoryCallback;
import com.paypad.vuk507.menu.tax.TaxSelectFragment;
import com.paypad.vuk507.menu.tax.interfaces.ReturnTaxCallback;
import com.paypad.vuk507.menu.unit.UnitEditFragment;
import com.paypad.vuk507.menu.unit.interfaces.ReturnUnitCallback;
import com.paypad.vuk507.model.BaseResponse;
import com.paypad.vuk507.model.Category;
import com.paypad.vuk507.model.Product;
import com.paypad.vuk507.model.TaxModel;
import com.paypad.vuk507.model.UnitModel;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.NumberTextWatcher;
import com.paypad.vuk507.utils.NumberTextWatcher2;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class ProductEditFragment extends BaseFragment {

    private View mView;

    @BindView(R.id.amountEt)
    EditText amountEt;
    @BindView(R.id.productNameEt)
    EditText productNameEt;
    @BindView(R.id.cancelImgv)
    ClickableImageView cancelImgv;
    @BindView(R.id.saveBtn)
    Button saveBtn;
    @BindView(R.id.productMainll)
    LinearLayout productMainll;
    @BindView(R.id.doitAmountEt)
    EditText doitAmountEt;

    @BindView(R.id.categoryll)
    LinearLayout categoryll;
    @BindView(R.id.categoryTv)
    TextView categoryTv;

    @BindView(R.id.taxll)
    LinearLayout taxll;
    @BindView(R.id.taxTypeTv)
    TextView taxTypeTv;

    private Realm realm;
    private Product product;
    private User user;

    public ProductEditFragment(@Nullable Product product) {
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
            mView = inflater.inflate(R.layout.fragment_edit_product, container, false);
            ButterKnife.bind(this, mView);
            initVariables();
            initListeners();
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

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
        //amountEt.addTextChangedListener(new NumberTextWatcher2(amountEt));

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonUtils.hideKeyBoard(Objects.requireNonNull(getContext()));
                checkValidProduct();
            }
        });

        cancelImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });

        productNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable != null && !editable.toString().isEmpty()){
                    product.setName(editable.toString());
                }else
                    product.setName("");
            }
        });

        categoryll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFragmentNavigation.pushFragment(new CategorySelectFragment(new ReturnCategoryCallback() {
                    @Override
                    public void OnReturn(Category category) {
                        if(category != null && category.getName() != null && !category.getName().isEmpty()){
                            categoryTv.setText(category.getName());
                            product.setCategoryId(category.getId());
                            Log.i("Info", "category_name:" + category.getName());
                        }

                    }
                }));
            }
        });

        taxll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFragmentNavigation.pushFragment(new TaxSelectFragment(new ReturnTaxCallback() {
                    @Override
                    public void OnReturn(TaxModel taxModel, ItemProcessEnum processEnum) {
                        if(taxModel != null && taxModel.getName() != null && !taxModel.getName().isEmpty()){
                            taxTypeTv.setText(taxModel.getName());
                            product.setTaxId(taxModel.getId());
                        }
                    }
                }));
            }
        });
    }

    private void checkValidProduct() {
        if(product.getName() == null || product.getName().isEmpty()){
            CommonUtils.snackbarDisplay(productMainll,
                    Objects.requireNonNull(getContext()), getContext().getResources().getString(R.string.product_name_can_not_be_empty));
            return;
        }

        if(product.getCategoryId() == 0){
            CommonUtils.snackbarDisplay(productMainll,
                    Objects.requireNonNull(getContext()), getContext().getResources().getString(R.string.product_category_can_not_be_empty));
            return;
        }

        updateProduct();
    }

    private void updateProduct() {





        boolean inserted = false;
        realm.beginTransaction();

        if(product.getId() == 0){
            product.setCreateDate(new Date());
            product.setId(ProductDBHelper.getCurrentPrimaryKeyId());
            inserted = true;
        }

        Product tempProduct = realm.copyToRealm(product);

        String amountStr = amountEt.getText().toString()
                .concat(".")
                .concat(!doitAmountEt.getText().toString().isEmpty() ? doitAmountEt.getText().toString() : "00");
        double amount = Double.valueOf(amountStr);

        tempProduct.setAmount(amount);
        //tempProduct.setName(productNameEt.getText().toString());


        realm.commitTransaction();

        boolean finalInserted = inserted;
        /*ProductDBHelper.createOrUpdateProduct(tempProduct, new CompleteCallback() {
            @Override
            public void onComplete(BaseResponse baseResponse) {
                CommonUtils.showToastShort(getActivity(), baseResponse.getMessage());
                if(baseResponse.isSuccess()){
                    deleteButtonStatus = 1;
                    CommonUtils.setBtnFirstCondition(Objects.requireNonNull(getContext()), btnDelete,
                            getContext().getResources().getString(R.string.delete_unit));
                    btnDelete.setEnabled(false);

                    if(finalInserted)
                        returnTaxCallback.OnReturn((TaxModel) baseResponse.getObject(), ItemProcessEnum.INSERTED);
                    else
                        returnTaxCallback.OnReturn((TaxModel) baseResponse.getObject(), ItemProcessEnum.CHANGED);

                    clearViews();
                }
            }
        });*/
    }

    private void initVariables() {
        realm = Realm.getDefaultInstance();
        if(product == null)
            product = new Product();

        //products = realm.where(Product.class).findAllAsync();
    }



}