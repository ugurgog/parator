package com.paypad.vuk507.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.paypad.vuk507.MainActivity;
import com.paypad.vuk507.R;
import com.paypad.vuk507.db.CustomRealmHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.login.utils.LoginUtils;

import org.greenrobot.eventbus.EventBus;


public class InitialActivity extends AppCompatActivity {

    RelativeLayout mainActLayout;
    ImageView appIconImgv;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        CommonUtils.hideKeyBoard(this);
        initVariables();

        //CustomRealmHelper.deleteRealmDB();

        String username = LoginUtils.getUsernameFromCache(InitialActivity.this);
        String password = LoginUtils.getPasswordFromCache(InitialActivity.this);

        if(!username.isEmpty() && !password.isEmpty()){
            startLoginProcess(username, password);
        } else
            startLoginActivity();
    }

    private void initVariables() {
        mainActLayout = findViewById(R.id.mainActLayout);
        appIconImgv = findViewById(R.id.appIconImgv);
    }

    public void startLoginProcess(String username, String password) {

        User user = UserDBHelper.getUserByUsernameAndPassword(username, password);

        if(user != null){
            if(user.isLoggedIn()){
                EventBus.getDefault().postSticky(new UserBus(user));
                updateDeviceTokenForFCM();
                startActivity(new Intent(InitialActivity.this, MainActivity.class));
                finish();
            }else
                startLoginActivity();
        }else
            startLoginActivity();
    }

    private void startLoginActivity(){
        EventBus.getDefault().postSticky(new UserBus(user));
        updateDeviceTokenForFCM();
        startActivity(new Intent(InitialActivity.this, LoginActivity.class));
        finish();
    }

    public void updateDeviceTokenForFCM() {
        /*TokenDBHelper.updateTokenSigninValue(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid(), CHAR_E,
                new OnCompleteCallback() {
                    @Override
                    public void OnCompleted() {
                        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                            @Override
                            public void onSuccess(InstanceIdResult instanceIdResult) {
                                String deviceToken = instanceIdResult.getToken();
                                TokenDBHelper.sendTokenToServer(deviceToken, Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());
                            }
                        });
                    }

                    @Override
                    public void OnFailed(String message) {

                    }
                });*/
    }
}
