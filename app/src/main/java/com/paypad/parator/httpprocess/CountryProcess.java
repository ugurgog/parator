package com.paypad.parator.httpprocess;

import android.content.Context;
import android.os.AsyncTask;

import com.paypad.parator.enums.CountryDataEnum;
import com.paypad.parator.httpprocess.interfaces.OnEventListener;
import com.paypad.parator.model.pojo.CountryPhoneCode;
import com.paypad.parator.utils.network.NetworkUtils;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.paypad.parator.utils.network.NetworkUtils.COUNTRY_NAMES_PARAM;
import static com.paypad.parator.utils.network.NetworkUtils.COUNTRY_PHONE_CODES_PARAM;

public class CountryProcess extends AsyncTask<Void, Void, String> {

    private OnEventListener<List<String>> mCallBack;
    private OnEventListener<List<CountryPhoneCode>> mPhonesCallBack;
    public Exception mException;
    private Context context;
    private CountryDataEnum countryDataEnum;

    public CountryProcess(Context context, CountryDataEnum countryDataEnum, OnEventListener callback) {
        mCallBack = callback;
        mPhonesCallBack = callback;
        this.context = context;
        this.countryDataEnum = countryDataEnum;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if(countryDataEnum == CountryDataEnum.NAMES){
            if (mCallBack != null) {
                mCallBack.onTaskContinue();
            }
        }else if(countryDataEnum == CountryDataEnum.PHONE_CODES){
            if (mPhonesCallBack != null) {
                mPhonesCallBack.onTaskContinue();
            }
        }
    }

    @Override
    protected String doInBackground(Void... voids) {

        URL strUrl = null;

        if(countryDataEnum == CountryDataEnum.NAMES)
            strUrl = NetworkUtils.buildCountryUrlByParam(COUNTRY_NAMES_PARAM);
        else if(countryDataEnum == CountryDataEnum.PHONE_CODES)
            strUrl = NetworkUtils.buildCountryUrlByParam(COUNTRY_PHONE_CODES_PARAM);

        String sessionResultStr = null;
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(strUrl)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                sessionResultStr = response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
                mException = e;
            }

        } catch (Exception e) {
            mException = e;
            e.printStackTrace();
        }
        return sessionResultStr;
    }

    @Override
    protected void onPostExecute(String s) {
        if (s != null && !s.equals("")) {
            try {
                if(countryDataEnum == CountryDataEnum.NAMES)
                    mCallBack.onSuccess(parseCountryNames(s));
                else if(countryDataEnum == CountryDataEnum.PHONE_CODES)
                    mPhonesCallBack.onSuccess(parseCountryPhoneCodes(s));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            if(countryDataEnum == CountryDataEnum.NAMES)
                mCallBack.onFailure(mException);
            else if(countryDataEnum == CountryDataEnum.PHONE_CODES)
                mPhonesCallBack.onFailure(mException);
        }
    }

    private List<String> parseCountryNames(String jsonStr) throws JSONException {
        List<String> countries = new ArrayList<>();
        JSONObject resultJSONObject = new JSONObject(jsonStr);

        Iterator<String> keys= resultJSONObject.keys();
        while (keys.hasNext())
        {
            String keyValue = keys.next();
            String countryName = resultJSONObject.getString(keyValue);
            countries.add(countryName);
        }

        return countries;
    }

    private List<CountryPhoneCode> parseCountryPhoneCodes(String jsonStr) throws JSONException {
        List<CountryPhoneCode> phoneCodes = new ArrayList<>();
        JSONObject resultJSONObject = new JSONObject(jsonStr);

        Iterator<String> keys= resultJSONObject.keys();
        while (keys.hasNext())
        {
            String keyValue = keys.next();
            String dialCode = resultJSONObject.getString(keyValue);
            CountryPhoneCode countryDial = new CountryPhoneCode(keyValue, dialCode);
            phoneCodes.add(countryDial);
        }

        return phoneCodes;
    }
}
