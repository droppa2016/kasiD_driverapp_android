package co.za.kasi.dialogs;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.textview.MaterialTextView;


import co.za.kasi.R;
import co.za.kasi.utils.ProfilePictureSurfaceView;

public class CaptureProfileDialog extends DialogFragment {

    private static final int REQUEST_CAMERA_PERMISSION = 201;
    View view;
    Toolbar toolbar;
    private AppCompatImageButton btnBackButton, btnWhatsappButton;
    private MaterialTextView title;
    private CardView crdCaptureParcel, crdFlipCamera, crdSelectImage;

    private SurfaceView surfaceView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    // private static final int REQUEST_CAMERA_PERMISSION = 201;
    //This class provides methods to play DTMF tones
    private ToneGenerator toneGen1;
    private MaterialTextView barcodeText;
    private String barcodeData;

    private PhotoUri photoUri;
    private Uri imageRes = null;

    private ProfilePictureSurfaceView cameraSurfaceView;
    private AppCompatButton retry, upload;
    private LinearLayout onPreview;

    //private CardDetailsListener cardDetailsListener;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = getActivity().getLayoutInflater().inflate(R.layout.profile_capture, null);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        Window window = getDialog().getWindow();
        getDialog().setCancelable(false);
        window.setBackgroundDrawable(getContext().getDrawable(R.drawable.dialog_transparent_backgroudn));
        init();

        crdCaptureParcel.setOnClickListener(v -> {
            cameraSurfaceView.captureImage();
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    private void init() {

        toolbar = view.findViewById(R.id.custom_toolbar);

        btnBackButton = view.findViewById(R.id.btnBack);
        surfaceView = view.findViewById(R.id.surface_view);
        barcodeText = view.findViewById(R.id.barcode_text);
        crdCaptureParcel = view.findViewById(R.id.crdCaptureParcel);
        crdSelectImage = view.findViewById(R.id.crdSelectFromFiles);
        crdFlipCamera = view.findViewById(R.id.crdSwitchCamera);
        retry = view.findViewById(R.id.btnRetry);
        upload = view.findViewById(R.id.btnUpload);
        onPreview = view.findViewById(R.id.lytonPreview);
        crdFlipCamera.setVisibility(View.VISIBLE);
        crdSelectImage.setVisibility(View.VISIBLE);

        cameraSurfaceView = view.findViewById(R.id.cameraSurfaceView);

        btnBackButton.setOnClickListener(v ->
                dismiss());
//
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 1);

        }

        crdFlipCamera.setOnClickListener(v -> {
            cameraSurfaceView.switchCamera();
        });

        cameraSurfaceView.setOnImageCapturedListener(new ProfilePictureSurfaceView.OnImageCapturedListener() {
            @Override
            public void onImageCaptured(Uri imageUri) {
                imageRes = imageUri;
                Log.e("TAG_image_uri", "onImageCaptured: === " + imageUri);
                Log.d("TAG_image_uri", "onImageCaptured: === " + imageRes);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onPreview.setVisibility(View.VISIBLE);
                        crdCaptureParcel.setVisibility(View.GONE);
                        crdFlipCamera.setVisibility(View.GONE);
                        crdSelectImage.setVisibility(View.GONE);
                    }
                });
            }
        });

        retry.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            onPreview.setVisibility(View.GONE);
            crdCaptureParcel.setVisibility(View.VISIBLE);
            crdFlipCamera.setVisibility(View.VISIBLE);
            crdSelectImage.setVisibility(View.VISIBLE);
            cameraSurfaceView.closeCamera();
            cameraSurfaceView.openCamera();
        });

        upload.setOnClickListener(v -> {
            photoUri.photoUri(imageRes, getTag());
            dismiss();
        });
    }

    /*==========================================================*/

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        photoUri = (PhotoUri) context;
    }

    public void setOnReturnURI(PhotoUri uri) {
        this.photoUri = photoUri;
    }

    public interface PhotoUri {
        void returnPhotoUri(Uri uri, String tag);

        void photoUri(Uri uri, String tag);
    }
}
