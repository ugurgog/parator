package com.paypad.vuk507.charge;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.charge.dynamicStruct.DynamicStructListAdapter;
import com.paypad.vuk507.charge.dynamicStruct.StructSelectFragment;
import com.paypad.vuk507.charge.dynamicStruct.interfaces.ReturnDynamicBoxListener;
import com.paypad.vuk507.db.DynamicBoxModelDBHelper;
import com.paypad.vuk507.db.ProductDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.DynamicStructEnum;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.model.DynamicBoxModel;
import com.paypad.vuk507.uiUtils.keypad.KeyPad;
import com.paypad.vuk507.uiUtils.keypad.KeyPadClick;
import com.paypad.vuk507.uiUtils.keypad.keyPadClickListener;
import com.paypad.vuk507.menu.product.adapters.ProductTaxListAdapter;
import com.paypad.vuk507.menu.product.interfaces.ReturnItemCallback;
import com.paypad.vuk507.model.Product;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.utils.CommonUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;

public class KeypadFragment extends BaseFragment implements
        StructSelectFragment.StructSelectListener {

    View mView;

    @BindView(R.id.currencySymbolTv)
    TextView currencySymbolTv;
    @BindView(R.id.taxTypeRv)
    RecyclerView taxTypeRv;

    private KeyPad keypad;
    //private ProductTaxListAdapter productTaxListAdapter;
    private User user;
    private DynamicStructListAdapter dynamicStructListAdapter;

    private StructSelectFragment structSelectFragment;
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
        keypad = mView.findViewById(R.id.keypad);
        currencySymbolTv.setText(CommonUtils.getCurrency().getSymbol());
        initRecyclerView();
        structSelectFragment = new StructSelectFragment();
        structSelectFragment.setStructListener(this);
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
        setProductAdapter();

        /*LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        taxTypeRv.setLayoutManager(linearLayoutManager);
        taxTypeRv.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getContext()), LinearLayoutManager.HORIZONTAL));*/
    }

    public void setProductAdapter(){
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
            public void onReturn(DynamicBoxModel dynamicBoxModel) {

            }
        });
        taxTypeRv.setAdapter(dynamicStructListAdapter);


        /*RealmResults<Product> products = ProductDBHelper.getAllProducts(user.getUuid());
        List<Product> productList = new ArrayList(products);
        productTaxListAdapter = new ProductTaxListAdapter(getContext(), productList, new ReturnItemCallback() {
            @Override
            public void OnReturn(Product product, ItemProcessEnum processEnum) {
                structSelectFragment.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), structSelectFragment.getTag());
            }
        });
        taxTypeRv.setAdapter(productTaxListAdapter);*/
    }

    @Override
    public void onStructClick(DynamicStructEnum dynamicStructEnum) {

    }
}