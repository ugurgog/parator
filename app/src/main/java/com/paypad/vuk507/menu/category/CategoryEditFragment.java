package com.paypad.vuk507.menu.category;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.db.CategoryDBHelper;
import com.paypad.vuk507.db.ProductDBHelper;
import com.paypad.vuk507.db.UnitDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.interfaces.CustomDialogListener;
import com.paypad.vuk507.menu.category.interfaces.ReturnCategoryCallback;
import com.paypad.vuk507.menu.product.SelectColorFragment;
import com.paypad.vuk507.menu.product.adapters.ColorSelectAdapter;
import com.paypad.vuk507.model.Product;
import com.paypad.vuk507.model.UnitModel;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.model.Category;
import com.paypad.vuk507.model.User;
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

public class CategoryEditFragment extends BaseFragment
    implements ColorSelectAdapter.ColorReturnCallback {

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

    public CategoryEditFragment(@Nullable Category category, ReturnCategoryCallback returnCategoryCallback) {
        this.category = category;
        this.returnCategoryCallback = returnCategoryCallback;
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
                initSelectColorFragment();
                mFragmentNavigation.pushFragment(selectColorFragment);
            }
        });
    }

    private void initSelectColorFragment(){
        selectColorFragment = new SelectColorFragment(CategoryEditFragment.class.getName(),
                ((category != null && category.getName() != null) ? category.getName() : "" ),
                mColorId);
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
                .setEditTextVisibility(View.GONE)
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
        CommonUtils.setBtnFirstCondition(getContext(), btnDelete, getContext().getResources().getString(R.string.delete_unit));

        mColorId = R.color.Gray;

        if (category == null) {
            category = new Category();
            btnDelete.setEnabled(false);
            toolbarTitleTv.setText(getContext().getResources().getString(R.string.create_category));
        } else{
            categoryNameEt.setText(category.getName());
            toolbarTitleTv.setText(getContext().getResources().getString(R.string.edit_category));
            mColorId = category.getColorId();
        }
        imageRl.setBackgroundColor(getResources().getColor(mColorId, null));
    }

    private void checkValidCategory() {
        updateCategory();
    }

    private void updateCategory() {

        boolean inserted = false;
        realm.beginTransaction();

        if(category.getId() == 0){
            category.setCreateDate(new Date());
            category.setId(CategoryDBHelper.getCurrentPrimaryKeyId());
            inserted = true;
        }

        Category tempCategory = realm.copyToRealm(category);

        tempCategory.setName(categoryNameEt.getText().toString());
        tempCategory.setCreateUsername(user.getUsername());
        tempCategory.setColorId(mColorId);

        realm.commitTransaction();

        boolean finalInserted = inserted;
        CategoryDBHelper.createOrUpdateCategory(tempCategory, new CompleteCallback() {
            @Override
            public void onComplete(BaseResponse baseResponse) {
                CommonUtils.showToastShort(getActivity(), baseResponse.getMessage());
                if(baseResponse.isSuccess()){
                    deleteButtonStatus = 1;
                    CommonUtils.setBtnFirstCondition(Objects.requireNonNull(getContext()), btnDelete,
                            getContext().getResources().getString(R.string.delete_unit));
                    btnDelete.setEnabled(false);

                    returnCategoryCallback.OnReturn((Category) baseResponse.getObject());

                    clearViews();
                    Objects.requireNonNull(getActivity()).onBackPressed();
                }
            }
        });
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
}