package com.paypad.vuk507.menu.category;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.db.CategoryDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.interfaces.ReturnSizeCallback;
import com.paypad.vuk507.menu.category.interfaces.ReturnCategoryCallback;
import com.paypad.vuk507.model.Category;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.ShapeUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class CategorySelectFragment extends BaseFragment {

    private View mView;

    @BindView(R.id.backImgv)
    ClickableImageView backImgv;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.searchEdittext)
    EditText searchEdittext;
    @BindView(R.id.addItemImgv)
    ImageView addItemImgv;
    @BindView(R.id.searchCancelImgv)
    ImageView searchCancelImgv;
    @BindView(R.id.searchResultTv)
    TextView searchResultTv;
    @BindView(R.id.createCategoryBtn)
    Button createCategoryBtn;

    @BindView(R.id.categoryRv)
    RecyclerView categoryRv;

    private CategorySelectListAdapter categorySelectListAdapter;

    private Realm realm;

    private RealmResults<Category> categories;
    private List<Category> categoryList;
    private User user;
    private ReturnCategoryCallback returnCategoryCallback;

    public CategorySelectFragment(ReturnCategoryCallback returnCategoryCallback) {
        this.returnCategoryCallback = returnCategoryCallback;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

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
        if(user == null)
            user = UserDBHelper.getUserFromCache(getContext());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_category, container, false);
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
        backImgv.setOnClickListener(view -> Objects.requireNonNull(getActivity()).onBackPressed());

        createCategoryBtn.setOnClickListener(view -> mFragmentNavigation.pushFragment(new CategoryEditFragment(null, new ReturnCategoryCallback() {
            @Override
            public void OnReturn(Category category) {
                updateAdapterWithCurrentList();
            }
        })));

        searchEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && !s.toString().trim().isEmpty()) {
                    updateAdapter(s.toString());
                    searchCancelImgv.setVisibility(View.VISIBLE);
                } else {
                    updateAdapter("");
                    searchCancelImgv.setVisibility(View.GONE);
                }
            }
        });

        searchCancelImgv.setOnClickListener(v -> {
            searchEdittext.setText("");
            searchCancelImgv.setVisibility(View.GONE);
            CommonUtils.showKeyboard(getContext(),false, searchEdittext);
        });
    }

    private void initVariables() {
        realm = Realm.getDefaultInstance();
        toolbarTitleTv.setText(Objects.requireNonNull(getContext()).getResources().getString(R.string.categories));
        addItemImgv.setVisibility(View.GONE);
        setShapes();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        categoryRv.setLayoutManager(linearLayoutManager);
        categoryRv.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getContext()), LinearLayoutManager.VERTICAL));
        updateAdapterWithCurrentList();
    }

    public void updateAdapterWithCurrentList(){
        categories = CategoryDBHelper.getAllCategories(user.getUsername());
        categoryList = new ArrayList(categories);
        categorySelectListAdapter = new CategorySelectListAdapter(getContext(), categoryList, mFragmentNavigation, new ReturnCategoryCallback() {
            @Override
            public void OnReturn(Category category) {
                returnCategoryCallback.OnReturn(category);
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });
        categoryRv.setAdapter(categorySelectListAdapter);
    }

    private void setShapes() {
        createCategoryBtn.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.White, null),
                getResources().getColor(R.color.DodgerBlue, null), GradientDrawable.RECTANGLE, 20, 2));
    }

    public void updateAdapter(String searchText) {
        if (searchText != null && categorySelectListAdapter != null) {
            categorySelectListAdapter.updateAdapter(searchText, new ReturnSizeCallback() {
                @Override
                public void OnReturn(int size) {
                    if (size == 0 && categoryList.size() > 0)
                        searchResultTv.setVisibility(View.VISIBLE);
                    else
                        searchResultTv.setVisibility(View.GONE);
                }
            });
        }
    }
}