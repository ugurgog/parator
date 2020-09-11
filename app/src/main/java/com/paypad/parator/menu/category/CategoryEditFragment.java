package com.paypad.parator.menu.category;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.db.CategoryDBHelper;
import com.paypad.parator.db.ProductDBHelper;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.interfaces.CustomDialogListener;
import com.paypad.parator.interfaces.TutorialPopupCallback;
import com.paypad.parator.menu.category.interfaces.ReturnCategoryCallback;
import com.paypad.parator.menu.product.SelectColorFragment;
import com.paypad.parator.menu.product.interfaces.ColorImageReturnCallback;
import com.paypad.parator.model.Category;
import com.paypad.parator.model.Product;
import com.paypad.parator.model.User;
import com.paypad.parator.model.pojo.BaseResponse;
import com.paypad.parator.model.pojo.PhotoSelectUtil;
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

public class CategoryEditFragment extends BaseFragment
    implements ColorImageReturnCallback,
        WalkthroughCallback {

    private View mView;

    @BindView(R.id.categoryNameEt)
    EditText categoryNameEt;
    @BindView(R.id.cancelImgv)
    ClickableImageView cancelImgv;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.saveBtn)
    Button saveBtn;
    @BindView(R.id.btnDelete)
    Button btnDelete;
    @BindView(R.id.imageRl)
    RelativeLayout imageRl;
    @BindView(R.id.itemShortNameTv)
    TextView itemShortNameTv;

    private Realm realm;
    private Category category;
    private ReturnCategoryCallback returnCategoryCallback;
    private User user;
    private int deleteButtonStatus = 1;
    private SelectColorFragment selectColorFragment;
    private int mColorId;
    private String itemName = "";
    private Context mContext;

    private int walkthrough;
    private WalkthroughCallback walkthroughCallback;
    private PopupWindow btnPopup;
    private Tutorial tutorial;

    public CategoryEditFragment(@Nullable Category category, int walkthrough, ReturnCategoryCallback returnCategoryCallback) {
        this.category = category;
        this.returnCategoryCallback = returnCategoryCallback;
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
            mView = inflater.inflate(R.layout.fragment_category_edit, container, false);
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
                dismissPopup();
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });

        categoryNameEt.addTextChangedListener(new TextWatcher() {
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
                    itemName = editable.toString();
                    itemShortNameTv.setText(DataUtils.getProductNameShortenName(itemName));

                    if(btnPopup != null && walkthrough == WALK_THROUGH_CONTINUE){
                        btnPopup.dismiss();
                        tutorial.setLayoutVisibility(View.VISIBLE);
                    }

                } else {
                    CommonUtils.setSaveBtnEnability(false, saveBtn, getContext());
                    itemName = "";
                    itemShortNameTv.setText(itemName);

                    if(btnPopup != null && walkthrough == WALK_THROUGH_CONTINUE){
                        btnPopup.showAsDropDown(categoryNameEt);
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
                    CommonUtils.setBtnSecondCondition(Objects.requireNonNull(getContext()), btnDelete,
                            getContext().getResources().getString(R.string.confirm_delete));
                }else if(deleteButtonStatus == 2){

                    RealmResults<Product> products = ProductDBHelper.getProductsByCategoryId(category.getId());

                    if(products != null && products.size() > 0){
                        showDeleteDialog(products);
                    }else
                        deleteCategory();
                }
            }
        });

        imageRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnPopup != null)
                    btnPopup.dismiss();
                initSelectColorFragment();
                mFragmentNavigation.pushFragment(selectColorFragment);
            }
        });
    }

    private void initSelectColorFragment(){
        selectColorFragment = new SelectColorFragment(CategoryEditFragment.class.getName(), itemName, mColorId, null);
        selectColorFragment.setColorReturnCallback(this);
    }

    private void showDeleteDialog(RealmResults<Product> products){
        String deleteMessage = getResources().getString(R.string.category_delete_question_description1)
                .concat(" ")
                .concat(String.valueOf(products.size()))
                .concat(" ")
                .concat(getResources().getString(R.string.category_delete_question_description2));

        new CustomDialogBox.Builder((Activity) getContext())
                .setTitle(getContext().getResources().getString(R.string.delete_category))
                .setMessage(deleteMessage)
                .setNegativeBtnVisibility(View.GONE)
                .setPositiveBtnVisibility(View.VISIBLE)
                .setPositiveBtnText(getContext().getResources().getString(R.string.yes))
                .setPositiveBtnBackground(getContext().getResources().getColor(R.color.bg_screen1, null))
                .setDurationTime(0)
                .isCancellable(true)
                .setEdittextVisibility(View.GONE)
                .OnPositiveClicked(new CustomDialogListener() {
                    @Override
                    public void OnClick() {
                        deleteButtonStatus = 1;
                        CommonUtils.setBtnFirstCondition(Objects.requireNonNull(getContext()), btnDelete,
                                getContext().getResources().getString(R.string.delete_category));
                    }
                }).build();
    }

    private void deleteCategory(){
        BaseResponse baseResponse = CategoryDBHelper.deleteCategory(category.getId());
        DataUtils.showBaseResponseMessage(getContext(), baseResponse);

        if(!baseResponse.isSuccess())
            return;

        returnCategoryCallback.OnReturn((Category) baseResponse.getObject());
        Objects.requireNonNull(getActivity()).onBackPressed();
    }

    private void initVariables() {
        realm = Realm.getDefaultInstance();
        CommonUtils.setSaveBtnEnability(false, saveBtn, getContext());
        CommonUtils.setBtnFirstCondition(getContext(), btnDelete, getContext().getResources().getString(R.string.delete_category));

        mColorId = CommonUtils.getItemColors()[0];

        if (category == null) {
            category = new Category();
            btnDelete.setEnabled(false);
            toolbarTitleTv.setText(getContext().getResources().getString(R.string.create_category));
            btnDelete.setVisibility(View.GONE);
        } else{
            itemName = category.getName();
            categoryNameEt.setText(itemName);
            toolbarTitleTv.setText(getContext().getResources().getString(R.string.edit_category));
            mColorId = category.getColorId();
            itemShortNameTv.setText(DataUtils.getProductNameShortenName(itemName));
        }
        imageRl.setBackgroundColor(getResources().getColor(mColorId, null));
        checkTutorialIsActive();
    }

    private void checkTutorialIsActive() {
        tutorial = mView.findViewById(R.id.tutorial);
        tutorial.setWalkthroughCallback(this);
        tutorial.setTutorialMessage(mContext.getResources().getString(R.string.now_tap_save_button));

        if(walkthrough == WALK_THROUGH_CONTINUE){
            CommonUtils.displayPopupWindow(categoryNameEt, mContext, mContext.getResources().getString(R.string.enter_category_name_message),
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

    private void checkValidCategory() {
        updateCategory();
    }

    private void updateCategory() {
        realm.beginTransaction();

        if(category.getId() == 0){
            category.setId(CategoryDBHelper.getCurrentPrimaryKeyId());
            category.setCreateDate(new Date());
            category.setUserId(user.getId());
            category.setDeleted(false);
        }else {
            category.setUpdateDate(new Date());
            category.setUpdateUserId(user.getId());
        }

        category.setName(categoryNameEt.getText().toString());
        category.setColorId(mColorId);

        realm.commitTransaction();

        BaseResponse baseResponse = CategoryDBHelper.createOrUpdateCategory(category);
        DataUtils.showBaseResponseMessage(getContext(), baseResponse);

        if(baseResponse.isSuccess()){
            if(btnPopup != null) {
                btnPopup.dismiss();
                btnPopup = null;
            }

            deleteButtonStatus = 1;
            CommonUtils.setBtnFirstCondition(Objects.requireNonNull(getContext()), btnDelete,
                    getContext().getResources().getString(R.string.delete_category));
            btnDelete.setEnabled(false);

            returnCategoryCallback.OnReturn((Category) baseResponse.getObject());

            clearViews();
            Objects.requireNonNull(getActivity()).onBackPressed();
        }
    }

    private void clearViews() {
        categoryNameEt.setText("");
        CommonUtils.setSaveBtnEnability(false, saveBtn, getContext());
        category = new Category();
        CommonUtils.hideKeyBoard(Objects.requireNonNull(getContext()));
    }

    @Override
    public void OnColorReturn(int colorId) {
        mColorId = colorId;
        imageRl.setBackgroundColor(getResources().getColor(colorId, null));
    }

    @Override
    public void OnImageReturn(byte[] itemPictureByteArray, PhotoSelectUtil photoSelectUtil) {

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