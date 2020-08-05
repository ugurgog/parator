package com.paypad.vuk507.menu.transactions.unused;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.model.Transaction;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IssueRefundFragment extends BaseFragment{

    private View mView;

    @BindView(R.id.cancelImgv)
    ClickableImageView cancelImgv;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.saveBtn)
    Button saveBtn;

    @BindView(R.id.issueTablayout)
    TabLayout issueTablayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    private Transaction mTransaction;

    private IssueItemsFragment issueItemsFragment;
    private IssueAmountFragment issueAmountFragment;

    public IssueRefundFragment(Transaction transactions) {
        mTransaction = transactions;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_issue_refund, container, false);
            ButterKnife.bind(this, mView);
            initVariables();
            initListeners();
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    private void initListeners() {
        cancelImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });
    }

    private void initVariables() {
        toolbarTitleTv.setText(Objects.requireNonNull(getContext()).getResources().getString(R.string.order_refund));
        saveBtn.setText(getContext().getResources().getString(R.string.continue_text));
        setUpPager();
    }

    private void setUpPager() {
        /*setupViewPager();
        tablayout.setupWithViewPager(htab_viewpager);
        setupTabIcons();
        setTabListener();*/
    }


    private void setupViewPager() {
        /*keypadFragment = new KeypadFragment();
        keypadFragment.setSaleCalculateCallback(this);
        keypadFragment.setPaymentStatusCallback(this);
        keypadFragment.setReturnViewCallback(this);

        libraryFragment = new LibraryFragment();
        libraryFragment.setSaleCalculateCallback(this);
        libraryFragment.setReturnViewCallback(this);

        chargeViewPagerAdapter = new CustomViewPagerAdapter(getFragmentManager());
        chargeViewPagerAdapter.addFrag(keypadFragment, getContext().getResources().getString(R.string.keypad));
        chargeViewPagerAdapter.addFrag(libraryFragment, getContext().getResources().getString(R.string.library));
        htab_viewpager.setAdapter(chargeViewPagerAdapter);*/
    }


}