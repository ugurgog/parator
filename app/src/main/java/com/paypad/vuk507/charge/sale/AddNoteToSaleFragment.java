package com.paypad.vuk507.charge.sale;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.charge.interfaces.OnKeyboardVisibilityListener;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CommonUtils;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddNoteToSaleFragment extends BaseFragment  implements OnKeyboardVisibilityListener {

    View mView;

    @BindView(R.id.noteEt)
    EditText noteEt;
    @BindView(R.id.cancelImgv)
    ClickableImageView cancelImgv;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.saveBtn)
    Button saveBtn;
    @BindView(R.id.mainll)
    LinearLayout mainll;

    private NoteCallback noteCallback;
    private String note;
    private boolean closedClicked;
    private boolean keyboardVisibility;

    @Override
    public void onVisibilityChanged(boolean visible) {
        keyboardVisibility = visible;
        if(!visible && closedClicked)
            Objects.requireNonNull(getActivity()).onBackPressed();
    }

    public interface NoteCallback{
        void onNoteReturn(String note);
    }

    public AddNoteToSaleFragment(String note, NoteCallback noteCallback) {
        this.note = note;
        this.noteCallback = noteCallback;
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_add_note, container, false);
        ButterKnife.bind(this, mView);
        initVariables();
        initListeners();
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    private void initVariables() {
        toolbarTitleTv.setText(getResources().getString(R.string.add_note));
        saveBtn.setText(getResources().getString(R.string.done));
        setKeyboardVisibilityListener(this);

        if(note != null && !note.isEmpty()){
            noteEt.setText(note);
        }
    }

    private void initListeners() {
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closedClicked = true;
                noteCallback.onNoteReturn(noteEt.getText().toString());

                if(!keyboardVisibility)
                    Objects.requireNonNull(getActivity()).onBackPressed();
                else
                    CommonUtils.hideKeyBoard(Objects.requireNonNull(getContext()));
            }
        });

        cancelImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closedClicked = true;

                if(!keyboardVisibility)
                    Objects.requireNonNull(getActivity()).onBackPressed();
                else
                    CommonUtils.hideKeyBoard(Objects.requireNonNull(getContext()));
            }
        });
    }

    private void setKeyboardVisibilityListener(final OnKeyboardVisibilityListener onKeyboardVisibilityListener) {
        final View parentView = ((ViewGroup) mView.findViewById(R.id.mainll)).getChildAt(0);
        parentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            private boolean alreadyOpen;
            private final int defaultKeyboardHeightDP = 100;
            private final int EstimatedKeyboardDP = defaultKeyboardHeightDP + 48;
            private final Rect rect = new Rect();

            @Override
            public void onGlobalLayout() {
                int estimatedKeyboardHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, EstimatedKeyboardDP, parentView.getResources().getDisplayMetrics());
                parentView.getWindowVisibleDisplayFrame(rect);
                int heightDiff = parentView.getRootView().getHeight() - (rect.bottom - rect.top);
                boolean isShown = heightDiff >= estimatedKeyboardHeight;

                if (isShown == alreadyOpen) {
                    return;
                }
                alreadyOpen = isShown;
                onKeyboardVisibilityListener.onVisibilityChanged(isShown);
            }
        });
    }

}