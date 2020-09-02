package com.paypad.parator.menu.support;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.paypad.parator.enums.SupportListEnum;
import com.paypad.parator.interfaces.MenuItemCallback;
import com.paypad.parator.interfaces.SupportListItemCallback;
import com.paypad.parator.interfaces.TutorialSelectedCallback;
import com.paypad.parator.menu.category.CategoryFragment;
import com.paypad.parator.menu.category.interfaces.ReturnCategoryCallback;
import com.paypad.parator.menu.discount.DiscountFragment;
import com.paypad.parator.menu.discount.interfaces.ReturnDiscountCallback;
import com.paypad.parator.menu.product.ProductFragment;
import com.paypad.parator.menu.product.interfaces.ReturnItemCallback;
import com.paypad.parator.menu.support.toursandtutorials.ToursAndTutorialsFragment;
import com.paypad.parator.menu.tax.TaxFragment;
import com.paypad.parator.menu.unit.UnitFragment;
import com.paypad.parator.model.Category;
import com.paypad.parator.model.Discount;
import com.paypad.parator.model.Product;
import com.paypad.parator.utils.ClickableImage.ClickableImageView;
import com.paypad.parator.utils.CommonUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.paypad.parator.constants.CustomConstants.LANGUAGE_TR;

public class SupportFragment extends BaseFragment implements SupportListItemCallback, TutorialSelectedCallback {

    private View mView;

    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.backImgv)
    ClickableImageView backImgv;
    @BindView(R.id.itemsRv)
    RecyclerView itemsRv;

    private Context mContext;
    private ToursAndTutorialsFragment toursAndTutorialsFragment;
    private TutorialSelectedCallback tutorialSelectedCallback;

    public SupportFragment() {

    }

    public void setTutorialSelectedCallback(TutorialSelectedCallback tutorialSelectedCallback) {
        this.tutorialSelectedCallback = tutorialSelectedCallback;
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
        toolbarTitleTv.setText(mContext.getResources().getString(R.string.support));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        itemsRv.setLayoutManager(linearLayoutManager);
        setAdapter();
    }

    private void setAdapter() {
        SupportListAdapter itemAdapter = new SupportListAdapter();
        itemAdapter.setSupportListItemCallback(this);
        itemsRv.setAdapter(itemAdapter);
    }

    @Override
    public void OnItemReturn(SupportListEnum itemType) {
        if(itemType == SupportListEnum.TOURS_AND_TUTORIALS){
            initToursAndTutorialsFragment();
            mFragmentNavigation.pushFragment(toursAndTutorialsFragment);
        }
    }

    private void initToursAndTutorialsFragment(){
        toursAndTutorialsFragment = new ToursAndTutorialsFragment();
        toursAndTutorialsFragment.setTutorialSelectedCallback(this);
    }

    @Override
    public void OnSelectedTutorial(int selectedTutorial) {
        tutorialSelectedCallback.OnSelectedTutorial(selectedTutorial);
    }

    public class SupportListAdapter extends RecyclerView.Adapter<SupportListAdapter.ItemHolder> {

        private SupportListEnum[] values;
        private SupportListItemCallback supportListItemCallback;

        SupportListAdapter() {
            this.values = SupportListEnum.values();
        }

        public void setSupportListItemCallback(SupportListItemCallback supportListItemCallback) {
            this.supportListItemCallback = supportListItemCallback;
        }

        @NonNull
        @Override
        public SupportListAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView;
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_simple_list_item, parent, false);
            return new SupportListAdapter.ItemHolder(itemView);
        }

        public class ItemHolder extends RecyclerView.ViewHolder {

            private CardView itemCv;
            private TextView nameTv;
            private SupportListEnum itemType;
            private int position;

            public ItemHolder(View view) {
                super(view);
                itemCv = view.findViewById(R.id.itemCv);
                nameTv = view.findViewById(R.id.nameTv);

                itemCv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        supportListItemCallback.OnItemReturn(itemType);
                    }
                });
            }

            public void setData(SupportListEnum itemType, int position) {
                this.itemType = itemType;
                this.position = position;

                if(CommonUtils.getLanguage().equals(LANGUAGE_TR))
                    nameTv.setText(itemType.getLabelTr());
                else
                    nameTv.setText(itemType.getLabelEn());
            }
        }

        @Override
        public void onBindViewHolder(final SupportListAdapter.ItemHolder holder, final int position) {
            SupportListEnum itemType = values[position];
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
}