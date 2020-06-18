package com.paypad.vuk507.charge;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.charge.dynamicStruct.DynamicItemSelectFragmant;
import com.paypad.vuk507.charge.dynamicStruct.adapters.DynamicStructListAdapter;
import com.paypad.vuk507.charge.dynamicStruct.StructSelectFragment;
import com.paypad.vuk507.charge.dynamicStruct.interfaces.ReturnDynamicBoxListener;
import com.paypad.vuk507.db.DynamicBoxModelDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.DynamicStructEnum;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.enums.PaymentTypeEnum;
import com.paypad.vuk507.enums.ProductUnitTypeEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.interfaces.CustomDialogListener;
import com.paypad.vuk507.model.Category;
import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.model.DynamicBoxModel;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.uiUtils.keypad.KeyPad;
import com.paypad.vuk507.uiUtils.keypad.KeyPadClick;
import com.paypad.vuk507.uiUtils.keypad.keyPadClickListener;
import com.paypad.vuk507.model.Product;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.CustomDialogBox;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.paypad.vuk507.constants.CustomConstants.LANGUAGE_TR;

public class KeypadFragment extends BaseFragment implements
        StructSelectFragment.StructSelectListener,
        DynamicItemSelectFragmant.ProductSelectListener,
        DynamicItemSelectFragmant.DiscountSelectListener,
        DynamicItemSelectFragmant.CategorySelectListener,
        DynamicItemSelectFragmant.PaymentSelectListener {

    View mView;

    @BindView(R.id.currencySymbolTv)
    TextView currencySymbolTv;
    @BindView(R.id.taxTypeRv)
    RecyclerView taxTypeRv;
    @BindView(R.id.noteMainll)
    LinearLayout noteMainll;
    @BindView(R.id.notePicImgv)
    ImageView notePicImgv;
    @BindView(R.id.keypadMainLl)
    LinearLayout keypadMainLl;

    private KeyPad keypad;
    //private ProductTaxListAdapter productTaxListAdapter;
    private User user;
    private DynamicStructListAdapter dynamicStructListAdapter;

    private StructSelectFragment structSelectFragment;
    private DynamicItemSelectFragmant dynamicItemSelectFragmant;

    private Realm realm;
    private long categoryId;

    private static final int DYNAMIC_BOX_COUNT = 8;

    public KeypadFragment() {

    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_keypad, container, false);
        ButterKnife.bind(this, mView);
        initVariables();
        initListeners();
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

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

    private void initVariables() {
        realm = Realm.getDefaultInstance();
        keypad = mView.findViewById(R.id.keypad);
        currencySymbolTv.setText(CommonUtils.getCurrency().getSymbol());

        structSelectFragment = new StructSelectFragment();
        structSelectFragment.setStructListener(this);

        initRecyclerView();

    }

    private void initListeners() {
        keypad.setOnNumPadClickListener(new KeyPadClick(new keyPadClickListener() {
            @Override
            public void onKeypadClicked(ArrayList<Integer> nums) {

                Log.i("Info", "nums:" + nums);
            }
        }));
    }

    private void initRecyclerView(){
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.HORIZONTAL, false);
        taxTypeRv.setLayoutManager(gridLayoutManager);
        setDynamicBoxAdapter();
    }

    public void setDynamicBoxAdapter(){
        if(user == null || user.getUuid() == null)
            return;

        RealmResults<DynamicBoxModel> dynamicBoxModels = DynamicBoxModelDBHelper.getAllDynamicBoxes(user.getUuid());
        List<DynamicBoxModel> dynamicBoxModelList = new ArrayList(dynamicBoxModels);

        if(dynamicBoxModelList.size() < DYNAMIC_BOX_COUNT){
            for(int i=dynamicBoxModelList.size(); i < DYNAMIC_BOX_COUNT; i++){
                DynamicBoxModel dynamicBoxModel = new DynamicBoxModel();
                dynamicBoxModelList.add(dynamicBoxModel);
            }
        }else {
            DynamicBoxModel dynamicBoxModel = new DynamicBoxModel();
            dynamicBoxModelList.add(dynamicBoxModel);
        }

        dynamicStructListAdapter = new DynamicStructListAdapter(getContext(), dynamicBoxModelList, mFragmentNavigation, new ReturnDynamicBoxListener() {
            @Override
            public void onReturn(DynamicBoxModel dynamicBoxModel, ItemProcessEnum processEnum) {

                if(processEnum == ItemProcessEnum.SELECTED){

                    if(dynamicBoxModel.getStructId() == 0){
                        structSelectFragment.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), structSelectFragment.getTag());
                    }else {
                        //TODO - burada artik odemeye dahil edecegiz
                    }
                }else if(processEnum == ItemProcessEnum.DELETED){
                    deleteDynamicBox(dynamicBoxModel);
                }
            }
        });
        taxTypeRv.setAdapter(dynamicStructListAdapter);
    }

    private void deleteDynamicBox(DynamicBoxModel dynamicBoxModel){
        new CustomDialogBox.Builder((Activity) getContext())
                .setMessage(getContext().getResources().getString(R.string.sure_to_delete_dynamic_box))
                .setNegativeBtnVisibility(View.VISIBLE)
                .setNegativeBtnText(getContext().getResources().getString(R.string.cancel))
                .setNegativeBtnBackground(getContext().getResources().getColor(R.color.Silver, null))
                .setPositiveBtnVisibility(View.VISIBLE)
                .setPositiveBtnText(getContext().getResources().getString(R.string.yes))
                .setPositiveBtnBackground(getContext().getResources().getColor(R.color.bg_screen1, null))
                .setDurationTime(0)
                .isCancellable(true)
                .setEditTextVisibility(View.GONE)
                .OnPositiveClicked(new CustomDialogListener() {
                    @Override
                    public void OnClick() {
                        DynamicBoxModelDBHelper.deleteDynamicBoxByStructAndItemId(dynamicBoxModel.getStructId(), dynamicBoxModel.getItemId(), user.getUuid(), new CompleteCallback() {
                            @Override
                            public void onComplete(BaseResponse baseResponse) {
                                CommonUtils.showToastShort(getContext(), baseResponse.getMessage());
                                if(baseResponse.isSuccess()){
                                    setDynamicBoxAdapter();
                                }
                            }
                        });

                    }
                })
                .OnNegativeClicked(new CustomDialogListener() {
                    @Override
                    public void OnClick() {

                    }
                }).build();
    }

    @Override
    public void onStructClick(DynamicStructEnum dynamicStructEnum, boolean fromCategory) {
        dynamicItemSelectFragmant = new DynamicItemSelectFragmant(dynamicStructEnum);
        dynamicItemSelectFragmant.setProductSelectListener(this);
        dynamicItemSelectFragmant.setDiscountSelectListener(this);
        dynamicItemSelectFragmant.setCategorySelectListener(this);
        dynamicItemSelectFragmant.setPaymentSelectListener(this);

        if(fromCategory)
            dynamicItemSelectFragmant.setCategoryId(categoryId);
        else
            dynamicItemSelectFragmant.setCategoryId(0);

        dynamicItemSelectFragmant.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), dynamicItemSelectFragmant.getTag());
    }

    @Override
    public void onPClick(Product product) {
        DynamicBoxModel dynamicBoxModel = new DynamicBoxModel();
        dynamicBoxModel.setCreateDate(new Date());
        dynamicBoxModel.setItemId(product.getId());
        dynamicBoxModel.setItemName(product.getName());
        dynamicBoxModel.setUserUuid(user.getUuid());
        dynamicBoxModel.setStructId(DynamicStructEnum.PRODUCT_SET.getId());
        createDynamicBox(dynamicBoxModel);
    }

    @Override
    public void onDClick(Discount discount) {
        DynamicBoxModel dynamicBoxModel = new DynamicBoxModel();
        dynamicBoxModel.setCreateDate(new Date());
        dynamicBoxModel.setItemId(discount.getId());
        dynamicBoxModel.setItemName(discount.getName());
        dynamicBoxModel.setUserUuid(user.getUuid());
        dynamicBoxModel.setStructId(DynamicStructEnum.DISCOUNT_SET.getId());
        createDynamicBox(dynamicBoxModel);
    }

    @Override
    public void onCClick(Category category) {
        Log.i("Info", "category_name:" + category.getName());
        this.categoryId = category.getId();
        dismissDynamicFragment();
        onStructClick(DynamicStructEnum.PRODUCT_SET, true);
    }

    private void createDynamicBox(DynamicBoxModel dynamicBoxModel){
        DynamicBoxModelDBHelper.createOrUpdateDynamicBox(dynamicBoxModel, new CompleteCallback() {
            @Override
            public void onComplete(BaseResponse baseResponse) {
                CommonUtils.showToastShort(getActivity(), baseResponse.getMessage());
                if(baseResponse.isSuccess()){
                    setDynamicBoxAdapter();
                }
                dismissDynamicFragment();
            }
        });
    }

    private void dismissDynamicFragment() {
        if(dynamicItemSelectFragmant != null)
            dynamicItemSelectFragmant.dismiss();
    }

    @Override
    public void onPaymentClick(PaymentTypeEnum paymentType) {
        DynamicBoxModel dynamicBoxModel = new DynamicBoxModel();
        dynamicBoxModel.setCreateDate(new Date());
        dynamicBoxModel.setItemId(paymentType.getId());
        dynamicBoxModel.setItemName(CommonUtils.getLanguage().equals(LANGUAGE_TR) ? paymentType.getLabelTr() : paymentType.getLabelEn());
        dynamicBoxModel.setUserUuid(user.getUuid());
        dynamicBoxModel.setStructId(DynamicStructEnum.PAYMENT_SET.getId());
        createDynamicBox(dynamicBoxModel);
    }
}