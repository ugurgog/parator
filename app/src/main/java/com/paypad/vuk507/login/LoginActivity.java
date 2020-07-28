package com.paypad.vuk507.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

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
import android.widget.RelativeLayout;

import com.paypad.vuk507.R;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.login.utils.Validation;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.login.utils.LoginUtils;
import com.paypad.vuk507.utils.ShapeUtil;


public class LoginActivity extends AppCompatActivity
        implements View.OnClickListener {

    RelativeLayout backgroundLayout;
    EditText usernameEt;
    EditText passwordEt;
    AppCompatTextView registerText;
    AppCompatTextView forgetPasText;
    Button btnLogin;

    //Local
    private String username;
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
        usernameEt.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.White, null),
                getResources().getColor(R.color.DodgerBlue, null), GradientDrawable.RECTANGLE, 20, 2));
        passwordEt.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.White, null),
                getResources().getColor(R.color.DodgerBlue, null), GradientDrawable.RECTANGLE, 20, 2));
        btnLogin.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.DodgerBlue, null),
                getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 20, 2));
    }

    private void initVariables() {
        initUIValues();
        setClickableTexts(this);
        initUIListeners();
        progressDialog = new ProgressDialog(this);
    }

    private void initUIValues() {
        backgroundLayout = findViewById(R.id.loginLayout);
        usernameEt = findViewById(R.id.usernameEt);
        passwordEt = findViewById(R.id.passwordEt);
        registerText = findViewById(R.id.btnRegister);
        forgetPasText = findViewById(R.id.btnForgetPassword);
        btnLogin = findViewById(R.id.btnLogin);
    }

    private void initUIListeners() {
        backgroundLayout.setOnClickListener(this);
        usernameEt.setOnClickListener(this);
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

        username = usernameEt.getText().toString();
        userPassword = passwordEt.getText().toString();

        //validation controls
        if (!checkValidation(username, userPassword)) {
            return;
        }

        startLoginProcess(username, userPassword);
    }

    private boolean checkValidation(String username, String password) {

        //username validation
        if (!Validation.getInstance().isValidUserName(this, username)) {
            progressDialog.dismiss();
            CommonUtils.showToastShort(LoginActivity.this, Validation.getInstance().getErrorMessage());
            return false;
        }

        if (!Validation.getInstance().isValidPassword(LoginActivity.this, password)) {
            progressDialog.dismiss();
            CommonUtils.showToastShort(LoginActivity.this, Validation.getInstance().getErrorMessage());
            return false;
        }

        return true;
    }

    private void startLoginProcess(final String username, String userPassword) {

        User user = UserDBHelper.getUserByUsernameAndPassword(username, userPassword);
        progressDialog.dismiss();

        if(user != null){
            BaseResponse baseResponse = UserDBHelper.updateUserLoggedInStatus(username, true);

            if(baseResponse.isSuccess()){
                LoginUtils.applySharedPreferences(LoginActivity.this, user.getUsername(),
                        user.getPassword(), user.getUuid());
                startMainPage();
            }

        }else
            CommonUtils.showToastShort(LoginActivity.this, getResources().getString(R.string.invalid_username_or_password));
    }


    private void startMainPage() {
        Intent intent = new Intent(this, InitialActivity.class);
        startActivity(intent);
        finish();
    }
}
