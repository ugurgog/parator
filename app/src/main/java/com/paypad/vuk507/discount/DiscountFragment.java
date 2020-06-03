package com.paypad.vuk507.discount;

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
import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.model.Product;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.NumberTextWatcher;
import com.paypad.vuk507.utils.ShapeUtil;

import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class DiscountFragment extends BaseFragment {

    private View mView;

    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.cancelImgv)
    ClickableImageView cancelImgv;
    @BindView(R.id.saveBtn)
    Button saveBtn;
    @BindView(R.id.discountMainll)
    LinearLayout discountMainll;
    @BindView(R.id.discountImgv)
    ImageView discountImgv;
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

    private RealmResults<Discount> discounts;

    public DiscountFragment(@Nullable Discount discount) {
        this.discount = discount;
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
            mView = inflater.inflate(R.layout.fragment_discount, container, false);
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
                reviseAmountViews(s);
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
                reviseDiscountRateView(s);
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
                reviseDiscountRateView(s);
            }
        });
    }

    private void reviseDiscountRateView(Editable text){
        if (text != null && !text.toString().isEmpty()) {
            discountRateEt.setEnabled(false);
            discountRateEt.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.LemonChiffon, null),
                    getResources().getColor(R.color.DodgerBlue, null), GradientDrawable.RECTANGLE, 20, 2));
        } else {
            discountRateEt.setEnabled(true);
            discountRateEt.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.White, null),
                    getResources().getColor(R.color.DodgerBlue, null), GradientDrawable.RECTANGLE, 20, 2));
        }
    }

    private void reviseAmountViews(Editable text){
        if (text != null && !text.toString().isEmpty()) {
            amountEt.setEnabled(false);
            amountEt.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.LemonChiffon, null),
                    getResources().getColor(R.color.DodgerBlue, null), GradientDrawable.RECTANGLE, 20, 2));
            doitAmountEt.setEnabled(false);
            doitAmountEt.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.LemonChiffon, null),
                    getResources().getColor(R.color.DodgerBlue, null), GradientDrawable.RECTANGLE, 20, 2));
        } else {
            amountEt.setEnabled(true);
            amountEt.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.White, null),
                    getResources().getColor(R.color.DodgerBlue, null), GradientDrawable.RECTANGLE, 20, 2));
            doitAmountEt.setEnabled(true);
            doitAmountEt.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.White, null),
                    getResources().getColor(R.color.DodgerBlue, null), GradientDrawable.RECTANGLE, 20, 2));
        }
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
        realm.executeTransaction(new Realm.Transaction(){

            @Override
            public void execute(Realm realm) {

                try{
                    Number currentIdNum = realm.where(Discount.class).max("id");
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

                    int rate = (discountRateEt.getText() != null && !discountRateEt.getText().toString().isEmpty()) ?
                            Integer.parseInt(discountRateEt.getText().toString()) : 0;

                    discount.setId(nextId);
                    discount.setAmount(amount);
                    discount.setName(discountNameEt.getText().toString());
                    discount.setCreateDate(new Date());
                    discount.setRate(rate);

                    realm.insertOrUpdate(discount);
                }catch (Exception e){
                    CommonUtils.showToastShort(getActivity(), "Discount cannot be updated!");
                    return;
                }

                CommonUtils.showToastShort(getActivity(), "Discount is saved/updated!");
                clearViews();
            }
        });
    }

    private void clearViews() {
        setShapes();
        setEnabilityOfViews(true);
        clearViewsText();
    }

    private void clearViewsText() {
        discountNameEt.setText("");
        discountRateEt.setText("");
        amountEt.setText("");
        doitAmountEt.setText("");
    }

    private void initVariables() {
        setShapes();
        Glide.with(Objects.requireNonNull(getActivity())).load(R.drawable.icon_discount).into(discountImgv);

        realm = Realm.getDefaultInstance();
        if(discount == null) {
            toolbarTitleTv.setText(getActivity().getResources().getString(R.string.create_discount));
            discount = new Discount();
        }else
            toolbarTitleTv.setText(getActivity().getResources().getString(R.string.edit_discount));


        discounts = realm.where(Discount.class).findAllAsync();
        discounts.addChangeListener(realmChangeListener);
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


    private RealmChangeListener<RealmResults<Discount>> realmChangeListener = (discounts1) -> {
        System.out.println(discounts1.toString());
    };


}