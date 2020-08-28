package com.paypad.parator;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.tabs.TabLayout;
import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.FragmentControllers.FragNavController;
import com.paypad.parator.FragmentControllers.FragNavTransactionOptions;
import com.paypad.parator.FragmentControllers.FragmentHistory;
import com.paypad.parator.charge.ChargeFragment;

import java.util.Objects;

import butterknife.ButterKnife;

import static com.paypad.parator.FragmentControllers.FragNavController.TAB1;
import static com.paypad.parator.constants.AnimationConstants.ANIMATE_DOWN_TO_UP;
import static com.paypad.parator.constants.AnimationConstants.ANIMATE_LEFT_TO_RIGHT;
import static com.paypad.parator.constants.AnimationConstants.ANIMATE_RIGHT_TO_LEFT;
import static com.paypad.parator.constants.AnimationConstants.ANIMATE_UP_TO_DOWN;


public class MainActivity extends FragmentActivity implements
        BaseFragment.FragmentNavigation,
        FragNavController.TransactionListener,
        FragNavController.RootFragmentListener {

    private FrameLayout contentFrame;

    private TabLayout bottomTabLayout;

    private LinearLayout tabMainLayout;

    private int selectedTabColor, unSelectedTabColor;

    private String ANIMATION_TAG;

    private FragNavTransactionOptions transactionOptions;

    //public KeypadFragment keypadFragment;
    //public LibraryFragment libraryFragment;

    private ChargeFragment chargeFragment;

    private int[] mTabIconsSelected = {
            R.drawable.ic_keyboard_black_24dp,
            R.drawable.ic_library_books_black_24dp};

    private String[] TABS;

    private FragNavController mNavController;

    private FragmentHistory fragmentHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        unSelectedTabColor = this.getResources().getColor(R.color.DarkGray, null);
        selectedTabColor = this.getResources().getColor(R.color.Black, null);

        initValues();

        fragmentHistory = new FragmentHistory();

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