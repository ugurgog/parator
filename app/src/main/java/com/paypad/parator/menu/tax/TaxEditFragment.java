package com.paypad.parator.menu.tax;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.db.ProductDBHelper;
import com.paypad.parator.db.TaxDBHelper;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.enums.ItemProcessEnum;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.interfaces.CustomDialogListener;
import com.paypad.parator.interfaces.TutorialPopupCallback;
import com.paypad.parator.menu.tax.interfaces.ReturnTaxCallback;
import com.paypad.parator.model.Product;
import com.paypad.parator.model.TaxModel;
import com.paypad.parator.model.User;
import com.paypad.parator.model.pojo.BaseResponse;
import com.paypad.parator.uiUtils.tutorial.Tutorial;
import com.paypad.parator.uiUtils.tutorial.WalkthroughCallback;
import com.paypad.parator.utils.ClickableImage.ClickableImageView;
import com.paypad.parator.utils.CommonUtils;
import com.paypad.parator.utils.CustomDialogBox;
import com.paypad.parator.utils.DataUtils;
import com.paypad.parator.utils.NumberFormatWatcher;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.paypad.parator.constants.CustomConstants.MAX_RATE_VALUE;
import static com.paypad.parator.constants.CustomConstants.TYPE_RATE;
import static com.paypad.parator.constants.CustomConstants.WALK_THROUGH_CONTINUE;
import static com.paypad.parator.constants.CustomConstants.WALK_THROUGH_END;

public class TaxEditFragment extends BaseFragment implements WalkthroughCallback {

    private View mView;

    @BindView(R.id.taxNameEt)
    EditText taxNameEt;
    @BindView(R.id.amountRateNameTv)
    TextView amountRateNameTv;
    @BindView(R.id.cancelImgv)
    ClickableImageView cancelImgv;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.saveBtn)
    Button saveBtn;
    @BindView(R.id.btnDelete)
    Button btnDelete;
    @BindView(R.id.amountRateEt)
    EditText amountRateEt;
    @BindView(R.id.taxMainll)
    LinearLayout taxMainll;

    private Realm realm;
    private TaxModel taxModel;
    private ReturnTaxCallback returnTaxCallback;
    private User user;
    private int deleteButtonStatus = 1;
    private Context mContext;
    private NumberFormatWatcher numberFormatWatcher;

    private int walkthrough;
    private WalkthroughCallback walkthroughCallback;
    private PopupWindow namePopup;
    private PopupWindow ratePopup;
    private Tutorial tutorial;

    public TaxEditFragment(@Nullable TaxModel taxModel, int walkthrough, ReturnTaxCallback returnTaxCallback) {
        this.taxModel = taxModel;
        this.returnTaxCallback = returnTaxCallback;
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
    public void onResume() {
        super.onResume();
        initVariables();
        initListeners();
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_tax_edit, container, false);
            ButterKnife.bind(this, mView);
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    private void initListeners() {
        cancelImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissPopup();
                ((Activity) mContext).onBackPressed();
            }
        });

        taxNameEt.addTextChangedListener(new TextWatcher() {
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

        initNumberFormatWatcher();
        amountRateEt.addTextChangedListener(numberFormatWatcher);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidTax();
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

                    RealmResults<Product> products = ProductDBHelper.getProductsByTaxId(taxModel.getId());

                    if(products != null && products.size() > 0){
                        showDeleteDialog(products);
                    }else
                        deleteTax();
                }
            }
        });
    }

    private void initNumberFormatWatcher(){
        numberFormatWatcher = new NumberFormatWatcher(amountRateEt, TYPE_RATE, MAX_RATE_VALUE);
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
    }

    private void showDeleteDialog(RealmResults<Product> products){
        String deleteMessage = getResources().getString(R.string.tax_delete_question_description1)
                .concat(" ")
                .concat(String.valueOf(products.size()))
                .concat(" ")
                .concat(getResources().getString(R.string.tax_delete_question_description2));

        new CustomDialogBox.Builder((Activity) mContext)
                .setTitle(mContext.getResources().getString(R.string.delete_tax))
                .setMessage(deleteMessage)
                .setPositiveBtnVisibility(View.VISIBLE)
                .setNegativeBtnVisibility(View.GONE)
                .setPositiveBtnText(mContext.getResources().getString(R.string.ok))
                .setPositiveBtnBackground(mContext.getResources().getColor(R.color.bg_screen1, null))
                .setDurationTime(0)
                .isCancellable(true)
                .setEdittextVisibility(View.GONE)
                .OnPositiveClicked(new CustomDialogListener() {
                    @Override
                    public void OnClick() {
                        deleteButtonStatus = 1;
                        CommonUtils.setBtnFirstCondition(mContext, btnDelete,
                                mContext.getResources().getString(R.string.delete_tax));
                    }
                }).build();
    }

    private void deleteTax(){

        BaseResponse baseResponse =  TaxDBHelper.deleteTax(taxModel.getId());
        DataUtils.showBaseResponseMessage(mContext, baseResponse);

        if(!baseResponse.isSuccess())
            return;

        returnTaxCallback.OnReturn((TaxModel) baseResponse.getObject(), ItemProcessEnum.DELETED);
        Objects.requireNonNull(getActivity()).onBackPressed();
    }

    private void initVariables() {
        realm = Realm.getDefaultInstance();
        amountRateNameTv.setText(mContext.getResources().getString(R.string.tax_rate));
        amountRateEt.setHint("0 %");
        CommonUtils.setBtnFirstCondition(mContext, btnDelete, mContext.getResources().getString(R.string.delete_tax));

        if(taxModel == null){
            taxModel = new TaxModel();
            btnDelete.setEnabled(false);
            toolbarTitleTv.setText(mContext.getResources().getString(R.string.create_tax));
            btnDelete.setVisibility(View.GONE);
        }else{
            toolbarTitleTv.setText(mContext.getResources().getString(R.string.edit_tax));
            taxNameEt.setText(taxModel.getName());
            if(taxModel.getTaxRate() != 0){
                CommonUtils.setAmountToView(taxModel.getTaxRate(), amountRateEt, TYPE_RATE);
            }
        }
        checkTutorialIsActive();
    }

    private void checkTutorialIsActive() {
        tutorial = mView.findViewById(R.id.tutorial);
        tutorial.setWalkthroughCallback(this);
        tutorial.setTutorialMessage(mContext.getResources().getString(R.string.now_tap_save_button));

        if(walkthrough == WALK_THROUGH_CONTINUE){
            CommonUtils.displayPopupWindow(taxNameEt, mContext, mContext.getResources().getString(R.string.enter_tax_name_message),
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
            CommonUtils.displayPopupWindow(amountRateEt, mContext, mContext.getResources().getString(R.string.enter_tax_rate_message),
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

    private void checkValidTax() {
        CommonUtils.hideKeyBoard(mContext);
        if(taxNameEt.getText() == null || taxNameEt.getText().toString().isEmpty()){
            CommonUtils.snackbarDisplay(taxMainll,
                    mContext, mContext.getResources().getString(R.string.tax_name_can_not_be_empty));
            return;
        }

        if(amountRateEt.getText() == null || amountRateEt.getText().toString().isEmpty()){
            CommonUtils.snackbarDisplay(taxMainll,
                    mContext, mContext.getResources().getString(R.string.tax_rate_can_not_be_empty));
            return;
        }

        updateTax();
    }

    private void updateTax() {

        boolean inserted = false;
        realm.beginTransaction();

        if(taxModel.getId() == 0){
            taxModel.setId(TaxDBHelper.getCurrentPrimaryKeyId());
            taxModel.setUserId(user.getId());
            taxModel.setCreateDate(new Date());
            taxModel.setDeleted(false);
            inserted = true;
        }else {
            taxModel.setUpdateUserId(user.getId());
            taxModel.setUpdateDate(new Date());
        }

        taxModel.setName(taxNameEt.getText().toString());

        if(amountRateEt.getText() != null && !amountRateEt.getText().toString().isEmpty()){
            double amount = DataUtils.getDoubleValueFromFormattedString(amountRateEt.getText().toString());
            taxModel.setTaxRate(amount);
        }

        realm.commitTransaction();

        boolean finalInserted = inserted;

        BaseResponse baseResponse = TaxDBHelper.createOrUpdateTax(taxModel);
        DataUtils.showBaseResponseMessage(mContext, baseResponse);

        if(baseResponse.isSuccess()){
            deleteButtonStatus = 1;
            CommonUtils.setBtnFirstCondition(mContext, btnDelete,
                    mContext.getResources().getString(R.string.delete_tax));
            btnDelete.setEnabled(false);

            if(finalInserted)
                returnTaxCallback.OnReturn((TaxModel) baseResponse.getObject(), ItemProcessEnum.INSERTED);
            else
                returnTaxCallback.OnReturn((TaxModel) baseResponse.getObject(), ItemProcessEnum.CHANGED);

            clearViews();
            Objects.requireNonNull(getActivity()).onBackPressed();
        }
    }

    private void clearViews() {
        taxNameEt.setText("");
        amountRateEt.setText("");
        taxModel = new TaxModel();
        CommonUtils.hideKeyBoard(mContext);
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