package com.paypad.parator.menu.settings.checkoutoptions;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;
import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.enums.PaymentTypeEnum;
import com.paypad.parator.utils.ClickableImage.ClickableImageView;
import com.paypad.parator.utils.CommonUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;
import static com.paypad.parator.constants.CustomConstants.LANGUAGE_TR;

public class PaymentTypesEditFragment extends BaseFragment {

    private View mView;

    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.backImgv)
    ClickableImageView backImgv;
    @BindView(R.id.itemsRv)
    RecyclerView itemsRv;
    @BindView(R.id.previewTv)
    TextView previewTv;

    private Context mContext;

    public PaymentTypesEditFragment() {

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
            mView = inflater.inflate(R.layout.fragment_edit_payment_types, container, false);
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
                ((Activity)mContext).onBackPressed();
            }
        });

        previewTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFragmentNavigation.pushFragment(new PaymentTypesPreviewFragment());
            }
        });
    }

    private void initVariables() {
        toolbarTitleTv.setText(mContext.getResources().getString(R.string.payment_types));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        itemsRv.setLayoutManager(linearLayoutManager);
        setAdapter();
    }

    private void setAdapter() {
        PaymentTypesAdapter itemAdapter = new PaymentTypesAdapter();
        itemsRv.setAdapter(itemAdapter);
    }

    public class PaymentTypesAdapter extends RecyclerView.Adapter<PaymentTypesAdapter.ItemHolder> {

        private PaymentTypeEnum[] values;
        private SharedPreferences loginPreferences;
        private SharedPreferences.Editor loginPrefsEditor;

        PaymentTypesAdapter() {
            this.values = PaymentTypeEnum.values();
            loginPreferences = mContext.getSharedPreferences("disabledPaymentTypes", MODE_PRIVATE);
            loginPrefsEditor = loginPreferences.edit();
        }

        @NonNull
        @Override
        public PaymentTypesAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView;
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_edit_payment_types, parent, false);
            return new PaymentTypesAdapter.ItemHolder(itemView);
        }

        public class ItemHolder extends RecyclerView.ViewHolder {

            private TextView paymentTypeNameTv;
            private LabeledSwitch paymTypeSwitch;
            private PaymentTypeEnum itemType;
            private int position;

            public ItemHolder(View view) {
                super(view);
                paymentTypeNameTv = view.findViewById(R.id.paymentTypeNameTv);
                paymTypeSwitch = view.findViewById(R.id.paymTypeSwitch);

                paymTypeSwitch.setOnToggledListener(new OnToggledListener() {
                    @Override
                    public void onSwitched(ToggleableView toggleableView, boolean enabled) {
                        if(!enabled){
                            int disabledCount = 0;
                            PaymentTypeEnum[] paymentTypeEnums = PaymentTypeEnum.values();

                            for(PaymentTypeEnum paymentType : paymentTypeEnums){
                                if(!loginPreferences.getBoolean(String.valueOf(paymentType.getId()), false))
                                    disabledCount++;
                            }

                            if(disabledCount == paymentTypeEnums.length - 1){
                                paymTypeSwitch.setOn(true);
                                return;
                            }
                        }

                        if(itemType.getId() == PaymentTypeEnum.CASH.getId())
                            loginPrefsEditor.putBoolean(String.valueOf(PaymentTypeEnum.CASH.getId()), enabled);
                        else if(itemType.getId() == PaymentTypeEnum.CREDIT_CARD.getId())
                            loginPrefsEditor.putBoolean(String.valueOf(PaymentTypeEnum.CREDIT_CARD.getId()), enabled);
                        else if(itemType.getId() == PaymentTypeEnum.GIFT_CARD.getId())
                            loginPrefsEditor.putBoolean(String.valueOf(PaymentTypeEnum.GIFT_CARD.getId()), enabled);
                        else if(itemType.getId() == PaymentTypeEnum.CHECK.getId())
                            loginPrefsEditor.putBoolean(String.valueOf(PaymentTypeEnum.CHECK.getId()), enabled);

                        loginPrefsEditor.commit();
                    }
                });
            }

            public void setData(PaymentTypeEnum itemType, int position) {
                this.itemType = itemType;
                this.position = position;
                paymentTypeNameTv.setText(CommonUtils.getLanguage().equals(LANGUAGE_TR) ? itemType.getLabelTr() : itemType.getLabelEn());

                if(itemType.getId() == PaymentTypeEnum.CASH.getId())
                    paymTypeSwitch.setOn(loginPreferences.getBoolean(String.valueOf(PaymentTypeEnum.CASH.getId()), false));
                else if(itemType.getId() == PaymentTypeEnum.CREDIT_CARD.getId())
                    paymTypeSwitch.setOn(loginPreferences.getBoolean(String.valueOf(PaymentTypeEnum.CREDIT_CARD.getId()), false));
                else if(itemType.getId() == PaymentTypeEnum.GIFT_CARD.getId())
                    paymTypeSwitch.setOn(loginPreferences.getBoolean(String.valueOf(PaymentTypeEnum.GIFT_CARD.getId()), false));
                else if(itemType.getId() == PaymentTypeEnum.CHECK.getId())
                    paymTypeSwitch.setOn(loginPreferences.getBoolean(String.valueOf(PaymentTypeEnum.CHECK.getId()), false));
            }
        }

        @Override
        public void onBindViewHolder(final PaymentTypesAdapter.ItemHolder holder, final int position) {
            PaymentTypeEnum itemType = values[position];
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