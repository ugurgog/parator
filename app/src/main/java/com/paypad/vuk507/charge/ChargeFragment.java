package com.paypad.vuk507.charge;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;
import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.MainActivity;
import com.paypad.vuk507.R;
import com.paypad.vuk507.adapter.ChargePagerAdapter;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.login.InitialActivity;
import com.paypad.vuk507.login.utils.LoginUtils;
import com.paypad.vuk507.menu.item.ItemListFragment;
import com.paypad.vuk507.model.BaseResponse;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.utils.CommonUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import devlight.io.library.ntb.NavigationTabBar;

public class ChargeFragment extends BaseFragment {

    View mView;

    @BindView(R.id.ntb_horizontal)
    NavigationTabBar navigationTabBar;
    @BindView(R.id.htab_viewpager)
    ViewPager htab_viewpager;
    @BindView(R.id.settingsImgv) 
    ImageView settingsImgv;

    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.navViewLayout)
    NavigationView navViewLayout;

    private static final int TAB_KEYPAD = 0;
    private static final int TAB_LIBRARY = 1;

    private int selectedTabPosition = TAB_KEYPAD;
    private boolean mDrawerState;

    private ChargePagerAdapter chargePagerAdapter;
    private User user;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(sticky = true)
    public void accountHolderUserReceived(UserBus userBus){
        user = userBus.getUser();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_charge, container, false);
            ButterKnife.bind(this, mView);
            initNavigationBar();
            setUpPager();
            setDrawerListeners();
        }
        return mView;
    }

    private void initNavigationBar(){
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_keyboard_black_24dp, null),
                        Color.parseColor("#d1395c"))
                        .title(Objects.requireNonNull(getContext()).getResources().getString(R.string.keypad))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_library_books_black_24dp, null),
                        Color.parseColor("#FF861F"))
                        .title(getContext().getResources().getString(R.string.library))
                        .build()
        );

        navigationTabBar.setModels(models);
    }

    private void setUpPager() {
        chargePagerAdapter = new ChargePagerAdapter(getFragmentManager(), 2);
        htab_viewpager.setAdapter(chargePagerAdapter);
        //htab_viewpager.setPageTransformer(true, new RotateUpTransformer());
        navigationTabBar.setViewPager(htab_viewpager, 0);
        setTabListener();
    }

    private void setTabListener() {

        navigationTabBar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
                //viewPager.setCurrentItem(position);
                selectedTabPosition = position;
            }

            @Override
            public void onPageSelected(final int position) {

            }

            @Override
            public void onPageScrollStateChanged(final int state) {

            }
        });
    }

    public int getSelectedTabPosition() {
        return selectedTabPosition;
    }

    public void setDrawerListeners() {
        drawerLayout.addDrawerListener(new ActionBarDrawerToggle(getActivity(),
                drawerLayout,
                null,
                0,
                0) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                mDrawerState = false;
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mDrawerState = true;
            }
        });

        settingsImgv.setOnClickListener(v -> {
            settingsImgv.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));
            if (mDrawerState) {
                drawerLayout.closeDrawer(Gravity.LEFT);
            } else {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        navViewLayout.setNavigationItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.checkout:
                    drawerLayout.closeDrawer(Gravity.LEFT);

                    boolean commit = LoginUtils.deleteSharedPreferences(getContext());

                    if(commit){
                        UserDBHelper.updateUserLoggedInStatus(user.getUsername(), false, new CompleteCallback() {
                            @Override
                            public void onComplete(BaseResponse baseResponse) {
                                if(baseResponse.isSuccess()){
                                    Objects.requireNonNull(getActivity()).finish();
                                    startActivity(new Intent(getActivity(), InitialActivity.class));
                                }else
                                    CommonUtils.showToastShort(getContext(), baseResponse.getMessage());
                            }
                        });
                    }else
                        CommonUtils.showToastShort(getContext(), "cache delete error!");
                    break;

                case R.id.transactions:
                    drawerLayout.closeDrawer(Gravity.LEFT);
                    startTransactionsFragment();
                    break;

                case R.id.reports:
                    drawerLayout.closeDrawer(Gravity.LEFT);
                    startReportsFragment();
                    break;

                case R.id.customers:
                    drawerLayout.closeDrawer(Gravity.LEFT);
                    startCustomersFragment();
                    break;

                case R.id.items:
                    drawerLayout.closeDrawer(Gravity.LEFT);
                    startItemsFragment();
                    break;

                case R.id.settings:
                    drawerLayout.closeDrawer(Gravity.LEFT);
                    startSettingsFragment();
                    break;

                case R.id.support:
                    drawerLayout.closeDrawer(Gravity.LEFT);
                    startSupportFragment();
                    break;

                case R.id.notifications:
                    drawerLayout.closeDrawer(Gravity.LEFT);
                    startNotificationsFragment();
                    break;


                default:
                    break;
            }

            return false;
        });

    }

    private void startNotificationsFragment() {
    }

    private void startSupportFragment() {
    }

    private void startSettingsFragment() {
    }

    private void startItemsFragment() {
        mFragmentNavigation.pushFragment(new ItemListFragment());
    }

    private void startCustomersFragment() {
    }

    private void startReportsFragment() {
    }

    private void startTransactionsFragment() {
    }
}