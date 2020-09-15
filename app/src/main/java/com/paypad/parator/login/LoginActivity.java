package com.paypad.parator.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.paypad.parator.R;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.enums.ToastEnum;
import com.paypad.parator.login.utils.LoginUtils;
import com.paypad.parator.login.utils.Validation;
import com.paypad.parator.model.User;
import com.paypad.parator.model.pojo.BaseResponse;
import com.paypad.parator.utils.CommonUtils;
import com.paypad.parator.utils.ShapeUtil;


public class LoginActivity extends AppCompatActivity
        implements View.OnClickListener {

    LinearLayout backgroundLayout;
    EditText emailEt;
    EditText passwordEt;
    AppCompatTextView registerText;
    AppCompatTextView forgetPasText;
    Button btnLogin;

    //Local
    private String email;
    private String userPassword;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setContentView(R.layout.activity_login);

        initVariables();
        setShapes();
    }

    public void setShapes() {
        emailEt.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.White, null),
                getResources().getColor(R.color.DodgerBlue, null), GradientDrawable.RECTANGLE, 0, 2));
        passwordEt.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.White, null),
                getResources().getColor(R.color.DodgerBlue, null), GradientDrawable.RECTANGLE, 0, 2));
        btnLogin.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.DodgerBlue, null),
                getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 0, 2));
    }

    private void initVariables() {
        initUIValues();
        setClickableTexts(this);
        initUIListeners();
        progressDialog = new ProgressDialog(this);
    }

    private void initUIValues() {
        backgroundLayout = findViewById(R.id.loginLayout);
        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        registerText = findViewById(R.id.btnRegister);
        forgetPasText = findViewById(R.id.btnForgetPassword);
        btnLogin = findViewById(R.id.btnLogin);
    }

    private void initUIListeners() {
        backgroundLayout.setOnClickListener(this);
        emailEt.setOnClickListener(this);
        passwordEt.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
    }

    private void setClickableTexts(Activity act) {
        String textRegister = getResources().getString(R.string.create_account);
        String textForgetPssword = getResources().getString(R.string.forget_password);
        final SpannableString spanStringRegister = new SpannableString(textRegister);
        final SpannableString spanStringForgetPas = new SpannableString(textForgetPssword);
        spanStringRegister.setSpan(new UnderlineSpan(), 0, spanStringRegister.length(), 0);
        spanStringForgetPas.setSpan(new UnderlineSpan(), 0, spanStringForgetPas.length(), 0);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {

                if (textView.equals(registerText)) {
                    //Toast.makeText(LoginActivity.this, "RegisterActivity click!", Toast.LENGTH_SHORT).show();
                    registerTextClicked();
                } else if (textView.equals(forgetPasText)) {
                    //Toast.makeText(LoginActivity.this, "Forgetpas click!", Toast.LENGTH_SHORT).show();
                    forgetPasTextClicked();
                }
            }
        };
        spanStringRegister.setSpan(clickableSpan, 0, spanStringRegister.length(), 0);
        spanStringForgetPas.setSpan(clickableSpan, 0, spanStringForgetPas.length(), 0);

        registerText.setText(spanStringRegister);
        forgetPasText.setText(spanStringForgetPas);
        registerText.setMovementMethod(LinkMovementMethod.getInstance());
        forgetPasText.setMovementMethod(LinkMovementMethod.getInstance());
        registerText.setHighlightColor(Color.TRANSPARENT);
        forgetPasText.setHighlightColor(Color.TRANSPARENT);
        //registerText.setLinkTextColor(getResources().getColor(R.color.White));
        //forgetPasText.setLinkTextColor(getResources().getColor(R.color.White));
    }

    @Override
    public void onClick(View view) {

        if (view == backgroundLayout) {
            CommonUtils.hideKeyBoard(LoginActivity.this);
        } else if (view == btnLogin) {
            loginBtnClicked();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }


    private void registerTextClicked() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void forgetPasTextClicked() {
        //Intent intent = new Intent(this, ForgetPasswordActivity.class);
        //startActivity(intent);
    }

    private void loginBtnClicked() {

        progressDialog.setMessage(this.getString(R.string.logging_user));
        progressDialog.show();

        email = emailEt.getText().toString();
        userPassword = passwordEt.getText().toString();

        //validation controls
        if (!checkValidation(email, userPassword)) {
            return;
        }

        startLoginProcess(email, userPassword);
    }

    private boolean checkValidation(String email, String password) {

        //username validation
        if (!Validation.getInstance().isValidEmail(this, email)) {
            progressDialog.dismiss();
            CommonUtils.showCustomToast(LoginActivity.this, Validation.getInstance().getErrorMessage(), ToastEnum.TOAST_WARNING);
            return false;
        }

        if (!Validation.getInstance().isValidPassword(LoginActivity.this, password)) {
            progressDialog.dismiss();
            CommonUtils.showCustomToast(LoginActivity.this, Validation.getInstance().getErrorMessage(), ToastEnum.TOAST_WARNING);
            return false;
        }

        return true;
    }

    private void startLoginProcess(final String email, String userPassword) {

        User user = UserDBHelper.getUserByEmailAndPassword(email, userPassword);
        progressDialog.dismiss();

        if(user != null){
            BaseResponse baseResponse = UserDBHelper.updateUserLoggedInStatus(user.getId(), true);

            if(baseResponse.isSuccess()){
                LoginUtils.applySharedPreferences(LoginActivity.this, user.getEmail(),
                        user.getPassword(), user.getId());
                startMainPage();
            }

        }else
            CommonUtils.showCustomToast(LoginActivity.this, getResources().getString(R.string.invalid_username_or_password), ToastEnum.TOAST_WARNING);
    }


    private void startMainPage() {
        Intent intent = new Intent(this, InitialActivity.class);
        startActivity(intent);
        finish();
    }
}
