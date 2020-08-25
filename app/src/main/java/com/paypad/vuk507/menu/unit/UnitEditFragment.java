package com.paypad.vuk507.menu.unit;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.db.ProductDBHelper;
import com.paypad.vuk507.db.UnitDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.interfaces.CustomDialogListener;
import com.paypad.vuk507.menu.unit.interfaces.ReturnUnitCallback;
import com.paypad.vuk507.model.Product;
import com.paypad.vuk507.model.UnitModel;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.CustomDialogBox;
import com.paypad.vuk507.utils.DataUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class UnitEditFragment extends BaseFragment {

    private View mView;

    @BindView(R.id.unitNameEt)
    EditText unitNameEt;
    @BindView(R.id.cancelImgv)
    ClickableImageView cancelImgv;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.saveBtn)
    Button saveBtn;
    @BindView(R.id.btnDelete)
    Button btnDelete;

    private Realm realm;
    private UnitModel unitModel;
    private ReturnUnitCallback returnUnitCallback;
    private User user;
    private int deleteButtonStatus = 1;

    public UnitEditFragment(@Nullable UnitModel unitModel, ReturnUnitCallback returnUnitCallback) {
        this.unitModel = unitModel;
        this.returnUnitCallback = returnUnitCallback;
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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_unit_edit, container, false);
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

        unitNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable != null && !editable.toString().isEmpty()) {
                    CommonUtils.setSaveBtnEnability(true, saveBtn, getContext());
                } else {
                    CommonUtils.setSaveBtnEnability(false, saveBtn, getContext());
                }
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

                    RealmResults<Product> products = ProductDBHelper.getProductsByUnitId(unitModel.getId());

                    if(products != null && products.size() > 0){
                        showDeleteDialog(products);
                    }else
                        deleteUnit();
                }
            }
        });
    }

    private void showDeleteDialog(RealmResults<Product> products){
        String deleteMessage = getResources().getString(R.string.unit_delete_question_description1)
                .concat(" ")
                .concat(String.valueOf(products.size()))
                .concat(" ")
                .concat(getResources().getString(R.string.unit_delete_question_description2));

        new CustomDialogBox.Builder((Activity) getContext())
                .setTitle(getContext().getResources().getString(R.string.delete_unit))
                .setMessage(deleteMessage)
                .setPositiveBtnVisibility(View.VISIBLE)
                .setNegativeBtnVisibility(View.GONE)
                .setPositiveBtnText(getContext().getResources().getString(R.string.ok))
                .setPositiveBtnBackground(getContext().getResources().getColor(R.color.bg_screen1, null))
                .setDurationTime(0)
                .isCancellable(true)
                .setEdittextVisibility(View.GONE)
                .OnPositiveClicked(new CustomDialogListener() {
                    @Override
                    public void OnClick() {
                        deleteButtonStatus = 1;
                        CommonUtils.setBtnFirstCondition(Objects.requireNonNull(getContext()), btnDelete,
                                getContext().getResources().getString(R.string.delete_unit));
                    }
                }).build();
    }

    private void deleteUnit(){

        BaseResponse baseResponse = UnitDBHelper.deleteUnit(unitModel.getId());
        DataUtils.showBaseResponseMessage(getContext(), baseResponse);

        if(!baseResponse.isSuccess())
            return;

        returnUnitCallback.OnReturn((UnitModel) baseResponse.getObject(), ItemProcessEnum.DELETED);
        Objects.requireNonNull(getActivity()).onBackPressed();
    }

    private void initVariables() {
        realm = Realm.getDefaultInstance();
        setShapes();
        CommonUtils.setSaveBtnEnability(false, saveBtn, getContext());

        if(unitModel == null){
            unitModel = new UnitModel();
            btnDelete.setEnabled(false);
            toolbarTitleTv.setText(getContext().getResources().getString(R.string.create_unit));
        }else{
            unitNameEt.setText(unitModel.getName());
            toolbarTitleTv.setText(getContext().getResources().getString(R.string.edit_unit));
        }
    }

    private void setShapes() {
        //unitNameEt.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.White, null),
        //        getResources().getColor(R.color.DodgerBlue, null), GradientDrawable.RECTANGLE, 20, 2));
        CommonUtils.setBtnFirstCondition(getContext(), btnDelete, getContext().getResources().getString(R.string.delete_unit));
    }

    private void checkValidCategory() {
        updateUnit();
    }

    private void updateUnit() {

        boolean inserted = false;
        realm.beginTransaction();

        if(unitModel.getId() == 0){
            unitModel.setId(UnitDBHelper.getCurrentPrimaryKeyId());
            unitModel.setCreateDate(new Date());
            unitModel.setUserId(user.getId());
            unitModel.setDeleted(false);
            inserted = true;
        }else {
            unitModel.setUpdateDate(new Date());
            unitModel.setUpdateUserId(user.getId());
        }

        unitModel.setName(unitNameEt.getText().toString());

        UnitModel tempUnit = realm.copyToRealm(unitModel);

        realm.commitTransaction();

        boolean finalInserted = inserted;

        BaseResponse baseResponse = UnitDBHelper.createOrUpdateUnit(tempUnit);
        DataUtils.showBaseResponseMessage(getContext(), baseResponse);

        if(baseResponse.isSuccess()){
            deleteButtonStatus = 1;
            CommonUtils.setBtnFirstCondition(Objects.requireNonNull(getContext()), btnDelete,
                    getContext().getResources().getString(R.string.delete_unit));
            btnDelete.setEnabled(false);

            if(finalInserted)
                returnUnitCallback.OnReturn((UnitModel) baseResponse.getObject(), ItemProcessEnum.INSERTED);
            else
                returnUnitCallback.OnReturn((UnitModel) baseResponse.getObject(), ItemProcessEnum.CHANGED);

            clearViews();
            Objects.requireNonNull(getActivity()).onBackPressed();
        }
    }

    private void clearViews() {
        unitNameEt.setText("");
        CommonUtils.setSaveBtnEnability(false, saveBtn, getContext());
        unitModel = new UnitModel();
        CommonUtils.hideKeyBoard(Objects.requireNonNull(getContext()));
    }
}