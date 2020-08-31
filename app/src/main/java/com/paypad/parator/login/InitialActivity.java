package com.paypad.parator.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.paypad.parator.MainActivity;
import com.paypad.parator.R;
import com.paypad.parator.db.CustomRealmHelper;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.login.utils.LoginUtils;
import com.paypad.parator.model.User;
import com.paypad.parator.utils.CommonUtils;

import org.greenrobot.eventbus.EventBus;


public class InitialActivity extends AppCompatActivity {

    RelativeLayout mainActLayout;
    ImageView appIconImgv;

    User user;
    private String email;
    private String password;
    private String applicationStart = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_initial);
        CommonUtils.hideKeyBoard(this);
        initVariables();

        //CustomRealmHelper.deleteRealmDB();

        email = LoginUtils.getEmailFromCache(InitialActivity.this);
        password = LoginUtils.getPasswordFromCache(InitialActivity.this);

        if(!email.isEmpty() && !password.isEmpty()){
            startLoginProcess(email, password);
        } else
            startLoginActivity();

        //thread.start();
    }

    private void initVariables() {
        mainActLayout = findViewById(R.id.mainActLayout);
        appIconImgv = findViewById(R.id.appIconImgv);
        Glide.with(InitialActivity.this)
                .load(R.drawable.pos_icon)
                .into(appIconImgv);
    }

    public void startLoginProcess(String email, String password) {

        User user = UserDBHelper.getUserByEmailAndPassword(email, password);

        if(user != null){
            if(user.isLoggedIn()){
                EventBus.getDefault().postSticky(new UserBus(user));
                updateDeviceTokenForFCM();
                applicationStart = MainActivity.class.getName();
                thread.start();
                //startActivity(new Intent(InitialActivity.this, MainActivity.class));
                //finish();
            }else
                startLoginActivity();
        }else
            startLoginActivity();
    }

    private void startLoginActivity(){
        EventBus.getDefault().postSticky(new UserBus(user));
        updateDeviceTokenForFCM();
        applicationStart = LoginActivity.class.getName();
        thread.start();
        //startActivity(new Intent(InitialActivity.this, LoginActivity.class));
        //finish();
    }


    Thread thread = new Thread() {
        @Override
        public void run() {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(applicationStart.equals(MainActivity.class.getName())){
                startActivity(new Intent(InitialActivity.this, MainActivity.class));
                finish();
            }else if(applicationStart.equals(LoginActivity.class.getName())){
                startActivity(new Intent(InitialActivity.this, LoginActivity.class));
                finish();
            }
        }
    };

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
