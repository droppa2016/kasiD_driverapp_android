package co.za.kasi.dialogs;

import static co.za.kasi.services.LocalStorage.context;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;


import co.za.kasi.R;
import co.za.kasi.utils.LifeCycleObserver;

public class PhotoChooserDialog extends AppCompatDialogFragment implements CaptureProfileDialog.PhotoUri {

    //private CardDetailsListener cardDetailsListener;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    int MY_PERMISSIONS_REQUEST_CAMERA = 0;
    private AppCompatButton uploadFromFile, openCamera;

    private LifeCycleObserver observer;
    private ActivityResultLauncher<Void> cameraLauncher;

    private PhotoDialogListener photoDialogListener;
    private Uri imageUri;

    /*ActivityResultLauncher<Intent> fileImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<>() {
        @Override
        public void onActivityResult(ActivityResult o) {

            if (o.getResultCode() == RESULT_OK) {
                assert o.getData() != null;
                imageUri = o.getData().getData();
                photoUri(imageUri, getTag());
            }


        }
    });


    public void openGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        fileImage.launch(intent);

    }*/


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.RoundedCornersDialog);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.photo_chooser_layout, null);

        observer = new LifeCycleObserver(requireActivity().getActivityResultRegistry());
        getLifecycle().addObserver(observer);

        uploadFromFile = view.findViewById(R.id.uploadFromFileBtn);
        openCamera = view.findViewById(R.id.goToCameraBtn);

        builder.setView(view).setCancelable(true);


        openCamera.setOnClickListener(v -> {

            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

                CaptureProfileDialog captureProfileDialog = new CaptureProfileDialog();
                captureProfileDialog.show(getActivity().getSupportFragmentManager(), "Capture driver profile");
                dismiss();

//                cameraSetUp();
//
//                Toast.makeText(getActivity(), "Permission has been granted", Toast.LENGTH_SHORT).show();

            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
            }

        });




        return builder.create();

    }

    public Uri getImageUri(Context inContent, Bitmap image) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), image, "image", null);

        return Uri.parse(path);

    }


    @Override
    public void returnPhotoUri(Uri uri, String tag) {

    }

    @Override
    public void photoUri(Uri uri, String tag) {

    }

    public interface PhotoDialogListener {

        void applyText(String tag);

    }


}
