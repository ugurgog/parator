package com.paypad.parator.menu.reports.saleReport;

import androidx.fragment.app.FragmentActivity;

import com.paypad.parator.model.pojo.ReportModel;

public class ReportUIHelper {

    public static void addSaleOverviewFragment(FragmentActivity activity, ReportModel reportModel, int saleViewId){
            SalesSummaryOverviewFragment salesSummaryOverviewFragment = new SalesSummaryOverviewFragment(reportModel);
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(saleViewId, salesSummaryOverviewFragment, SalesSummaryOverviewFragment.class.getName())
                    .addToBackStack(null)
                    .commit();
    }

    public static void addSaleDetailsFragment(FragmentActivity activity, ReportModel reportModel, int saleViewId){
        SalesSummaryDetailFragment salesSummaryDetailFragment = new SalesSummaryDetailFragment(reportModel);
        activity.getSupportFragmentManager().beginTransaction()
                .replace(saleViewId, salesSummaryDetailFragment, SalesSummaryDetailFragment.class.getName())
                .addToBackStack(null)
                .commit();
    }
}
