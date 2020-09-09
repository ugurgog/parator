package com.paypad.parator.charge.payment.utils;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface SendMailApi {

    @FormUrlEncoded
    @POST("messages")
    Call<ResponseBody> sendEmail(
            @Field("from") String from,
            @Field("to") String to,
            @Field("subject") String subject,
            @Field("text") String text
    );
}
