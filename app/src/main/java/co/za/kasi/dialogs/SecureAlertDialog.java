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
import co.za.kasi.enums.AccountStatus;

public class SecureAlertDialog extends DialogFragment {
    private MaterialTextView message;
    private AppCompatButton exit, proceed;
    View view;

    public interface MyClickListener {
        void onSignOutListener(String tag);
        void onClickListener();
    }

    private MyClickListener listener;

    //private CardDetailsListener cardDetailsListener;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = getActivity().getLayoutInflater().inflate(R.layout.secure_alert_layout, null);
        Window window = getDialog().getWindow();
        getDialog().setCancelable(false);
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
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

        String error = getArguments().getString("error");

        String type = getArguments().getString("type");

        message = view.findViewById(R.id.alertMessage);
        exit = view.findViewById(R.id.btnExit);
        proceed = view.findViewById(R.id.btnContinue);
        message.setText(error);

        if(type.equalsIgnoreCase(AccountStatus.EXIT_APP.name())){
            proceed.setVisibility(View.GONE);
        }

        exit.setOnClickListener(v -> {
            listener.onSignOutListener(type);
            dismiss();
        });
        proceed.setOnClickListener(v -> {
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
