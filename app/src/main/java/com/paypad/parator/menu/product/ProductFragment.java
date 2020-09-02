package com.paypad.parator.menu.product;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.db.ProductDBHelper;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.enums.ItemProcessEnum;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.interfaces.ClickCallback;
import com.paypad.parator.interfaces.CustomDialogListener;
import com.paypad.parator.interfaces.ReturnSizeCallback;
import com.paypad.parator.interfaces.TutorialPopupCallback;
import com.paypad.parator.menu.product.adapters.ProductListAdapter;
import com.paypad.parator.menu.product.interfaces.ReturnItemCallback;
import com.paypad.parator.model.Product;
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

public class ProductFragment extends BaseFragment implements WalkthroughCallback, ClickCallback {

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
    @BindView(R.id.createProductBtn)
    Button createProductBtn;
    @BindView(R.id.productRv)
    RecyclerView productRv;

    public ProductListAdapter productListAdapter;

    private Realm realm;
    private ReturnItemCallback returnItemCallback;

    private RealmResults<Product> products;
    private List<Product> productList;
    private User user;
    private int walkthrough;
    private Context mContext;
    private WalkthroughCallback walkthroughCallback;

    private ProductEditFragment productEditFragment;
    private PopupWindow btnPopup;


    public ProductFragment(int walkthrough) {
        this.walkthrough = walkthrough;
    }

    public void setReturnItemCallback(ReturnItemCallback returnItemCallback) {
        this.returnItemCallback = returnItemCallback;
    }

    public void setWalkthroughCallback(WalkthroughCallback walkthroughCallback) {
        this.walkthroughCallback = walkthroughCallback;
    }

    @Override
    public void onStart() {
        super.onStart();
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
            mView = inflater.inflate(R.layout.fragment_product, container, false);
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
                if(btnPopup != null)
                    btnPopup.dismiss();
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });

        createProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnPopup != null)
                    btnPopup.dismiss();
                initProductEditFragment();
                mFragmentNavigation.pushFragment(productEditFragment);
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

    private void initProductEditFragment(){
        productEditFragment = new ProductEditFragment(null, walkthrough, new ReturnItemCallback() {
            @Override
            public void OnReturn(Product product, ItemProcessEnum processEnum) {
                updateAdapterWithCurrentList();

                if(walkthrough == WALK_THROUGH_CONTINUE){

                    new CustomDialogBoxVert.Builder((Activity) mContext)
                            .setTitle(mContext.getResources().getString(R.string.created_your_first_item))
                            .setMessage(mContext.getResources().getString(R.string.created_your_first_item_desc))
                            .setNegativeBtnVisibility(View.VISIBLE)
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
        productEditFragment.setWalkthroughCallback(this);
    }

    private void initVariables() {
        realm = Realm.getDefaultInstance();
        toolbarTitleTv.setText(Objects.requireNonNull(getContext()).getResources().getString(R.string.items));
        addItemImgv.setVisibility(View.GONE);
        setShapes();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        productRv.setLayoutManager(linearLayoutManager);
        updateAdapterWithCurrentList();

        if(walkthrough == WALK_THROUGH_CONTINUE){
            CommonUtils.displayPopupWindow(createProductBtn, mContext, mContext.getResources().getString(R.string.select_create_item),
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

    public void updateAdapterWithCurrentList(){

        products = ProductDBHelper.getAllProducts(user.getId());
        productList = new ArrayList(products);
        productListAdapter = new ProductListAdapter(getContext(), productList, mFragmentNavigation, null, new ReturnItemCallback() {
            @Override
            public void OnReturn(Product product, ItemProcessEnum processEnum) {
                updateAdapterWithCurrentList();

                if(processEnum != ItemProcessEnum.SELECTED)
                    returnItemCallback.OnReturn(product, processEnum);
            }
        });
        productListAdapter.setClickCallback(this);
        productRv.setAdapter(productListAdapter);
    }

    private void setShapes() {
        //createTaxBtn.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.White, null),
        //        getResources().getColor(R.color.DodgerBlue, null), GradientDrawable.RECTANGLE, 20, 2));
    }

    public void updateAdapter(String searchText) {
        if (searchText != null && productListAdapter != null) {
            productListAdapter.updateAdapter(searchText, new ReturnSizeCallback() {
                @Override
                public void OnReturn(int size) {
                    if (size == 0 && (productList != null && productList.size() > 0))
                        searchResultTv.setVisibility(View.VISIBLE);
                    else
                        searchResultTv.setVisibility(View.GONE);
                }
            });
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
        //walkthrough = WALK_THROUGH_END;
        //OnWalkthroughResult(walkthrough);
    }
}