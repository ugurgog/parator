package com.paypad.vuk507.menu.reports;

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

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.db.AutoIncrementDBHelper;
import com.paypad.vuk507.db.ReceiptDBHelper;
import com.paypad.vuk507.db.SaleDBHelper;
import com.paypad.vuk507.db.TaxDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.FinancialReportsEnum;
import com.paypad.vuk507.enums.PrinterStatusEnum;
import com.paypad.vuk507.enums.ReceiptTypeEnum;
import com.paypad.vuk507.enums.ReportsEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.menu.reports.adapters.ReportAdapter;
import com.paypad.vuk507.menu.reports.interfaces.ReturnReportItemCallback;
import com.paypad.vuk507.menu.reports.util.PrintSalesReportManager;
import com.paypad.vuk507.model.AutoIncrement;
import com.paypad.vuk507.model.Receipt;
import com.paypad.vuk507.model.Sale;
import com.paypad.vuk507.model.TaxModel;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.model.pojo.SaleModel;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.DataUtils;
import com.paypad.vuk507.utils.LogUtil;
import com.paypad.vuk507.utils.dialogboxutil.DialogBoxUtil;
import com.paypad.vuk507.utils.dialogboxutil.interfaces.InfoDialogBoxCallback;
import com.paypad.vuk507.utils.sunmiutils.SunmiPrintHelper;
import com.sunmi.peripheral.printer.InnerResultCallbcak;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

import static com.paypad.vuk507.constants.CustomConstants.LANGUAGE_TR;

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

    private PrintSalesReportManager printSalesReportManager;
    private User user;
    private List<SaleModel> saleModels;
    private Realm realm;
    private String eodResultStr;

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
                checkPrinterStatus();
            }
        });
    }

    private void checkPrinterStatus(){
        PrinterStatusEnum printerStatus = SunmiPrintHelper.getInstance().getPrinterStatus();
        if(!printerStatus.isNormal()){
            DialogBoxUtil.showErrorDialog(getContext(),
                    CommonUtils.getLanguage().equals(LANGUAGE_TR) ? printerStatus.getLabelTr() : printerStatus.getLabelEn(), new InfoDialogBoxCallback() {
                        @Override
                        public void okClick() {

                        }
                    });
            return;
        }else
            printEndOfDayReport();
    }

    private void printEndOfDayReport() {
        saleModels = SaleDBHelper.getSaleModelsNotProcessedEOD(user.getUuid());

        if(saleModels.size() == 0){
            CommonUtils.showToastShort(getContext(), getResources().getString(R.string.no_sale_for_eod_process));
            return;
        }

        printSalesReportManager = new PrintSalesReportManager(getContext(), FinancialReportsEnum.DAILY_X_REPORT, saleModels, user, new Date(), new Date() );
        printSalesReportManager.setCallback(mCallback);

        printSalesReportManager.printSaleReport();
    }

    private void initVariables() {
        realm = Realm.getDefaultInstance();
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
                        CommonUtils.showToastShort(getContext(), "Print successful");

                        boolean isSuccess = true;
                        for(SaleModel saleModel : saleModels){
                            isSuccess = updateSale(saleModel.getSale(), true);
                            if(!isSuccess)
                                break;
                        }

                        if(!isSuccess){
                            for(SaleModel saleModel : saleModels)
                                updateSale(saleModel.getSale(), false);

                            CommonUtils.showToastShort(getContext(), getContext().getResources().getString(R.string.eod_failed));
                        }else{
                            CommonUtils.showToastShort(getContext(), getContext().getResources().getString(R.string.eod_success));
                            updateAutoIncrementBatchNum();
                        }

                    }else{
                        CommonUtils.showToastShort(getContext(), "Print failed");
                    }
                }
            });
        }
    };

    private boolean updateSale(Sale sale, boolean isEndOfDayProcessed){
        realm.beginTransaction();

        Sale tempSale = realm.copyToRealm(sale);

        tempSale.setEndOfDayProcessed(isEndOfDayProcessed);

        realm.commitTransaction();

        BaseResponse baseResponse = SaleDBHelper.createOrUpdateSale(tempSale);
        DataUtils.showBaseResponseMessage(getContext(), baseResponse);

        if(!baseResponse.isSuccess())
            return false;

        return true;
    }

    private void updateAutoIncrementBatchNum(){
        long currentBatchNum = AutoIncrementDBHelper.getCurrentBatchNum(user.getUuid());
        long currentReceiptNum = AutoIncrementDBHelper.getCurrentReceiptNum(user.getUuid());

        AutoIncrement autoIncrement = AutoIncrementDBHelper.getAutoIncrement(user.getUuid());

        realm.beginTransaction();

        AutoIncrement tempAutoIncrement = realm.copyToRealm(autoIncrement);
        tempAutoIncrement.setBatchNum(currentBatchNum);
        tempAutoIncrement.setReceiptNum(currentReceiptNum);

        realm.commitTransaction();

        BaseResponse baseResponse = AutoIncrementDBHelper.createOrUpdateAutoIncrement(tempAutoIncrement);
        DataUtils.showBaseResponseMessage(getContext(), baseResponse);

        LogUtil.logAutoIncrement(tempAutoIncrement);

        saveReceipt(currentReceiptNum);
    }

    private void saveReceipt(long currentReceiptNum){

        Receipt receipt = new Receipt();

        realm.beginTransaction();
        receipt.setReceiptId(UUID.randomUUID().toString());

        Receipt tempReceipt = realm.copyToRealm(receipt);

        tempReceipt.setReceiptNum(currentReceiptNum);
        tempReceipt.setReceiptType(ReceiptTypeEnum.END_OF_DAY.getId());
        tempReceipt.setContent(eodResultStr);
        tempReceipt.setCreateUserId(user.getUuid());
        tempReceipt.setCreateDate(new Date());
        tempReceipt.setUpdateUserId(user.getUuid());
        tempReceipt.setUpdateDate(new Date());

        realm.commitTransaction();

        LogUtil.logReceipt(tempReceipt);

        BaseResponse baseResponse = ReceiptDBHelper.createOrUpdateReceipt(tempReceipt);
        DataUtils.showBaseResponseMessage(getContext(), baseResponse);
    }
}
