package com.paypad.vuk507.charge.payment.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Config;
import android.widget.Toast;

import com.paypad.vuk507.R;
import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.model.pojo.BaseResponse;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

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
}