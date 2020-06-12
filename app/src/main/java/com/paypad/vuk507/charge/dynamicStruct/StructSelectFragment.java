package com.paypad.vuk507.charge.dynamicStruct;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.charge.dynamicStruct.adapters.StructSelectListAdapter;
import com.paypad.vuk507.db.TaxDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.enums.TaxRateEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.interfaces.ReturnSizeCallback;
import com.paypad.vuk507.menu.tax.TaxEditFragment;
import com.paypad.vuk507.menu.tax.adapters.TaxSelectListAdapter;
import com.paypad.vuk507.menu.tax.interfaces.ReturnTaxCallback;
import com.paypad.vuk507.model.TaxModel;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CommonUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class StructSelectFragment extends BaseFragment {

    @BindView(R.id.closeImgBtn)
    ImageButton closeImgBtn;
    @BindView(R.id.structRv)
    RecyclerView structRv;

    private View mView;

    private StructSelectListAdapter structSelectListAdapter;

    public StructSelectFragment() {

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_struct_select, container, false);
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
        closeImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });
    }

    private void initVariables() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        structRv.setLayoutManager(linearLayoutManager);
        structRv.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getContext()), LinearLayoutManager.VERTICAL));
        updateAdapterWithCurrentList();
    }

    public void updateAdapterWithCurrentList(){
        structSelectListAdapter = new StructSelectListAdapter(getContext(), mFragmentNavigation);
        structRv.setAdapter(structSelectListAdapter);
    }
}