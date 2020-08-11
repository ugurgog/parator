package com.paypad.vuk507.menu.reports.util;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.XAxisValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.paypad.vuk507.R;
import com.paypad.vuk507.charge.order.OrderManager;
import com.paypad.vuk507.enums.ChartSaleSelectionEnum;
import com.paypad.vuk507.enums.DayEnum;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.paypad.vuk507.constants.CustomConstants.LANGUAGE_TR;

public class ChartManager2 {

    private Context mContext;
    private List<SaleModel> saleModels;

    private ArrayList<BarEntry> BARENTRY ;
    private BarDataSet Bardataset ;
    private BarData BARDATA ;
    private LineData lineData;
    private BarChart barChart;
    private LineChart lineChart;
    ArrayList<String> BarEntryLabels ;

    private Date startDate;
    private Date endDate;
    private ReportSelectionEnum reportSelectionEnum;
    private ChartSaleSelectionEnum chartSaleSelectionEnum;

    public ChartManager2(Context context, List<SaleModel> saleModels, ChartSaleSelectionEnum chartSaleSelectionEnum) {
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

        setDefaultXYAxisValuesForBarChart();

        BARDATA = new BarData(BarEntryLabels, Bardataset);
        Bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
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

        setDefaultXYAxisValuesForBarChart();

        BARDATA = new BarData(BarEntryLabels, Bardataset);
        Bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
    }

    //----------------------------------------------------------

    public void fillOneMonthChartVariables(){
        List<ChartSales> salesList = getOneMonthSalesList();

        Collections.sort(salesList, new DayComparator());

        LineDataSet dataSet = null;

        ArrayList<Entry> entries = new ArrayList<>();
        setBarEntryLabelsWithOneMonthDays();

        String monthStr = "";
        if(reportSelectionEnum == ReportSelectionEnum.ONE_M)
            monthStr = mContext.getResources().getString(R.string.month);
        else if(reportSelectionEnum == ReportSelectionEnum.THREE_M)
            monthStr = "3 ".concat(mContext.getResources().getString(R.string.month));

        if (chartSaleSelectionEnum == ChartSaleSelectionEnum.GROSS_SALES) {

            dataSet = new LineDataSet(entries, mContext.getResources().getString(R.string.gross_sales).concat("/").concat(monthStr));
            for (ChartSales chartOneDaySales : salesList)
                entries.add(new Entry((float) chartOneDaySales.getGrossAmount(), chartOneDaySales.getDay()));

        } else if (chartSaleSelectionEnum == ChartSaleSelectionEnum.NET_SALES) {

            dataSet = new LineDataSet(entries, mContext.getResources().getString(R.string.net_sales).concat("/").concat(monthStr));
            for (ChartSales chartOneDaySales : salesList)
                entries.add(new Entry((float) chartOneDaySales.getNetAmount(), chartOneDaySales.getDay()));

        } else if (chartSaleSelectionEnum == ChartSaleSelectionEnum.SALES_COUNT) {

            dataSet = new LineDataSet(entries, mContext.getResources().getString(R.string.sales_count).concat("/").concat(monthStr));
            for (ChartSales chartOneDaySales : salesList)
                entries.add(new Entry((float) chartOneDaySales.getSaleCount(), chartOneDaySales.getDay()));

        }

        dataSet.setColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        dataSet.setValueTextColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark));

        setDefaultXYAxisValuesForLineChart();

        lineData = new LineData(BarEntryLabels, dataSet);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
    }

    public void setBarEntryLabelsWithOneDayHours(){
        BarEntryLabels = new ArrayList<String>();

        for (int i = 0; i < 24; i++)
            BarEntryLabels.add((i % 3 == 0 || i == 0) ? String.valueOf(i) : " ");

    }

    public void setBarEntryLabelsWithWeekDays(){
        BarEntryLabels = new ArrayList<String>();

        String[] days = new String[]{"Sun", "Mon", "Tue", "Wed", "Thur", "Fri", "Sat"};

        if(CommonUtils.getLanguage().equals(LANGUAGE_TR))
            days = new String[]{"Pzt", "Sal", "Çar", "Per", "Cum", "Ctes", "Paz"};

        BarEntryLabels.addAll(Arrays.asList(days).subList(0, 7));
    }

    public void setBarEntryLabelsWithOneMonthDays(){
        BarEntryLabels = new ArrayList<String>();

        final String[] months = new String[]{"1", " ", " ", "4", " ", " ", "7", " ", " ", "10", " ", " ",
                "13", " ", " ", "16", " ", " ", "19", " ", " ", "22", " ", " ", "25", " ", " ", "28", " ", " ", "31"};

        BarEntryLabels.addAll(Arrays.asList(months));
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

        getBarChart().getLegend().setEnabled(false);

        YAxis yAxisRight = getBarChart().getAxisRight();
        yAxisRight.setEnabled(false);

        YAxis yAxisLeft = getBarChart().getAxisLeft();
        yAxisLeft.setGranularity(1f);
        yAxisLeft.setAxisMinValue(0f);

        YAxisValueFormatter valueFormatter = new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, YAxis yAxis) {
                NumberFormat nf = new DecimalFormat("#.####");
                return nf.format(value).concat(" ").concat(CommonUtils.getCurrency().getSymbol());
            }
        };

        if(chartSaleSelectionEnum == ChartSaleSelectionEnum.GROSS_SALES || chartSaleSelectionEnum == ChartSaleSelectionEnum.NET_SALES)
            yAxisLeft.setValueFormatter(valueFormatter);
        else
            yAxisLeft.setValueFormatter(null);
    }

    private void setDefaultXYAxisValuesForLineChart(){
        XAxis xAxis = getLineChart().getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinValue(0f);

        getLineChart().getLegend().setEnabled(false);

        YAxis yAxisRight = getLineChart().getAxisRight();
        yAxisRight.setEnabled(false);

        YAxis yAxisLeft = getLineChart().getAxisLeft();
        yAxisLeft.setGranularity(1f);
        yAxisLeft.setAxisMinValue(0f);

        YAxisValueFormatter valueFormatter = new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, YAxis yAxis) {
                NumberFormat nf = new DecimalFormat("#.####");
                return nf.format(value).concat(" ").concat(CommonUtils.getCurrency().getSymbol());
            }
        };

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
                chartOneDaySales1.setNetAmount(grossSalesAmount - OrderManager.getTotalDiscountAmountOfSale(saleModel.getSale(), saleModel.getSaleItems()));
                chartOneDaySales1.setSaleCount(1);
                chartOneDaySalesList.add(chartOneDaySales1);
            }else {

                for(ChartSales chartOneDaySales : chartOneDaySalesList){
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
                chartOneDaySales1.setNetAmount(grossSalesAmount - OrderManager.getTotalDiscountAmountOfSale(saleModel.getSale(), saleModel.getSaleItems()));
                chartOneDaySales1.setSaleCount(1);
                chartOneDaySalesList.add(chartOneDaySales1);
            }else {
                for(ChartSales chartOneDaySales : chartOneDaySalesList){
                    if(chartOneDaySales.getDay() == currentDay){

                        double grossSalesAmount = getGrossAmount(saleModel.getSaleItems());

                        chartOneDaySales.setGrossAmount(chartOneDaySales.getGrossAmount() + grossSalesAmount);
                        chartOneDaySales.setNetAmount(chartOneDaySales.getGrossAmount() - OrderManager.getTotalDiscountAmountOfSale(saleModel.getSale(), saleModel.getSaleItems()));
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
                chartOneDaySales1.setNetAmount(grossSalesAmount - OrderManager.getTotalDiscountAmountOfSale(saleModel.getSale(), saleModel.getSaleItems()));
                chartOneDaySales1.setSaleCount(1);
                chartOneDaySalesList.add(chartOneDaySales1);
            }else {
                for(ChartSales chartOneDaySales : chartOneDaySalesList){
                    if(chartOneDaySales.getDay() == currentDay){

                        double grossSalesAmount = getGrossAmount(saleModel.getSaleItems());

                        chartOneDaySales.setGrossAmount(chartOneDaySales.getGrossAmount() + grossSalesAmount);
                        chartOneDaySales.setNetAmount(chartOneDaySales.getGrossAmount() - OrderManager.getTotalDiscountAmountOfSale(saleModel.getSale(), saleModel.getSaleItems()));
                        chartOneDaySales.setSaleCount(chartOneDaySales.getSaleCount() + 1);
                        break;
                    }
                }
            }
            previousDay = currentDay;
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

    public BarChart getBarChart() {
        return barChart;
    }

    public void setBarChart(BarChart barChart) {
        this.barChart = barChart;
    }

    public LineChart getLineChart() {
        return lineChart;
    }

    public void setLineChart(LineChart lineChart) {
        this.lineChart = lineChart;
    }

    public BarData getBARDATA() {
        return BARDATA;
    }

    public LineData getLineData() {
        return lineData;
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


