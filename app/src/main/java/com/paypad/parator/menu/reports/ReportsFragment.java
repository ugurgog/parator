package com.paypad.parator.menu.reports;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.db.AutoIncrementDBHelper;
import com.paypad.parator.db.RefundDBHelper;
import com.paypad.parator.db.SaleDBHelper;
import com.paypad.parator.db.TransactionDBHelper;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.enums.FinancialReportsEnum;
import com.paypad.parator.enums.PrinterStatusEnum;
import com.paypad.parator.enums.ReportsEnum;
import com.paypad.parator.enums.ToastEnum;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.menu.reports.adapters.ReportAdapter;
import com.paypad.parator.menu.reports.interfaces.ReturnReportItemCallback;
import com.paypad.parator.menu.reports.util.PrintEODReportManager;
import com.paypad.parator.model.AutoIncrement;
import com.paypad.parator.model.Refund;
import com.paypad.parator.model.Order;
import com.paypad.parator.model.Transaction;
import com.paypad.parator.model.User;
import com.paypad.parator.model.pojo.BaseResponse;
import com.paypad.parator.utils.ClickableImage.ClickableImageView;
import com.paypad.parator.utils.CommonUtils;
import com.paypad.parator.utils.DataUtils;
import com.paypad.parator.utils.LogUtil;
import com.paypad.parator.utils.sunmiutils.SunmiPrintHelper;
import com.sunmi.peripheral.printer.InnerResultCallbcak;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

import static com.paypad.parator.constants.CustomConstants.LANGUAGE_TR;

public class ReportsFragment extends BaseFragment  implements ReturnReportItemCallback {

    private View mView;

    @BindView(R.id.reportsRv)
    RecyclerView reportsRv;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.backImgv)
    ClickableImageView backImgv;

    @BindView(R.id.nameTv)
    TextView nameTv;
    @BindView(R.id.mainLl)
    LinearLayout mainLl;
    @BindView(R.id.endOfDayBtn)
    Button endOfDayBtn;

    private PrintEODReportManager printEODReportManager;
    private User user;
    //private List<SaleModel> saleModels;
    private List<Order> orders;
    private Realm realm;
    private String eodResultStr;
    private AutoIncrement autoIncrement;

    private List<Transaction> transactions;
    private List<Refund> refunds;

    public ReportsFragment() {

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
    public void accountHolderUserReceived(UserBus userBus) {
        user = userBus.getUser();
        if (user == null)
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
            mView = inflater.inflate(R.layout.fragment_reports, container, false);
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

        mainLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFragmentNavigation.pushFragment(new SaleReportsFragment());
            }
        });

        endOfDayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processEOD();
            }
        });
    }

    private void processEOD() {
        transactions = TransactionDBHelper.getTransactionsByZNum(autoIncrement.getzNum());
        refunds = RefundDBHelper.getRefundsByZNum(autoIncrement.getzNum());

        if(transactions.size() == 0 && refunds.size() == 0){
            CommonUtils.showCustomToast(getContext(), getResources().getString(R.string.no_sale_for_eod_process), ToastEnum.TOAST_WARNING);
            return;
        }


        boolean errorOccurred = false;
        for(Transaction transaction : transactions){
            boolean success = updateTransaction(transaction, true);

            if(!success){
                errorOccurred = true;
                updateTransaction(transaction, false);
                break;
            }
        }

        if(!errorOccurred){
            for(Refund refund : refunds){
                boolean success = updateRefund(refund, true);

                if(!success){
                    errorOccurred = true;
                    updateRefund(refund, false);
                    break;
                }
            }
        }

        if(!errorOccurred){
            CommonUtils.showCustomToast(getContext(), getContext().getResources().getString(R.string.eod_success), ToastEnum.TOAST_SUCCESS);
            startPrintProcess();
            updateAutoIncrementBatchNum();
        }else
            CommonUtils.showCustomToast(getContext(), getContext().getResources().getString(R.string.eod_failed), ToastEnum.TOAST_ERROR);

        /*saleModels = SaleDBHelper.getSaleModelsNotProcessedEOD(user.getUuid());

        if(saleModels.size() == 0){
            CommonUtils.showToastShort(getContext(), getResources().getString(R.string.no_sale_for_eod_process));
            return;
        }

        boolean isSuccess = true;
        for(SaleModel saleModel : saleModels){
            isSuccess = updateSale(saleModel.getOrder(), true);
            if(!isSuccess)
                break;
        }

        if(!isSuccess){
            for(SaleModel saleModel : saleModels)
                updateSale(saleModel.getOrder(), false);

            CommonUtils.showToastShort(getContext(), getContext().getResources().getString(R.string.eod_failed));
        }else{
            CommonUtils.showToastShort(getContext(), getContext().getResources().getString(R.string.eod_success));
            startPrintProcess();
            updateAutoIncrementBatchNum();
        }*/
    }

    private void startPrintProcess(){
        PrinterStatusEnum printerStatus = SunmiPrintHelper.getInstance().getPrinterStatus();

        if(!printerStatus.isNormal()){
            CommonUtils.showCustomToast(getContext(),
                    CommonUtils.getLanguage().equals(LANGUAGE_TR) ? printerStatus.getLabelTr() : printerStatus.getLabelEn(), ToastEnum.TOAST_ERROR);
            return;
        }

        orders = SaleDBHelper.getOrdersByZNum(autoIncrement.getzNum());
        printEODReportManager = new PrintEODReportManager(getContext(), FinancialReportsEnum.DAILY_X_REPORT, orders, user, new Date(), new Date(), autoIncrement.getzNum(),
                transactions);
        printEODReportManager.setCallback(mCallback);

        printEODReportManager.printSaleReport();
    }

    private void initVariables() {
        realm = Realm.getDefaultInstance();
        autoIncrement = AutoIncrementDBHelper.getAutoIncrementByUserId(user.getId());
        toolbarTitleTv.setText(getContext().getResources().getString(R.string.reports));
        nameTv.setText(getContext().getResources().getString(R.string.sales_report));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        reportsRv.setLayoutManager(linearLayoutManager);
        setAdapter();
    }

    private void setAdapter() {
        ReportAdapter reportAdapter = new ReportAdapter();
        reportAdapter.setReturnReportItemCallback(this);
        reportsRv.setAdapter(reportAdapter);
    }

    @Override
    public void OnReturnReportItem(ReportsEnum reportsEnum) {
        if(reportsEnum == ReportsEnum.SALE_REPORTS){
            mFragmentNavigation.pushFragment(new SaleReportsFragment());
        }else if(reportsEnum == ReportsEnum.FINANCIAL_REPORTS){
            mFragmentNavigation.pushFragment(new ListFinancialReportsFragment());
        }
    }

    InnerResultCallbcak mCallback = new InnerResultCallbcak() {
        @Override
        public void onRunResult(boolean isSuccess) throws RemoteException {

        }

        @Override
        public void onReturnString(String result) throws RemoteException {
            eodResultStr = result;
        }

        @Override
        public void onRaiseException(int code, String msg) throws RemoteException {

        }

        @Override
        public void onPrintResult(int code, String msg) throws RemoteException {
            final int res = code;
            ((Activity)getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(res == 0){
                        CommonUtils.showCustomToast(getContext(), "Print successful", ToastEnum.TOAST_SUCCESS);

                        /*boolean isSuccess = true;
                        for(SaleModel saleModel : saleModels){
                            isSuccess = updateSale(saleModel.getOrder(), true);
                            if(!isSuccess)
                                break;
                        }

                        if(!isSuccess){
                            for(SaleModel saleModel : saleModels)
                                updateSale(saleModel.getOrder(), false);

                            CommonUtils.showToastShort(getContext(), getContext().getResources().getString(R.string.eod_failed));
                        }else{
                            CommonUtils.showToastShort(getContext(), getContext().getResources().getString(R.string.eod_success));
                            updateAutoIncrementBatchNum();
                        }*/

                    }else{
                        CommonUtils.showCustomToast(getContext(), "Print failed", ToastEnum.TOAST_ERROR);
                    }
                }
            });
        }
    };

    /*private boolean updateSale(Order sale, boolean isEndOfDayProcessed){
        realm.beginTransaction();

        Order tempSale = realm.copyFromRealm(sale);
        tempSale.setEndOfDayProcessed(isEndOfDayProcessed);

        realm.commitTransaction();

        BaseResponse baseResponse = SaleDBHelper.createOrUpdateSale(tempSale);
        DataUtils.showBaseResponseMessage(getContext(), baseResponse);

        if(!baseResponse.isSuccess())
            return false;

        return true;
    }*/

    private boolean updateTransaction(Transaction transaction, boolean isEndOfDayProcessed){
        realm.beginTransaction();

        Transaction transaction1 = realm.copyFromRealm(transaction);
        transaction1.setEODProcessed(isEndOfDayProcessed);
        transaction1.setEodDate(new Date());

        realm.commitTransaction();

        BaseResponse baseResponse = TransactionDBHelper.createOrUpdateTransaction(transaction1);
        DataUtils.showBaseResponseMessage(getContext(), baseResponse);

        if(!baseResponse.isSuccess())
            return false;

        return true;
    }

    private boolean updateRefund(Refund refund, boolean isEndOfDayProcessed){
        realm.beginTransaction();

        Refund refund1 = realm.copyFromRealm(refund);
        refund1.setEODProcessed(isEndOfDayProcessed);
        refund1.setEodDate(new Date());

        realm.commitTransaction();

        BaseResponse baseResponse = RefundDBHelper.createOrUpdateRefund(refund1);
        DataUtils.showBaseResponseMessage(getContext(), baseResponse);

        if(!baseResponse.isSuccess())
            return false;

        return true;
    }

    private void updateAutoIncrementBatchNum(){

        BaseResponse baseResponse = AutoIncrementDBHelper.updateZnumByNextValue(autoIncrement);
        autoIncrement = (AutoIncrement) baseResponse.getObject();

        LogUtil.logAutoIncrement(autoIncrement);
    }

}
