package com.paypad.parator.menu.group;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.db.GroupDBHelper;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.enums.ItemProcessEnum;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.menu.group.interfaces.ReturnGroupCallback;
import com.paypad.parator.model.Group;
import com.paypad.parator.model.User;
import com.paypad.parator.model.pojo.BaseResponse;
import com.paypad.parator.utils.ClickableImage.ClickableImageView;
import com.paypad.parator.utils.CommonUtils;
import com.paypad.parator.utils.DataUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class GroupCreateFragment extends BaseFragment {

    private View mView;

    @BindView(R.id.nameEt)
    EditText nameEt;
    @BindView(R.id.cancelImgv)
    ClickableImageView cancelImgv;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.saveBtn)
    Button saveBtn;
    @BindView(R.id.btnDelete)
    Button btnDelete;
    @BindView(R.id.mainll)
    LinearLayout mainll;

    private Realm realm;
    private Group group;
    private ReturnGroupCallback returnGroupCallback;
    private User user;
    private int deleteButtonStatus = 1;

    public GroupCreateFragment(Group group, ReturnGroupCallback returnGroupCallback) {
        this.group = group;
        this.returnGroupCallback = returnGroupCallback;
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
            mView = inflater.inflate(R.layout.fragment_edit_group, container, false);
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

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidGroup();
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

                    BaseResponse baseResponse = GroupDBHelper.deleteGroup(group.getId(), user.getId());
                    DataUtils.showBaseResponseMessage(getContext(), baseResponse);

                    if(baseResponse.isSuccess()){
                        returnGroupCallback.OnGroupReturn((Group) baseResponse.getObject(), ItemProcessEnum.DELETED);
                    }
                }
            }
        });
    }

    private void initVariables() {
        realm = Realm.getDefaultInstance();
        CommonUtils.setBtnFirstCondition(getContext(), btnDelete, getContext().getResources().getString(R.string.delete_group));

        if(group == null){
            group = new Group();
            btnDelete.setEnabled(false);
            toolbarTitleTv.setText(getContext().getResources().getString(R.string.create_group));
        }else{
            toolbarTitleTv.setText(getContext().getResources().getString(R.string.edit_group));
            nameEt.setText(group.getName());
        }
    }

    private void checkValidGroup() {
        CommonUtils.hideKeyBoard(Objects.requireNonNull(getContext()));
        if(nameEt.getText() == null || nameEt.getText().toString().isEmpty()){
            CommonUtils.snackbarDisplay(mainll,
                    Objects.requireNonNull(getContext()), getContext().getResources().getString(R.string.group_name_can_not_be_empty));
            return;
        }

        updateGroup();
    }

    private void updateGroup() {

        boolean inserted = false;
        realm.beginTransaction();

        if(group.getId() == 0){
            group.setId(GroupDBHelper.getGroupCurrentPrimaryKeyId());
            group.setCreateDate(new Date());
            group.setUserId(user.getId());
            group.setDeleted(false);
            inserted = true;
        }else {
            group.setUpdateDate(new Date());
            group.setUpdateUserId(user.getId());
        }

        Group tempGroup = realm.copyToRealm(group);
        tempGroup.setName(nameEt.getText().toString());

        realm.commitTransaction();

        boolean finalInserted = inserted;

        BaseResponse baseResponse = GroupDBHelper.createOrUpdateGroup(tempGroup);
        DataUtils.showBaseResponseMessage(getContext(), baseResponse);

        if(baseResponse.isSuccess()){
            deleteButtonStatus = 1;
            CommonUtils.setBtnFirstCondition(Objects.requireNonNull(getContext()), btnDelete,
                    getContext().getResources().getString(R.string.delete_group));
            btnDelete.setEnabled(false);

            if(finalInserted)
                returnGroupCallback.OnGroupReturn((Group) baseResponse.getObject(), ItemProcessEnum.INSERTED);
            else
                returnGroupCallback.OnGroupReturn((Group) baseResponse.getObject(), ItemProcessEnum.CHANGED);

            clearViews();
            Objects.requireNonNull(getActivity()).onBackPressed();
        }
    }

    private void clearViews() {
        nameEt.setText("");
        group = new Group();
        CommonUtils.hideKeyBoard(Objects.requireNonNull(getContext()));
    }
}