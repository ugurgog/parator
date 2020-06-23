package com.paypad.vuk507.charge.sale;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.charge.interfaces.AmountCallback;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.model.Product;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.uiUtils.keypad.KeyPadClick;
import com.paypad.vuk507.uiUtils.keypad.KeyPadSingleNumberListener;
import com.paypad.vuk507.uiUtils.keypad.KeyPadWithoutAdd;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.DataUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddNoteToSaleFragment extends BaseFragment {

    View mView;

    @BindView(R.id.noteEt)
    EditText noteEt;
    @BindView(R.id.cancelImgv)
    ClickableImageView cancelImgv;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.saveBtn)
    Button saveBtn;

    private NoteCallback noteCallback;
    private String note;

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

        if(note != null && !note.isEmpty()){
            noteEt.setText(note);
        }
    }

    private void initListeners() {
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteCallback.onNoteReturn(noteEt.getText().toString());
                CommonUtils.hideKeyBoard(Objects.requireNonNull(getContext()));
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });

        cancelImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonUtils.hideKeyBoard(Objects.requireNonNull(getContext()));
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });
    }
}