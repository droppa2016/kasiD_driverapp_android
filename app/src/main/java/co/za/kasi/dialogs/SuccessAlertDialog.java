package co.za.kasi.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textview.MaterialTextView;

import co.za.kasi.R;


public class SuccessAlertDialog extends DialogFragment {
    private MaterialTextView message;
    private AppCompatButton  btnOk;
    View view;

    public interface MyClickListener {
        void onClickListener();
    }


    private MyClickListener listener;

    //private CardDetailsListener cardDetailsListener;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = getActivity().getLayoutInflater().inflate(R.layout.layout_success_dialog, null);
        Window window = getDialog().getWindow();
        getDialog().setCancelable(false);
        if (window != null) {
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawable(getContext().getDrawable(R.drawable.dialog_transparent_backgroudn));
        }

        init();


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    private void init() {

//        String error = getArguments().getString("message");
        message = view.findViewById(R.id.alertMessage);
        btnOk = view.findViewById(R.id.btnContinue);
        //message.setText(error);

        btnOk.setOnClickListener(v -> {
            listener.onClickListener();
            dismiss();
        });

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (MyClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement MyDialogListener");
        }
    }
}
