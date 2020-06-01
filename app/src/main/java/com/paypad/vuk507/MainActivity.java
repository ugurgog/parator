package com.paypad.vuk507;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.material.tabs.TabLayout;
import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.FragmentControllers.FragNavController;
import com.paypad.vuk507.FragmentControllers.FragNavTransactionOptions;
import com.paypad.vuk507.FragmentControllers.FragmentHistory;
import com.paypad.vuk507.charge.KeypadFragment;
import com.paypad.vuk507.charge.LibraryFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.Objects;
import java.util.Stack;

import butterknife.ButterKnife;

import static com.paypad.vuk507.FragmentControllers.FragNavController.TAB1;
import static com.paypad.vuk507.constants.AnimationConstants.ANIMATE_DOWN_TO_UP;
import static com.paypad.vuk507.constants.AnimationConstants.ANIMATE_LEFT_TO_RIGHT;
import static com.paypad.vuk507.constants.AnimationConstants.ANIMATE_RIGHT_TO_LEFT;
import static com.paypad.vuk507.constants.AnimationConstants.ANIMATE_UP_TO_DOWN;


public class MainActivity extends FragmentActivity implements
        BaseFragment.FragmentNavigation,
        FragNavController.TransactionListener,
        FragNavController.RootFragmentListener {

    public static FrameLayout contentFrame;

    public TabLayout bottomTabLayout;
    public RelativeLayout screenShotMainLayout;
    public Button screenShotCancelBtn;
    public Button screenShotApproveBtn;

    public LinearLayout tabMainLayout;

    private int selectedTabColor, unSelectedTabColor;

    public String ANIMATION_TAG;

    public FragNavTransactionOptions transactionOptions;


    public KeypadFragment keypadFragment;
    public LibraryFragment libraryFragment;

    private int[] mTabIconsSelected = {
            R.mipmap.ic_keypad,
            R.mipmap.ic_library};

    public String[] TABS;

    private FragNavController mNavController;

    private FragmentHistory fragmentHistory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        unSelectedTabColor = this.getResources().getColor(R.color.White, null);
        selectedTabColor = this.getResources().getColor(R.color.DarkGray, null);

        initValues();

        fragmentHistory = new FragmentHistory();

        mNavController = FragNavController.newBuilder(savedInstanceState, getSupportFragmentManager(), R.id.content_frame)
                .transactionListener(this)
                .rootFragmentListener(this, TABS.length)
                .build();

        switchTab(0);
        updateTabSelection(0);

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

    private void initValues() {
        ButterKnife.bind(this);
        bottomTabLayout = findViewById(R.id.bottom_tab_layout);
        contentFrame = findViewById(R.id.content_frame);
        tabMainLayout = findViewById(R.id.tabMainLayout);
        TABS = getResources().getStringArray(R.array.tab_name);
        initTab();
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

    public void clearStackGivenIndex(int index){
        mNavController.clearStackWithGivenIndex(index);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
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
            mNavController.popFragment(transactionOptions);
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

    public void switchAndUpdateTabSelection(int position) {
        switchTab(position);
        updateTabSelection(position);
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

    public void updateTabSelection(int currentTab) {

        for (int i = 0; i < TABS.length; i++) {
            TabLayout.Tab selectedTab = bottomTabLayout.getTabAt(i);

            if (currentTab != i) {
                //selectedTab.getCustomView().setSelected(false);
                Objects.requireNonNull(selectedTab.getIcon()).setColorFilter(unSelectedTabColor, PorterDuff.Mode.SRC_IN);
            } else {
                //selectedTab.getCustomView().setSelected(true);
                Objects.requireNonNull(selectedTab.getIcon()).setColorFilter(selectedTabColor, PorterDuff.Mode.SRC_IN);
            }
        }
    }

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
    public void onTabTransaction(Fragment fragment, int index) {

    }

    @Override
    public Fragment getRootFragment(int index) {
        switch (index) {

            case TAB1:
                keypadFragment = new KeypadFragment();
                return keypadFragment;
            case FragNavController.TAB2:
                libraryFragment = new LibraryFragment();
                return libraryFragment;

        }
        throw new IllegalStateException("Need to send an index that we know");
    }

    @Override
    public void onFragmentTransaction(Fragment fragment, FragNavController.TransactionType transactionType) {

    }
}