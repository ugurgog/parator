package com.paypad.vuk507.menu.discount;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
import com.paypad.vuk507.menu.discount.interfaces.ReturnDiscountCallback;
import com.paypad.vuk507.model.BaseResponse;
import com.paypad.vuk507.model.Category;
import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.ShapeUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.Objects;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

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
    @BindView(R.id.discountRateEt)
    EditText discountRateEt;

    @BindView(R.id.amountEt)
    EditText amountEt;
    @BindView(R.id.doitAmountEt)
    EditText doitAmountEt;

    private Realm realm;
    private Discount discount;
    private User user;
    private ReturnDiscountCallback returnDiscountCallback;

    private boolean discountNameFilled = false;
    private boolean amountFilled = false;
    private boolean rateFilled = false;
    private boolean firstOpen = false;

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

        discountRateEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                reviseAmountViews((s != null && !s.toString().isEmpty()) ? s.toString() : "");
            }
        });

        amountEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                reviseDiscountRateView((s != null && !s.toString().isEmpty()) ? s.toString() : "");
            }
        });

        doitAmountEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                reviseDiscountRateView((s != null && !s.toString().isEmpty()) ? s.toString() : "");
            }
        });

        discountNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable != null && !editable.toString().isEmpty()) {
                    discountNameFilled = true;
                } else {
                    discountNameFilled = false;
                }
                checkSaveButtonEnability();
            }
        });
    }

    public void checkSaveButtonEnability(){
        if(discountNameFilled && (rateFilled || amountFilled) && !firstOpen)
            CommonUtils.setSaveBtnEnability(true, saveBtn, getContext());
        else
            CommonUtils.setSaveBtnEnability(false, saveBtn, getContext());
    }

    private void reviseDiscountRateView(String text){
        if (!text.isEmpty()) {
            amountFilled = true;
            discountRateEt.setEnabled(false);
            discountRateEt.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.LemonChiffon, null),
                    getResources().getColor(R.color.DodgerBlue, null), GradientDrawable.RECTANGLE, 20, 2));
        } else {
            amountFilled = false;
            discountRateEt.setEnabled(true);
            discountRateEt.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.White, null),
                    getResources().getColor(R.color.DodgerBlue, null), GradientDrawable.RECTANGLE, 20, 2));
        }
        checkSaveButtonEnability();
    }

    private void reviseAmountViews(String text){
        if (!text.isEmpty()) {
            rateFilled = true;
            amountEt.setEnabled(false);
            amountEt.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.LemonChiffon, null),
                    getResources().getColor(R.color.DodgerBlue, null), GradientDrawable.RECTANGLE, 20, 2));
            doitAmountEt.setEnabled(false);
            doitAmountEt.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.LemonChiffon, null),
                    getResources().getColor(R.color.DodgerBlue, null), GradientDrawable.RECTANGLE, 20, 2));
        } else {
            rateFilled = false;
            amountEt.setEnabled(true);
            amountEt.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.White, null),
                    getResources().getColor(R.color.DodgerBlue, null), GradientDrawable.RECTANGLE, 20, 2));
            doitAmountEt.setEnabled(true);
            doitAmountEt.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.White, null),
                    getResources().getColor(R.color.DodgerBlue, null), GradientDrawable.RECTANGLE, 20, 2));
        }
        checkSaveButtonEnability();
    }

    private void checkValidProduct() {
        if(discountNameEt.getText() == null || discountNameEt.getText().toString().isEmpty()){
            CommonUtils.snackbarDisplay(discountMainll,
                    Objects.requireNonNull(getContext()), getContext().getResources().getString(R.string.discount_name_can_not_be_empty));
            return;
        }

        if((discountRateEt.getText() == null || discountRateEt.getText().toString().isEmpty()) &&
                (amountEt.getText() == null || amountEt.getText().toString().isEmpty()) &&
                (doitAmountEt.getText() == null || doitAmountEt.getText().toString().isEmpty())){
            CommonUtils.snackbarDisplay(discountMainll,
                    Objects.requireNonNull(getContext()),
                    getContext().getResources().getString(R.string.discount_rate_amount_can_not_be_empty));
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

        String amountStr = amountEt.getText().toString()
                .concat(".")
                .concat(!doitAmountEt.getText().toString().isEmpty() ? doitAmountEt.getText().toString() : "00");
        double amount = Double.valueOf(amountStr);

        int rate = (discountRateEt.getText() != null && !discountRateEt.getText().toString().isEmpty()) ?
                Integer.parseInt(discountRateEt.getText().toString()) : 0;

        tempDiscount.setAmount(amount);
        tempDiscount.setName(discountNameEt.getText().toString());
        tempDiscount.setRate(rate);
        tempDiscount.setCreateUsername(user.getUsername());

        //if(tempDiscount.getId() == 0)
        //    tempDiscount.setCreateDate(new Date());
        realm.commitTransaction();

        boolean finalInserted = inserted;
        DiscountDBHelper.createOrUpdateDiscount(tempDiscount, new CompleteCallback() {
            @Override
            public void onComplete(BaseResponse baseResponse) {
                CommonUtils.showToastShort(getActivity(), baseResponse.getMessage());
                if(baseResponse.isSuccess()){
                    returnDiscountCallback.OnReturn((Discount) baseResponse.getObject());
                    clearItems();

                    if(!finalInserted){
                        Objects.requireNonNull(getActivity()).onBackPressed();
                    }
                }
            }
        });
    }

    private void clearItems() {
        setShapes();
        setEnabilityOfViews(true);
        clearViewsText();
        discount = new Discount();
        CommonUtils.hideKeyBoard(Objects.requireNonNull(getContext()));
    }

    private void clearViewsText() {
        discountNameEt.setText("");
        discountRateEt.setText("");
        amountEt.setText("");
        doitAmountEt.setText("");
    }

    private void initVariables() {
        setShapes();
        Glide.with(Objects.requireNonNull(getActivity())).load(R.drawable.icon_discount).into(editItemImgv);
        checkSaveButtonEnability();

        realm = Realm.getDefaultInstance();
        if(discount == null) {
            toolbarTitleTv.setText(getActivity().getResources().getString(R.string.create_discount));
            discount = new Discount();
        }else {
            toolbarTitleTv.setText(getActivity().getResources().getString(R.string.edit_discount));
            fillDiscountFields();
        }
    }

    private void fillDiscountFields() {
        discountNameEt.setText(discount.getName());
        discountNameFilled = true;
        firstOpen = true;

        if(discount.getRate() != 0){
            discountRateEt.setText(String.valueOf(discount.getRate()));
            reviseAmountViews(discountRateEt.getText().toString());
            rateFilled = true;
        }

        if(discount.getAmount() != 0){
            String amountStr = String.valueOf(discount.getAmount());
            String[] parts = amountStr.split(Pattern.quote("."));

            if(Double.parseDouble(parts[0]) != 0){
                amountEt.setText(parts[0]);
                reviseDiscountRateView(amountEt.getText().toString());
            }

            if(Double.parseDouble(parts[1]) != 0){
                doitAmountEt.setText(parts[1]);
                reviseDiscountRateView(doitAmountEt.getText().toString());
            }

            amountFilled = true;
        }

        firstOpen = false;
    }

    private void setEnabilityOfViews(boolean enability){
        discountRateEt.setEnabled(enability);
        amountEt.setEnabled(enability);
        doitAmountEt.setEnabled(enability);
    }

    private void setShapes() {
        discountNameEt.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.transparent, null),
                getResources().getColor(R.color.DodgerBlue, null), GradientDrawable.RECTANGLE, 20, 2));

        discountRateEt.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.transparent, null),
                getResources().getColor(R.color.DodgerBlue, null), GradientDrawable.RECTANGLE, 20, 2));

        amountEt.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.transparent, null),
                getResources().getColor(R.color.DodgerBlue, null), GradientDrawable.RECTANGLE, 20, 2));

        doitAmountEt.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.transparent, null),
                getResources().getColor(R.color.DodgerBlue, null), GradientDrawable.RECTANGLE, 20, 2));
    }



}