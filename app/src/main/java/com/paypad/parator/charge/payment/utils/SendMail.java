package com.paypad.parator.charge.payment.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.paypad.parator.MainActivity;
import com.paypad.parator.R;
import com.paypad.parator.httpprocess.RetrofitMailClient;
import com.paypad.parator.model.pojo.BaseResponse;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.core.MediaType;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_OK;

public class SendMail extends AsyncTask<Void,Void,Void> {

    //Declaring Variables
    private Context context;
    private Session session;

    //Information to send email
    private String email;
    private String subject;
    private String message;

    private ProgressDialog progressDialog;
    private BaseResponse baseResponse;
    private MailSendCallback mailSendCallback;

    public interface MailSendCallback{
        void OnMailSendResponse(BaseResponse baseResponse, String email);
        void OnBackPressed();
    }

    public void setMailSendCallback(MailSendCallback mailSendCallback) {
        this.mailSendCallback = mailSendCallback;
    }

    public SendMail(Context context, String email, String subject, String message){
        this.context = context;
        this.email = email;
        this.subject = subject;
        this.message = message;

        baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        //sendSimpleMessage();
        //sendSimpleMessage1();
        sendSimpleMessage3();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(context,"Sending message","Please wait...",false,false);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progressDialog.dismiss();
        mailSendCallback.OnMailSendResponse(baseResponse, email);
    }

    @Override
    protected Void doInBackground(Void... params) {
        Properties props = new Properties();

        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.debug", "true");

        //Creating a new session
        session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    //Authenticating the password
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(MailConfig.EMAIL, MailConfig.PASSWORD);
                    }
                });
        try {
            MimeMessage mm = new MimeMessage(session);

            mm.setFrom(new InternetAddress(MailConfig.EMAIL));
            mm.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            mm.setSubject(subject);
            mm.setText(message);

            Transport.send(mm);

        } catch (MessagingException e) {
            baseResponse.setSuccess(false);
            baseResponse.setMessage(context.getResources().getString(R.string.error_while_sending_receipt_mail));
        }
        return null;
    }

    public static ClientResponse sendSimpleMessage1() {
        Client client = Client.create();
        client.addFilter(new HTTPBasicAuthFilter("api", "ff229cee544c5e07053cb6d3bdde3986-0f472795-89727d40"));
        WebResource webResource = client.resource("https://api.mailgun.net/v3/sandbox7eeb77506d5e4f329a8632b5f084f644.mailgun.org/messages");
        MultivaluedMapImpl formData = new MultivaluedMapImpl();
        formData.add("from", "Mailgun Sandbox <postmaster@sandbox7eeb77506d5e4f329a8632b5f084f644.mailgun.org>");
        formData.add("to", "Ugur Gogebakan <ugurgogebakan07@gmail.com>");
        formData.add("subject", "Hello Ugur Gogebakan");
        formData.add("text", "Congratulations Ugur Gogebakan, you just sent an email with Mailgun!  You are truly awesome!");
        return webResource.type(MediaType.APPLICATION_FORM_URLENCODED).
                post(ClientResponse.class, formData);
    }

    public JsonNode sendSimpleMessage() {

        try{
            HttpResponse<JsonNode> request = Unirest.post("https://api.mailgun.net/v3/" + "sandbox7eeb77506d5e4f329a8632b5f084f644.mailgun.org" + "/messages")
                    .basicAuth("api", "ff229cee544c5e07053cb6d3bdde3986-0f472795-89727d40")
                    .field("from", "Excited User <postmaster@sandbox7eeb77506d5e4f329a8632b5f084f644.mailgun.org>")
                    .field("to", "Ugur Gogebakan <ugogebakan@hotmail.com>")
                    .field("subject", "hello")
                    .field("text", "testing")
                    .asJson();

            return request.getBody();
        }catch (Exception e){
            Log.i("Info", "sendSimpleMessage error:" + e);
        }
        return null;
    }

    public void sendSimpleMessage3(){
        RetrofitMailClient.getInstance()
                .getApi()
                .sendEmail("postmaster@sandboxaeecbd98c2f2436796b5b23d90ed13d1.mailgun.org", email, subject, message)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        Log.i("Info", "response.code():" + response.code());

                        if (response.code() == HTTP_OK) {
                            try {
                                JSONObject obj = new JSONObject(response.body().string());
                                Toast.makeText(context, obj.getString("message"), Toast.LENGTH_LONG).show();
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}