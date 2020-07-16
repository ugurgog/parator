package com.paypad.vuk507.menu.reports.saleReport;

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.paypad.vuk507.R;
import com.paypad.vuk507.model.pojo.ReportModel;

public class ReportUIHelper {

    public static void addSaleOverviewFragment(FragmentActivity activity, ReportModel reportModel, int saleViewId){
            SaleOverviewFragment saleOverviewFragment = new SaleOverviewFragment(reportModel);
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(saleViewId, saleOverviewFragment, SaleOverviewFragment.class.getName())
                    .addToBackStack(null)
                    .commit();
    }

    public static void addSaleDetailsFragment(FragmentActivity activity, ReportModel reportModel, int saleViewId){
        SaleDetailsFragment saleDetailsFragment = new SaleDetailsFragment(reportModel);
        activity.getSupportFragmentManager().beginTransaction()
                .replace(saleViewId, saleDetailsFragment, SaleDetailsFragment.class.getName())
                .addToBackStack(null)
                .commit();
    }
}
