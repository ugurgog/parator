package com.paypad.vuk507.menu.reports.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.paypad.vuk507.R;
import com.paypad.vuk507.charge.order.IOrderManager;
import com.paypad.vuk507.charge.order.OrderManager;
import com.paypad.vuk507.enums.ChartSaleSelectionEnum;
import com.paypad.vuk507.enums.DayEnum;
import com.paypad.vuk507.enums.MonthEnum;
import com.paypad.vuk507.enums.ReportSelectionEnum;
import com.paypad.vuk507.menu.reports.saleReport.SalesTopCategoriesFragment;
import com.paypad.vuk507.menu.reports.saleReport.TopCategory;
import com.paypad.vuk507.model.SaleItem;
import com.paypad.vuk507.model.pojo.SaleModel;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.DataUtils;
import com.paypad.vuk507.utils.LogUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.paypad.vuk507.constants.CustomConstants.LANGUAGE_EN;
import static com.paypad.vuk507.constants.CustomConstants.LANGUAGE_TR;
import static com.paypad.vuk507.constants.CustomConstants.TYPE_PRICE;

public class ChartManager {

    /*private Context mContext;
    private List<SaleModel> saleModels;

    private ArrayList<BarEntry> BARENTRY ;
    private BarDataSet Bardataset ;
    private BarData BARDATA ;
    private LineData lineData;
    private BarChart barChart;
    private LineChart lineChart;

    private Date startDate;
    private Date endDate;
    private ReportSelectionEnum reportSelectionEnum;

    public ChartManager(Context context, List<SaleModel> saleModels) {
        mContext = context;
        this.saleModels = saleModels;
    }

    public void fillOneDayChartVariables(ChartSaleSelectionEnum chartSaleSelectionEnum){
        List<ChartSales> chartOneDaySalesList = getOneDaySalesList();

        BARENTRY = new ArrayList<>();

        if (chartSaleSelectionEnum == ChartSaleSelectionEnum.GROSS_SALES) {

            //for(int i=0; i<24; i++){
            //    BARENTRY.add(new BarEntry((int) i, (float) 0f));
            //}

            for (ChartSales chartOneDaySales : chartOneDaySalesList)

                //BARENTRY.set((int) chartOneDaySales.getHour(), new BarEntry((int) chartOneDaySales.getHour(), (float) chartOneDaySales.getGrossAmount()));

                BARENTRY.add(new BarEntry((int) chartOneDaySales.getHour(), (float) chartOneDaySales.getGrossAmount()));

            Bardataset = new BarDataSet(BARENTRY, mContext.getResources().getString(R.string.gross_sales).concat("/").concat(mContext.getResources().getString(R.string.hour)));

        } else if (chartSaleSelectionEnum == ChartSaleSelectionEnum.NET_SALES) {

            for (ChartSales chartOneDaySales : chartOneDaySalesList)
                BARENTRY.add(new BarEntry((int) chartOneDaySales.getHour(), (float) chartOneDaySales.getNetAmount()));

            Bardataset = new BarDataSet(BARENTRY, mContext.getResources().getString(R.string.net_sales).concat("/").concat(mContext.getResources().getString(R.string.hour)));
        } else if (chartSaleSelectionEnum == ChartSaleSelectionEnum.SALES_COUNT) {

            for (ChartSales chartOneDaySales : chartOneDaySalesList)
                BARENTRY.add(new BarEntry((int) chartOneDaySales.getHour(), (float) chartOneDaySales.getSaleCount()));

            Bardataset = new BarDataSet(BARENTRY, mContext.getResources().getString(R.string.sales_count).concat("/").concat(mContext.getResources().getString(R.string.hour)));
        }

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(Bardataset);

        setOneDayXYAxisValues();

        BARDATA = new BarData(dataSets);
        BARDATA.setBarWidth(0.9f);
        Bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
    }

    public void fillOneWeekChartVariables(ChartSaleSelectionEnum chartSaleSelectionEnum){
        List<ChartSales> chartOneDaySalesList = getOneWeekSalesList();

        BARENTRY = new ArrayList<>();

        if (chartSaleSelectionEnum == ChartSaleSelectionEnum.GROSS_SALES) {

            for (ChartSales chartOneDaySales : chartOneDaySalesList)
                BARENTRY.add(new BarEntry(chartOneDaySales.getDay(), (float) chartOneDaySales.getGrossAmount()));

            Bardataset = new BarDataSet(BARENTRY, mContext.getResources().getString(R.string.gross_sales).concat("/").concat(mContext.getResources().getString(R.string.week)));

        } else if (chartSaleSelectionEnum == ChartSaleSelectionEnum.NET_SALES) {

            for (ChartSales chartOneDaySales : chartOneDaySalesList)
                BARENTRY.add(new BarEntry(chartOneDaySales.getDay(), (float) chartOneDaySales.getNetAmount()));

            Bardataset = new BarDataSet(BARENTRY, mContext.getResources().getString(R.string.net_sales).concat("/").concat(mContext.getResources().getString(R.string.week)));
        } else if (chartSaleSelectionEnum == ChartSaleSelectionEnum.SALES_COUNT) {

            for (ChartSales chartOneDaySales : chartOneDaySalesList)
                BARENTRY.add(new BarEntry(chartOneDaySales.getDay(), (float) chartOneDaySales.getSaleCount()));

            Bardataset = new BarDataSet(BARENTRY, mContext.getResources().getString(R.string.sales_count).concat("/").concat(mContext.getResources().getString(R.string.week)));
        }

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(Bardataset);

        setOneWeekXYAxisValues();

        BARDATA = new BarData(dataSets);
        Bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
    }

    public void fillOneMonthChartVariables(ChartSaleSelectionEnum chartSaleSelectionEnum){
        List<ChartSales> salesList = getOneMonthSalesList();

        Collections.sort(salesList, new DayComparator());

        LineDataSet dataSet = null;

        ArrayList<Entry> entries = new ArrayList<>();

        String monthStr = "";
        if(reportSelectionEnum == ReportSelectionEnum.ONE_M)
            monthStr = mContext.getResources().getString(R.string.month);
        else if(reportSelectionEnum == ReportSelectionEnum.THREE_M)
            monthStr = "3 ".concat(mContext.getResources().getString(R.string.month));

        if (chartSaleSelectionEnum == ChartSaleSelectionEnum.GROSS_SALES) {

            for (ChartSales chartOneDaySales : salesList)
                entries.add(new Entry(chartOneDaySales.getDay(), (float) chartOneDaySales.getGrossAmount()));

            dataSet = new LineDataSet(entries, mContext.getResources().getString(R.string.gross_sales).concat("/").concat(monthStr));

        } else if (chartSaleSelectionEnum == ChartSaleSelectionEnum.NET_SALES) {

            for (ChartSales chartOneDaySales : salesList)
                entries.add(new Entry(chartOneDaySales.getDay(), (float) chartOneDaySales.getNetAmount()));

            dataSet = new LineDataSet(entries, mContext.getResources().getString(R.string.net_sales).concat("/").concat(monthStr));
        } else if (chartSaleSelectionEnum == ChartSaleSelectionEnum.SALES_COUNT) {

            for (ChartSales chartOneDaySales : salesList)
                entries.add(new Entry(chartOneDaySales.getDay(), (float) chartOneDaySales.getSaleCount()));

            dataSet = new LineDataSet(entries, mContext.getResources().getString(R.string.sales_count).concat("/").concat(monthStr));
        }

        dataSet.setColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        dataSet.setValueTextColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark));

        if(reportSelectionEnum == ReportSelectionEnum.ONE_M)
            setOneMonthXYAxisValues();
        else if(reportSelectionEnum == ReportSelectionEnum.THREE_M)
            setThreeMonthXYAxisValues();

        lineData = new LineData(dataSet);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
    }

    public static class DayComparator implements Comparator<ChartSales> {
        @Override
        public int compare(ChartSales o1, ChartSales o2) {
            return (int) (o2.getDay() - o1.getDay());
        }
    }

    private void setOneDayXYAxisValues(){
        XAxis xAxis = getBarChart().getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        final String[] hours = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};


        Log.i("Info", "hours----------");


        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Log.i("Info", "hours[(int) value]:" + hours[(int) value]);

                return hours[(int) value];
            }
        };



        //xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        //xAxis.setLabelCount(hours.length, true);

        xAxis.setValueFormatter(formatter);

        getBarChart().setVisibleXRangeMaximum(hours.length);
        getBarChart().setVisibleXRangeMinimum(hours.length);
        getBarChart().getDescription().setEnabled(false);


        YAxis yAxisRight = getBarChart().getAxisRight();
        yAxisRight.setEnabled(false);

        YAxis yAxisLeft = getBarChart().getAxisLeft();
        yAxisLeft.setGranularity(1f);

        getBarChart().setFitBars(true); // make the x-axis fit exactly all bars
    }

    private void setOneWeekXYAxisValues(){
        XAxis xAxis = getBarChart().getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        String[] days = new String[]{"Sub", "Mon", "Tue", "Wed", "Thur", "Fri", "Sat"};

        if(CommonUtils.getLanguage().equals(LANGUAGE_TR))
            days = new String[]{"Pzt", "Sal", "Çar", "Per", "Cum", "Ctes", "Paz"};

        String[] finalDays = days;

        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return finalDays[(int) value];
            }
        };
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);
        xAxis.setLabelCount(7);

        YAxis yAxisRight = getBarChart().getAxisRight();
        yAxisRight.setEnabled(false);

        YAxis yAxisLeft = getBarChart().getAxisLeft();
        yAxisLeft.setGranularity(1f);
    }

    private void setOneMonthXYAxisValues(){
        XAxis xAxis = getLineChart().getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        final String[] months = new String[]{"1", "", "", "4", "", "",
                "7", "", "", "10", "", "",
                "13", "", "", "16", "", "",
                "19", "", "", "22", "", "",
                "25", "", "", "28", "", "",
                "31"};

        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return months[(int) value];
            }
        };
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);
        xAxis.setLabelCount(31);

        YAxis yAxisRight = getLineChart().getAxisRight();
        yAxisRight.setEnabled(false);

        YAxis yAxisLeft = getLineChart().getAxisLeft();
        yAxisLeft.setGranularity(1f);
    }

    private void setThreeMonthXYAxisValues(){
        XAxis xAxis = getLineChart().getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        List<String> dates = new ArrayList<>();

        int monthNum = DataUtils.getMonthNumFromDate(startDate);

        MonthEnum monthEnum1 = MonthEnum.getById(monthNum);
        MonthEnum monthEnum2 = MonthEnum.getById(monthNum + 1);
        MonthEnum monthEnum3 = MonthEnum.getById(monthNum + 2);

        dates.add("1" + monthEnum1.getEngCode());
        for(int i=0; i<13; i++)
            dates.add(" ");
        dates.add("15" + monthEnum1.getEngCode());
        for(int i=0; i<17; i++)
            dates.add(" ");

        dates.add("1" + monthEnum2.getEngCode());
        for(int i=0; i<13; i++)
            dates.add(" ");
        dates.add("15" + monthEnum2.getEngCode());
        for(int i=0; i<17; i++)
            dates.add(" ");

        dates.add("1" + monthEnum3.getEngCode());
        for(int i=0; i<13; i++)
            dates.add(" ");
        dates.add("15" + monthEnum3.getEngCode());
        for(int i=0; i<30; i++)
            dates.add(" ");

        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return dates.get((int) value);
            }
        };
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);
        xAxis.setLabelCount(31);

        YAxis yAxisRight = getLineChart().getAxisRight();
        yAxisRight.setEnabled(false);

        YAxis yAxisLeft = getLineChart().getAxisLeft();
        yAxisLeft.setGranularity(1f);
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
    }*/
}


