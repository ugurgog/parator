package com.paypad.parator.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.paypad.parator.R;
import com.paypad.parator.db.AutoIncrementDBHelper;
import com.paypad.parator.db.CategoryDBHelper;
import com.paypad.parator.db.GlobalSettingsDBHelper;
import com.paypad.parator.db.TaxDBHelper;
import com.paypad.parator.db.UnitDBHelper;
import com.paypad.parator.enums.DayEnum;
import com.paypad.parator.enums.MonthEnum;
import com.paypad.parator.enums.ProductUnitTypeEnum;
import com.paypad.parator.enums.TaxRateEnum;
import com.paypad.parator.enums.ToastEnum;
import com.paypad.parator.model.AutoIncrement;
import com.paypad.parator.model.GlobalSettings;
import com.paypad.parator.model.Product;
import com.paypad.parator.model.TaxModel;
import com.paypad.parator.model.UnitModel;
import com.paypad.parator.model.pojo.BaseResponse;
import com.paypad.parator.model.pojo.Country;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;

import static com.paypad.parator.constants.CustomConstants.LANGUAGE_TR;

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
            CommonUtils.showCustomToast(mContext, "Error while calling customer!", ToastEnum.TOAST_ERROR);
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
            CommonUtils.showCustomToast(mContext, "There is no email client installed.", ToastEnum.TOAST_ERROR);
            ex.printStackTrace();
        }
    }

    public static void showBaseResponseMessage(Context context, BaseResponse baseResponse){
        if(baseResponse.getMessage() != null && !baseResponse.getMessage().isEmpty())
            CommonUtils.showCustomToast(context, baseResponse.getMessage(), ToastEnum.TOAST_WARNING);
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

    public static int getDayOfYearFromDate(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayCount = cal.get(Calendar.DAY_OF_YEAR);
        return dayCount;
    }

    public static int getYearFromDate(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        return year;
    }

    public static Date getStartTimeOfDate(Date startDate){
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date getEndTimeOfDate(Date endDate){
        Calendar cal = Calendar.getInstance();
        cal.setTime(endDate);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    public static List<TaxModel> getAllTaxes(String userId){
        List<TaxModel> taxModels = new ArrayList<>();

        TaxRateEnum[] taxRateEnums = TaxRateEnum.values();

        for(TaxRateEnum taxRateEnum : taxRateEnums){
            TaxModel taxModel = new TaxModel();
            taxModel.setId(taxRateEnum.getId());
            taxModel.setTaxRate(taxRateEnum.getRateValue());
            taxModels.add(taxModel);
        }

        taxModels.addAll(TaxDBHelper.getAllTaxes(userId));
        return taxModels;
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

    public static void checkPrinterSettings(String userId){

        GlobalSettings globalSettings = GlobalSettingsDBHelper.getPrinterSetting(userId);

        if(globalSettings != null)
            return;

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        globalSettings = new GlobalSettings();
        globalSettings.setCreateDate(new Date());
        globalSettings.setUpdateDate(new Date());
        globalSettings.setUserId(userId);
        globalSettings.setCreateUserId(userId);
        globalSettings.setUpdateUserId(userId);
        globalSettings.setCustomerAutoPrint(true);
        globalSettings.setMerchantAutoPrint(false);

        GlobalSettings tempGlobalSettings = realm.copyToRealm(globalSettings);

        realm.commitTransaction();

        GlobalSettingsDBHelper.updatePrinterSettings(tempGlobalSettings);
    }

    public static void checkAutoIncrement(String userId){
        AutoIncrement autoIncrement = AutoIncrementDBHelper.getAutoIncrementByUserId(userId);

        if(autoIncrement != null)
            return;

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        autoIncrement = new AutoIncrement();
        autoIncrement.setUserId(userId);
        autoIncrement.setfNum(1);
        autoIncrement.setzNum(1);
        autoIncrement.setOrderNumCounter(1);

        realm.copyToRealm(autoIncrement);

        realm.commitTransaction();

        //AutoIncrementDBHelper.createOrUpdateAutoIncrement(tempAutoIncrement);
    }

    public static String getOrderRetrefNum(String userId){
        AutoIncrement autoIncrement = AutoIncrementDBHelper.getAutoIncrementByUserId(userId);

        @SuppressLint("DefaultLocale") String julianDate = String.format("%03d", getDayOfYearFromDate(new Date()));
        String yearShort = String.valueOf(getYearFromDate(new Date())).substring(2,4);
        @SuppressLint("DefaultLocale") String counter = String.format("%06d", autoIncrement.getOrderNumCounter());

        String retrefNum = julianDate.concat(yearShort).concat(counter);
        Log.i("Info", ":: getOrderRetrefNum retrefNum:" + retrefNum);
        return retrefNum;
    }

    public static List<Country> getCountries(Context context){
        List<Country> countries = new ArrayList<>();
        try {
            JSONArray countryArray = new JSONArray(loadJSONFromAsset(context));

            for(int i =0; i<countryArray.length(); i++){
                JSONObject jsonObject = countryArray.getJSONObject(i);
                String countryName = jsonObject.getString("name");
                String countryCode = jsonObject.getString("code");
                Country country = new Country(countryName, countryCode);
                countries.add(country);
            }

            return countries;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getResources().openRawResource(R.raw.country_names);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
