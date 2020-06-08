package com.paypad.vuk507.menu.tax;


import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.db.TaxDBHelper;
import com.paypad.vuk507.db.UnitDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.menu.tax.interfaces.ReturnTaxCallback;
import com.paypad.vuk507.menu.unit.interfaces.ReturnUnitCallback;
import com.paypad.vuk507.model.BaseResponse;
import com.paypad.vuk507.model.TaxModel;
import com.paypad.vuk507.model.UnitModel;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.ShapeUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class TaxEditFragment extends BaseFragment {

    private View mView;

    @BindView(R.id.taxNameEt)
    EditText taxNameEt;
    @BindView(R.id.taxRateEt)
    EditText taxRateEt;
    @BindView(R.id.cancelImgv)
    ClickableImageView cancelImgv;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.saveBtn)
    Button saveBtn;
    @BindView(R.id.btnDelete)
    Button btnDelete;

    private Realm realm;
    private TaxModel taxModel;
    private ReturnTaxCallback returnTaxCallback;
    private User user;
    private int deleteButtonStatus = 1;

    public TaxEditFragment(@Nullable TaxModel taxModel, ReturnTaxCallback returnTaxCallback) {
        this.taxModel = taxModel;
        this.returnTaxCallback = returnTaxCallback;
    }

    @Override
    public void onStart() {
        super.onStart();
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
            initVariables();
            initListeners();
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
                Objects.requireNonNull(getActivity()).onBackPressed();
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
                checkSaveBtnEnable();
            }
        });

        taxRateEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkSaveBtnEnable();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidCategory();
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
                    TaxDBHelper.deleteTax(taxModel.getId(), new CompleteCallback() {
                        @Override
                        public void onComplete(BaseResponse baseResponse) {
                            CommonUtils.showToastShort(getContext(), baseResponse.getMessage());
                            if(baseResponse.isSuccess()){
                                returnTaxCallback.OnReturn((TaxModel) baseResponse.getObject(), ItemProcessEnum.DELETED);
                                Objects.requireNonNull(getActivity()).onBackPressed();
                            }
                        }
                    });
                }
            }
        });
    }

    private void checkSaveBtnEnable(){
        if(taxNameEt.getText() != null && !taxNameEt.getText().toString().isEmpty() &&
                taxRateEt.getText() != null && !taxRateEt.getText().toString().isEmpty()){
            CommonUtils.setSaveBtnEnability(true, saveBtn, getContext());
        }else
            CommonUtils.setSaveBtnEnability(false, saveBtn, getContext());
    }

    private void initVariables() {
        realm = Realm.getDefaultInstance();
        setShapes();
        CommonUtils.setSaveBtnEnability(false, saveBtn, getContext());

        if(taxModel == null){
            taxModel = new TaxModel();
            btnDelete.setEnabled(false);
            toolbarTitleTv.setText(getContext().getResources().getString(R.string.create_tax));
        }else{
            taxNameEt.setText(taxModel.getName());
            taxRateEt.setText(String.valueOf(taxModel.getTaxRate()));
        }

        toolbarTitleTv.setText(getContext().getResources().getString(R.string.edit_tax));
    }

    private void setShapes() {
        /*taxNameEt.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.White, null),
                getResources().getColor(R.color.DodgerBlue, null), GradientDrawable.RECTANGLE, 20, 2));
        taxRateEt.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.White, null),
                getResources().getColor(R.color.DodgerBlue, null), GradientDrawable.RECTANGLE, 20, 2));*/
        CommonUtils.setBtnFirstCondition(getContext(), btnDelete, getContext().getResources().getString(R.string.delete_tax));
    }

    private void checkValidCategory() {
        updateUnit();
    }

    private void updateUnit() {

        boolean inserted = false;
        realm.beginTransaction();

        if(taxModel.getId() == 0){
            taxModel.setCreateDate(new Date());
            taxModel.setId(TaxDBHelper.getCurrentPrimaryKeyId());
            inserted = true;
        }

        TaxModel tempTax = realm.copyToRealm(taxModel);

        tempTax.setName(taxNameEt.getText().toString());
        tempTax.setTaxRate(Integer.parseInt(taxRateEt.getText().toString()));
        tempTax.setCreateUsername(user.getUsername());

        realm.commitTransaction();

        boolean finalInserted = inserted;
        TaxDBHelper.createOrUpdateTax(tempTax, new CompleteCallback() {
            @Override
            public void onComplete(BaseResponse baseResponse) {
                CommonUtils.showToastShort(getActivity(), baseResponse.getMessage());
                if(baseResponse.isSuccess()){
                    deleteButtonStatus = 1;
                    CommonUtils.setBtnFirstCondition(Objects.requireNonNull(getContext()), btnDelete,
                            getContext().getResources().getString(R.string.delete_tax));
                    btnDelete.setEnabled(false);

                    if(finalInserted)
                        returnTaxCallback.OnReturn((TaxModel) baseResponse.getObject(), ItemProcessEnum.INSERTED);
                    else
                        returnTaxCallback.OnReturn((TaxModel) baseResponse.getObject(), ItemProcessEnum.CHANGED);

                    clearViews();
                }
            }
        });
    }

    private void clearViews() {
        taxNameEt.setText("");
        taxRateEt.setText("");
        CommonUtils.setSaveBtnEnability(false, saveBtn, getContext());
        taxModel = new TaxModel();
        CommonUtils.hideKeyBoard(Objects.requireNonNull(getContext()));
    }
}