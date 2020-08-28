package com.paypad.parator.menu.discount;


import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.bumptech.glide.Glide;
import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.db.DiscountDBHelper;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.enums.ItemProcessEnum;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.menu.discount.interfaces.ReturnDiscountCallback;
import com.paypad.parator.model.Discount;
import com.paypad.parator.model.User;
import com.paypad.parator.model.pojo.BaseResponse;
import com.paypad.parator.utils.ClickableImage.ClickableImageView;
import com.paypad.parator.utils.CommonUtils;
import com.paypad.parator.utils.DataUtils;
import com.paypad.parator.utils.NumberFormatWatcher;
import com.paypad.parator.utils.ShapeUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

import static com.paypad.parator.constants.CustomConstants.MAX_PRICE_VALUE;
import static com.paypad.parator.constants.CustomConstants.MAX_RATE_VALUE;
import static com.paypad.parator.constants.CustomConstants.TYPE_PRICE;
import static com.paypad.parator.constants.CustomConstants.TYPE_RATE;

public class DiscountEditFragment extends BaseFragment {

    private View mView;

    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.cancelImgv)
    ClickableImageView cancelImgv;
    @BindView(R.id.saveBtn)
    Button saveBtn;
    @BindView(R.id.discountMainll)
    LinearLayout discountMainll;
    @BindView(R.id.editItemImgv)
    ImageView editItemImgv;
    @BindView(R.id.discountNameEt)
    EditText discountNameEt;

    @BindView(R.id.rateSymbolTv)
    TextView rateSymbolTv;
    @BindView(R.id.amountSymbolTv)
    TextView amountSymbolTv;

    @BindView(R.id.amountRateEt)
    EditText amountRateEt;
    @BindView(R.id.btnDelete)
    Button btnDelete;

    private Realm realm;
    private Discount discount;
    private User user;
    private ReturnDiscountCallback returnDiscountCallback;
    private int selectionType = TYPE_RATE;
    private NumberFormatWatcher numberFormatWatcher;
    private int deleteButtonStatus = 1;

    public DiscountEditFragment(@Nullable Discount discount, ReturnDiscountCallback returnDiscountCallback) {
        this.discount = discount;
        this.returnDiscountCallback = returnDiscountCallback;
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
            Objects.requireNonNull(getActivity()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            mView = inflater.inflate(R.layout.fragment_edit_discount, container, false);
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
        amountSymbolTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectionType = TYPE_PRICE;
                amountRateEt.setText("");
                shapeAmountRateSymbols();
            }
        });

        rateSymbolTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectionType = TYPE_RATE;
                amountRateEt.setText("");
                shapeAmountRateSymbols();
            }
        });

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

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(deleteButtonStatus == 1){
                    deleteButtonStatus ++;
                    CommonUtils.setBtnSecondCondition(Objects.requireNonNull(getContext()), btnDelete,
                            getContext().getResources().getString(R.string.confirm_delete));
                }else if(deleteButtonStatus == 2){
                    BaseResponse baseResponse = DiscountDBHelper.deleteDiscount(discount.getId());
                    DataUtils.showBaseResponseMessage(getContext(), baseResponse);

                    if(baseResponse.isSuccess()){
                        returnDiscountCallback.OnReturn((Discount) baseResponse.getObject(), ItemProcessEnum.DELETED);
                        Objects.requireNonNull(getActivity()).onBackPressed();
                    }
                }
            }
        });
    }

    private void shapeAmountRateSymbols(){
        if(selectionType == TYPE_RATE){
            rateSymbolTv.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.custom_btn_bg_color, null),
                    getResources().getColor(R.color.DarkGray, null), GradientDrawable.RECTANGLE, 0, 2));
            amountSymbolTv.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.White, null),
                    getResources().getColor(R.color.DarkGray, null), GradientDrawable.RECTANGLE, 0, 2));
            setNumberFormatWatcher(TYPE_RATE);
            amountRateEt.setHint("0 %");
        }else {
            amountSymbolTv.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.custom_btn_bg_color, null),
                    getResources().getColor(R.color.DarkGray, null), GradientDrawable.RECTANGLE, 0, 2));
            rateSymbolTv.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.White, null),
                    getResources().getColor(R.color.DarkGray, null), GradientDrawable.RECTANGLE, 0, 2));
            setNumberFormatWatcher(TYPE_PRICE);
            amountRateEt.setHint("0.00 ".concat(CommonUtils.getCurrency().getSymbol()));
        }
    }

    private void setNumberFormatWatcher(int selectionType){
        if(numberFormatWatcher != null)
            amountRateEt.removeTextChangedListener(numberFormatWatcher);

        double maxValue = selectionType == TYPE_RATE ? MAX_RATE_VALUE : MAX_PRICE_VALUE;
        numberFormatWatcher = new NumberFormatWatcher(amountRateEt, selectionType, maxValue);
        amountRateEt.addTextChangedListener(numberFormatWatcher);
    }

    private void checkValidProduct() {
        if(discountNameEt.getText() == null || discountNameEt.getText().toString().isEmpty()){
            CommonUtils.snackbarDisplay(discountMainll,
                    Objects.requireNonNull(getContext()), getContext().getResources().getString(R.string.discount_name_can_not_be_empty));
            return;
        }

        if(amountRateEt.getText() == null || amountRateEt.getText().toString().isEmpty()){
            CommonUtils.snackbarDisplay(discountMainll,
                    Objects.requireNonNull(getContext()), getContext().getResources().getString(R.string.discount_rate_amount_can_not_be_empty));
            return;
        }

        updateDiscount();
    }

    private void updateDiscount() {

        boolean inserted = false;
        realm.beginTransaction();

        if(discount.getId() == 0){
            discount.setId(DiscountDBHelper.getCurrentPrimaryKeyId());
            discount.setCreateDate(new Date());
            discount.setUserId(user.getId());
            discount.setDeleted(false);
            inserted = true;
        }else {
            discount.setUpdateDate(new Date());
            discount.setUpdateUserId(user.getId());
        }

        double amountRateValue = DataUtils.getDoubleValueFromFormattedString(amountRateEt.getText().toString());

        if(selectionType == TYPE_PRICE){
            discount.setAmount(amountRateValue);
            discount.setRate(0);
        } else if(selectionType == TYPE_RATE){
            discount.setRate(amountRateValue);
            discount.setAmount(0);
        }

        discount.setName(discountNameEt.getText().toString());

        realm.commitTransaction();

        boolean finalInserted = inserted;

        BaseResponse baseResponse = DiscountDBHelper.createOrUpdateDiscount(discount);
        DataUtils.showBaseResponseMessage(getContext(), baseResponse);

        if(baseResponse.isSuccess()){
            deleteButtonStatus = 1;

            if(finalInserted)
                returnDiscountCallback.OnReturn((Discount) baseResponse.getObject(), ItemProcessEnum.INSERTED);
            else
                returnDiscountCallback.OnReturn((Discount) baseResponse.getObject(), ItemProcessEnum.CHANGED);

            clearItems();
            Objects.requireNonNull(getActivity()).onBackPressed();
        }
    }

    private void clearItems() {
        clearViewsText();
        discount = new Discount();
        CommonUtils.hideKeyBoard(Objects.requireNonNull(getContext()));
    }

    private void clearViewsText() {
        discountNameEt.setText("");
        amountRateEt.setText("");
    }

    private void initVariables() {
        Glide.with(Objects.requireNonNull(getActivity())).load(R.drawable.icon_discount_white_64dp).into(editItemImgv);
        amountSymbolTv.setText(CommonUtils.getCurrency().getSymbol());
        numberFormatWatcher = new NumberFormatWatcher(amountRateEt, TYPE_RATE, MAX_RATE_VALUE);

        int padding = CommonUtils.getPaddingInPixels(getContext(), 25);
        editItemImgv.setPadding(padding,padding,padding,padding);

        realm = Realm.getDefaultInstance();
        if(discount == null) {
            toolbarTitleTv.setText(getActivity().getResources().getString(R.string.create_discount));
            discount = new Discount();
        }else {
            toolbarTitleTv.setText(getActivity().getResources().getString(R.string.edit_discount));
        }
        fillDiscountFields();
    }

    private void fillDiscountFields() {
        discountNameEt.setText(discount.getName());

        if(discount.getAmount() > 0){
            selectionType = TYPE_PRICE;
            CommonUtils.setAmountToView(discount.getAmount(), amountRateEt, selectionType);
        }else if(discount.getRate() > 0){
            selectionType = TYPE_RATE;
            CommonUtils.setAmountToView(discount.getRate(), amountRateEt, selectionType);
        }

        shapeAmountRateSymbols();
    }
}