package com.paypad.vuk507.menu.product;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.db.CategoryDBHelper;
import com.paypad.vuk507.db.ProductDBHelper;
import com.paypad.vuk507.db.TaxDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.enums.ProductUnitTypeEnum;
import com.paypad.vuk507.enums.TaxRateEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.menu.category.CategorySelectFragment;
import com.paypad.vuk507.menu.category.interfaces.ReturnCategoryCallback;
import com.paypad.vuk507.menu.product.interfaces.ColorImageReturnCallback;
import com.paypad.vuk507.menu.product.interfaces.ReturnItemCallback;
import com.paypad.vuk507.menu.tax.TaxSelectFragment;
import com.paypad.vuk507.menu.tax.interfaces.ReturnTaxCallback;
import com.paypad.vuk507.menu.unit.UnitSelectFragment;
import com.paypad.vuk507.menu.unit.interfaces.ReturnUnitCallback;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.model.Category;
import com.paypad.vuk507.model.Product;
import com.paypad.vuk507.model.TaxModel;
import com.paypad.vuk507.model.UnitModel;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.model.pojo.PhotoSelectUtil;
import com.paypad.vuk507.utils.BitmapUtils;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.DataUtils;
import com.paypad.vuk507.utils.NumberFormatWatcher
        ;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

import static com.paypad.vuk507.constants.CustomConstants.LANGUAGE_TR;
import static com.paypad.vuk507.constants.CustomConstants.MAX_PRICE_VALUE;
import static com.paypad.vuk507.constants.CustomConstants.TYPE_PRICE;

public class ProductEditFragment extends BaseFragment implements
        ColorImageReturnCallback {

    private View mView;

    @BindView(R.id.amountRateEt)
    EditText amountRateEt;
    @BindView(R.id.productNameEt)
    EditText productNameEt;
    @BindView(R.id.cancelImgv)
    ClickableImageView cancelImgv;
    @BindView(R.id.saveBtn)
    Button saveBtn;
    @BindView(R.id.productMainll)
    LinearLayout productMainll;
    @BindView(R.id.productDescriptionEt)
    EditText productDescriptionEt;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;

    @BindView(R.id.categoryll)
    LinearLayout categoryll;
    @BindView(R.id.categoryTv)
    TextView categoryTv;

    @BindView(R.id.taxll)
    LinearLayout taxll;
    @BindView(R.id.taxTypeTv)
    TextView taxTypeTv;

    @BindView(R.id.unitll)
    LinearLayout unitll;
    @BindView(R.id.unitTypeTv)
    TextView unitTypeTv;

    @BindView(R.id.imageRl)
    RelativeLayout imageRl;
    @BindView(R.id.editItemImgv)
    ImageView editItemImgv;
    @BindView(R.id.itemShortNameTv)
    TextView itemShortNameTv;

    @BindView(R.id.btnDelete)
    Button btnDelete;

    private Realm realm;
    private Product productXX;
    private User user;
    private int deleteButtonStatus = 1;
    private ReturnItemCallback returnCallback;
    private boolean photoExist = false;

    private PhotoSelectUtil photoSelectUtil;
    private UnitModel mUnitModel;

    private TaxModel myTaxModel;
    private Category myCategory;
    private byte[] itemPictureByteArray = null;
    private SelectColorFragment selectColorFragment;
    private CategorySelectFragment categorySelectFragment;

    private int mColorId;
    private String itemName = "";

    public ProductEditFragment(@Nullable Product product, ReturnItemCallback returnItemCallback) {
        this.productXX = product;
        this.returnCallback = returnItemCallback;
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
        amountRateEt.addTextChangedListener(new NumberFormatWatcher(amountRateEt, TYPE_PRICE, MAX_PRICE_VALUE));

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

        categoryll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initCategorySelectFragment();
                mFragmentNavigation.pushFragment(categorySelectFragment);
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
                            myTaxModel = taxModel;
                        }
                    }
                }));
            }
        });

        unitll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFragmentNavigation.pushFragment(new UnitSelectFragment(new ReturnUnitCallback() {
                    @Override
                    public void OnReturn(UnitModel unitModel, ItemProcessEnum processEnum) {
                        mUnitModel = unitModel;
                        unitTypeTv.setText(mUnitModel.getName());
                    }
                }));
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(deleteButtonStatus == 1){
                    deleteButtonStatus ++;
                    CommonUtils.setBtnSecondCondition(Objects.requireNonNull(getContext()), btnDelete,
                            getContext().getResources().getString(R.string.confirm_delete));
                }else if(deleteButtonStatus == 2){
                    BaseResponse baseResponse = ProductDBHelper.deleteProduct(productXX.getId());
                    DataUtils.showBaseResponseMessage(getContext(), baseResponse);

                    if(baseResponse.isSuccess()){
                        returnCallback.OnReturn((Product) baseResponse.getObject(), ItemProcessEnum.DELETED);
                        Objects.requireNonNull(getActivity()).onBackPressed();
                    }
                }
            }
        });

        imageRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                initSelectColorFragment();
                mFragmentNavigation.pushFragment(selectColorFragment);


                //chooseImageProcess();
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
                if(editable != null && !editable.toString().isEmpty() && !photoExist){
                    itemName = editable.toString();
                    itemShortNameTv.setText(DataUtils.getProductNameShortenName(itemName));
                }else{
                    itemName = "";
                    itemShortNameTv.setText("");
                }
            }
        });
    }

    private void initCategorySelectFragment(){
        categorySelectFragment = new CategorySelectFragment(new ReturnCategoryCallback() {
            @Override
            public void OnReturn(Category category) {
                if(category != null && category.getName() != null && !category.getName().isEmpty()){
                    categoryTv.setText(category.getName());
                    myCategory = category;
                }
            }
        });
        categorySelectFragment.setClassTag(this.getClass().getName());
    }

    private void initSelectColorFragment(){
        selectColorFragment = new SelectColorFragment(ProductEditFragment.class.getName(), itemName, mColorId,
                ((productXX != null && productXX.getProductImage() != null) ? productXX.getProductImage() : null));
        selectColorFragment.setColorReturnCallback(this);
    }


    private void checkValidProduct() {
        if(productNameEt.getText() == null || productNameEt.getText().toString().isEmpty()){
            CommonUtils.snackbarDisplay(productMainll,
                    Objects.requireNonNull(getContext()), getContext().getResources().getString(R.string.product_name_can_not_be_empty));
            return;
        }

        /*if(myCategory == null || myCategory.getName() == null || myCategory.getName().isEmpty()){
            CommonUtils.snackbarDisplay(productMainll,
                    Objects.requireNonNull(getContext()), getContext().getResources().getString(R.string.product_category_can_not_be_empty));
            return;
        }*/

        if(unitTypeTv.getText() == null || unitTypeTv.getText().toString().isEmpty()){
            CommonUtils.snackbarDisplay(productMainll,
                    Objects.requireNonNull(getContext()), getContext().getResources().getString(R.string.product_unit_type_can_not_be_empty));
            return;
        }

        if(myTaxModel == null || myTaxModel.getName() == null || myTaxModel.getName().isEmpty()){
            CommonUtils.snackbarDisplay(productMainll,
                    Objects.requireNonNull(getContext()), getContext().getResources().getString(R.string.product_tax_can_not_be_empty));
            return;
        }

        updateProduct();
    }

    private void updateProduct() {

        boolean inserted = false;
        realm.beginTransaction();

        if(productXX.getId() == 0){
            productXX.setCreateDate(new Date());
            productXX.setId(ProductDBHelper.getCurrentPrimaryKeyId());
            productXX.setUserUuid(user.getUuid());
            inserted = true;
        }

        Product tempProduct = realm.copyToRealm(productXX);

        if(amountRateEt.getText() != null && !amountRateEt.getText().toString().isEmpty()){
            double amount = DataUtils.getDoubleValueFromFormattedString(amountRateEt.getText().toString());
            tempProduct.setAmount(amount);
        }else
            tempProduct.setAmount(0);

        tempProduct.setUnitId(mUnitModel.getId());

        if(myTaxModel != null)
            tempProduct.setTaxId(myTaxModel.getId());
            //tempProduct.setTaxId(myTaxModel.getId());

        if(myCategory != null)
            tempProduct.setCategoryId(myCategory.getId());

        tempProduct.setName(productNameEt.getText().toString());

        tempProduct.setColorId(mColorId);

        tempProduct.setProductImage(itemPictureByteArray);

        tempProduct.setDescription(productDescriptionEt.getText().toString());

        realm.commitTransaction();

        BaseResponse baseResponse = ProductDBHelper.createOrUpdateProduct(tempProduct);
        DataUtils.showBaseResponseMessage(getContext(), baseResponse);

        if(baseResponse.isSuccess()){
            deleteButtonStatus = 1;
            CommonUtils.setBtnFirstCondition(Objects.requireNonNull(getContext()), btnDelete,
                    getContext().getResources().getString(R.string.delete_item));
            btnDelete.setEnabled(false);

            ItemProcessEnum processEnum;
            if(inserted)
                processEnum = ItemProcessEnum.INSERTED;
            else
                processEnum = ItemProcessEnum.CHANGED;

            returnCallback.OnReturn((Product) baseResponse.getObject(), processEnum);

            clearViews();
            Objects.requireNonNull(getActivity()).onBackPressed();
        }
    }

    private void clearViews(){
        productXX = new Product();
        amountRateEt.setText("");
        productNameEt.setText("");
        categoryTv.setText(getContext().getResources().getString(R.string.select_category));
        unitTypeTv.setText(getContext().getResources().getString(R.string.select_unit));
        taxTypeTv.setText(getContext().getResources().getString(R.string.select_tax));
        editItemImgv.setImageDrawable(null);
        itemShortNameTv.setText("");
    }

    private void initVariables() {
        if(realm == null)
            realm = Realm.getDefaultInstance();

        amountRateEt.setHint("0.00 ".concat(CommonUtils.getCurrency().getSymbol()));

        mColorId = CommonUtils.getItemColors()[0];

        if(productXX == null){
            productXX = new Product();
            toolbarTitleTv.setText(getResources().getString(R.string.create_item));
            imageRl.setBackgroundColor(getResources().getColor(mColorId, null));
            ProductUnitTypeEnum unitType = ProductUnitTypeEnum.PER_ITEM;

            mUnitModel = new UnitModel();
            mUnitModel.setId(unitType.getId());
            mUnitModel.setName(CommonUtils.getLanguage().equals(LANGUAGE_TR) ? unitType.getLabelTr() : unitType.getLabelEn());

            unitTypeTv.setText(CommonUtils.getLanguage().equals(LANGUAGE_TR) ? unitType.getLabelTr() : unitType.getLabelEn());
            productXX.setUnitId(unitType.getId());
        }else {
            toolbarTitleTv.setText(getResources().getString(R.string.edit_item));
            setFilledProductVariables();
        }
    }

    private void setFilledProductVariables() {
        itemName = productXX.getName();
        productNameEt.setText(itemName);

        if(productXX.getProductImage() != null){
            photoExist = true;
            editItemImgv.post(new Runnable() {
                @Override
                public void run() {
                    Bitmap bmp = BitmapFactory.decodeByteArray(productXX.getProductImage(), 0, productXX.getProductImage().length);

                    try{
                        editItemImgv.setImageBitmap(Bitmap.createScaledBitmap(bmp, editItemImgv.getWidth(),
                                editItemImgv.getHeight(), false));

                    }catch (IllegalArgumentException e){
                        int size = Math.round(TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP, Objects.requireNonNull(getContext()).getResources().getDimension(R.dimen.product_imageview_default_size),
                                getContext().getResources().getDisplayMetrics()));
                        editItemImgv.setImageBitmap(Bitmap.createScaledBitmap(bmp, size, size, false));
                    }
                }
            });
        }else {
            photoExist = false;
            mColorId = productXX.getColorId();

            if(mColorId != 0)
                imageRl.setBackgroundColor(getResources().getColor(mColorId, null));

            itemShortNameTv.setText(DataUtils.getProductShortenName(productXX));
        }

        if(productXX.getCategoryId() != 0){
            myCategory = CategoryDBHelper.getCategory(productXX.getCategoryId());

            if(myCategory != null && myCategory.getName() != null)
                categoryTv.setText(myCategory.getName());
        }else
            categoryTv.setText(getContext().getResources().getString(R.string.uncategorized));

        if(productXX.getUnitId() != 0){
            mUnitModel = DataUtils.getUnitModelById(productXX.getUnitId());

            if(mUnitModel != null && mUnitModel.getName() != null)
                unitTypeTv.setText(mUnitModel.getName());
        }

        if(productXX.getTaxId() != 0){
            myTaxModel = DataUtils.getTaxModelById(productXX.getTaxId());
            taxTypeTv.setText(myTaxModel.getName());
        }

        if(productXX.getAmount() != 0){
            CommonUtils.setAmountToView(productXX.getAmount(), amountRateEt, TYPE_PRICE);
        }

        if(productXX.getProductImage() != null)
            itemPictureByteArray = productXX.getProductImage();

        if(productXX.getDescription() != null)
            productDescriptionEt.setText(productXX.getDescription());
    }

    private void setProductPhoto() {
        if (photoSelectUtil != null && photoSelectUtil.getBitmap() != null) {
            photoExist = true;
            Glide.with(Objects.requireNonNull(getActivity()))
                    .load(photoSelectUtil.getBitmap())
                    .apply(RequestOptions.centerCropTransform())
                    .into(editItemImgv);
            itemPictureByteArray = BitmapUtils.getByteArrayFromBitmap(photoSelectUtil.getBitmap());
            itemShortNameTv.setText("");
        } else if (productXX.getName() != null && !productXX.getName().trim().isEmpty()) {
            photoExist = false;
            editItemImgv.setImageDrawable(null);
            itemPictureByteArray = null;
        }
    }

    @Override
    public void OnColorReturn(int colorId) {
        mColorId = colorId;
        imageRl.setBackgroundColor(getResources().getColor(colorId, null));
    }

    @Override
    public void OnImageReturn(byte[] itemPictureByteArray, PhotoSelectUtil photoSelectUtil) {
        this.itemPictureByteArray = itemPictureByteArray;
        this.photoSelectUtil = photoSelectUtil;
        setProductPhoto();
    }
}