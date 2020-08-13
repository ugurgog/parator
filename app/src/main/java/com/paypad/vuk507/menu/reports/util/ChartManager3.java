package com.paypad.vuk507.menu.reports.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.XAxisValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.paypad.vuk507.R;
import com.paypad.vuk507.charge.order.OrderManager1;
import com.paypad.vuk507.enums.ChartSaleSelectionEnum;
import com.paypad.vuk507.enums.DayEnum;
import com.paypad.vuk507.enums.MonthEnum;
import com.paypad.vuk507.enums.ReportSelectionEnum;
import com.paypad.vuk507.model.SaleItem;
import com.paypad.vuk507.model.pojo.SaleModel;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.DataUtils;
import com.paypad.vuk507.utils.LogUtil;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

import static com.paypad.vuk507.constants.CustomConstants.LANGUAGE_TR;

public class ChartManager3 {

    private Context mContext;
    private List<SaleModel> saleModels;

    private ArrayList<BarEntry> BARENTRY ;
    private BarDataSet Bardataset ;
    private BarData BARDATA ;
    private BarChart barChart;
    ArrayList<String> BarEntryLabels ;

    private Date startDate;
    private Date endDate;
    private ReportSelectionEnum reportSelectionEnum;
    private ChartSaleSelectionEnum chartSaleSelectionEnum;

    public ChartManager3(Context context, List<SaleModel> saleModels, ChartSaleSelectionEnum chartSaleSelectionEnum) {
        mContext = context;
        this.saleModels = saleModels;
        this.chartSaleSelectionEnum = chartSaleSelectionEnum;
    }

    public void fillOneDayChartVariables(){
        List<ChartSales> chartOneDaySalesList = getOneDaySalesList();

        BARENTRY = new ArrayList<>();

        setBarEntryLabelsWithOneDayHours();

        if (chartSaleSelectionEnum == ChartSaleSelectionEnum.GROSS_SALES) {

            Bardataset = new BarDataSet(BARENTRY, mContext.getResources().getString(R.string.gross_sales).concat("/").concat(mContext.getResources().getString(R.string.hour)));

            for (ChartSales chartOneDaySales : chartOneDaySalesList)
                BARENTRY.add(new BarEntry((float) chartOneDaySales.getGrossAmount(), (int) chartOneDaySales.getHour()));

        } else if (chartSaleSelectionEnum == ChartSaleSelectionEnum.NET_SALES) {

            Bardataset = new BarDataSet(BARENTRY, mContext.getResources().getString(R.string.net_sales).concat("/").concat(mContext.getResources().getString(R.string.hour)));

            for (ChartSales chartOneDaySales : chartOneDaySalesList)
                BARENTRY.add(new BarEntry((float) chartOneDaySales.getNetAmount(), (int) chartOneDaySales.getHour()));


        } else if (chartSaleSelectionEnum == ChartSaleSelectionEnum.SALES_COUNT) {

            Bardataset = new BarDataSet(BARENTRY, mContext.getResources().getString(R.string.sales_count).concat("/").concat(mContext.getResources().getString(R.string.hour)));

            for (ChartSales chartOneDaySales : chartOneDaySalesList)
                BARENTRY.add(new BarEntry( (float) chartOneDaySales.getSaleCount(),(int) chartOneDaySales.getHour()));
        }

        XAxis xAxis = getBarChart().getXAxis();
        XAxisValueFormatter xAxisValueFormatter = new XAxisValueFormatter() {
            @Override
            public String getXValue(String original, int index, ViewPortHandler viewPortHandler) {
                String hourLabel = CommonUtils.getLanguage().equals(LANGUAGE_TR) ? " s" : " h";

                if(!original.trim().isEmpty())
                    return original.concat(hourLabel);
                else
                    return original;
            }
        };
        xAxis.setValueFormatter(xAxisValueFormatter);

        Bardataset.setDrawValues(false);
        Bardataset.setColors(new int[]{Color.rgb(0, 128, 255)});
        BARDATA = new BarData(BarEntryLabels, Bardataset);

        setDefaultXYAxisValuesForBarChart();
    }

    //----------------------------------------------------------

    public void fillOneWeekChartVariables(){
        List<ChartSales> chartOneDaySalesList = getOneWeekSalesList();

        BARENTRY = new ArrayList<>();
        setBarEntryLabelsWithWeekDays();

        if (chartSaleSelectionEnum == ChartSaleSelectionEnum.GROSS_SALES) {

            Bardataset = new BarDataSet(BARENTRY, mContext.getResources().getString(R.string.gross_sales).concat("/").concat(mContext.getResources().getString(R.string.week)));

            for (ChartSales chartOneDaySales : chartOneDaySalesList)
                BARENTRY.add(new BarEntry((float) chartOneDaySales.getGrossAmount(), chartOneDaySales.getDay()));

        } else if (chartSaleSelectionEnum == ChartSaleSelectionEnum.NET_SALES) {

            Bardataset = new BarDataSet(BARENTRY, mContext.getResources().getString(R.string.net_sales).concat("/").concat(mContext.getResources().getString(R.string.week)));

            for (ChartSales chartOneDaySales : chartOneDaySalesList)
                BARENTRY.add(new BarEntry((float) chartOneDaySales.getNetAmount(), chartOneDaySales.getDay()));

        } else if (chartSaleSelectionEnum == ChartSaleSelectionEnum.SALES_COUNT) {

            Bardataset = new BarDataSet(BARENTRY, mContext.getResources().getString(R.string.sales_count).concat("/").concat(mContext.getResources().getString(R.string.week)));

            for (ChartSales chartOneDaySales : chartOneDaySalesList)
                BARENTRY.add(new BarEntry((float) chartOneDaySales.getSaleCount(), chartOneDaySales.getDay()));
        }

        Bardataset.setDrawValues(false);
        Bardataset.setColors(new int[]{Color.rgb(0, 128, 255)});
        BARDATA = new BarData(BarEntryLabels, Bardataset);

        setDefaultXYAxisValuesForBarChart();
    }

    //----------------------------------------------------------

    public void fillOneMonthChartVariables(){
        List<ChartSales> salesList = getOneMonthSalesList();

        BARENTRY = new ArrayList<>();

        setBarEntryLabelsWithOneMonthDays();

        if (chartSaleSelectionEnum == ChartSaleSelectionEnum.GROSS_SALES) {

            Bardataset = new BarDataSet(BARENTRY, mContext.getResources().getString(R.string.gross_sales).concat("/").concat(mContext.getResources().getString(R.string.month)));
            for (ChartSales chartOneDaySales : salesList)
                BARENTRY.add(new BarEntry((float) chartOneDaySales.getGrossAmount(), chartOneDaySales.getDay()));

        } else if (chartSaleSelectionEnum == ChartSaleSelectionEnum.NET_SALES) {

            Bardataset = new BarDataSet(BARENTRY, mContext.getResources().getString(R.string.net_sales).concat("/").concat(mContext.getResources().getString(R.string.month)));
            for (ChartSales chartOneDaySales : salesList)
                BARENTRY.add(new BarEntry((float) chartOneDaySales.getNetAmount(), chartOneDaySales.getDay()));

        } else if (chartSaleSelectionEnum == ChartSaleSelectionEnum.SALES_COUNT) {

            Bardataset = new BarDataSet(BARENTRY, mContext.getResources().getString(R.string.sales_count).concat("/").concat(mContext.getResources().getString(R.string.month)));
            for (ChartSales chartOneDaySales : salesList)
                BARENTRY.add(new BarEntry((float) chartOneDaySales.getSaleCount(), chartOneDaySales.getDay()));
        }

        Bardataset.setDrawValues(false);
        Bardataset.setColors(new int[]{Color.rgb(0, 128, 255)});
        BARDATA = new BarData(BarEntryLabels, Bardataset);

        setDefaultXYAxisValuesForBarChart();
    }

    public void fillThreeMonthChartVariables(){
        List<ChartSales> salesList = getThreeMonthSalesList();

        BARENTRY = new ArrayList<>();

        setBarEntryLabelsWithThreeMonthDays();

        String monthStr = "3 ".concat(mContext.getResources().getString(R.string.month));

        if (chartSaleSelectionEnum == ChartSaleSelectionEnum.GROSS_SALES) {

            Bardataset = new BarDataSet(BARENTRY, mContext.getResources().getString(R.string.gross_sales).concat("/").concat(monthStr));
            for (ChartSales chartOneDaySales : salesList)
                BARENTRY.add(new BarEntry((float) chartOneDaySales.getGrossAmount(), chartOneDaySales.getDay()));

        } else if (chartSaleSelectionEnum == ChartSaleSelectionEnum.NET_SALES) {

            Bardataset = new BarDataSet(BARENTRY, mContext.getResources().getString(R.string.net_sales).concat("/").concat(monthStr));
            for (ChartSales chartOneDaySales : salesList)
                BARENTRY.add(new BarEntry((float) chartOneDaySales.getNetAmount(), chartOneDaySales.getDay()));

        } else if (chartSaleSelectionEnum == ChartSaleSelectionEnum.SALES_COUNT) {

            Bardataset = new BarDataSet(BARENTRY, mContext.getResources().getString(R.string.sales_count).concat("/").concat(monthStr));
            for (ChartSales chartOneDaySales : salesList)
                BARENTRY.add(new BarEntry((float) chartOneDaySales.getSaleCount(), chartOneDaySales.getDay()));
        }

        Bardataset.setDrawValues(false);
        Bardataset.setColors(new int[]{Color.rgb(0, 128, 255)});
        BARDATA = new BarData(BarEntryLabels, Bardataset);

        setDefaultXYAxisValuesForBarChart();
    }

    public void fillOneYearChartVariables(){
        List<ChartSales> salesList = getOneYearSalesList();

        BARENTRY = new ArrayList<>();

        setBarEntryLabelsWithOneYear();

        if (chartSaleSelectionEnum == ChartSaleSelectionEnum.GROSS_SALES) {

            Bardataset = new BarDataSet(BARENTRY, mContext.getResources().getString(R.string.gross_sales).concat("/")
                    .concat(mContext.getResources().getString(R.string.year)));

            for (ChartSales chartOneDaySales : salesList)
                BARENTRY.add(new BarEntry((float) chartOneDaySales.getGrossAmount(), chartOneDaySales.getMonth()));

        } else if (chartSaleSelectionEnum == ChartSaleSelectionEnum.NET_SALES) {

            Bardataset = new BarDataSet(BARENTRY, mContext.getResources().getString(R.string.net_sales).concat("/").concat(mContext.getResources().getString(R.string.year)));
            for (ChartSales chartOneDaySales : salesList)
                BARENTRY.add(new BarEntry((float) chartOneDaySales.getNetAmount(), chartOneDaySales.getMonth()));

        } else if (chartSaleSelectionEnum == ChartSaleSelectionEnum.SALES_COUNT) {

            Bardataset = new BarDataSet(BARENTRY, mContext.getResources().getString(R.string.sales_count).concat("/").concat(mContext.getResources().getString(R.string.year)));
            for (ChartSales chartOneDaySales : salesList)
                BARENTRY.add(new BarEntry((float) chartOneDaySales.getSaleCount(), chartOneDaySales.getMonth()));
        }

        Bardataset.setDrawValues(false);
        Bardataset.setColors(new int[]{Color.rgb(0, 128, 255)});
        BARDATA = new BarData(BarEntryLabels, Bardataset);

        setDefaultXYAxisValuesForBarChart();
    }


    public void fillUndefinedChartVariables(){
        List<ChartSales> salesList = getUndefinedSalesList();

        BARENTRY = new ArrayList<>();

        setBarEntryLabelsWithUndefined(salesList);

        if (chartSaleSelectionEnum == ChartSaleSelectionEnum.GROSS_SALES) {

            Bardataset = new BarDataSet(BARENTRY, mContext.getResources().getString(R.string.gross_sales).concat("/")
                    .concat(mContext.getResources().getString(R.string.year)));

            for (ChartSales chartOneDaySales : salesList)
                BARENTRY.add(new BarEntry((float) chartOneDaySales.getGrossAmount(), chartOneDaySales.getMonth()));

        } else if (chartSaleSelectionEnum == ChartSaleSelectionEnum.NET_SALES) {

            Bardataset = new BarDataSet(BARENTRY, mContext.getResources().getString(R.string.net_sales).concat("/").concat(mContext.getResources().getString(R.string.year)));
            for (ChartSales chartOneDaySales : salesList)
                BARENTRY.add(new BarEntry((float) chartOneDaySales.getNetAmount(), chartOneDaySales.getMonth()));

        } else if (chartSaleSelectionEnum == ChartSaleSelectionEnum.SALES_COUNT) {

            Bardataset = new BarDataSet(BARENTRY, mContext.getResources().getString(R.string.sales_count).concat("/").concat(mContext.getResources().getString(R.string.year)));
            for (ChartSales chartOneDaySales : salesList)
                BARENTRY.add(new BarEntry((float) chartOneDaySales.getSaleCount(), chartOneDaySales.getMonth()));
        }

        Bardataset.setDrawValues(false);
        Bardataset.setColors(new int[]{Color.rgb(0, 128, 255)});
        BARDATA = new BarData(BarEntryLabels, Bardataset);

        setDefaultXYAxisValuesForBarChart();
    }

    public void setBarEntryLabelsWithOneDayHours(){
        BarEntryLabels = new ArrayList<String>();

        for (int i = 0; i < 24; i++)
            BarEntryLabels.add(i % 3 == 0 ? String.valueOf(i) : " ");
    }

    public void setBarEntryLabelsWithWeekDays(){
        BarEntryLabels = new ArrayList<String>();

        if(CommonUtils.getLanguage().equals(LANGUAGE_TR))
            BarEntryLabels.addAll(DayEnum.getTrCodeList());
        else
            BarEntryLabels.addAll(DayEnum.getEnCodeList());
    }

    public void setBarEntryLabelsWithOneMonthDays(){
        BarEntryLabels = new ArrayList<String>();

        final String[] months = new String[]{"1", " ", " ", "4", " ", " ", "7", " ", " ", "10", " ", " ",
                "13", " ", " ", "16", " ", " ", "19", " ", " ", "22", " ", " ", "25", " ", " ", "28", " ", " ", "31"};

        BarEntryLabels.addAll(Arrays.asList(months));
    }

    public void setBarEntryLabelsWithOneYear(){
        BarEntryLabels = new ArrayList<String>();

        if(CommonUtils.getLanguage().equals(LANGUAGE_TR))
            BarEntryLabels.addAll(MonthEnum.getTrCodeList());
        else
            BarEntryLabels.addAll(MonthEnum.getEnCodeList());
    }

    public void setBarEntryLabelsWithUndefined(List<ChartSales> salesList){
        BarEntryLabels = new ArrayList<String>();

        for(ChartSales chartSales : salesList){
            int len = String.valueOf(chartSales.getMonth()).length();
            int monthNum = Integer.parseInt(String.valueOf(chartSales.getMonth()).substring(4, len));
            BarEntryLabels.add(CommonUtils.getLanguage().equals(LANGUAGE_TR) ? MonthEnum.getById(monthNum).getTrCode() : MonthEnum.getById(monthNum).getEngCode());
        }
    }

    public void setBarEntryLabelsWithThreeMonthDays(){
        BarEntryLabels = new ArrayList<String>();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Log.i("Info", "setBarEntryLabelsWithThreeMonthDays  startDate:" + startDate);

        int monthNum = DataUtils.getMonthNumFromDate(startDate);
        setMonthLabels(monthNum);

        monthNum++;
        setMonthLabels(monthNum);

        monthNum++;
        setMonthLabels(monthNum);
    }
    private void setMonthLabels(int monthNum){
        List<String> dates = new ArrayList<>();

        Calendar mycal = new GregorianCalendar(DataUtils.getYearFromDate(startDate), monthNum , 1);
        int daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 1; i <= daysInMonth; i++) {
            int dayNum = DataUtils.getDateNumFromDate(mycal.getTime());

            MonthEnum monthEnum = MonthEnum.getById(monthNum);

            if(i==1 || i==15)
                dates.add(String.valueOf(dayNum).concat(" ").concat(CommonUtils.getLanguage().equals(LANGUAGE_TR) ? monthEnum.getTrCode() : monthEnum.getEngCode()));
            else
                dates.add(" ");

            mycal.add(Calendar.DATE, 1);
        }
        BarEntryLabels.addAll(dates);
    }

    public static class DayComparator implements Comparator<ChartSales> {
        @Override
        public int compare(ChartSales o1, ChartSales o2) {
            return (int) (o2.getDay() - o1.getDay());
        }
    }

    private void setDefaultXYAxisValuesForBarChart(){
        XAxis xAxis = getBarChart().getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        xAxis.setAxisMinValue(0f);

        getBarChart().getXAxis().setDrawGridLines(false);

        getBarChart().getLegend().setEnabled(false);

        YAxis yAxisRight = getBarChart().getAxisRight();
        yAxisRight.setEnabled(false);

        YAxis yAxisLeft = getBarChart().getAxisLeft();
        yAxisLeft.setGranularity(1f);
        yAxisLeft.setAxisMinValue(0f);

        YAxisValueFormatter valueFormatter = new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, YAxis yAxis) {
                //NumberFormat xx = new DecimalFormat("###,##0.00");
                NumberFormat xx = new DecimalFormat("###,##0");
                return xx.format(value).concat(" ").concat(CommonUtils.getCurrency().getSymbol());
            }
        };

        getBarChart().setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                //float x = e.getXIndex();
                //float y = e.getVal();
                getBarChart().getBarData().getDataSets().get(dataSetIndex).setDrawValues(true);
            }

            @Override
            public void onNothingSelected() {
                getBarChart().getBarData().setDrawValues(false);
            }
        });

        //xAxis.setAxisMaxValue(BarEntryLabels.size());
        //getBarChart().getAxisLeft().setLabelCount(BarEntryLabels.size(), true);

        if(chartSaleSelectionEnum == ChartSaleSelectionEnum.GROSS_SALES || chartSaleSelectionEnum == ChartSaleSelectionEnum.NET_SALES)
            yAxisLeft.setValueFormatter(valueFormatter);
        else
            yAxisLeft.setValueFormatter(null);
    }

    private List<ChartSales> getOneDaySalesList(){
        List<ChartSales> chartOneDaySalesList = new ArrayList<>();
        long previousHour = -1;

        for(SaleModel saleModel: saleModels){

            LogUtil.logSale(saleModel.getSale());

            if(DataUtils.getHourNumOfDate(saleModel.getSale().getCreateDate()) != previousHour){
                ChartSales chartOneDaySales1 = new ChartSales();
                chartOneDaySales1.setHour(DataUtils.getHourNumOfDate(saleModel.getSale().getCreateDate()));

                double grossSalesAmount = getGrossAmount(saleModel.getSaleItems());

                chartOneDaySales1.setGrossAmount(grossSalesAmount);
                //chartOneDaySales1.setNetAmount(grossSalesAmount - OrderManager.getTotalDiscountAmountOfSale(saleModel.getSale(), saleModel.getSaleItems()));
                chartOneDaySales1.setNetAmount(grossSalesAmount - saleModel.getSale().getTotalDiscountAmount());

                chartOneDaySales1.setSaleCount(1);
                chartOneDaySalesList.add(chartOneDaySales1);
            }else {

                for(ChartSales chartOneDaySales : chartOneDaySalesList){
                    if(chartOneDaySales.getHour() == DataUtils.getHourNumOfDate(saleModel.getSale().getCreateDate())){

                        double grossSalesAmount = getGrossAmount(saleModel.getSaleItems());

                        chartOneDaySales.setGrossAmount(chartOneDaySales.getGrossAmount() + grossSalesAmount);

                        //chartOneDaySales.setNetAmount(chartOneDaySales.getGrossAmount() - OrderManager.getTotalDiscountAmountOfSale(saleModel.getSale(), saleModel.getSaleItems()));
                        chartOneDaySales.setNetAmount(chartOneDaySales.getGrossAmount() - saleModel.getSale().getTotalDiscountAmount());

                        chartOneDaySales.setSaleCount(chartOneDaySales.getSaleCount() + 1);
                        break;
                    }
                }
            }
            previousHour = DataUtils.getHourNumOfDate(saleModel.getSale().getCreateDate());
        }
        return chartOneDaySalesList;
    }

    private List<ChartSales> getOneWeekSalesList(){
        List<ChartSales> chartOneDaySalesList = new ArrayList<>();

        int previousDay = -1;

        for(SaleModel saleModel: saleModels){

            LogUtil.logSale(saleModel.getSale());

            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE");

            String dayCode = simpleDateformat.format(saleModel.getSale().getCreateDate());

            int currentDay = Objects.requireNonNull(DayEnum.getByCode(dayCode)).getId();

            if(currentDay != previousDay){
                ChartSales chartOneDaySales1 = new ChartSales();
                chartOneDaySales1.setDay(currentDay);

                double grossSalesAmount = getGrossAmount(saleModel.getSaleItems());

                chartOneDaySales1.setGrossAmount(grossSalesAmount);
                //chartOneDaySales1.setNetAmount(grossSalesAmount - OrderManager.getTotalDiscountAmountOfSale(saleModel.getSale(), saleModel.getSaleItems()));
                chartOneDaySales1.setNetAmount(grossSalesAmount - saleModel.getSale().getTotalDiscountAmount());
                chartOneDaySales1.setSaleCount(1);
                chartOneDaySalesList.add(chartOneDaySales1);
            }else {
                for(ChartSales chartOneDaySales : chartOneDaySalesList){
                    if(chartOneDaySales.getDay() == currentDay){

                        double grossSalesAmount = getGrossAmount(saleModel.getSaleItems());

                        chartOneDaySales.setGrossAmount(chartOneDaySales.getGrossAmount() + grossSalesAmount);
                        //chartOneDaySales.setNetAmount(chartOneDaySales.getGrossAmount() - OrderManager.getTotalDiscountAmountOfSale(saleModel.getSale(), saleModel.getSaleItems()));
                        chartOneDaySales.setNetAmount(chartOneDaySales.getGrossAmount() - saleModel.getSale().getTotalDiscountAmount());
                        chartOneDaySales.setSaleCount(chartOneDaySales.getSaleCount() + 1);
                        break;
                    }
                }
            }
            previousDay = currentDay;
        }
        return chartOneDaySalesList;
    }

    private List<ChartSales> getOneMonthSalesList(){
        List<ChartSales> chartOneDaySalesList = new ArrayList<>();

        int previousDay = -1;

        for(SaleModel saleModel: saleModels){

            LogUtil.logSale(saleModel.getSale());

            int currentDay = DataUtils.getDateNumFromDate(saleModel.getSale().getCreateDate());

            if(currentDay != previousDay){
                ChartSales chartOneDaySales1 = new ChartSales();
                chartOneDaySales1.setDay(currentDay);

                double grossSalesAmount = getGrossAmount(saleModel.getSaleItems());

                chartOneDaySales1.setGrossAmount(grossSalesAmount);
                //chartOneDaySales1.setNetAmount(grossSalesAmount - OrderManager.getTotalDiscountAmountOfSale(saleModel.getSale(), saleModel.getSaleItems()));
                chartOneDaySales1.setNetAmount(grossSalesAmount - saleModel.getSale().getTotalDiscountAmount());
                chartOneDaySales1.setSaleCount(1);
                chartOneDaySalesList.add(chartOneDaySales1);
            }else {
                for(ChartSales chartOneDaySales : chartOneDaySalesList){
                    if(chartOneDaySales.getDay() == currentDay){

                        double grossSalesAmount = getGrossAmount(saleModel.getSaleItems());

                        chartOneDaySales.setGrossAmount(chartOneDaySales.getGrossAmount() + grossSalesAmount);
                        //chartOneDaySales.setNetAmount(chartOneDaySales.getGrossAmount() - OrderManager.getTotalDiscountAmountOfSale(saleModel.getSale(), saleModel.getSaleItems()));
                        chartOneDaySales.setNetAmount(chartOneDaySales.getGrossAmount() - saleModel.getSale().getTotalDiscountAmount());
                        chartOneDaySales.setSaleCount(chartOneDaySales.getSaleCount() + 1);
                        break;
                    }
                }
            }
            previousDay = currentDay;
        }
        return chartOneDaySalesList;
    }


    private List<ChartSales> getThreeMonthSalesList(){
        List<ChartSales> chartOneDaySalesList = new ArrayList<>();

        int previousDay = -1;

        for(SaleModel saleModel: saleModels){

            LogUtil.logSale(saleModel.getSale());

            int currentDay = DataUtils.getDayOfYearFromDate(saleModel.getSale().getCreateDate());

            if(currentDay != previousDay){
                ChartSales chartOneDaySales1 = new ChartSales();
                chartOneDaySales1.setDay(currentDay);

                double grossSalesAmount = getGrossAmount(saleModel.getSaleItems());

                chartOneDaySales1.setGrossAmount(grossSalesAmount);
                //chartOneDaySales1.setNetAmount(grossSalesAmount - OrderManager.getTotalDiscountAmountOfSale(saleModel.getSale(), saleModel.getSaleItems()));
                chartOneDaySales1.setNetAmount(grossSalesAmount - saleModel.getSale().getTotalDiscountAmount());
                chartOneDaySales1.setSaleCount(1);
                chartOneDaySalesList.add(chartOneDaySales1);
            }else {
                for(ChartSales chartOneDaySales : chartOneDaySalesList){
                    if(chartOneDaySales.getDay() == currentDay){

                        double grossSalesAmount = getGrossAmount(saleModel.getSaleItems());

                        chartOneDaySales.setGrossAmount(chartOneDaySales.getGrossAmount() + grossSalesAmount);
                        //chartOneDaySales.setNetAmount(chartOneDaySales.getGrossAmount() - OrderManager.getTotalDiscountAmountOfSale(saleModel.getSale(), saleModel.getSaleItems()));
                        chartOneDaySales.setNetAmount(chartOneDaySales.getGrossAmount() - saleModel.getSale().getTotalDiscountAmount());
                        chartOneDaySales.setSaleCount(chartOneDaySales.getSaleCount() + 1);
                        break;
                    }
                }
            }
            previousDay = currentDay;
        }

        Collections.sort(chartOneDaySalesList, new DayComparator());

        int dayOfYear = chartOneDaySalesList.get(0).getDay();
        int startDayOfYear = DataUtils.getDayOfYearFromDate(startDate);

        if(dayOfYear > startDayOfYear){
            for(ChartSales chartSales : chartOneDaySalesList)
                chartSales.setDay(chartSales.getDay() - startDayOfYear);
        }

        return chartOneDaySalesList;
    }

    private List<ChartSales> getOneYearSalesList(){
        List<ChartSales> chartOneDaySalesList = new ArrayList<>();

        int previousMonth = -1;

        for(SaleModel saleModel: saleModels){

            LogUtil.logSale(saleModel.getSale());

            int currentMonth = DataUtils.getMonthNumFromDate(saleModel.getSale().getCreateDate());

            if(currentMonth != previousMonth){
                ChartSales chartOneDaySales1 = new ChartSales();
                chartOneDaySales1.setMonth(currentMonth);

                double grossSalesAmount = getGrossAmount(saleModel.getSaleItems());

                chartOneDaySales1.setGrossAmount(grossSalesAmount);
                //chartOneDaySales1.setNetAmount(grossSalesAmount - OrderManager.getTotalDiscountAmountOfSale(saleModel.getSale(), saleModel.getSaleItems()));
                chartOneDaySales1.setNetAmount(grossSalesAmount - saleModel.getSale().getTotalDiscountAmount());
                chartOneDaySales1.setSaleCount(1);
                chartOneDaySalesList.add(chartOneDaySales1);
            }else {
                for(ChartSales chartOneDaySales : chartOneDaySalesList){
                    if(chartOneDaySales.getMonth() == currentMonth){

                        double grossSalesAmount = getGrossAmount(saleModel.getSaleItems());

                        chartOneDaySales.setGrossAmount(chartOneDaySales.getGrossAmount() + grossSalesAmount);
                        //chartOneDaySales.setNetAmount(chartOneDaySales.getGrossAmount() - OrderManager.getTotalDiscountAmountOfSale(saleModel.getSale(), saleModel.getSaleItems()));
                        chartOneDaySales.setNetAmount(chartOneDaySales.getGrossAmount() - saleModel.getSale().getTotalDiscountAmount());
                        chartOneDaySales.setSaleCount(chartOneDaySales.getSaleCount() + 1);
                        break;
                    }
                }
            }
            previousMonth = currentMonth;
        }

        return chartOneDaySalesList;
    }

    private List<ChartSales> getUndefinedSalesList(){
        List<ChartSales> chartOneDaySalesList = new ArrayList<>();

        int previousMonth = -1;

        for(SaleModel saleModel: saleModels){

            LogUtil.logSale(saleModel.getSale());

            int currentMonth = Integer.parseInt(String.valueOf(DataUtils.getYearFromDate(saleModel.getSale().getCreateDate()))
                    .concat(String.valueOf(DataUtils.getMonthNumFromDate(saleModel.getSale().getCreateDate()))));

            if(currentMonth != previousMonth){
                ChartSales chartOneDaySales1 = new ChartSales();
                chartOneDaySales1.setMonth(currentMonth);

                double grossSalesAmount = getGrossAmount(saleModel.getSaleItems());

                chartOneDaySales1.setGrossAmount(grossSalesAmount);
                //chartOneDaySales1.setNetAmount(grossSalesAmount - OrderManager.getTotalDiscountAmountOfSale(saleModel.getSale(), saleModel.getSaleItems()));
                chartOneDaySales1.setNetAmount(grossSalesAmount - saleModel.getSale().getTotalDiscountAmount());
                chartOneDaySales1.setSaleCount(1);
                chartOneDaySalesList.add(chartOneDaySales1);
            }else {
                for(ChartSales chartOneDaySales : chartOneDaySalesList){
                    if(chartOneDaySales.getMonth() == currentMonth){

                        double grossSalesAmount = getGrossAmount(saleModel.getSaleItems());

                        chartOneDaySales.setGrossAmount(chartOneDaySales.getGrossAmount() + grossSalesAmount);
                        //chartOneDaySales.setNetAmount(chartOneDaySales.getGrossAmount() - OrderManager.getTotalDiscountAmountOfSale(saleModel.getSale(), saleModel.getSaleItems()));
                        chartOneDaySales.setNetAmount(chartOneDaySales.getGrossAmount() - saleModel.getSale().getTotalDiscountAmount());
                        chartOneDaySales.setSaleCount(chartOneDaySales.getSaleCount() + 1);
                        break;
                    }
                }
            }
            previousMonth = currentMonth;
        }

        return chartOneDaySalesList;
    }

    public void fillUndefinedSalesList(){
        int differenceDayNum = DataUtils.getDifferenceDays(startDate, endDate);

        if(differenceDayNum == 0)
            fillOneDayChartVariables();
        else if(differenceDayNum <= 7)
            fillOneWeekChartVariables();
        else if(differenceDayNum <= 31)
            fillOneMonthChartVariables();
        else if(differenceDayNum <= 92)
            fillThreeMonthChartVariables();
        else if(differenceDayNum <= 365)
            fillOneYearChartVariables();
        else
            fillUndefinedChartVariables();

    }

    private double getGrossAmount(List<SaleItem> saleItems){
        double grossSalesAmount = 0d;
        for (SaleItem saleItem : saleItems) {
            grossSalesAmount = grossSalesAmount + saleItem.getGrossAmount();
        }
        return grossSalesAmount;
    }

    public BarChart getBarChart() {
        return barChart;
    }

    public void setBarChart(BarChart barChart) {
        this.barChart = barChart;
    }

    public BarData getBARDATA() {
        return BARDATA;
    }


    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public ReportSelectionEnum getReportSelectionEnum() {
        return reportSelectionEnum;
    }

    public void setReportSelectionEnum(ReportSelectionEnum reportSelectionEnum) {
        this.reportSelectionEnum = reportSelectionEnum;
    }

    public void setChartSaleSelectionEnum(ChartSaleSelectionEnum chartSaleSelectionEnum) {
        this.chartSaleSelectionEnum = chartSaleSelectionEnum;
    }

    class ChartSales{

        private long hour;
        private int day;
        private int month;
        private double grossAmount;
        private double netAmount;
        private long saleCount;

        public long getHour() {
            return hour;
        }

        public void setHour(long hour) {
            this.hour = hour;
        }

        public int getDay() {
            return day;
        }

        public void setDay(int dayCount) {
            this.day = dayCount;
        }

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
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
}


