package com.paypad.vuk507.menu.reports;

import android.content.Context;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.paypad.vuk507.R;
import com.paypad.vuk507.charge.order.IOrderManager;
import com.paypad.vuk507.charge.order.OrderManager;
import com.paypad.vuk507.enums.ChartSaleSelectionEnum;
import com.paypad.vuk507.enums.ReportSelectionEnum;
import com.paypad.vuk507.model.SaleItem;
import com.paypad.vuk507.model.pojo.SaleModel;
import com.paypad.vuk507.utils.DataUtils;
import com.paypad.vuk507.utils.LogUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChartManager {

    private Context mContext;
    private List<SaleModel> saleModels;

    private ArrayList<BarEntry> BARENTRY ;
    private ArrayList BarEntryLabels ;
    private BarDataSet Bardataset ;
    private BarData BARDATA ;

    public BarData getBARDATA() {
        return BARDATA;
    }

    public ChartManager(Context context, List<SaleModel> saleModels) {
        mContext = context;
        this.saleModels = saleModels;
        BARENTRY = new ArrayList<>();
        BarEntryLabels = new ArrayList();
    }

    public void fillOneDayChartVariables(ChartSaleSelectionEnum chartSaleSelectionEnum){
        List<ChartOneDaySales> chartOneDaySalesList = getOneDaySalesList();

        AddValuesToBarEntryLabelsForDailySales();
        BARENTRY = new ArrayList<>();

        if (chartSaleSelectionEnum == ChartSaleSelectionEnum.GROSS_SALES) {
            Bardataset = new BarDataSet(BARENTRY, mContext.getResources().getString(R.string.gross_sales));

            for (ChartOneDaySales chartOneDaySales : chartOneDaySalesList) {
                BARENTRY.add(new BarEntry((float) chartOneDaySales.getGrossAmount(), (int) chartOneDaySales.getHour()));
            }
        } else if (chartSaleSelectionEnum == ChartSaleSelectionEnum.NET_SALES) {
            Bardataset = new BarDataSet(BARENTRY, mContext.getResources().getString(R.string.net_sales));

            for (ChartOneDaySales chartOneDaySales : chartOneDaySalesList) {
                BARENTRY.add(new BarEntry((float) chartOneDaySales.getNetAmount(), (int) chartOneDaySales.getHour()));
            }
        } else if (chartSaleSelectionEnum == ChartSaleSelectionEnum.SALES_COUNT) {
            Bardataset = new BarDataSet(BARENTRY, mContext.getResources().getString(R.string.sales_count));

            for (ChartOneDaySales chartOneDaySales : chartOneDaySalesList) {
                BARENTRY.add(new BarEntry((float) chartOneDaySales.getSaleCount(), (int) chartOneDaySales.getHour()));
            }
        }

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(Bardataset);




        BARDATA = new BarData(BarEntryLabels, Bardataset);
        //BARDATA = new BarData(dataSets);


        Bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
    }

    private List<ChartOneDaySales> getOneDaySalesList(){
        List<ChartOneDaySales> chartOneDaySalesList = new ArrayList<>();
        long previousHour = -1;

        for(SaleModel saleModel: saleModels){

            LogUtil.logSale(saleModel.getSale());

            if(DataUtils.getHourNumOfDate(saleModel.getSale().getCreateDate()) != previousHour){
                ChartOneDaySales chartOneDaySales1 = new ChartOneDaySales();
                chartOneDaySales1.setHour(DataUtils.getHourNumOfDate(saleModel.getSale().getCreateDate()));

                double grossSalesAmount = getGrossAmount(saleModel.getSaleItems());

                chartOneDaySales1.setGrossAmount(grossSalesAmount);
                chartOneDaySales1.setNetAmount(grossSalesAmount - OrderManager.getTotalDiscountAmountOfSale(saleModel.getSale(), saleModel.getSaleItems()));
                chartOneDaySales1.setSaleCount(1);
                chartOneDaySalesList.add(chartOneDaySales1);
            }else {

                for(ChartOneDaySales chartOneDaySales : chartOneDaySalesList){
                    if(chartOneDaySales.getHour() == DataUtils.getHourNumOfDate(saleModel.getSale().getCreateDate())){

                        double grossSalesAmount = getGrossAmount(saleModel.getSaleItems());

                        chartOneDaySales.setGrossAmount(chartOneDaySales.getGrossAmount() + grossSalesAmount);
                        chartOneDaySales.setNetAmount(chartOneDaySales.getGrossAmount() - OrderManager.getTotalDiscountAmountOfSale(saleModel.getSale(), saleModel.getSaleItems()));
                        chartOneDaySales.setSaleCount(chartOneDaySales.getSaleCount() + 1);
                        break;
                    }
                }
            }
            previousHour = DataUtils.getHourNumOfDate(saleModel.getSale().getCreateDate());
        }
        return chartOneDaySalesList;
    }

    private double getGrossAmount(List<SaleItem> saleItems){
        double grossSalesAmount = 0d;
        for (SaleItem saleItem : saleItems) {
            grossSalesAmount = grossSalesAmount + saleItem.getGrossAmount();
        }
        return grossSalesAmount;
    }

    public void AddValuesToBarEntryLabelsForDailySales(){
        BarEntryLabels = new ArrayList<>();
        BarEntryLabels.add("0");
        BarEntryLabels.add(" ");
        BarEntryLabels.add(" ");
        BarEntryLabels.add("3");
        BarEntryLabels.add(" ");
        BarEntryLabels.add(" ");
        BarEntryLabels.add("6");
        BarEntryLabels.add(" ");
        BarEntryLabels.add(" ");
        BarEntryLabels.add("9");
        BarEntryLabels.add(" ");
        BarEntryLabels.add(" ");
        BarEntryLabels.add("12");
        BarEntryLabels.add(" ");
        BarEntryLabels.add(" ");
        BarEntryLabels.add("15");
        BarEntryLabels.add(" ");
        BarEntryLabels.add(" ");
        BarEntryLabels.add("18");
        BarEntryLabels.add(" ");
        BarEntryLabels.add(" ");
        BarEntryLabels.add("21");
        BarEntryLabels.add(" ");
        BarEntryLabels.add(" ");

    }



}

class ChartOneDaySales{

    private long hour;
    private double grossAmount;
    private double netAmount;
    private long saleCount;

    public long getHour() {
        return hour;
    }

    public void setHour(long hour) {
        this.hour = hour;
    }

    public double getGrossAmount() {
        return grossAmount;
    }

    public void setGrossAmount(double grossAmount) {
        this.grossAmount = grossAmount;
    }

    public double getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(double netAmount) {
        this.netAmount = netAmount;
    }

    public long getSaleCount() {
        return saleCount;
    }

    public void setSaleCount(long saleCount) {
        this.saleCount = saleCount;
    }
}
