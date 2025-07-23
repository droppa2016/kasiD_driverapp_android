package co.za.kasi.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
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


public class Alert extends DialogFragment {

    View view;
    private MaterialTextView message, tvtitle;
    private AppCompatButton exit, proceed;
    private Context context;

    public Alert(Context context) {
        this.context = context;
    }

    ActionAcallBack actionAcallBack;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = LayoutInflater.from(context).inflate(R.layout.alert_dialog_view,null);
        init();
        actionAcallBack = (ActionAcallBack) view.getContext();
        Window window = getDialog().getWindow();
        getDialog().setCancelable(false);
        window.setBackgroundDrawable(getContext().getDrawable(R.drawable.dialog_transparent_backgroudn));

        return view;
    }

    private void init() {

        String context = getArguments().getString("message");
        String title = getArguments().getString("title");
        String type = getArguments().getString("type");
        message = view.findViewById(R.id.alertMessage);
        tvtitle = view.findViewById(R.id.alertTiitle);
        exit = view.findViewById(R.id.btnExit);
        proceed = view.findViewById(R.id.btnContinue);
        message.setText(context);

        tvtitle.setText(title);

        exit.setOnClickListener(v -> {
            actionAcallBack.positivePress(v);
            dismiss();
        });
        proceed.setOnClickListener(v -> {
            actionAcallBack.negativePress(v);
            dismiss();
        });

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

    public interface ActionAcallBack{
        void positivePress(View view);
        void negativePress(View view);
    }

}
