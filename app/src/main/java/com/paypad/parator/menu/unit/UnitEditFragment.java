package com.paypad.parator.menu.unit;

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
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.db.ProductDBHelper;
import com.paypad.parator.db.UnitDBHelper;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.enums.ItemProcessEnum;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.interfaces.CustomDialogListener;
import com.paypad.parator.interfaces.TutorialPopupCallback;
import com.paypad.parator.menu.unit.interfaces.ReturnUnitCallback;
import com.paypad.parator.model.Product;
import com.paypad.parator.model.UnitModel;
import com.paypad.parator.model.User;
import com.paypad.parator.model.pojo.BaseResponse;
import com.paypad.parator.uiUtils.tutorial.Tutorial;
import com.paypad.parator.uiUtils.tutorial.WalkthroughCallback;
import com.paypad.parator.utils.ClickableImage.ClickableImageView;
import com.paypad.parator.utils.CommonUtils;
import com.paypad.parator.utils.CustomDialogBox;
import com.paypad.parator.utils.DataUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.paypad.parator.constants.CustomConstants.WALK_THROUGH_CONTINUE;
import static com.paypad.parator.constants.CustomConstants.WALK_THROUGH_END;

public class UnitEditFragment extends BaseFragment implements WalkthroughCallback {

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
    private Context mContext;

    private int walkthrough;
    private WalkthroughCallback walkthroughCallback;
    private PopupWindow btnPopup;
    private Tutorial tutorial;

    public UnitEditFragment(@Nullable UnitModel unitModel, int walkthrough, ReturnUnitCallback returnUnitCallback) {
        this.unitModel = unitModel;
        this.returnUnitCallback = returnUnitCallback;
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
                    CommonUtils.setSaveBtnEnability(true, saveBtn, mContext);

                    if(btnPopup != null && walkthrough == WALK_THROUGH_CONTINUE){
                        btnPopup.dismiss();
                        tutorial.setLayoutVisibility(View.VISIBLE);
                    }
                } else {
                    CommonUtils.setSaveBtnEnability(false, saveBtn, mContext);

                    if(btnPopup != null && walkthrough == WALK_THROUGH_CONTINUE){
                        btnPopup.showAsDropDown(unitNameEt);
                        tutorial.setLayoutVisibility(View.GONE);
                    }
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
                    CommonUtils.setBtnSecondCondition(mContext, btnDelete,
                            mContext.getResources().getString(R.string.confirm_delete));
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

        new CustomDialogBox.Builder((Activity) mContext)
                .setTitle(mContext.getResources().getString(R.string.delete_unit))
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
                                mContext.getResources().getString(R.string.delete_unit));
                    }
                }).build();
    }

    private void deleteUnit(){

        BaseResponse baseResponse = UnitDBHelper.deleteUnit(unitModel.getId());
        DataUtils.showBaseResponseMessage(mContext, baseResponse);

        if(!baseResponse.isSuccess())
            return;

        returnUnitCallback.OnReturn((UnitModel) baseResponse.getObject(), ItemProcessEnum.DELETED);
        ((Activity) mContext).onBackPressed();
    }

    private void initVariables() {
        realm = Realm.getDefaultInstance();
        setShapes();
        CommonUtils.setSaveBtnEnability(false, saveBtn, mContext);

        if(unitModel == null){
            unitModel = new UnitModel();
            btnDelete.setEnabled(false);
            toolbarTitleTv.setText(mContext.getResources().getString(R.string.create_unit));
            btnDelete.setVisibility(View.GONE);
        }else{
            unitNameEt.setText(unitModel.getName());
            toolbarTitleTv.setText(mContext.getResources().getString(R.string.edit_unit));
        }
        checkTutorialIsActive();
    }


    private void checkTutorialIsActive() {
        tutorial = mView.findViewById(R.id.tutorial);
        tutorial.setWalkthroughCallback(this);
        tutorial.setTutorialMessage(mContext.getResources().getString(R.string.now_tap_save_button));

        if(walkthrough == WALK_THROUGH_CONTINUE){
            CommonUtils.displayPopupWindow(unitNameEt, mContext, mContext.getResources().getString(R.string.enter_unit_name_message),
                    new TutorialPopupCallback() {
                        @Override
                        public void OnClosed() {
                            OnWalkthroughResult(WALK_THROUGH_END);
                            btnPopup.dismiss();
                            btnPopup = null;
                        }

                        @Override
                        public void OnGetPopup(PopupWindow popupWindow) {
                            btnPopup = popupWindow;
                        }
                    });
        }
    }

    private void setShapes() {
        CommonUtils.setBtnFirstCondition(mContext, btnDelete, getContext().getResources().getString(R.string.delete_unit));
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
        DataUtils.showBaseResponseMessage(mContext, baseResponse);

        if(baseResponse.isSuccess()){
            deleteButtonStatus = 1;
            CommonUtils.setBtnFirstCondition(mContext, btnDelete,
                    mContext.getResources().getString(R.string.delete_unit));
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
        CommonUtils.setSaveBtnEnability(false, saveBtn, mContext);
        unitModel = new UnitModel();
        CommonUtils.hideKeyBoard(mContext);
    }

    private void dismissPopup(){
        if(btnPopup != null){
            btnPopup.dismiss();
            btnPopup = null;
        }
    }

    @Override
    public void OnWalkthroughResult(int result) {
        walkthrough = result;
        walkthroughCallback.OnWalkthroughResult(result);
    }
}