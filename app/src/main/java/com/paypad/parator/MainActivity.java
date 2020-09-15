package com.paypad.parator;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.tabs.TabLayout;
import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.FragmentControllers.FragNavController;
import com.paypad.parator.FragmentControllers.FragNavTransactionOptions;
import com.paypad.parator.FragmentControllers.FragmentHistory;
import com.paypad.parator.charge.ChargeFragment;
import com.paypad.parator.db.PasscodeDBHelper;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.enums.PasscodeTimeoutEnum;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.interfaces.CustomDialogListener;
import com.paypad.parator.menu.category.CategoryEditFragment;
import com.paypad.parator.menu.customer.CustomerEditFragment;
import com.paypad.parator.menu.discount.DiscountEditFragment;
import com.paypad.parator.menu.group.GroupCreateFragment;
import com.paypad.parator.menu.product.ProductEditFragment;
import com.paypad.parator.menu.settings.passcode.PasscodeTypeActivity;
import com.paypad.parator.menu.support.reportproblem.fragments.NotifyProblemFragment;
import com.paypad.parator.menu.tax.TaxEditFragment;
import com.paypad.parator.menu.unit.UnitEditFragment;
import com.paypad.parator.model.Passcode;
import com.paypad.parator.model.User;
import com.paypad.parator.utils.CustomDialogBoxVert;
import com.paypad.parator.utils.ShapeUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Objects;

import butterknife.ButterKnife;
import fun.observe.touchy.MotionEventBroadcaster;
import fun.observe.touchy.MotionEventReceiver;

import static com.paypad.parator.FragmentControllers.FragNavController.TAB1;
import static com.paypad.parator.constants.AnimationConstants.ANIMATE_DOWN_TO_UP;
import static com.paypad.parator.constants.AnimationConstants.ANIMATE_LEFT_TO_RIGHT;
import static com.paypad.parator.constants.AnimationConstants.ANIMATE_RIGHT_TO_LEFT;
import static com.paypad.parator.constants.AnimationConstants.ANIMATE_UP_TO_DOWN;


public class MainActivity extends FragmentActivity implements
        BaseFragment.FragmentNavigation,
        FragNavController.TransactionListener,
        FragNavController.RootFragmentListener{

    private int[] mTabIconsSelected = {
            R.drawable.ic_keyboard_black_24dp,
            R.drawable.ic_library_books_black_24dp};

    private FrameLayout contentFrame;
    private TabLayout bottomTabLayout;
    private LinearLayout tabMainLayout;
    private RelativeLayout screenShotMainLayout;
    private Button screenShotCancelBtn;
    private Button screenShotApproveBtn;
    private int selectedTabColor, unSelectedTabColor;
    private String ANIMATION_TAG;
    private FragNavTransactionOptions transactionOptions;
    private ChargeFragment chargeFragment;
    private String[] TABS;
    private FragNavController mNavController;
    private FragmentHistory fragmentHistory;
    private User user;
    private CountDownTimer myCountDown;
    private Passcode passcode;
    private MotionEventReceiver motionEventReceiver;
    public static NotifyProblemFragment notifyProblemFragment;
    public static Activity mainActivity;

    @Subscribe(sticky = true)
    public void accountHolderUserReceived(UserBus userBus){
        user = userBus.getUser();
        if(user == null)
            user = UserDBHelper.getUserFromCache(MainActivity.this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(MainActivity.this);
        if(motionEventReceiver != null)
            MotionEventBroadcaster.removeReceiver(motionEventReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity = this;

        unSelectedTabColor = this.getResources().getColor(R.color.DarkGray, null);
        selectedTabColor = this.getResources().getColor(R.color.Black, null);

        EventBus.getDefault().register(MainActivity.this);

        initValues();

        fragmentHistory = new FragmentHistory();

        setMotionEventBroadcaster();
        setCounterForPasscode();

        mNavController = FragNavController.newBuilder(savedInstanceState, getSupportFragmentManager(), R.id.content_frame)
                .transactionListener(this)
                //.rootFragmentListener(this, TABS.length)
                .rootFragmentListener(this, 1)
                .build();

        switchTab(0);
        //updateTabSelection(0);

        bottomTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabSelectionControl(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                mNavController.clearStack();
                tabSelectionControl(tab);
            }
        });
    }

    public boolean isScreenShotLayoutVisible(){
        if(screenShotMainLayout.getVisibility() == View.VISIBLE)
            return true;
        return false;
    }

    public void clearStackGivenIndex(int index){
        mNavController.clearStackWithGivenIndex(index);
    }

    private void setMotionEventBroadcaster() {
        motionEventReceiver = new MotionEventReceiver() {
            @Override
            protected void onReceive(MotionEvent motionEvent) {
                cancelCounter();

                if(passcode != null && passcode.isEnabled() && passcode.getTimeOutId() != PasscodeTimeoutEnum.NEVER.getId())
                    startCounter();
            }
        };
        MotionEventBroadcaster.registerReceiver(motionEventReceiver);
    }

    public void setCounterForPasscode(){
        passcode = PasscodeDBHelper.getPasscodeByUserId(user.getId());

        if(passcode != null && passcode.isEnabled() && passcode.getTimeOutId() != PasscodeTimeoutEnum.NEVER.getId()){
            setCounter(PasscodeTimeoutEnum.getById(passcode.getTimeOutId()).getTimeout());
            startCounter();
        }else
            cancelCounter();
    }

    private void setCounter(int timeoutValue){
        myCountDown = new CountDownTimer(timeoutValue, 1000) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                Intent intent = new Intent(MainActivity.this, PasscodeTypeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("toolbarVisible", false);
                bundle.putString("passcodeVal", passcode.getPasscodeVal());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        };
    }

    private void startCounter(){
        if(myCountDown != null)
            myCountDown.start();
    }

    private void cancelCounter(){
        if(myCountDown != null)
            myCountDown.cancel();
    }

    private void initValues() {
        ButterKnife.bind(this);
        bottomTabLayout = findViewById(R.id.bottom_tab_layout);
        contentFrame = findViewById(R.id.content_frame);
        tabMainLayout = findViewById(R.id.tabMainLayout);
        screenShotMainLayout = findViewById(R.id.screenShotMainLayout);
        screenShotCancelBtn = findViewById(R.id.screenShotCancelBtn);
        screenShotApproveBtn = findViewById(R.id.screenShotApproveBtn);
        TABS = getResources().getStringArray(R.array.tab_name);
        initTab();
        setShapes();
    }

    public void setShapes() {
        screenShotCancelBtn.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.red_color_picker, null),
                getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 15, 0));
        screenShotApproveBtn.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.green_color_picker, null),
                getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 15, 0));
    }

    public void tabSelectionControl(TabLayout.Tab tab) {
        fragmentHistory.push(tab.getPosition());
        switchAndUpdateTabSelection(tab.getPosition());
    }

    private void initTab() {
        if (bottomTabLayout != null) {
            for (int i = 0; i < TABS.length; i++) {
                bottomTabLayout.addTab(bottomTabLayout.newTab());
                TabLayout.Tab tab = bottomTabLayout.getTabAt(i);

                if(tab != null){
                    tab.setIcon(mTabIconsSelected[i]);
                    tab.setText(TABS[i]);
                }
            }
            Objects.requireNonNull(bottomTabLayout.getTabAt(0).getIcon()).setColorFilter(selectedTabColor, PorterDuff.Mode.SRC_IN);
            bottomTabLayout.setTabTextColors(unSelectedTabColor, selectedTabColor);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        //EventBus.getDefault().unregister(this);
    }

    /*@Subscribe(sticky = true)
    public void customEventReceived(UserBus userBus){
        User user = userBus.getUser();
    }*/

    public void switchTab(int position) {
        mNavController.switchTab(position);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {

        if (!mNavController.isRootFragment()) {
            setTransactionOption();
            checkItemUpdateDiscard();
        } else {

            if (fragmentHistory.isEmpty()) {
                super.onBackPressed();
            } else {

                if (fragmentHistory.getStackSize() > 1) {

                    int position = fragmentHistory.popPrevious();
                    switchAndUpdateTabSelection(position);
                } else {
                    switchAndUpdateTabSelection(0);
                    fragmentHistory.emptyStack();
                }
            }
        }
    }

    private void checkItemUpdateDiscard(){
        if(mNavController.getCurrentFrag() instanceof ProductEditFragment ||
                mNavController.getCurrentFrag() instanceof CategoryEditFragment ||
                mNavController.getCurrentFrag() instanceof UnitEditFragment ||
                mNavController.getCurrentFrag() instanceof TaxEditFragment ||
                mNavController.getCurrentFrag() instanceof DiscountEditFragment ||
                mNavController.getCurrentFrag() instanceof CustomerEditFragment ||
                mNavController.getCurrentFrag() instanceof GroupCreateFragment){

            new CustomDialogBoxVert.Builder(MainActivity.this)
                    .setTitle(getResources().getString(R.string.unsaved_changes))
                    .setMessage(getResources().getString(R.string.unsaved_changes_desc))
                    .setNegativeBtnVisibility(View.VISIBLE)
                    .setNegativeBtnText(getResources().getString(R.string.resume))
                    .setNegativeBtnBackground(getResources().getColor(R.color.custom_btn_bg_color, null))
                    .setPositiveBtnVisibility(View.VISIBLE)
                    .setPositiveBtnText(getResources().getString(R.string.discard))
                    .setPositiveBtnBackground(getResources().getColor(R.color.DodgerBlue, null))
                    .setpBtnTextColor(getResources().getColor(R.color.White, null))
                    .setnBtnTextColor(getResources().getColor(R.color.DodgerBlue, null))
                    .setDurationTime(0)
                    .isCancellable(true)
                    .setEdittextVisibility(View.GONE)
                    .OnPositiveClicked(new CustomDialogListener() {
                        @Override
                        public void OnClick() {
                            mNavController.popFragment(transactionOptions);
                        }
                    })
                    .OnNegativeClicked(new CustomDialogListener() {
                        @Override
                        public void OnClick() {

                        }
                    }).build();

        }else
            mNavController.popFragment(transactionOptions);
    }

    public void switchAndUpdateTabSelection(int position) {
        switchTab(position);
        //updateTabSelection(position);
    }

    private void setTransactionOption() {
        if (transactionOptions == null) {
            transactionOptions = FragNavTransactionOptions.newBuilder().build();
        }

        if (ANIMATION_TAG != null) {
            switch (ANIMATION_TAG) {
                case ANIMATE_RIGHT_TO_LEFT:
                    transactionOptions.enterAnimation = R.anim.slide_from_right;
                    transactionOptions.exitAnimation = R.anim.slide_to_left;
                    transactionOptions.popEnterAnimation = R.anim.slide_from_left;
                    transactionOptions.popExitAnimation = R.anim.slide_to_right;
                    break;
                case ANIMATE_LEFT_TO_RIGHT:
                    transactionOptions.enterAnimation = R.anim.slide_from_left;
                    transactionOptions.exitAnimation = R.anim.slide_to_right;
                    transactionOptions.popEnterAnimation = R.anim.slide_from_right;
                    transactionOptions.popExitAnimation = R.anim.slide_to_left;
                    break;
                case ANIMATE_DOWN_TO_UP:
                    transactionOptions.enterAnimation = R.anim.slide_from_down;
                    transactionOptions.exitAnimation = R.anim.slide_to_up;
                    transactionOptions.popEnterAnimation = R.anim.slide_from_up;
                    transactionOptions.popExitAnimation = R.anim.slide_to_down;
                    break;
                case ANIMATE_UP_TO_DOWN:
                    transactionOptions.enterAnimation = R.anim.slide_from_up;
                    transactionOptions.exitAnimation = R.anim.slide_to_down;
                    transactionOptions.popEnterAnimation = R.anim.slide_from_down;
                    transactionOptions.popExitAnimation = R.anim.slide_to_up;
                    break;
                default:
                    transactionOptions = null;
            }
        } else
            transactionOptions = null;
    }

    /*public void updateTabSelection(int currentTab) {

        for (int i = 0; i < TABS.length; i++) {
            TabLayout.Tab selectedTab = bottomTabLayout.getTabAt(i);

            if (currentTab != i) {
                Objects.requireNonNull(selectedTab.getIcon()).setColorFilter(unSelectedTabColor, PorterDuff.Mode.SRC_IN);
            } else {
                Objects.requireNonNull(selectedTab.getIcon()).setColorFilter(selectedTabColor, PorterDuff.Mode.SRC_IN);
            }
        }
    }*/

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mNavController != null) {
            mNavController.onSaveInstanceState(outState);
        }
    }

    @Override
    public void pushFragment(Fragment fragment) {
        if (mNavController != null) {
            mNavController.pushFragment(fragment);
        }
    }

    @Override
    public void pushFragment(Fragment fragment, String animationTag) {

        ANIMATION_TAG = animationTag;
        setTransactionOption();

        if (mNavController != null) {
            mNavController.pushFragment(fragment, transactionOptions);
        }
    }

    @Override
    public void popFragments(int depth) {
        if (mNavController != null) {
            mNavController.popFragments(depth);
        }
    }

    @Override
    public void clearStackWithGivenIndex(int tab) {
        if (mNavController != null) {
            mNavController.clearStackWithGivenIndex(tab);
        }
    }

    @Override
    public void newSaleTriggered() {
        if(chargeFragment != null){
            chargeFragment.onItemsCleared();
        }
    }

    @Override
    public void onTabTransaction(Fragment fragment, int index) {

    }

    @Override
    public Fragment getRootFragment(int index) {
        switch (index) {

            case TAB1:
                chargeFragment = new ChargeFragment();
                return chargeFragment;

        }
        throw new IllegalStateException("Need to send an index that we know");
    }

    @Override
    public void onFragmentTransaction(Fragment fragment, FragNavController.TransactionType transactionType) {

    }

}