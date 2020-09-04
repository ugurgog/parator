package com.paypad.parator.menu.tax;


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
import com.paypad.parator.db.TaxDBHelper;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.enums.ItemProcessEnum;
import com.paypad.parator.enums.TaxRateEnum;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.interfaces.ClickCallback;
import com.paypad.parator.interfaces.CustomDialogListener;
import com.paypad.parator.interfaces.ReturnSizeCallback;
import com.paypad.parator.interfaces.TutorialPopupCallback;
import com.paypad.parator.menu.tax.adapters.TaxSelectListAdapter;
import com.paypad.parator.menu.tax.interfaces.ReturnTaxCallback;
import com.paypad.parator.model.TaxModel;
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

public class TaxFragment extends BaseFragment implements WalkthroughCallback, ClickCallback {

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
    @BindView(R.id.createTaxBtn)
    Button createTaxBtn;
    @BindView(R.id.taxRv)
    RecyclerView taxRv;

    private TaxSelectListAdapter taxSelectListAdapter;
    private TaxEditFragment taxEditFragment;

    private Realm realm;

    private RealmResults<TaxModel> taxModels;
    private List<TaxModel> taxModelList;
    private User user;
    private Context mContext;

    private int walkthrough;
    private WalkthroughCallback walkthroughCallback;
    private PopupWindow btnPopup;

    public TaxFragment(int walkthrough) {
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
            mView = inflater.inflate(R.layout.fragment_tax, container, false);
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

        createTaxBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnPopup != null)
                    btnPopup.dismiss();
                initTaxEditFragment();
                mFragmentNavigation.pushFragment(taxEditFragment);
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

    private void initTaxEditFragment(){
        taxEditFragment = new TaxEditFragment(null, walkthrough, new ReturnTaxCallback() {
            @Override
            public void OnReturn(TaxModel taxModel, ItemProcessEnum processEnum) {
                updateAdapterWithCurrentList();

                if(walkthrough == WALK_THROUGH_CONTINUE){

                    new CustomDialogBoxVert.Builder((Activity) mContext)
                            .setTitle(mContext.getResources().getString(R.string.created_your_first_tax))
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
        taxEditFragment.setWalkthroughCallback(this);
    }

    private void initVariables() {
        realm = Realm.getDefaultInstance();
        toolbarTitleTv.setText(Objects.requireNonNull(getContext()).getResources().getString(R.string.taxes));
        addItemImgv.setVisibility(View.GONE);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        taxRv.setLayoutManager(linearLayoutManager);
        taxRv.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getContext()), LinearLayoutManager.VERTICAL));
        updateAdapterWithCurrentList();
        checkTutorialIsActive();
    }

    private void checkTutorialIsActive() {
        if(walkthrough == WALK_THROUGH_CONTINUE){
            CommonUtils.displayPopupWindow(createTaxBtn, mContext, mContext.getResources().getString(R.string.select_create_tax),
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
        taxModelList = new ArrayList<>();
        addDefaultTaxTitle(getResources().getString(R.string.system_taxes));
        fillItems();

        taxModels = TaxDBHelper.getAllTaxes(user.getId());

        if(taxModels != null && taxModels.size() > 0)
            addDefaultTaxTitle(getResources().getString(R.string.user_defined_taxes));

        taxModelList.addAll(new ArrayList(taxModels));
        taxSelectListAdapter = new TaxSelectListAdapter(taxModelList, mFragmentNavigation, new ReturnTaxCallback() {
            @Override
            public void OnReturn(TaxModel taxModel, ItemProcessEnum processEnum) {
                updateAdapterWithCurrentList();
            }
        }, null);
        taxSelectListAdapter.setClickCallback(this);
        taxRv.setAdapter(taxSelectListAdapter);
    }

    private void addDefaultTaxTitle(String taxName){
        TaxModel taxModel = new TaxModel();
        taxModel.setId(0);
        taxModel.setName(taxName);
        taxModelList.add(taxModel);
    }

    private void fillItems() {
        TaxRateEnum[] values = TaxRateEnum.values();
        for(TaxRateEnum item : values){
            TaxModel taxModel = new TaxModel();
            taxModel.setName(item.getLabel());
            taxModel.setTaxRate(item.getRateValue());
            taxModel.setId(item.getId());
            taxModelList.add(taxModel);
        }
    }

    public void updateAdapter(String searchText) {
        if (searchText != null && taxSelectListAdapter != null) {
            taxSelectListAdapter.updateAdapter(searchText, new ReturnSizeCallback() {
                @Override
                public void OnReturn(int size) {
                    if (size == 0 && (taxModelList != null && taxModelList.size() > 0))
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