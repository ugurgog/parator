package com.paypad.vuk507.menu.group.unused;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.db.CustomerDBHelper;
import com.paypad.vuk507.db.GroupDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.interfaces.ReturnSizeCallback;
import com.paypad.vuk507.menu.group.adapters.GroupListAdapter;
import com.paypad.vuk507.menu.group.interfaces.ReturnGroupCallback;
import com.paypad.vuk507.model.Group;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CommonUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class GroupListFragment extends BottomSheetDialogFragment {

    private View contentView;

    private ClickableImageView backImgv;
    private AppCompatTextView toolbarTitleTv;
    private EditText searchEdittext;
    private ImageView searchCancelImgv;
    private TextView searchResultTv;
    private RecyclerView contactRv;
    private LinearLayout contactMainll;

    private GroupListAdapter groupListAdapter;
    private ReturnGroupCallback returnGroupCallback;
    private Realm realm;

    private RealmResults<Group> groups;
    private List<Group> groupList;
    private User user;
    private BaseFragment.FragmentNavigation mFragmentNavigation;

    public GroupListFragment(ReturnGroupCallback returnGroupCallback, BaseFragment.FragmentNavigation mFragmentNavigation) {
        this.returnGroupCallback = returnGroupCallback;
        this.mFragmentNavigation = mFragmentNavigation;
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

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {

        super.setupDialog(dialog, style);
        contentView = View.inflate(getContext(), R.layout.fragment_contact_list, null);

        dialog.setContentView(contentView);

        initVariables();
        initListeners();

        View parent = ((View) contentView.getParent());

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) parent.getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
            ((BottomSheetBehavior) behavior).setPeekHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
            ((BottomSheetBehavior) behavior).setState(BottomSheetBehavior.STATE_EXPANDED);
        }

        parent.setBackgroundColor(getResources().getColor(android.R.color.transparent, null));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        contactRv.setLayoutManager(linearLayoutManager);

        updateAdapterWithCurrentList();
    }

    private void initVariables() {
        realm = Realm.getDefaultInstance();
        contactMainll = contentView.findViewById(R.id.contactMainll);
        contactRv = contentView.findViewById(R.id.contactRv);
        searchEdittext = contentView.findViewById(R.id.searchEdittext);
        searchCancelImgv = contentView.findViewById(R.id.searchCancelImgv);
        searchResultTv = contentView.findViewById(R.id.searchResultTv);
        toolbarTitleTv = contentView.findViewById(R.id.toolbarTitleTv);
        backImgv = contentView.findViewById(R.id.backImgv);

        toolbarTitleTv.setText(getResources().getString(R.string.groups));
    }

    private void initListeners() {
        backImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        searchEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && !s.toString().trim().isEmpty()) {
                    updateAdapter(s.toString());
                    searchCancelImgv.setVisibility(View.VISIBLE);
                } else {
                    updateAdapter("");
                    searchCancelImgv.setVisibility(View.GONE);
                }
            }
        });

        searchCancelImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEdittext.setText("");
                searchCancelImgv.setVisibility(View.GONE);
                CommonUtils.showKeyboard(getContext(),false, searchEdittext);
            }
        });
    }

    public void updateAdapterWithCurrentList(){
        groups = GroupDBHelper.getUserGroups(user.getUuid());
        groupList = new ArrayList(groups);

        groupListAdapter = new GroupListAdapter(groupList, new ReturnGroupCallback() {
            @Override
            public void OnGroupReturn(Group group, ItemProcessEnum processEnum) {

            }
        });
        contactRv.setAdapter(groupListAdapter);
    }

    public void updateAdapter(String searchText) {
        if (searchText != null) {
            groupListAdapter.updateAdapter(searchText, new ReturnSizeCallback() {
                @Override
                public void OnReturn(int size) {
                    if (size == 0)
                        searchResultTv.setVisibility(View.VISIBLE);
                    else
                        searchResultTv.setVisibility(View.GONE);
                }
            });
        }
    }
}