package com.paypad.vuk507.menu.reports.saleReport;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.menu.reports.saleReport.adapter.TopItemsAdapter;
import com.paypad.vuk507.model.pojo.ReportModel;
import com.paypad.vuk507.model.pojo.ReportOrderItem;
import com.paypad.vuk507.utils.ShapeUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SalesTopItemsFragment extends BaseFragment {

    private View mView;

    @BindView(R.id.grossBtn)
    Button grossBtn;
    @BindView(R.id.countBtn)
    Button countBtn;
    @BindView(R.id.topItemsRv)
    RecyclerView topItemsRv;
    @BindView(R.id.showMoreTv)
    TextView showMoreTv;

    private ReportModel reportModel;

    private boolean isGrossSelected = true;
    private TopItemsAdapter topItemsAdapter;
    private List<ReportOrderItem> orderItems;

    private static final int TOP_ITEM_MAX_COUNT = 5;

    private static final int SHOW_MORE = 0;
    private static final int SHOW_LESS = 1;

    private int showMoreLess = SHOW_LESS;

    public SalesTopItemsFragment(ReportModel reportModel) {
        this.reportModel = reportModel;
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_top_items, container, false);
        ButterKnife.bind(this, mView);
        initVariables();
        initListeners();
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    private void initVariables() {
        setShapes();
        setAdapter();
    }

    private void initListeners() {
        grossBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isGrossSelected = true;
                setShapes();
                updateAdapter();
            }
        });

        countBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isGrossSelected = false;
                setShapes();
                updateAdapter();
            }
        });

        showMoreTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(showMoreLess == SHOW_LESS){
                    showMoreLess = SHOW_MORE;
                    showMoreTv.setText(getContext().getResources().getString(R.string.show_less));
                } else{
                    showMoreLess = SHOW_LESS;
                    showMoreTv.setText(getContext().getResources().getString(R.string.show_more));
                }

                setItems();
            }
        });
    }

    private void setShapes() {
        if(isGrossSelected){
            grossBtn.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.Black, null),
                    getResources().getColor(R.color.Black, null), GradientDrawable.RECTANGLE, 0, 2));
            countBtn.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.White, null),
                    getResources().getColor(R.color.Black, null), GradientDrawable.RECTANGLE, 0, 2));
            grossBtn.setTextColor(getResources().getColor(R.color.White, null));
            countBtn.setTextColor(getResources().getColor(R.color.Black, null));
        }else {
            grossBtn.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.White, null),
                    getResources().getColor(R.color.Black, null), GradientDrawable.RECTANGLE, 0, 2));
            countBtn.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.Black, null),
                    getResources().getColor(R.color.Black, null), GradientDrawable.RECTANGLE, 0, 2));
            grossBtn.setTextColor(getResources().getColor(R.color.Black, null));
            countBtn.setTextColor(getResources().getColor(R.color.White, null));
        }
    }

    private void setAdapter(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        topItemsRv.setLayoutManager(linearLayoutManager);
        Collections.sort(reportModel.getTopItems(), new SaleCountComparator());

        setItems();
    }

    private void setItems(){
        if(reportModel.getTopItems().size() <= TOP_ITEM_MAX_COUNT)
            showMoreTv.setVisibility(View.GONE);

        if(reportModel.getTopItems().size() <= TOP_ITEM_MAX_COUNT)
            orderItems = reportModel.getTopItems();
        else{
            if(showMoreLess == SHOW_LESS)
                orderItems = reportModel.getTopItems().subList(0, TOP_ITEM_MAX_COUNT);
            else
                orderItems = reportModel.getTopItems();
        }
        updateAdapter();
    }

    private void updateAdapter() {
        topItemsAdapter = new TopItemsAdapter(orderItems, isGrossSelected);
        topItemsRv.setAdapter(topItemsAdapter);
    }

    public static class SaleCountComparator implements Comparator<ReportOrderItem> {
        @Override
        public int compare(ReportOrderItem o1, ReportOrderItem o2) {
            return (int) (o2.getSaleCount() - o1.getSaleCount());
        }
    }

}