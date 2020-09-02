package com.paypad.parator.menu.product;

import android.app.Activity;
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
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.db.CategoryDBHelper;
import com.paypad.parator.db.ProductDBHelper;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.enums.ItemProcessEnum;
import com.paypad.parator.enums.ProductUnitTypeEnum;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.interfaces.TutorialPopupCallback;
import com.paypad.parator.menu.category.CategorySelectFragment;
import com.paypad.parator.menu.category.interfaces.ReturnCategoryCallback;
import com.paypad.parator.menu.product.interfaces.ColorImageReturnCallback;
import com.paypad.parator.menu.product.interfaces.ReturnItemCallback;
import com.paypad.parator.menu.tax.TaxSelectFragment;
import com.paypad.parator.menu.tax.interfaces.ReturnTaxCallback;
import com.paypad.parator.menu.unit.UnitSelectFragment;
import com.paypad.parator.menu.unit.interfaces.ReturnUnitCallback;
import com.paypad.parator.model.Category;
import com.paypad.parator.model.Product;
import com.paypad.parator.model.TaxModel;
import com.paypad.parator.model.UnitModel;
import com.paypad.parator.model.User;
import com.paypad.parator.model.pojo.BaseResponse;
import com.paypad.parator.model.pojo.PhotoSelectUtil;
import com.paypad.parator.uiUtils.tutorial.WalkthroughCallback;
import com.paypad.parator.utils.BitmapUtils;
import com.paypad.parator.utils.ClickableImage.ClickableImageView;
import com.paypad.parator.utils.CommonUtils;
import com.paypad.parator.utils.DataUtils;
import com.paypad.parator.utils.NumberFormatWatcher;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

import static com.paypad.parator.constants.CustomConstants.LANGUAGE_TR;
import static com.paypad.parator.constants.CustomConstants.MAX_PRICE_VALUE;
import static com.paypad.parator.constants.CustomConstants.TYPE_PRICE;
import static com.paypad.parator.constants.CustomConstants.WALK_THROUGH_CONTINUE;
import static com.paypad.parator.constants.CustomConstants.WALK_THROUGH_END;

public class ProductEditFragment extends BaseFragment implements
        ColorImageReturnCallback,
        WalkthroughCallback{

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
    @BindView(R.id.scrollView)
    ScrollView scrollView;

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
    private int walkthrough;
    private WalkthroughCallback walkthroughCallback;
    private Context mContext;

    private PopupWindow itemNamePopup;
    private PopupWindow itemTaxPopup;
    private PopupWindow itemPricePopup;
    private boolean taxPopupShowed = false;

    public ProductEditFragment(@Nullable Product product, int walkthrough, ReturnItemCallback returnItemCallback) {
        this.productXX = product;
        this.returnCallback = returnItemCallback;
        this.walkthrough = walkthrough;
    }

    public void setWalkthroughCallback(WalkthroughCallback walkthroughCallback) {
        this.walkthroughCallback = walkthroughCallback;
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

    private void dismissPopups(){
        if(itemNamePopup != null)
            itemNamePopup.dismiss();

        if(itemTaxPopup != null)
            itemTaxPopup.dismiss();

        if(itemPricePopup != null)
            itemPricePopup.dismiss();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(sticky = true)
    public void accountHolderUserReceived(UserBus userBus){
        user = userBus.getUser();
        if(user == null)
            user = UserDBHelper.getUserFromCache(mContext);
    }

    private void initListeners() {
        amountRateEt.addTextChangedListener(new NumberFormatWatcher(amountRateEt, TYPE_PRICE, MAX_PRICE_VALUE));

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonUtils.hideKeyBoard(mContext);
                checkValidProduct();
            }
        });

        cancelImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissPopups();
                ((Activity)mContext).onBackPressed();
            }
        });

        categoryll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initCategorySelectFragment();
                dismissPopups();
                mFragmentNavigation.pushFragment(categorySelectFragment);
            }
        });

        taxll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissPopups();
                mFragmentNavigation.pushFragment(new TaxSelectFragment(new ReturnTaxCallback() {
                    @Override
                    public void OnReturn(TaxModel taxModel, ItemProcessEnum processEnum) {
                        if(taxModel != null && taxModel.getName() != null && !taxModel.getName().isEmpty()){
                            taxTypeTv.setText(taxModel.getName());
                            myTaxModel = taxModel;

                            if(itemTaxPopup != null && walkthrough == WALK_THROUGH_CONTINUE){
                                itemTaxPopup.dismiss();
                                itemTaxPopup = null;
                                askForTypePrice();
                            }
                        }
                    }
                }));
            }
        });

        unitll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissPopups();
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
                dismissPopups();
                if(deleteButtonStatus == 1){
                    deleteButtonStatus ++;
                    CommonUtils.setBtnSecondCondition(mContext, btnDelete,
                            mContext.getResources().getString(R.string.confirm_delete));
                }else if(deleteButtonStatus == 2){
                    BaseResponse baseResponse = ProductDBHelper.deleteProduct(productXX.getId());
                    DataUtils.showBaseResponseMessage(mContext, baseResponse);

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
                dismissPopups();
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
                    if(itemNamePopup != null && walkthrough == WALK_THROUGH_CONTINUE){
                        itemNamePopup.dismiss();

                        if(!taxPopupShowed)
                            askForSelectTax();
                    }

                    itemName = editable.toString();
                    itemShortNameTv.setText(DataUtils.getProductNameShortenName(itemName));
                }else{
                    itemName = "";
                    itemShortNameTv.setText("");
                }
            }
        });
    }

    private void askForSelectTax(){
        taxPopupShowed = true;
        CommonUtils.displayPopupWindow(taxll, mContext, mContext.getResources().getString(R.string.select_tax),
                new TutorialPopupCallback() {
                    @Override
                    public void OnClosed() {
                        OnWalkthroughResult(WALK_THROUGH_END);
                        itemTaxPopup = null;
                    }

                    @Override
                    public void OnGetPopup(PopupWindow popupWindow) {
                        itemTaxPopup = popupWindow;
                    }
                });
    }

    private void askForTypePrice(){
        if(itemTaxPopup != null && itemTaxPopup.isShowing())
            itemTaxPopup.dismiss();

        CommonUtils.displayPopupWindow(amountRateEt, mContext, mContext.getResources().getString(R.string.type_item_amount_message),
                new TutorialPopupCallback() {
                    @Override
                    public void OnClosed() {
                        OnWalkthroughResult(WALK_THROUGH_END);
                        itemPricePopup = null;
                    }

                    @Override
                    public void OnGetPopup(PopupWindow popupWindow) {
                        itemPricePopup = popupWindow;
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
                    mContext, mContext.getResources().getString(R.string.product_name_can_not_be_empty));
            return;
        }

        if(unitTypeTv.getText() == null || unitTypeTv.getText().toString().isEmpty()){
            CommonUtils.snackbarDisplay(productMainll,
                    mContext, mContext.getResources().getString(R.string.product_unit_type_can_not_be_empty));
            return;
        }

        if(myTaxModel == null || myTaxModel.getName() == null || myTaxModel.getName().isEmpty()){
            CommonUtils.snackbarDisplay(productMainll,
                    mContext, mContext.getResources().getString(R.string.product_tax_can_not_be_empty));
            return;
        }

        updateProduct();
    }

    private void updateProduct() {

        boolean inserted = false;
        realm.beginTransaction();

        if(productXX.getId() == 0){
            productXX.setId(ProductDBHelper.getCurrentPrimaryKeyId());
            productXX.setCreateDate(new Date());
            productXX.setUserId(user.getId());
            productXX.setDeleted(false);
            inserted = true;
        }else {
            productXX.setUpdateDate(new Date());
            productXX.setUpdateUserId(user.getId());
        }

        if(amountRateEt.getText() != null && !amountRateEt.getText().toString().isEmpty()){
            double amount = DataUtils.getDoubleValueFromFormattedString(amountRateEt.getText().toString());
            productXX.setAmount(amount);
        }else
            productXX.setAmount(0);

        productXX.setUnitId(mUnitModel.getId());

        if(myTaxModel != null)
            productXX.setTaxId(myTaxModel.getId());

        if(myCategory != null)
            productXX.setCategoryId(myCategory.getId());

        productXX.setName(productNameEt.getText().toString());
        productXX.setColorId(mColorId);
        productXX.setProductImage(itemPictureByteArray);
        productXX.setDescription(productDescriptionEt.getText().toString());

        realm.commitTransaction();

        BaseResponse baseResponse = ProductDBHelper.createOrUpdateProduct(productXX);
        DataUtils.showBaseResponseMessage(mContext, baseResponse);

        if(baseResponse.isSuccess()){
            dismissPopups();
            deleteButtonStatus = 1;
            CommonUtils.setBtnFirstCondition(mContext, btnDelete,
                    mContext.getResources().getString(R.string.delete_item));
            btnDelete.setEnabled(false);

            ItemProcessEnum processEnum;
            if(inserted)
                processEnum = ItemProcessEnum.INSERTED;
            else
                processEnum = ItemProcessEnum.CHANGED;

            returnCallback.OnReturn((Product) baseResponse.getObject(), processEnum);

            clearViews();
            ((Activity) mContext).onBackPressed();
        }
    }

    private void clearViews(){
        productXX = new Product();
        amountRateEt.setText("");
        productNameEt.setText("");
        categoryTv.setText(mContext.getResources().getString(R.string.select_category));
        unitTypeTv.setText(mContext.getResources().getString(R.string.select_unit));
        taxTypeTv.setText(mContext.getResources().getString(R.string.select_tax));
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

        checkTutorialActivity();
    }

    private void checkTutorialActivity() {
        if(walkthrough == WALK_THROUGH_CONTINUE){
            CommonUtils.displayPopupWindow(productNameEt, mContext, mContext.getResources().getString(R.string.enter_item_name_message),
                    new TutorialPopupCallback() {
                        @Override
                        public void OnClosed() {
                            OnWalkthroughResult(WALK_THROUGH_END);
                            itemNamePopup = null;
                        }

                        @Override
                        public void OnGetPopup(PopupWindow popupWindow) {
                            itemNamePopup = popupWindow;
                        }
                    });
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
                                TypedValue.COMPLEX_UNIT_DIP, mContext.getResources().getDimension(R.dimen.product_imageview_default_size),
                                mContext.getResources().getDisplayMetrics()));
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
            categoryTv.setText(mContext.getResources().getString(R.string.uncategorized));

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

    @Override
    public void OnWalkthroughResult(int result) {
        walkthrough = result;
        walkthroughCallback.OnWalkthroughResult(result);
    }
}