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
import com.paypad.vuk507.db.SaleDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.FinancialReportsEnum;
import com.paypad.vuk507.enums.ReportsEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.menu.reports.adapters.ReportAdapter;
import com.paypad.vuk507.menu.reports.interfaces.ReturnReportItemCallback;
import com.paypad.vuk507.menu.reports.util.PrintSalesReportManager;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.model.pojo.SaleModel;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.DataUtils;
import com.sunmi.peripheral.printer.InnerResultCallbcak;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

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
                List<SaleModel> saleModels = SaleDBHelper.getSaleModelsForReport(user.getUuid(),
                        null, DataUtils.getStartTimeOfDate(new Date()), DataUtils.getEndTimeOfDate(new Date()));

                printSalesReportManager = new PrintSalesReportManager(getContext(), FinancialReportsEnum.DAILY_X_REPORT, saleModels, user.getUsername(), new Date(), new Date() );
                printSalesReportManager.setCallback(mCallback);

                printSalesReportManager.printSaleReport();
            }
        });
    }

    private void initVariables() {
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
                    }else{
                        CommonUtils.showToastShort(getContext(), "Print failed");
                    }
                }
            });
        }
    };
}
