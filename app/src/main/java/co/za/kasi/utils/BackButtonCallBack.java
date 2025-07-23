package co.za.kasi.utils;

import android.content.Context;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.activity.OnBackPressedDispatcherOwner;
import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;

public class BackButtonCallBack implements OnBackPressedDispatcherOwner {

    BackButtonPressedListener backButtonPressed;

    public void onBackButtonPress(Context context){
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                backButtonPressed = (BackButtonPressedListener) context;
                backButtonPressed.getBackPressed();
            }
        };

        getOnBackPressedDispatcher().addCallback(onBackPressedCallback);
    }

    @NonNull
    @Override
    public OnBackPressedDispatcher getOnBackPressedDispatcher() {
        return null;
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return null;
    }

    public interface BackButtonPressedListener{
        void getBackPressed();
    }
}
