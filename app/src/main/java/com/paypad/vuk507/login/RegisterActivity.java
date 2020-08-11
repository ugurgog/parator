package com.paypad.vuk507.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.paypad.vuk507.R;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.login.utils.LoginUtils;
import com.paypad.vuk507.login.utils.Validation;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.ShapeUtil;

import java.util.Objects;
import java.util.UUID;


public class RegisterActivity extends AppCompatActivity
        implements View.OnClickListener {

    RelativeLayout registerLayout;
    EditText usernameET;
    EditText passwordET;
    Button btnRegister;

    //Local
    private String userName;
    private String userPassword;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        init();
        setShapes();
    }

    public void setShapes(){
        usernameET.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.White, null),
                getResources().getColor(R.color.DodgerBlue, null), GradientDrawable.RECTANGLE, 20, 2));
        passwordET.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.White, null),
                getResources().getColor(R.color.DodgerBlue, null), GradientDrawable.RECTANGLE, 20, 2));
        btnRegister.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.DodgerBlue, null),
                getResources().getColor(R.color.DodgerBlue, null), GradientDrawable.RECTANGLE, 20, 2));
    }

    private void init() {
        registerLayout = findViewById(R.id.registerLayout);
        usernameET = findViewById(R.id.input_username);
        passwordET = findViewById(R.id.input_password);
        btnRegister = findViewById(R.id.btnRegister);
        registerLayout.setOnClickListener(this);
        usernameET.setOnClickListener(this);
        passwordET.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
    }

    @Override
    public void onClick(View v) {

        if (v == btnRegister) {
            btnRegisterClicked();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    Objects.requireNonNull(imm).hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private void btnRegisterClicked() {

        progressDialog.setMessage(this.getString(R.string.registering_user));
        progressDialog.show();

        userName = usernameET.getText().toString();
        userPassword = passwordET.getText().toString();

        //validation controls
        if (!checkValidation()) {
            return;
        }

        createUser();
    }

    private boolean checkValidation() {

        User user = UserDBHelper.getUserByUsername(userName);

        progressDialog.dismiss();

        if(user != null){
            CommonUtils.showToastShort(RegisterActivity.this,
                    getResources().getString(R.string.username_is_defined_before));
            return false;
        }

        //username validation
        if (!Validation.getInstance().isValidUserName(this, userName)) {
            CommonUtils.showToastShort(RegisterActivity.this, Validation.getInstance().getErrorMessage());
            return false;
        }

        //password validation
        if (!Validation.getInstance().isValidPassword(this, userPassword)) {
            CommonUtils.showToastShort(RegisterActivity.this, Validation.getInstance().getErrorMessage());
            return false;
        }

        return true;
    }

    private void createUser() {

        String uuid = UUID.randomUUID().toString();

        BaseResponse baseResponse = UserDBHelper.createUser(userName, userPassword, uuid, true);

        progressDialog.dismiss();

        if(baseResponse.isSuccess()){
            LoginUtils.applySharedPreferences(RegisterActivity.this,
                    userName, userPassword, uuid);

            Intent intent = new Intent(RegisterActivity.this, InitialActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else{
            CommonUtils.showToastShort(RegisterActivity.this, baseResponse.getMessage());
        }
    }
}
