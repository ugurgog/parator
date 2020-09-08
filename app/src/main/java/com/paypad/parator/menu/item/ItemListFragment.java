package com.paypad.parator.menu.item;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.enums.ItemProcessEnum;
import com.paypad.parator.enums.ItemsEnum;
import com.paypad.parator.enums.TutorialTypeEnum;
import com.paypad.parator.interfaces.MenuItemCallback;
import com.paypad.parator.menu.category.CategoryFragment;
import com.paypad.parator.menu.category.interfaces.ReturnCategoryCallback;
import com.paypad.parator.menu.discount.DiscountFragment;
import com.paypad.parator.menu.discount.interfaces.ReturnDiscountCallback;
import com.paypad.parator.menu.product.ProductFragment;
import com.paypad.parator.menu.product.interfaces.ReturnItemCallback;
import com.paypad.parator.menu.tax.TaxFragment;
import com.paypad.parator.menu.unit.UnitFragment;
import com.paypad.parator.model.Category;
import com.paypad.parator.model.Discount;
import com.paypad.parator.model.Product;
import com.paypad.parator.uiUtils.tutorial.Tutorial;
import com.paypad.parator.uiUtils.tutorial.WalkthroughCallback;
import com.paypad.parator.utils.ClickableImage.ClickableImageView;
import com.paypad.parator.utils.CommonUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.paypad.parator.constants.CustomConstants.LANGUAGE_TR;
import static com.paypad.parator.constants.CustomConstants.WALK_THROUGH_CONTINUE;
import static com.paypad.parator.constants.CustomConstants.WALK_THROUGH_END;

public class ItemListFragment extends BaseFragment implements
        ReturnDiscountCallback,
        ReturnItemCallback,
        ReturnCategoryCallback,
        MenuItemCallback ,
        WalkthroughCallback {

    private View mView;

    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.backImgv)
    ClickableImageView backImgv;
    @BindView(R.id.itemsRv)
    RecyclerView itemsRv;

    private DiscountUpdateCallback discountUpdateCallback;
    private ProductUpdateCallback productUpdateCallback;
    private CategoryUpdateCallback categoryUpdateCallback;
    private WalkthroughCallback walkthroughCallback;
    private int walkthrough;
    private Tutorial tutorial;
    private TutorialTypeEnum mTutorialType;
    private Context mContext;

    @Override
    public void OnItemReturn(ItemsEnum itemType) {
        if(itemType == ItemsEnum.ALL_ITEMS){
            ProductFragment productFragment =
                    new ProductFragment(mTutorialType == TutorialTypeEnum.TUTORIAL_CREATE_ITEM ? walkthrough : WALK_THROUGH_END);
            productFragment.setReturnItemCallback(this);
            productFragment.setWalkthroughCallback(this);
            mFragmentNavigation.pushFragment(productFragment);
        }else if(itemType == ItemsEnum.CATEGORIES){
            CategoryFragment categoryFragment =
                    new CategoryFragment(mTutorialType == TutorialTypeEnum.TUTORIAL_CREATE_CATEGORY ? walkthrough : WALK_THROUGH_END);
            categoryFragment.setReturnCategoryCallback(this);
            categoryFragment.setWalkthroughCallback(this);
            mFragmentNavigation.pushFragment(categoryFragment);
        }else if(itemType == ItemsEnum.DISCOUNTS){
            DiscountFragment discountFragment = new DiscountFragment(mTutorialType == TutorialTypeEnum.TUTORIAL_CREATE_DISCOUNT ? walkthrough : WALK_THROUGH_END);
            discountFragment.setDiscountCallback(this);
            discountFragment.setWalkthroughCallback(this);
            mFragmentNavigation.pushFragment(discountFragment);
        }else if(itemType == ItemsEnum.UNITS){
            UnitFragment unitFragment = new UnitFragment(mTutorialType == TutorialTypeEnum.TUTORIAL_CREATE_UNIT ? walkthrough : WALK_THROUGH_END);
            unitFragment.setWalkthroughCallback(this);
            mFragmentNavigation.pushFragment(unitFragment);
        }else if(itemType == ItemsEnum.TAXES){
            TaxFragment taxFragment = new TaxFragment(mTutorialType == TutorialTypeEnum.TUTORIAL_CREATE_TAX ? walkthrough : WALK_THROUGH_END);
            taxFragment.setWalkthroughCallback(this);
            mFragmentNavigation.pushFragment(taxFragment);
        }
    }

    public void setWalkthroughCallback(WalkthroughCallback walkthroughCallback) {
        this.walkthroughCallback = walkthroughCallback;
    }

    public interface DiscountUpdateCallback{
        void OnDiscountUpdated();
    }

    public interface ProductUpdateCallback{
        void OnProductUpdated();
    }

    public interface CategoryUpdateCallback{
        void OnCategoryUpdated();
    }

    public ItemListFragment(int walkthrough, TutorialTypeEnum tutorialType) {
        this.walkthrough = walkthrough;
        mTutorialType = tutorialType;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_item_list, container, false);
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
        toolbarTitleTv.setText(mContext.getResources().getString(R.string.items));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        itemsRv.setLayoutManager(linearLayoutManager);
        setAdapter();
        initTutorial();
    }

    private void initTutorial() {
        tutorial = mView.findViewById(R.id.tutorial);
        tutorial.setWalkthroughCallback(this);
        if(walkthrough == WALK_THROUGH_CONTINUE){
            tutorial.setLayoutVisibility(View.VISIBLE);

            if(mTutorialType == TutorialTypeEnum.TUTORIAL_CREATE_ITEM)
                tutorial.setTutorialMessage(mContext.getResources().getString(R.string.now_select_all_items));
            else if(mTutorialType == TutorialTypeEnum.TUTORIAL_CREATE_TAX)
                tutorial.setTutorialMessage(mContext.getResources().getString(R.string.now_select_taxes));
            else if(mTutorialType == TutorialTypeEnum.TUTORIAL_CREATE_CATEGORY)
                tutorial.setTutorialMessage(mContext.getResources().getString(R.string.now_select_categories));
            else if(mTutorialType == TutorialTypeEnum.TUTORIAL_CREATE_UNIT)
                tutorial.setTutorialMessage(mContext.getResources().getString(R.string.now_select_units));
            else if(mTutorialType == TutorialTypeEnum.TUTORIAL_CREATE_DISCOUNT)
                tutorial.setTutorialMessage(mContext.getResources().getString(R.string.now_select_discounts));
        }
    }

    private void setAdapter() {
        ItemsAdapter itemAdapter = new ItemsAdapter();
        itemAdapter.setMenuItemCallback(this);
        itemsRv.setAdapter(itemAdapter);
    }

    @Override
    public void OnReturn(Discount discount, ItemProcessEnum processType) {
        discountUpdateCallback.OnDiscountUpdated();
    }

    @Override
    public void OnReturn(Product product, ItemProcessEnum processEnum) {
        productUpdateCallback.OnProductUpdated();
    }

    @Override
    public void OnReturn(Category category) {
        categoryUpdateCallback.OnCategoryUpdated();
    }

    public void setDiscountUpdateCallback(DiscountUpdateCallback discountUpdateCallback) {
        this.discountUpdateCallback = discountUpdateCallback;
    }

    public void setProductUpdateCallback(ProductUpdateCallback productUpdateCallback) {
        this.productUpdateCallback = productUpdateCallback;
    }

    public void setCategoryUpdateCallback(CategoryUpdateCallback categoryUpdateCallback) {
        this.categoryUpdateCallback = categoryUpdateCallback;
    }

    public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemHolder> {

        private ItemsEnum[] values;
        private MenuItemCallback menuItemCallback;

        ItemsAdapter() {
            this.values = ItemsEnum.values();
        }

        void setMenuItemCallback(MenuItemCallback menuItemCallback) {
            this.menuItemCallback = menuItemCallback;
        }

        @NonNull
        @Override
        public ItemsAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView;
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_simple_list_item, parent, false);
            return new ItemsAdapter.ItemHolder(itemView);
        }

        public class ItemHolder extends RecyclerView.ViewHolder {

            private CardView itemCv;
            private TextView nameTv;
            private ItemsEnum itemType;
            private int position;

            public ItemHolder(View view) {
                super(view);
                itemCv = view.findViewById(R.id.itemCv);
                nameTv = view.findViewById(R.id.nameTv);

                itemCv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        menuItemCallback.OnItemReturn(itemType);
                    }
                });
            }

            public void setData(ItemsEnum itemType, int position) {
                this.itemType = itemType;
                this.position = position;

                if(CommonUtils.getLanguage().equals(LANGUAGE_TR))
                    nameTv.setText(itemType.getLabelTr());
                else
                    nameTv.setText(itemType.getLabelEn());
            }
        }

        @Override
        public void onBindViewHolder(final ItemsAdapter.ItemHolder holder, final int position) {
            ItemsEnum itemType = values[position];
            holder.setData(itemType, position);
        }

        @Override
        public int getItemCount() {
            if(values != null)
                return values.length;
            else
                return 0;
        }
    }

    @Override
    public void OnWalkthroughResult(int result) {
        walkthrough = result;
        walkthroughCallback.OnWalkthroughResult(result);
        if(result == WALK_THROUGH_END)
            tutorial.setLayoutVisibility(View.GONE);
    }
}