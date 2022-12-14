package com.paypad.parator.menu.reports.saleReport;

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

import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.menu.reports.saleReport.adapter.TopCategoryAdapter;
import com.paypad.parator.model.pojo.ReportModel;
import com.paypad.parator.model.pojo.ReportOrderItem;
import com.paypad.parator.utils.ShapeUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SalesTopCategoriesFragment extends BaseFragment {

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
    private TopCategoryAdapter topCategoryAdapter;
    private List<TopCategory> topCategories;
    private List<TopCategory> adapterTopCategories;

    private static final int TOP_ITEM_MAX_COUNT = 5;

    private static final int SHOW_MORE = 0;
    private static final int SHOW_LESS = 1;

    private int showMoreLess = SHOW_LESS;

    public SalesTopCategoriesFragment(ReportModel reportModel) {
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

        fillTopCategoriesList();
    }

    private void fillTopCategoriesList(){
        topCategories = new ArrayList<>();

        for (Map.Entry<String, List<ReportOrderItem>> entry : reportModel.getReportOrderItems().entrySet()) {

            List<ReportOrderItem> reportOrderItems = entry.getValue();
            String categoryName = entry.getKey();

            double totalGrossAmount = 0d;
            long totalSaleCount = 0;
            for(ReportOrderItem reportOrderItem : reportOrderItems){
                totalGrossAmount = totalGrossAmount + reportOrderItem.getGrossAmount();
                totalSaleCount = totalSaleCount + reportOrderItem.getSaleCount();
            }

            TopCategory topCategory = new TopCategory(categoryName, reportOrderItems);
            topCategory.setTotalGrossAmount(totalGrossAmount);
            topCategory.setTotalSaleCount(totalSaleCount);
            topCategories.add(topCategory);
        }

        Collections.sort(topCategories, new SaleCountComparator());

        setItems();
    }

    private void setItems(){

        if(topCategories.size() <= TOP_ITEM_MAX_COUNT)
            showMoreTv.setVisibility(View.GONE);

        if(topCategories.size() <= TOP_ITEM_MAX_COUNT)
            adapterTopCategories = topCategories;
        else {
            if(showMoreLess == SHOW_LESS)
                adapterTopCategories = topCategories.subList(0, TOP_ITEM_MAX_COUNT);
            else
                adapterTopCategories = topCategories;
        }
        updateAdapter();
    }

    private void updateAdapter() {
        topCategoryAdapter = new TopCategoryAdapter(getContext(), adapterTopCategories, isGrossSelected);
        topItemsRv.setAdapter(topCategoryAdapter);
    }

    public static class SaleCountComparator implements Comparator<TopCategory> {
        @Override
        public int compare(TopCategory o1, TopCategory o2) {
            return (int) (o2.getTotalSaleCount() - o1.getTotalSaleCount());
        }
    }
}