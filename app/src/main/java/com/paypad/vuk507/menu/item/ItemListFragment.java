package com.paypad.vuk507.menu.item;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.enums.ItemsEnum;
import com.paypad.vuk507.menu.category.CategoryFragment;
import com.paypad.vuk507.model.Category;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.ShapeUtil;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

import static com.paypad.vuk507.constants.CustomConstants.LANGUAGE_TR;

public class ItemListFragment extends BaseFragment {

    private View mView;

    @BindView(R.id.listView)
    ListView listView;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.backImgv)
    ClickableImageView backImgv;

    private ArrayList<String> itemList = new ArrayList<>();
    private ArrayAdapter<String> listAdapter ;

    public ItemListFragment() {

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
            mView = inflater.inflate(R.layout.fragment_item_list, container, false);
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
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                jumpToRelatedFragmnet(i);
            }
        });

        backImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });
    }

    private void jumpToRelatedFragmnet(int i) {
        switch (i){
            case 0:
                //TODO - All items fragment
                break;
            case 1:
                mFragmentNavigation.pushFragment(new CategoryFragment());
                break;
            case 2:
                //TODO - Modifiers
                break;
            case 3:
                //TODO - Discounts
                break;
            case 4:
                //TODO - Units
                break;
                default:break;
        }
    }

    private void initVariables() {
        toolbarTitleTv.setText(getContext().getResources().getString(R.string.items));
        fillItems();
        listAdapter = new ArrayAdapter<>(
                Objects.requireNonNull(getActivity()),
                android.R.layout.simple_list_item_1,
                itemList);
        listView.setAdapter(listAdapter);
    }

    private void fillItems() {
        ItemsEnum[] values = ItemsEnum.values();
        if(CommonUtils.getLanguage().equals(LANGUAGE_TR)){
            for(ItemsEnum item : values)
                itemList.add(item.getLabelTr());
        }else
            for(ItemsEnum item : values)
                itemList.add(item.getLabelEn());
    }
}