package com.paypad.vuk507.utils.network;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Scanner;

public class NetworkUtils {

    //Country network params
    private final static String COUNTRY_BASE_URL = "http://country.io/";
    private final static String COUNTRY_CONTINENT_PARAM = "continent.json";
    public final static String COUNTRY_NAMES_PARAM = "names.json";
    private final static String COUNTRY_ISO3_PARAM = "iso3.json";
    private final static String COUNTRY_CAPITALS_PARAM = "capital.json";
    private final static String COUNTRY_PHONE_CODES_PARAM = "phone.json";
    private final static String COUNTRY_CURRENCY_PARAM = "currency.json";

    public static URL buildCountryUrlByParam(String param) {
        String urlStr = COUNTRY_BASE_URL + param;
        Uri builtUri = Uri.parse(urlStr).buildUpon()
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }
}