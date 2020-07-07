package com.paypad.vuk507.charge.sale;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.charge.interfaces.AmountCallback;
import com.paypad.vuk507.charge.interfaces.ReturnSaleItemCallback;
import com.paypad.vuk507.charge.interfaces.SaleCalculateCallback;
import com.paypad.vuk507.charge.order.IOrderManager;
import com.paypad.vuk507.charge.order.OrderManager;
import com.paypad.vuk507.charge.sale.adapters.SaleItemDiscountListAdapter;
import com.paypad.vuk507.charge.sale.adapters.SaleListAdapter;
import com.paypad.vuk507.db.CategoryDBHelper;
import com.paypad.vuk507.db.DiscountDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.menu.tax.TaxSelectFragment;
import com.paypad.vuk507.menu.tax.interfaces.ReturnTaxCallback;
import com.paypad.vuk507.model.Category;
import com.paypad.vuk507.model.Discount;
import com.paypad.vuk507.model.Product;
import com.paypad.vuk507.model.SaleItem;
import com.paypad.vuk507.model.TaxModel;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.model.order.OrderItemTax;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.model.pojo.SaleModel;
import com.paypad.vuk507.model.pojo.SaleModelInstance;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.ConversionHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

import static com.paypad.vuk507.constants.CustomConstants.TYPE_PRICE;
import static com.paypad.vuk507.constants.CustomConstants.TYPE_RATE;

public class SaleItemEditFragment extends BaseFragment {

    private View mView;

    @BindView(R.id.cancelImgv)
    ClickableImageView cancelImgv;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.saveBtn)
    Button saveBtn;

    @BindView(R.id.pricell)
    LinearLayout pricell;
    @BindView(R.id.priceTv)
    TextView priceTv;
    @BindView(R.id.priceLayout)
    LinearLayout priceLayout;
    @BindView(R.id.includingTaxPriceTv)
    TextView includingTaxPriceTv;

    @BindView(R.id.noteEt)
    EditText noteEt;
    @BindView(R.id.discountsRv)
    RecyclerView discountsRv;
    @BindView(R.id.btnDelete)
    Button btnDelete;


    @BindView(R.id.reduceQuantityBtn)
    Button reduceQuantityBtn;
    @BindView(R.id.quantityCountTv)
    TextView quantityCountTv;
    @BindView(R.id.increaseQuantityBtn)
    Button increaseQuantityBtn;
    @BindView(R.id.quantity)
    TextView quantity;

    @BindView(R.id.taxSelectrl)
    RelativeLayout taxSelectrl;
    @BindView(R.id.taxNameTv)
    TextView taxNameTv;
    @BindView(R.id.taxRateTv)
    TextView taxRateTv;


    private User user;
    private SaleItem saleItem;
    private SaleItemDiscountListAdapter saleItemDiscountListAdapter;
    private double saleAmount;
    private int quantityCount = 0;
    private ReturnSaleItemCallback returnSaleItemCallback;
    private int deleteButtonStatus = 1;
    private OrderItemTax orderItemTax;
    private boolean isTaxUpdated = false;
    private TaxModel mTaxModel;
    private IOrderManager orderManager;

    SaleItemEditFragment(SaleItem saleItem, ReturnSaleItemCallback callback) {
        this.saleItem = saleItem;
        this.returnSaleItemCallback = callback;
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_sale_item_edit, container, false);
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
        cancelImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saleItem.setAmount(saleAmount);
                saleItem.setAmountIncludingTax(saleAmount + ((saleAmount / 100d) * orderItemTax.getTaxRate()));
                saleItem.setQuantity(quantityCount);
                saleItem.setNote(noteEt.getText().toString());
                orderManager.getOrderItemCount();

                if(isTaxUpdated && mTaxModel != null)
                    orderManager.setOrderItemTaxForCustomItem(saleItem, mTaxModel);

                orderManager.setDiscountedAmountOfSale();

                returnSaleItemCallback.onReturn(saleItem, ItemProcessEnum.CHANGED);
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });


        reduceQuantityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(quantityCount > 0)
                    quantityCount --;
                quantityCountTv.setText(String.valueOf(quantityCount));
            }
        });

        increaseQuantityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantityCount ++;
                quantityCountTv.setText(String.valueOf(quantityCount));

            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(deleteButtonStatus == 1){
                    deleteButtonStatus ++;
                    CommonUtils.setBtnSecondCondition(Objects.requireNonNull(getContext()), btnDelete,
                            getContext().getResources().getString(R.string.confirm_delete));
                }else if(deleteButtonStatus == 2){
                    SaleModelInstance.getInstance().getSaleModel().getSaleItems().remove(saleItem);
                    returnSaleItemCallback.onReturn(null, ItemProcessEnum.DELETED);
                    Objects.requireNonNull(getActivity()).onBackPressed();
                }
            }
        });

        pricell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(saleItem.isDynamicAmount()){
                    Product product = new Product();
                    product.setName(saleItem.getName());
                    product.setAmount(saleItem.getAmount());
                    mFragmentNavigation.pushFragment(new DynamicAmountFragment(product, new AmountCallback() {
                        @Override
                        public void OnDynamicAmountReturn(double amount) {
                            saleAmount = amount;
                            setItemPrice();
                        }
                    }));
                }
            }
        });

        taxSelectrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(saleItem.getProductId() <= 0){
                    mFragmentNavigation.pushFragment(new TaxSelectFragment(new ReturnTaxCallback() {
                        @Override
                        public void OnReturn(TaxModel taxModel, ItemProcessEnum processEnum) {
                            if(taxModel != null && taxModel.getName() != null && !taxModel.getName().isEmpty()){
                                mTaxModel = taxModel;
                                orderItemTax = ConversionHelper.convertTaxModelToOrderItemTax(taxModel);
                                setTaxFields();
                                isTaxUpdated = true;
                            }
                        }
                    }));
                }
            }
        });
    }

    private void initVariables() {
        orderManager = new OrderManager();
        discountsRv.setNestedScrollingEnabled(false);

        //if(!saleItem.isDynamicAmount())
        //    priceLayout.setVisibility(View.GONE);

        setSaleItemVariables();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        discountsRv.setLayoutManager(linearLayoutManager);
        updateAdapterWithCurrentList();
    }

    private void setSaleItemVariables() {
        saleAmount = saleItem.getAmount();
        orderItemTax = saleItem.getOrderItemTaxes().get(0);
        quantityCount = saleItem.getQuantity();

        setItemPrice();
        setTaxFields();

        if(saleItem.getNote() != null && !saleItem.getNote().isEmpty())
            noteEt.setText(saleItem.getNote());

        quantityCountTv.setText(String.valueOf(quantityCount));
    }

    private void setItemPrice(){
        priceTv.setText(CommonUtils.getDoubleStrValueForView(saleAmount, TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol()));
        setToolbarTitle(saleAmount);

        if(saleItem.isDynamicAmount())
            priceTv.setTextColor(getResources().getColor(R.color.Black, null));
        else
            priceTv.setTextColor(getResources().getColor(R.color.Gray, null));

        setIncludingTaxAmount();
    }

    private void setToolbarTitle(double amount) {
        String amountStr = saleItem.getName().concat(" ").concat(CommonUtils.getDoubleStrValueForView(amount, TYPE_PRICE)).concat(" ").concat(CommonUtils.getCurrency().getSymbol());
        toolbarTitleTv.setText(amountStr);
    }

    private void setTaxFields(){
        taxNameTv.setText((orderItemTax != null && orderItemTax.getName() != null) ? orderItemTax.getName() : "");
        taxRateTv.setText("% ".concat(CommonUtils.getDoubleStrValueForView(orderItemTax.getTaxRate(), TYPE_RATE)));

        if(saleItem.getProductId() <= 0){
            taxNameTv.setTextColor(getResources().getColor(R.color.Black, null));
            taxRateTv.setTextColor(getResources().getColor(R.color.Black, null));
        }else {
            taxNameTv.setTextColor(getResources().getColor(R.color.Gray, null));
            taxRateTv.setTextColor(getResources().getColor(R.color.Gray, null));
        }

        setIncludingTaxAmount();
    }

    private void setIncludingTaxAmount(){
        double includingAmount = saleAmount + ((saleAmount / 100d) * orderItemTax.getTaxRate());
        includingTaxPriceTv.setText(CommonUtils.getDoubleStrValueForView(includingAmount, TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol()));
    }

    public void updateAdapterWithCurrentList(){
        List<Discount> discountList = new ArrayList<>();

        for(Discount discount : DiscountDBHelper.getAllDiscounts(user.getUsername())){
            if(discount.getRate() > 0d){
                discountList.add(discount);
            }
        }

        saleItemDiscountListAdapter = new SaleItemDiscountListAdapter(discountList, saleItem);
        discountsRv.setAdapter(saleItemDiscountListAdapter);
    }
}