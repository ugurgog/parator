package com.paypad.vuk507.menu.product;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.db.CategoryDBHelper;
import com.paypad.vuk507.db.ProductDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.interfaces.CustomDialogListener;
import com.paypad.vuk507.menu.category.CategoryEditFragment;
import com.paypad.vuk507.menu.category.interfaces.ReturnCategoryCallback;
import com.paypad.vuk507.menu.product.adapters.ColorSelectAdapter;
import com.paypad.vuk507.menu.product.adapters.ProductListAdapter;
import com.paypad.vuk507.menu.product.interfaces.ReturnItemCallback;
import com.paypad.vuk507.model.Category;
import com.paypad.vuk507.model.Product;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.CustomDialogBox;
import com.paypad.vuk507.utils.DataUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;


public class SelectColorFragment extends BaseFragment
    implements ColorSelectAdapter.ColorReturnCallback {

    private View mView;

    @BindView(R.id.backImgv)
    ClickableImageView backImgv;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;

    @BindView(R.id.imageRl)
    RelativeLayout imageRl;
    @BindView(R.id.itemShortNameTv)
    TextView itemShortNameTv;
    @BindView(R.id.colorRv)
    RecyclerView colorRv;
    @BindView(R.id.photoLabelll)
    LinearLayout photoLabelll;
    @BindView(R.id.choosePhotoBtn)
    Button choosePhotoBtn;
    @BindView(R.id.takePhotoBtn)
    Button takePhotoBtn;

    private User user;

    private String mClassTag;
    private String itemName;
    private int colorId;
    private ColorSelectAdapter.ColorReturnCallback colorReturnCallback;

    public SelectColorFragment(String classTag, String itemName, int colorId) {
        this.mClassTag = classTag;
        this.itemName = itemName;
        this.colorId = colorId;
    }

    public void setColorReturnCallback(ColorSelectAdapter.ColorReturnCallback colorReturnCallback) {
        this.colorReturnCallback = colorReturnCallback;
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
            mView = inflater.inflate(R.layout.fragment_select_color, container, false);
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
        backImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });
    }

    private void initVariables() {
        setToolbarTitleTv();
        setAdapter();
        itemShortNameTv.setText(DataUtils.getProductNameShortenName(itemName));

        if(mClassTag.equals(CategoryEditFragment.class.getName()))
            photoLabelll.setVisibility(View.GONE);
    }

    private void setToolbarTitleTv(){
        if(mClassTag.equals(CategoryEditFragment.class.getName()))
            toolbarTitleTv.setText(getResources().getString(R.string.edit_category_tile));
        else if(mClassTag.equals(ProductEditFragment.class.getName()))
            toolbarTitleTv.setText(getResources().getString(R.string.edit_item_tile));
    }

    private void setAdapter(){
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false);
        colorRv.setLayoutManager(gridLayoutManager);

        int[] colorList = CommonUtils.getItemColors();
        ColorSelectAdapter colorSelectAdapter = new ColorSelectAdapter(getContext(), colorList, colorId);
        colorSelectAdapter.setColorReturnCallback(this);
        colorRv.setAdapter(colorSelectAdapter);
    }

    @Override
    public void OnColorReturn(int colorId) {
        imageRl.setBackgroundColor(getResources().getColor(colorId, null));
        colorReturnCallback.OnColorReturn(colorId);
    }
}