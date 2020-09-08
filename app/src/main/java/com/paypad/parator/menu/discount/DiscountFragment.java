package com.paypad.parator.menu.discount;


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
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.db.DiscountDBHelper;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.enums.ItemProcessEnum;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.interfaces.ClickCallback;
import com.paypad.parator.interfaces.CustomDialogListener;
import com.paypad.parator.interfaces.ReturnSizeCallback;
import com.paypad.parator.interfaces.TutorialPopupCallback;
import com.paypad.parator.menu.discount.adapters.DiscountListAdapter;
import com.paypad.parator.menu.discount.interfaces.ReturnDiscountCallback;
import com.paypad.parator.model.Discount;
import com.paypad.parator.model.User;
import com.paypad.parator.uiUtils.tutorial.WalkthroughCallback;
import com.paypad.parator.utils.ClickableImage.ClickableImageView;
import com.paypad.parator.utils.CommonUtils;
import com.paypad.parator.utils.CustomDialogBoxVert;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.paypad.parator.constants.CustomConstants.WALK_THROUGH_CONTINUE;
import static com.paypad.parator.constants.CustomConstants.WALK_THROUGH_END;

public class DiscountFragment extends BaseFragment implements WalkthroughCallback, ClickCallback {

    private View mView;

    @BindView(R.id.backImgv)
    ClickableImageView backImgv;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.searchEdittext)
    EditText searchEdittext;
    @BindView(R.id.addItemImgv)
    ImageView addItemImgv;
    @BindView(R.id.searchCancelImgv)
    ImageView searchCancelImgv;
    @BindView(R.id.searchResultTv)
    TextView searchResultTv;
    @BindView(R.id.createDiscountBtn)
    Button createDiscountBtn;

    @BindView(R.id.discountRv)
    RecyclerView discountRv;

    private User user;
    private Realm realm;
    private RealmResults<Discount> discounts;
    private List<Discount> discountList;
    private DiscountListAdapter discountListAdapter;
    private ReturnDiscountCallback discountCallback;
    private Context mContext;
    private DiscountEditFragment discountEditFragment;

    private int walkthrough;
    private WalkthroughCallback walkthroughCallback;
    private PopupWindow btnPopup;

    public DiscountFragment(int walkthrough) {
        this.walkthrough = walkthrough;
    }

    public void setWalkthroughCallback(WalkthroughCallback walkthroughCallback) {
        this.walkthroughCallback = walkthroughCallback;
    }

    public void setDiscountCallback(ReturnDiscountCallback discountCallback) {
        this.discountCallback = discountCallback;
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
            user = UserDBHelper.getUserFromCache(mContext);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_discount, container, false);
            ButterKnife.bind(this, mView);
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
                dismissPopup();
                ((Activity) mContext).onBackPressed();
            }
        });

        createDiscountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnPopup != null)
                    btnPopup.dismiss();
                initDiscountEditFragment(null, walkthrough);
                mFragmentNavigation.pushFragment(discountEditFragment);
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
                CommonUtils.showKeyboard(mContext,false, searchEdittext);
            }
        });
    }

    private void initVariables() {
        realm = Realm.getDefaultInstance();
        toolbarTitleTv.setText(mContext.getResources().getString(R.string.discounts));
        addItemImgv.setVisibility(View.GONE);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        discountRv.setLayoutManager(linearLayoutManager);
        //discountRv.addItemDecoration(new DividerItemDecoration(mContext, LinearLayoutManager.VERTICAL));
        updateAdapterWithCurrentList();
        checkTutorialIsActive();
    }

    private void checkTutorialIsActive() {
        if(walkthrough == WALK_THROUGH_CONTINUE){
            CommonUtils.displayPopupWindow(createDiscountBtn, mContext, mContext.getResources().getString(R.string.select_create_discount),
                    new TutorialPopupCallback() {
                        @Override
                        public void OnClosed() {
                            OnWalkthroughResult(WALK_THROUGH_END);
                            dismissPopup();
                        }

                        @Override
                        public void OnGetPopup(PopupWindow popupWindow) {
                            btnPopup = popupWindow;
                        }
                    });
        }
    }

    public void updateAdapterWithCurrentList(){
        discounts = DiscountDBHelper.getAllDiscounts(user.getId());
        discountList = new ArrayList(discounts);
        discountListAdapter = new DiscountListAdapter(mContext, discountList, mFragmentNavigation, new ReturnDiscountCallback() {
            @Override
            public void OnReturn(Discount discount, ItemProcessEnum processType) {
                initDiscountEditFragment(discount, WALK_THROUGH_END);
                mFragmentNavigation.pushFragment(discountEditFragment);
            }
        }, null);
        discountRv.setAdapter(discountListAdapter);
    }

    private void initDiscountEditFragment(Discount discount, int mWalkthrough){
        discountEditFragment = new DiscountEditFragment(discount, mWalkthrough, new ReturnDiscountCallback() {
            @Override
            public void OnReturn(Discount discount, ItemProcessEnum processType) {
                discountCallback.OnReturn(discount, processType);
                updateAdapterWithCurrentList();

                if(mWalkthrough == WALK_THROUGH_CONTINUE){

                    new CustomDialogBoxVert.Builder((Activity) mContext)
                            .setTitle(mContext.getResources().getString(R.string.created_your_first_discount))
                            .setMessage(mContext.getResources().getString(R.string.created_your_first_item_desc))
                            .setPositiveBtnVisibility(View.VISIBLE)
                            .setPositiveBtnText(mContext.getResources().getString(R.string.ok))
                            .setNegativeBtnText(mContext.getResources().getString(R.string.end_walkthrough))
                            .setPositiveBtnBackground(mContext.getResources().getColor(R.color.Green, null))
                            .setNegativeBtnBackground(mContext.getResources().getColor(R.color.custom_btn_bg_color, null))
                            .setNegativeBtnVisibility(View.GONE)
                            .setDurationTime(0)
                            .isCancellable(false)
                            .setEdittextVisibility(View.GONE)
                            .setpBtnTextColor(mContext.getResources().getColor(R.color.White, null))
                            .setnBtnTextColor(mContext.getResources().getColor(R.color.Green, null))
                            .OnPositiveClicked(new CustomDialogListener() {
                                @Override
                                public void OnClick() {
                                }
                            }).build();

                    walkthrough = WALK_THROUGH_END;
                    OnWalkthroughResult(walkthrough);
                    if(btnPopup != null)
                        btnPopup.dismiss();
                }
            }
        });
        discountEditFragment.setWalkthroughCallback(this);
    }

    public void updateAdapter(String searchText) {
        if (searchText != null && discountListAdapter != null) {
            discountListAdapter.updateAdapter(searchText, new ReturnSizeCallback() {
                @Override
                public void OnReturn(int size) {
                    if (size == 0 && (discountList != null && discountList.size() > 0))
                        searchResultTv.setVisibility(View.VISIBLE);
                    else
                        searchResultTv.setVisibility(View.GONE);
                }
            });
        }
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

    @Override
    public void OnClicked() {
        if(btnPopup != null)
            btnPopup.dismiss();
    }
}