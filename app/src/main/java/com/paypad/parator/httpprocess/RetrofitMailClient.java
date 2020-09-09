package com.paypad.parator.httpprocess;

import android.util.Base64;

import com.paypad.parator.charge.payment.utils.SendMailApi;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitMailClient {

    //private static final String BASE_URL = "https://api.mailgun.net/v3/sandboxc241453a79904554a01be513a4783c14.mailgun.org/";

    private static final String BASE_URL = "https://api.mailgun.net/v3/sandboxaeecbd98c2f2436796b5b23d90ed13d1.mailgun.org/";

    private static final String API_USERNAME = "api";

    //you need to change the value to your API key
    private static final String API_PASSWORD = "d11b9c94979856c3b1d3e98d6d96f486-0f472795-28c493fc";

    private static final String AUTH = "Basic " + Base64.encodeToString((API_USERNAME+":"+API_PASSWORD).getBytes(), Base64.NO_WRAP);

    private static RetrofitMailClient mInstance;
    private Retrofit retrofit;

    private RetrofitMailClient() {
        OkHttpClient okClient = new OkHttpClient.Builder()
                .addInterceptor(
                        new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                Request original = chain.request();

                                //Adding basic auth
                                Request.Builder requestBuilder = original.newBuilder()
                                        .header("Authorization", AUTH)
                                        .method(original.method(), original.body());

                                Request request = requestBuilder.build();
                                return chain.proceed(request);
                            }
                        })
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okClient)
                .build();
    }

    public static synchronized RetrofitMailClient getInstance() {
        if (mInstance == null) {
            mInstance = new RetrofitMailClient();
        }
        return mInstance;
    }

    public Retrofit getClient() {
        return retrofit;
    }

    public SendMailApi getApi() {
        return retrofit.create(SendMailApi.class);
    }
}
