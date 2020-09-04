package com.paypad.parator.menu.unit;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.db.UnitDBHelper;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.enums.ItemProcessEnum;
import com.paypad.parator.enums.ProductUnitTypeEnum;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.interfaces.ReturnSizeCallback;
import com.paypad.parator.menu.unit.adapters.UnitSelectListAdapter;
import com.paypad.parator.menu.unit.interfaces.ReturnUnitCallback;
import com.paypad.parator.model.UnitModel;
import com.paypad.parator.model.User;
import com.paypad.parator.utils.ClickableImage.ClickableImageView;
import com.paypad.parator.utils.CommonUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.paypad.parator.constants.CustomConstants.LANGUAGE_TR;
import static com.paypad.parator.constants.CustomConstants.WALK_THROUGH_END;

public class UnitSelectFragment extends BaseFragment {

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
    @BindView(R.id.createUnitBtn)
    Button createUnitBtn;

    @BindView(R.id.unitRv)
    RecyclerView unitRv;

    private UnitSelectListAdapter unitSelectListAdapter;

    private Realm realm;

    private RealmResults<UnitModel> unitModels;
    private List<UnitModel> unitModelList;
    private User user;
    private ReturnUnitCallback returnUnitCallback;

    public UnitSelectFragment(ReturnUnitCallback returnUnitCallback) {
        this.returnUnitCallback = returnUnitCallback;
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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_unit, container, false);
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

        createUnitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFragmentNavigation.pushFragment(new UnitEditFragment(null, WALK_THROUGH_END, new ReturnUnitCallback() {
                    @Override
                    public void OnReturn(UnitModel unitModel, ItemProcessEnum processEnum) {
                        updateAdapterWithCurrentList();
                    }
                }));
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

    private void initVariables() {
        realm = Realm.getDefaultInstance();
        toolbarTitleTv.setText(getContext().getResources().getString(R.string.units));
        addItemImgv.setVisibility(View.GONE);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        unitRv.setLayoutManager(linearLayoutManager);
        unitRv.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getContext()), LinearLayoutManager.VERTICAL));
        updateAdapterWithCurrentList();
    }

    public void updateAdapterWithCurrentList(){

        unitModelList = new ArrayList<>();
        addDefaultUnitTitle(getResources().getString(R.string.system_units));
        fillItems();

        unitModels = UnitDBHelper.getAllUnits(user.getId());

        if(unitModels != null && unitModels.size() > 0)
            addDefaultUnitTitle(getResources().getString(R.string.user_defined_units));

        unitModelList.addAll(new ArrayList(unitModels));

        unitSelectListAdapter = new UnitSelectListAdapter(unitModelList, mFragmentNavigation, ItemProcessEnum.SELECTED, new ReturnUnitCallback() {
            @Override
            public void OnReturn(UnitModel unitModel, ItemProcessEnum processEnum) {
                returnUnitCallback.OnReturn(unitModel, processEnum);
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });
        unitRv.setAdapter(unitSelectListAdapter);
    }

    private void addDefaultUnitTitle(String unitName){
        UnitModel unitModel = new UnitModel();
        unitModel.setId(0);
        unitModel.setName(unitName);
        unitModelList.add(unitModel);
    }

    private void fillItems() {
        ProductUnitTypeEnum[] values = ProductUnitTypeEnum.values();
        if(CommonUtils.getLanguage().equals(LANGUAGE_TR)){
            for(ProductUnitTypeEnum item : values){
                UnitModel unitModel = new UnitModel();
                unitModel.setId(item.getId());
                unitModel.setName(item.getLabelTr());
                unitModelList.add(unitModel);
            }
        }else{
            for(ProductUnitTypeEnum item : values){
                UnitModel unitModel = new UnitModel();
                unitModel.setId(item.getId());
                unitModel.setName(item.getLabelEn());
                unitModelList.add(unitModel);
            }
        }
    }

    public void updateAdapter(String searchText) {
        if (searchText != null && unitSelectListAdapter != null) {
            unitSelectListAdapter.updateAdapter(searchText, new ReturnSizeCallback() {
                @Override
                public void OnReturn(int size) {
                    if (size == 0 && (unitModelList != null && unitModelList.size() > 0))
                        searchResultTv.setVisibility(View.VISIBLE);
                    else
                        searchResultTv.setVisibility(View.GONE);
                }
            });
        }
    }
}