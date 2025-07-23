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

import co.za.kasi.R;


public class NotificationPermissionDialog extends DialogFragment {
    private AppCompatButton allow, dismiss;
    View view;

    public interface MyClickListener {
        void onAllow(AppCompatButton allow);
        void onDismiss(AppCompatButton allow);
    }


    private MyClickListener listener;

    //private CardDetailsListener cardDetailsListener;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = getActivity().getLayoutInflater().inflate(R.layout.notification_permission_layout, null);
        Window window = getDialog().getWindow();
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
        allow = view.findViewById(R.id.btnAllow)   ;
        dismiss = view.findViewById(R.id.btnDismiss);

        allow.setOnClickListener(v -> {
            listener.onAllow(dismiss);
            dismiss();
        });
        dismiss.setOnClickListener(v -> {
            listener.onDismiss(dismiss);
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
