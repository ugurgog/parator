package com.paypad.vuk507.menu.customer;

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
import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.db.DiscountDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.menu.customer.interfaces.ReturnCustomerCallback;
import com.paypad.vuk507.menu.discount.interfaces.ReturnDiscountCallback;
import com.paypad.vuk507.model.Customer;
import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.DataUtils;
import com.paypad.vuk507.utils.NumberFormatWatcher;
import com.paypad.vuk507.utils.ShapeUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

import static com.paypad.vuk507.constants.CustomConstants.TYPE_PRICE;
import static com.paypad.vuk507.constants.CustomConstants.TYPE_RATE;

public class CustomerEditFragment extends BaseFragment {

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
    private ReturnCustomerCallback returnCustomerCallback;
    private int selectionType = TYPE_RATE;
    private NumberFormatWatcher numberFormatWatcher;
    private int deleteButtonStatus = 1;

    public CustomerEditFragment(@Nullable Customer customer, ReturnCustomerCallback returnCustomerCallback) {
        //this.discount = discount;
        this.returnCustomerCallback = returnCustomerCallback;
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
            mView = inflater.inflate(R.layout.fragment_discount_edit, container, false);
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
                    DiscountDBHelper.deleteDiscount(discount.getId(), new CompleteCallback() {
                        @Override
                        public void onComplete(BaseResponse baseResponse) {
                            CommonUtils.showToastShort(getContext(), baseResponse.getMessage());
                            if(baseResponse.isSuccess()){
                                //returnDiscountCallback.OnReturn((Discount) baseResponse.getObject());
                                Objects.requireNonNull(getActivity()).onBackPressed();
                            }
                        }
                    });
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
        numberFormatWatcher = new NumberFormatWatcher(amountRateEt, selectionType);
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
            discount.setCreateDate(new Date());
            discount.setId(DiscountDBHelper.getCurrentPrimaryKeyId());
            inserted = true;
        }

        Discount tempDiscount = realm.copyToRealm(discount);

        double amountRateValue = DataUtils.getDoubleValueFromFormattedString(amountRateEt.getText().toString());

        if(selectionType == TYPE_PRICE){
            tempDiscount.setAmount(amountRateValue);
            tempDiscount.setRate(0);
        } else if(selectionType == TYPE_RATE){
            tempDiscount.setRate(amountRateValue);
            tempDiscount.setAmount(0);
        }

        tempDiscount.setName(discountNameEt.getText().toString());
        tempDiscount.setCreateUsername(user.getUsername());

        realm.commitTransaction();

        boolean finalInserted = inserted;
        DiscountDBHelper.createOrUpdateDiscount(tempDiscount, new CompleteCallback() {
            @Override
            public void onComplete(BaseResponse baseResponse) {
                CommonUtils.showToastShort(getActivity(), baseResponse.getMessage());
                if(baseResponse.isSuccess()){
                    deleteButtonStatus = 1;
                    //returnDiscountCallback.OnReturn((Discount) baseResponse.getObject());
                    clearItems();
                    Objects.requireNonNull(getActivity()).onBackPressed();
                }
            }
        });
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
        Glide.with(Objects.requireNonNull(getActivity())).load(R.drawable.icon_discount).into(editItemImgv);
        amountSymbolTv.setText(CommonUtils.getCurrency().getSymbol());
        numberFormatWatcher = new NumberFormatWatcher(amountRateEt, TYPE_RATE);

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