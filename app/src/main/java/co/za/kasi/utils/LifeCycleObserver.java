package co.za.kasi.utils;

import android.net.Uri;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

public class LifeCycleObserver implements DefaultLifecycleObserver {

    private final ActivityResultRegistry mRegistry;

    private ActivityResultLauncher<String> mGetContent;

  public LifeCycleObserver(@NonNull ActivityResultRegistry mRegistry) {
        this.mRegistry = mRegistry;
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {

      mGetContent = mRegistry.register("key", owner, new ActivityResultContracts.GetContent(),
              new ActivityResultCallback<Uri>() {
                  @Override
                  public void onActivityResult(Uri o) {

                  }
              });
    }

    public void selectImage() {
        // Open the activity to select an image
        mGetContent.launch("image/*");
    }
}
