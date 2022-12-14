package com.paypad.parator.menu.group;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.db.GroupDBHelper;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.enums.ItemProcessEnum;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.interfaces.ReturnObjectCallback;
import com.paypad.parator.menu.group.adapters.MultiGroupSelectListAdapter;
import com.paypad.parator.menu.group.interfaces.ReturnGroupCallback;
import com.paypad.parator.model.Group;
import com.paypad.parator.model.User;
import com.paypad.parator.utils.ClickableImage.ClickableImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class SelectMultiGroupFragment extends BaseFragment {

    private View mView;

    @BindView(R.id.cancelImgv)
    ClickableImageView cancelImgv;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.saveBtn)
    Button saveBtn;
    @BindView(R.id.infoTv)
    TextView infoTv;
    @BindView(R.id.createBtn)
    Button createBtn;
    @BindView(R.id.itemRv)
    RecyclerView itemRv;

    private MultiGroupSelectListAdapter multiGroupSelectListAdapter;
    private Realm realm;

    private RealmResults<Group> groups;
    private List<Group> groupList;
    private User user;
    private List<Group> selectedGroupList;
    private ReturnObjectCallback<List<Group>> returnObjectCallback;

    public SelectMultiGroupFragment(List<Group> selectedGroupList, ReturnObjectCallback returnObjectCallback) {
        this.selectedGroupList = selectedGroupList;
        this.returnObjectCallback = returnObjectCallback;
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
            mView = inflater.inflate(R.layout.fragment_select_group_for_customer_add, container, false);
            ButterKnife.bind(this, mView);
            initVariables();
            initListeners();
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    private void initVariables() {
        realm = Realm.getDefaultInstance();
        itemRv = mView.findViewById(R.id.itemRv);
        toolbarTitleTv.setText(getResources().getString(R.string.select_group));
        saveBtn.setText(getContext().getResources().getString(R.string.done));
        //CommonUtils.setSaveBtnEnability(false, saveBtn, getContext());
        infoTv.setVisibility(View.GONE);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        itemRv.setLayoutManager(linearLayoutManager);
        updateAdapterWithCurrentList();
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
                returnObjectCallback.OnReturn(selectedGroupList);
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFragmentNavigation.pushFragment(new GroupCreateFragment(null, new ReturnGroupCallback() {
                    @Override
                    public void OnGroupReturn(Group group, ItemProcessEnum processEnum) {
                        if(processEnum == ItemProcessEnum.INSERTED)
                            updateAdapterWithCurrentList();
                    }
                }));
            }
        });
    }

    public void updateAdapterWithCurrentList(){
        groups = GroupDBHelper.getUserGroups(user.getId());
        groupList = new ArrayList(groups);

        multiGroupSelectListAdapter = new MultiGroupSelectListAdapter(selectedGroupList, groupList, new ReturnGroupCallback() {
            @Override
            public void OnGroupReturn(Group group, ItemProcessEnum processEnum) {
                if(processEnum == ItemProcessEnum.SELECTED){
                    selectedGroupList.add(group);
                    //CommonUtils.setSaveBtnEnability(true, saveBtn, getContext());
                }else {
                    selectedGroupList.remove(group);

                    //if(selectedGroupList.size() == 0)
                    //    CommonUtils.setSaveBtnEnability(false, saveBtn, getContext());
                }
            }
        });
        itemRv.setAdapter(multiGroupSelectListAdapter);
    }
}