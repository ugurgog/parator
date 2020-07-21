package com.paypad.vuk507.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateUtils;
import android.util.Log;

import com.paypad.vuk507.R;
import com.paypad.vuk507.db.CategoryDBHelper;
import com.paypad.vuk507.db.TaxDBHelper;
import com.paypad.vuk507.db.UnitDBHelper;
import com.paypad.vuk507.enums.DayEnum;
import com.paypad.vuk507.enums.MonthEnum;
import com.paypad.vuk507.enums.ProductUnitTypeEnum;
import com.paypad.vuk507.enums.TaxRateEnum;
import com.paypad.vuk507.model.Category;
import com.paypad.vuk507.model.Customer;
import com.paypad.vuk507.model.Product;
import com.paypad.vuk507.model.TaxModel;
import com.paypad.vuk507.model.UnitModel;
import com.paypad.vuk507.model.pojo.BaseResponse;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.paypad.vuk507.constants.CustomConstants.LANGUAGE_TR;
import static com.paypad.vuk507.constants.CustomConstants.MAX_PRICE_VALUE;
import static com.paypad.vuk507.constants.CustomConstants.TYPE_PRICE;

public class DataUtils {

    public static String getProductShortenName(Product product){
        if(product.getName().length() > 2)
            return product.getName().substring(0, 2);
        else
            return product.getName();
    }

    public static String getProductNameShortenName(String name){

        if(name == null) return "";

        if(name.length() > 2)
            return name.substring(0, 2);
        else
            return name;
    }

    public static String getCustomerShortName(String name, String surname) {
        String shortName = "";

        if (name != null && !name.trim().isEmpty() && surname != null && !surname.trim().isEmpty()) {
            shortName = name.substring(0,1).toUpperCase().concat(surname.substring(0,1).toUpperCase());
        }else if (name != null && !name.trim().isEmpty()){
            if(name.length() > 1)
                shortName = name.substring(0,2).toUpperCase();
            else
                shortName = name.substring(0,1).toUpperCase();
        }

        return shortName;
    }

    public static double getDoubleValueFromFormattedString(String rateOrAmountStr){
        String numberStr = rateOrAmountStr.replaceAll("\\.", "").replaceAll(",", "");
        return Double.parseDouble(numberStr) / 100d;
    }

    public static String getContactShortName(String name) {
        StringBuilder returnValue = new StringBuilder();
        if (name != null && !name.trim().isEmpty()) {
            String[] seperatedName = name.trim().split(" ");
            for (String word : seperatedName) {
                if (returnValue.length() < 3)
                    returnValue.append(word.substring(0, 1).toUpperCase());
            }
        }

        return returnValue.toString();
    }

    public static void callCustomer(Context mContext, String phoneNumber) {
        try {
            mContext.startActivity(new Intent(Intent.ACTION_DIAL,
                    Uri.fromParts("tel", phoneNumber, null)));

        } catch (Exception e) {
            CommonUtils.showToastShort(mContext, "Error while calling customer!");
            e.printStackTrace();
        }
    }

    public static void sendEmailToCustomer(Context mContext, String toEmail) {
        String[] TO = {toEmail};
        //String[] CC = {"xyz@gmail.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");


        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        //emailIntent.putExtra(Intent.EXTRA_CC, CC);
        //emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your subject");
        //emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here");

        try {
            mContext.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            CommonUtils.showToastShort(mContext, "There is no email client installed.");
            ex.printStackTrace();
        }
    }

    public static String getTotalAmount(double totalAmount, int number){
        if(totalAmount == 0){
            totalAmount = (totalAmount  + number) / 100;
        }else {
            totalAmount = (totalAmount * 10) + (number / 100.00d);
        }

        if(totalAmount > MAX_PRICE_VALUE){
            totalAmount = MAX_PRICE_VALUE;
        }

        return CommonUtils.getDoubleStrValueForView(totalAmount, TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol());
    }

    public static void showBaseResponseMessage(Context context, BaseResponse baseResponse){
        if(baseResponse.getMessage() != null && !baseResponse.getMessage().isEmpty())
            CommonUtils.showToastShort(context, baseResponse.getMessage());
    }

    public static TaxModel getTaxModelById(long taxId){
        TaxModel taxModel = null;

        if(taxId < 0){
            TaxRateEnum taxRateEnum = TaxRateEnum.getById(taxId);
            taxModel = new TaxModel();
            taxModel.setId(taxRateEnum.getId());
            taxModel.setName(taxRateEnum.getLabel());
            taxModel.setTaxRate(taxRateEnum.getRateValue());
        }else if(taxId > 0)
            taxModel = TaxDBHelper.getTax(taxId);

        return taxModel;
    }

    public static UnitModel getUnitModelById(long unitId){
        UnitModel unitModel = null;

        if(unitId < 0){
            ProductUnitTypeEnum unitType = ProductUnitTypeEnum.getById(unitId);
            unitModel = new UnitModel();
            unitModel.setId(unitType.getId());
            unitModel.setName(CommonUtils.getLanguage().equals(LANGUAGE_TR) ? unitType.getLabelTr() : unitType.getLabelEn());
        }else if(unitId > 0)
            unitModel = UnitDBHelper.getUnit(unitId);

        return unitModel;
    }

    public static String getCategoryName(Context context, long categoryId){
        if(categoryId == 0)
            return context.getResources().getString(R.string.uncategorized);
        else
            return CategoryDBHelper.getCategory(categoryId).getName();
    }


    public static int getDifferenceDays(Date d1, Date d2) {
        if(d1 != null && d2 != null){
            @SuppressLint("SimpleDateFormat") LocalDate localDate1 = LocalDate.parse( new SimpleDateFormat("yyyy-MM-dd").format(d1) );
            @SuppressLint("SimpleDateFormat") LocalDate localDate2 = LocalDate.parse( new SimpleDateFormat("yyyy-MM-dd").format(d2) );
            return Days.daysBetween(localDate1, localDate2).getDays();
        }else
            return -1;
    }

    public static String getHourOfOrder(Date d1) {
        @SuppressLint("SimpleDateFormat") String time = new SimpleDateFormat("HH:mm").format(d1);
        return time;
    }

    public static long getHourNumOfDate(Date d1) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d1);
        long hour = cal.get(Calendar.HOUR_OF_DAY);
        return hour;
    }

    public static int getMonthNumFromDate(Date date){
        //Calendar cal = Calendar.getInstance();
        //cal.setTime(date);
        //int month = cal.get(Calendar.MONTH);
        //return month;

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM");
        return Integer.parseInt(dateFormat.format(date));
    }

    public static int getDateNumFromDate(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dateNum = cal.get(Calendar.DATE);
        return dateNum;
    }

    public static int getYearFromDate(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        return year;
    }

    public static String getTransactionFullDateName(Date date){

        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE");


        String dayCode = simpleDateformat.format(date);

        String dayVal = "";

        if(CommonUtils.getLanguage().equals(LANGUAGE_TR))
            dayVal = DayEnum.getByCode(dayCode).getLabelTr();
        else
            dayVal = DayEnum.getByCode(dayCode).getLabelEn();


        int monthNum = getMonthNumFromDate(date);
        String monthNumStr = "";

        if(CommonUtils.getLanguage().equals(LANGUAGE_TR))
            monthNumStr = MonthEnum.getById(monthNum).getLabelTr();
        else
            monthNumStr = MonthEnum.getById(monthNum).getLabelEn();


        StringBuilder returnValue = new StringBuilder();

        returnValue.append(dayVal).append(", ")
                .append(getDateNumFromDate(date))
                .append(" ")
                .append(monthNumStr)
                .append(" ")
                .append(getYearFromDate(date));



        return returnValue.toString();
    }

}
