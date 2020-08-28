package com.paypad.parator.charge.sale;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.charge.interfaces.AmountCallback;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.model.Product;
import com.paypad.parator.model.User;
import com.paypad.parator.uiUtils.keypad.KeyPadClick;
import com.paypad.parator.uiUtils.keypad.KeyPadSingleNumberListener;
import com.paypad.parator.uiUtils.keypad.KeyPadWithoutAdd;
import com.paypad.parator.utils.ClickableImage.ClickableImageView;
import com.paypad.parator.utils.CommonUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.paypad.parator.constants.CustomConstants.MAX_PRICE_VALUE;
import static com.paypad.parator.constants.CustomConstants.TYPE_PRICE;

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
                    if(totalAmount == 0){
                        totalAmount = (totalAmount  + number) / 100;
                    }else {
                        totalAmount = (totalAmount * 10) + (number / 100.00d);
                    }

                    if(totalAmount > MAX_PRICE_VALUE){
                        totalAmount = MAX_PRICE_VALUE;
                    }

                    String amountStr = CommonUtils.getDoubleStrValueForView(totalAmount, TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol());
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