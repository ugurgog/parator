package com.paypad.parator.menu.reports.financialreports;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.enums.FinancialReportsEnum;
import com.paypad.parator.enums.ToastEnum;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.menu.reports.adapters.FinancialReportAdapter;
import com.paypad.parator.menu.reports.interfaces.ReturnFinancialReportItemCallback;
import com.paypad.parator.menu.reports.util.PrintEODReportManager;
import com.paypad.parator.model.User;
import com.paypad.parator.utils.ClickableImage.ClickableImageView;
import com.paypad.parator.utils.CommonUtils;
import com.sunmi.peripheral.printer.InnerResultCallbcak;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.paypad.parator.constants.CustomConstants.LANGUAGE_TR;

public class SalesReportFragment extends BaseFragment implements ReturnFinancialReportItemCallback {

    private View mView;

    @BindView(R.id.reportsRv)
    RecyclerView reportsRv;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.backImgv)
    ClickableImageView backImgv;

    private PrintEODReportManager printEODReportManager;
    private User user;

    public SalesReportFragment() {

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
    }

    private void initVariables() {
        toolbarTitleTv.setText(CommonUtils.getLanguage().equals(LANGUAGE_TR) ? FinancialReportsEnum.SALES_REPORT.getLabelTr() : FinancialReportsEnum.SALES_REPORT.getLabelEn());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        reportsRv.setLayoutManager(linearLayoutManager);
        setAdapter();
    }

    private void setAdapter() {

        FinancialReportsEnum[] financialReportsEnums = new FinancialReportsEnum[]{
                FinancialReportsEnum.PRINT_DAILY_REPORT,
                FinancialReportsEnum.PRINT_MONTHLY_REPORT,
                FinancialReportsEnum.SEND_MONTHLY_REPORT,
                FinancialReportsEnum.DAILY_X_REPORT,
                FinancialReportsEnum.MONTHLY_X_REPORT
        };

        FinancialReportAdapter reportAdapter = new FinancialReportAdapter(financialReportsEnums);
        reportAdapter.setCallback(this);
        reportsRv.setAdapter(reportAdapter);
    }

    @Override
    public void OnReturnReportItem(FinancialReportsEnum reportsEnum) {

        /*if(reportsEnum == FinancialReportsEnum.DAILY_X_REPORT){

            List<SaleModel> saleModels = SaleDBHelper.getSaleModelsForReport(user.getUuid(),
                    null, DataUtils.getStartTimeOfDate(new Date()), DataUtils.getEndTimeOfDate(new Date()));

            printEODReportManager = new PrintEODReportManager(getContext(), reportsEnum, saleModels, user, new Date(), new Date() );
            printEODReportManager.setCallback(mCallback);

            printEODReportManager.printSaleReport();
        }*/
    }


    InnerResultCallbcak mCallback = new InnerResultCallbcak() {
        @Override
        public void onRunResult(boolean isSuccess) throws RemoteException {

        }

        @Override
        public void onReturnString(String result) throws RemoteException {

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
                    }else{
                        CommonUtils.showCustomToast(getContext(), "Print failed", ToastEnum.TOAST_ERROR);
                    }
                }
            });
        }
    };
}
