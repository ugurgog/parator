package com.paypad.vuk507.menu.category;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.db.CategoryDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.menu.category.interfaces.ReturnCategoryCallback;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class CategoryEditFragment extends BaseFragment {

    private View mView;

    @BindView(R.id.categoryNameEt)
    EditText categoryNameEt;
    @BindView(R.id.cancelImgv)
    ClickableImageView cancelImgv;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.saveBtn)
    Button saveBtn;

    private Realm realm;
    private Category category;
    private ReturnCategoryCallback returnCategoryCallback;
    private User user;

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
    }

    private void initVariables() {
        realm = Realm.getDefaultInstance();
        setShapes();
        CommonUtils.setSaveBtnEnability(false, saveBtn, getContext());

        if(category == null){
            category = new Category();
            toolbarTitleTv.setText(getContext().getResources().getString(R.string.create_category));
        }else
            categoryNameEt.setText(category.getName());
            toolbarTitleTv.setText(getContext().getResources().getString(R.string.edit_category));
    }

    private void setShapes() {
        categoryNameEt.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.White, null),
                getResources().getColor(R.color.DodgerBlue, null), GradientDrawable.RECTANGLE, 20, 2));
    }

    private void checkValidCategory() {
        updateCategory();
    }

    private void updateCategory() {

        if(category.getId() == 0){
            CategoryDBHelper.createCategory(categoryNameEt.getText().toString(), user.getUsername(), new CompleteCallback() {
                @Override
                public void onComplete(BaseResponse baseResponse) {
                    CommonUtils.showToastShort(getActivity(), baseResponse.getMessage());
                    if(baseResponse.isSuccess()){
                        returnCategoryCallback.OnReturn((Category) baseResponse.getObject());
                        clearViews();
                    }

                }
            });
        }else {
            CategoryDBHelper.updateCategory(category, categoryNameEt.getText().toString(), new CompleteCallback() {
                @Override
                public void onComplete(BaseResponse baseResponse) {
                    CommonUtils.showToastShort(getActivity(), baseResponse.getMessage());
                    if(baseResponse.isSuccess()){
                        returnCategoryCallback.OnReturn((Category) baseResponse.getObject());
                        clearViews();
                    }
                }
            });
        }
    }

    private void clearViews() {
        categoryNameEt.setText("");
        CommonUtils.setSaveBtnEnability(true, saveBtn, getContext());
        category = new Category();
    }
}