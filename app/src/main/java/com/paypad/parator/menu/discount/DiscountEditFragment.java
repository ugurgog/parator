package com.paypad.parator.menu.discount;


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
import android.widget.PopupWindow;
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
import com.paypad.parator.interfaces.TutorialPopupCallback;
import com.paypad.parator.menu.discount.interfaces.ReturnDiscountCallback;
import com.paypad.parator.model.Discount;
import com.paypad.parator.model.User;
import com.paypad.parator.model.pojo.BaseResponse;
import com.paypad.parator.uiUtils.tutorial.Tutorial;
import com.paypad.parator.uiUtils.tutorial.WalkthroughCallback;
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
import static com.paypad.parator.constants.CustomConstants.WALK_THROUGH_CONTINUE;
import static com.paypad.parator.constants.CustomConstants.WALK_THROUGH_END;

public class DiscountEditFragment extends BaseFragment implements WalkthroughCallback {

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
    private Context mContext;

    private int walkthrough;
    private WalkthroughCallback walkthroughCallback;
    private PopupWindow namePopup;
    private PopupWindow ratePopup;
    private Tutorial tutorial;

    public DiscountEditFragment(@Nullable Discount discount, int walkthrough, ReturnDiscountCallback returnDiscountCallback) {
        this.discount = discount;
        this.returnDiscountCallback = returnDiscountCallback;
        this.walkthrough = walkthrough;
    }

    public void setWalkthroughCallback(WalkthroughCallback walkthroughCallback) {
        this.walkthroughCallback = walkthroughCallback;
    }

    @Override
    public void onResume() {
        super.onResume();
        initVariables();
        initListeners();
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
        }
        return mView;
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
        dismissPopup();
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
                dismissPopup();
                ((Activity) mContext).onBackPressed();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(deleteButtonStatus == 1){
                    deleteButtonStatus ++;
                    CommonUtils.setBtnSecondCondition(mContext, btnDelete,
                            mContext.getResources().getString(R.string.confirm_delete));
                }else if(deleteButtonStatus == 2){
                    BaseResponse baseResponse = DiscountDBHelper.deleteDiscount(discount.getId());
                    DataUtils.showBaseResponseMessage(mContext, baseResponse);

                    if(baseResponse.isSuccess()){
                        returnDiscountCallback.OnReturn((Discount) baseResponse.getObject(), ItemProcessEnum.DELETED);
                        Objects.requireNonNull(getActivity()).onBackPressed();
                    }
                }
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
                    if(namePopup != null && walkthrough == WALK_THROUGH_CONTINUE){
                        namePopup.dismiss();
                        namePopup = null;

                        displayRatePopup();
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
        numberFormatWatcher.setReturnEtTextCallback(new NumberFormatWatcher.ReturnEtTextCallback() {
            @Override
            public void OnReturnEtValue(String text) {
                if(text != null && !text.isEmpty()){
                    if(walkthrough == WALK_THROUGH_CONTINUE){
                        tutorial.setLayoutVisibility(View.VISIBLE);

                        if(ratePopup != null){
                            ratePopup.dismiss();
                            ratePopup = null;
                        }
                    }
                }
            }
        });
        amountRateEt.addTextChangedListener(numberFormatWatcher);
    }

    private void checkValidProduct() {
        if(discountNameEt.getText() == null || discountNameEt.getText().toString().isEmpty()){
            CommonUtils.snackbarDisplay(discountMainll,
                    mContext, mContext.getResources().getString(R.string.discount_name_can_not_be_empty));
            return;
        }

        if(amountRateEt.getText() == null || amountRateEt.getText().toString().isEmpty()){
            CommonUtils.snackbarDisplay(discountMainll,
                    mContext, mContext.getResources().getString(R.string.discount_rate_amount_can_not_be_empty));
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
        DataUtils.showBaseResponseMessage(mContext, baseResponse);

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
        CommonUtils.hideKeyBoard(mContext);
    }

    private void clearViewsText() {
        discountNameEt.setText("");
        amountRateEt.setText("");
    }

    private void initVariables() {
        Glide.with(Objects.requireNonNull(getActivity())).load(R.drawable.icon_discount_white_64dp).into(editItemImgv);
        amountSymbolTv.setText(CommonUtils.getCurrency().getSymbol());
        numberFormatWatcher = new NumberFormatWatcher(amountRateEt, TYPE_RATE, MAX_RATE_VALUE);

        int padding = CommonUtils.getPaddingInPixels(mContext, 25);
        editItemImgv.setPadding(padding,padding,padding,padding);

        realm = Realm.getDefaultInstance();
        if(discount == null) {
            toolbarTitleTv.setText(getActivity().getResources().getString(R.string.create_discount));
            discount = new Discount();
            btnDelete.setVisibility(View.GONE);
        }else {
            toolbarTitleTv.setText(getActivity().getResources().getString(R.string.edit_discount));
        }
        fillDiscountFields();
        checkTutorialIsActive();
    }

    private void checkTutorialIsActive() {
        tutorial = mView.findViewById(R.id.tutorial);
        tutorial.setWalkthroughCallback(this);
        tutorial.setTutorialMessage(mContext.getResources().getString(R.string.now_tap_save_button));

        if(walkthrough == WALK_THROUGH_CONTINUE){
            CommonUtils.displayPopupWindow(discountNameEt, mContext, mContext.getResources().getString(R.string.enter_discount_name_message),
                    new TutorialPopupCallback() {
                        @Override
                        public void OnClosed() {
                            OnWalkthroughResult(WALK_THROUGH_END);
                            namePopup.dismiss();
                            namePopup = null;
                        }

                        @Override
                        public void OnGetPopup(PopupWindow popupWindow) {
                            namePopup = popupWindow;
                        }
                    });
        }
    }

    private void displayRatePopup(){
        if(walkthrough == WALK_THROUGH_CONTINUE && ratePopup == null){
            CommonUtils.displayPopupWindow(amountRateEt, mContext, mContext.getResources().getString(R.string.enter_discount_rate_message),
                    new TutorialPopupCallback() {
                        @Override
                        public void OnClosed() {
                            OnWalkthroughResult(WALK_THROUGH_END);
                            ratePopup.dismiss();
                            ratePopup = null;
                        }

                        @Override
                        public void OnGetPopup(PopupWindow popupWindow) {
                            ratePopup = popupWindow;
                        }
                    });
        }
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

    private void dismissPopup(){
        if(namePopup != null){
            namePopup.dismiss();
            namePopup = null;
        }

        if(ratePopup != null){
            ratePopup.dismiss();
            ratePopup = null;
        }
    }

    @Override
    public void OnWalkthroughResult(int result) {
        walkthrough = result;
        walkthroughCallback.OnWalkthroughResult(result);
    }
}