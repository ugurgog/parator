package com.paypad.parator.menu.settings.passcode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hanks.passcodeview.PasscodeView;
import com.paypad.parator.R;
import com.paypad.parator.enums.ProcessDirectionEnum;
import com.paypad.parator.uiUtils.keypad.KeyPadClick;
import com.paypad.parator.uiUtils.keypad.KeyPadPasscodeCreate;
import com.paypad.parator.uiUtils.keypad.KeyPadSingleNumberListener;
import com.paypad.parator.utils.ClickableImage.ClickableImageView;
import com.paypad.parator.utils.CommonUtils;

public class PasscodeTypeActivity extends AppCompatActivity {

    private ClickableImageView cancelImgv;
    private AppCompatTextView toolbarTitleTv;
    private Button saveBtn;
    private LinearLayout toolbarWithClose;
    private LinearLayout cbsll;

    private CheckBox cb1;
    private CheckBox cb2;
    private CheckBox cb3;
    private CheckBox cb4;

    private KeyPadPasscodeCreate keypad;
    private String typedPasscodeVal = "";
    private String passcodeVal;
    private boolean toolbarVisible;
    private Vibrator vibrator;

    private static final long VIBRATE_TIME  = 150;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode_type);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            passcodeVal = bundle.getString("passcodeVal");
            toolbarVisible = bundle.getBoolean("toolbarVisible");
        }

        initVariables();
        initListeners();
    }

    private void initVariables() {
        cancelImgv = findViewById(R.id.cancelImgv);
        toolbarTitleTv = findViewById(R.id.toolbarTitleTv);
        saveBtn = findViewById(R.id.saveBtn);
        cb1 = findViewById(R.id.cb1);
        cb2 = findViewById(R.id.cb2);
        cb3 = findViewById(R.id.cb3);
        cb4 = findViewById(R.id.cb4);
        keypad = findViewById(R.id.keypad);
        cbsll = findViewById(R.id.cbsll);
        toolbarWithClose = findViewById(R.id.toolbarWithClose);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        keypad.setBackBtnText(PasscodeTypeActivity.this, getResources().getString(R.string.clear));
        toolbarTitleTv.setText(getResources().getString(R.string.enter_passcode));

        saveBtn.setVisibility(View.GONE);

        if(!toolbarVisible)
            toolbarWithClose.setVisibility(View.GONE);
        else
            toolbarWithClose.setVisibility(View.VISIBLE);
    }

    private void initListeners() {
        keypad.setOnNumPadClickListener(new KeyPadClick(new KeyPadSingleNumberListener() {
            @Override
            public void onKeypadClicked(Integer number) {
                fillPasscodeCells(number);
            }
        }));

        cancelImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PasscodeTypeActivity.this.finish();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void fillPasscodeCells(Integer number) {
        if(number != -2){
            if(!cb1.isChecked())
                cb1.setChecked(true);
            else if(!cb2.isChecked())
                cb2.setChecked(true);
            else if(!cb3.isChecked())
                cb3.setChecked(true);
            else if(!cb4.isChecked())
                cb4.setChecked(true);

            if(typedPasscodeVal.trim().length() < 4)
                typedPasscodeVal = typedPasscodeVal.concat(String.valueOf(number));

            if(typedPasscodeVal.trim().length() == 4){

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if(typedPasscodeVal.trim().equals(passcodeVal)){
                            Intent intent = new Intent();
                            intent.putExtra("isSucceed", true);
                            setResult(RESULT_OK, intent);
                            finish();
                        } else{
                            clearViews();
                            //CommonUtils.showCustomToast(PasscodeTypeActivity.this, getResources().getString(R.string.wrong_password_entered));

                            startShakeAnimation();

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                vibrator.vibrate(VibrationEffect.createOneShot(VIBRATE_TIME, VibrationEffect.DEFAULT_AMPLITUDE));
                            } else
                                vibrator.vibrate(VIBRATE_TIME);
                        }
                    }
                }, 100);


            }
        }else
            clearViews();
    }

    private void startShakeAnimation(){
        Animation shake = AnimationUtils.loadAnimation(PasscodeTypeActivity.this, R.anim.shake_animation);
        shake.setDuration(50);
        cbsll.setAnimation(shake);
        cbsll.startAnimation(shake);
    }

    private void clearViews(){
        cb4.setChecked(false);
        cb3.setChecked(false);
        cb2.setChecked(false);
        cb1.setChecked(false);
        typedPasscodeVal = "";
    }

    @Override
    public void onBackPressed() {

    }
}
