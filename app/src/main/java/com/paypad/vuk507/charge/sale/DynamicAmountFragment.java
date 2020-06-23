package com.paypad.vuk507.charge.sale;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.charge.dynamicStruct.DynamicItemSelectFragmant;
import com.paypad.vuk507.charge.dynamicStruct.StructSelectFragment;
import com.paypad.vuk507.charge.dynamicStruct.adapters.DynamicStructListAdapter;
import com.paypad.vuk507.charge.dynamicStruct.interfaces.ReturnDynamicBoxListener;
import com.paypad.vuk507.charge.interfaces.AmountCallback;
import com.paypad.vuk507.db.CategoryDBHelper;
import com.paypad.vuk507.db.DynamicBoxModelDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.DynamicStructEnum;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.enums.PaymentTypeEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.interfaces.CustomDialogListener;
import com.paypad.vuk507.menu.category.interfaces.ReturnCategoryCallback;
import com.paypad.vuk507.model.Category;
import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.model.DynamicBoxModel;
import com.paypad.vuk507.model.Product;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.uiUtils.keypad.KeyPad;
import com.paypad.vuk507.uiUtils.keypad.KeyPadClick;
import com.paypad.vuk507.uiUtils.keypad.KeyPadSingleNumberListener;
import com.paypad.vuk507.uiUtils.keypad.KeyPadWithoutAdd;
import com.paypad.vuk507.uiUtils.keypad.keyPadClickListener;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.CustomDialogBox;
import com.paypad.vuk507.utils.DataUtils;

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
import static com.paypad.vuk507.constants.CustomConstants.MAX_PRICE_VALUE;
import static com.paypad.vuk507.constants.CustomConstants.TYPE_PRICE;

public class DynamicAmountFragment extends BaseFragment{

    View mView;

    @BindView(R.id.amountTv)
    TextView amountTv;
    @BindView(R.id.cancelImgv)
    ClickableImageView cancelImgv;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.saveBtn)
    Button saveBtn;

    private KeyPadWithoutAdd keypad;
    private User user;
    private Product product;
    private AmountCallback amountCallback;
    private double totalAmount = 0;

    public DynamicAmountFragment(Product product, AmountCallback amountCallback) {
        this.product = product;
        this.amountCallback = amountCallback;
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
        mView = inflater.inflate(R.layout.fragment_dynamic_amount, container, false);
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
        amountTv.setHint("0,00 ".concat(CommonUtils.getCurrency().getSymbol()));
        toolbarTitleTv.setText(product.getName());
        saveBtn.setText(getResources().getString(R.string.add));
    }

    private void initListeners() {
        keypad.setOnNumPadClickListener(new KeyPadClick(new KeyPadSingleNumberListener() {
            @Override
            public void onKeypadClicked(Integer number) {
                Log.i("Info", "number:" + number);

                if(number == -1){
                    amountTv.setText("");
                    totalAmount = 0;
                }else {
                    String amountStr = DataUtils.getTotalAmount(totalAmount, number);
                    amountTv.setText(amountStr);
                }
            }
        }));

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amountCallback.OnDynamicAmountReturn(totalAmount);
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });

        cancelImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });
    }
}