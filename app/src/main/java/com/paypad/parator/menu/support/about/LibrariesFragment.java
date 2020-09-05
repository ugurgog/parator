package com.paypad.parator.menu.support.about;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.MainActivity;
import com.paypad.parator.R;
import com.paypad.parator.enums.LibrariesEnum;
import com.paypad.parator.enums.SupportListEnum;
import com.paypad.parator.enums.TutorialTypeEnum;
import com.paypad.parator.interfaces.LibraryListItemCallback;
import com.paypad.parator.interfaces.SupportListItemCallback;
import com.paypad.parator.interfaces.TutorialSelectedCallback;
import com.paypad.parator.menu.support.reportproblem.fragments.NotifyProblemFragment;
import com.paypad.parator.menu.support.toursandtutorials.ToursAndTutorialsFragment;
import com.paypad.parator.utils.ClickableImage.ClickableImageView;
import com.paypad.parator.utils.CommonUtils;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.paypad.parator.constants.CustomConstants.LANGUAGE_TR;

public class LibrariesFragment extends BaseFragment implements LibraryListItemCallback {

    private View mView;

    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.backImgv)
    ClickableImageView backImgv;
    @BindView(R.id.itemsRv)
    RecyclerView itemsRv;

    private Context mContext;

    public LibrariesFragment() {

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_libraries, container, false);
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
        backImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });
    }

    private void initVariables() {
        toolbarTitleTv.setText(mContext.getResources().getString(R.string.libraries));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        itemsRv.setLayoutManager(linearLayoutManager);
        setAdapter();
    }

    private void setAdapter() {
        LibraryListAdapter itemAdapter = new LibraryListAdapter();
        itemAdapter.setLibraryListItemCallback(this);
        itemsRv.setAdapter(itemAdapter);
    }

    @Override
    public void OnItemReturn(LibrariesEnum itemType) {
        if(itemType.getLink() != null && !itemType.getLink().isEmpty()){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(itemType.getLink()));
            startActivity(browserIntent);
        }
    }

    public class LibraryListAdapter extends RecyclerView.Adapter<LibraryListAdapter.ItemHolder> {

        private LibrariesEnum[] values;
        private LibraryListItemCallback libraryListItemCallback;

        LibraryListAdapter() {
            this.values = LibrariesEnum.values();
        }

        public void setLibraryListItemCallback(LibraryListItemCallback libraryListItemCallback) {
            this.libraryListItemCallback = libraryListItemCallback;
        }

        @NonNull
        @Override
        public LibraryListAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView;
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_support_library_item, parent, false);
            return new LibraryListAdapter.ItemHolder(itemView);
        }

        public class ItemHolder extends RecyclerView.ViewHolder {

            private CardView itemCv;
            private TextView nameTv;
            private TextView ownerTv;
            private LibrariesEnum itemType;
            private int position;

            public ItemHolder(View view) {
                super(view);
                itemCv = view.findViewById(R.id.itemCv);
                nameTv = view.findViewById(R.id.nameTv);
                ownerTv = view.findViewById(R.id.ownerTv);

                itemCv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        libraryListItemCallback.OnItemReturn(itemType);
                    }
                });
            }

            public void setData(LibrariesEnum itemType, int position) {
                this.itemType = itemType;
                this.position = position;

                if(itemType.isTitle()){
                    nameTv.setText(itemType.getLabel());
                    ownerTv.setText("");
                }else {
                    nameTv.setText("   ".concat(itemType.getLabel()));
                    ownerTv.setText(itemType.getOwner());
                }
            }
        }

        @Override
        public void onBindViewHolder(final LibraryListAdapter.ItemHolder holder, final int position) {
            LibrariesEnum itemType = values[position];
            holder.setData(itemType, position);
        }

        @Override
        public int getItemCount() {
            if(values != null)
                return values.length;
            else
                return 0;
        }
    }
}