package co.za.kasi.dialogs;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.textview.MaterialTextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


import co.za.kasi.R;
import co.za.kasi.model.BucketBooking;
import co.za.kasi.model.accountDTO.DriverDTO;
import co.za.kasi.model.accountDTO.UserDTO;
import co.za.kasi.services.DriverHttpService;
import co.za.kasi.services.LocalStorage;
import co.za.kasi.utils.CameraSurfaceView;
import co.za.kasi.utils.ReusableFunctions;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanPODDialog extends DialogFragment {

    private AppCompatImageButton btnBackButton, btnWhatsappButton;
    private MaterialTextView title;
    View view;
    Toolbar toolbar;

    private UserDTO user;
    private DriverDTO driver;
    private String token;
    private CardView crdCaptureParcel;

    private SurfaceView surfaceView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    //This class provides methods to play DTMF tones
    private ToneGenerator toneGen1;
    private String barcodeData;

    private LinearLayout onPreview;
    private AppCompatButton retry, upload;
    private String path = null;
    private boolean scanned = false;

    private DriverHttpService services;

    private CameraSurfaceView cameraSurfaceView;
    private Loader loader;

    ReturnStatus returnStatus;


    ArrayList<String> bucketBookings;
    ArrayList<String> scannedBucketBookings;
    ArrayList<String> canAddParcelBookings;

    //private CardDetailsListener cardDetailsListener;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = getActivity().getLayoutInflater().inflate(R.layout.scanning_parcels, container, true);
        setStyle(DialogFragment.STYLE_NORMAL,
                android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        Window window = getDialog().getWindow();
        getDialog().setCancelable(false);
        window.setBackgroundDrawable(getContext().getDrawable(R.drawable.dialog_transparent_backgroudn));
        //returnStatus = (ReturnStatus) view.getContext();
        init();

//        activityType = getArguments().getString("activityType");
//        bookingOid = getArguments().getString("bookingOid");
//        transportType = getArguments().getString("transportType");

        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    // Handle the back button press here
                    dismiss();
                    getActivity().recreate();
                    return true; // Consume the event
                }
                return false; // Pass the event to the next listener
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Close the camera
        Dialog dialog = getDialog();
                if (dialog != null) {
                    int width = ViewGroup.LayoutParams.MATCH_PARENT;
                    int height = ViewGroup.LayoutParams.MATCH_PARENT;
                    dialog.getWindow().setLayout(width, height);
                }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Open the camera
        cameraSurfaceView.closeCamera();
    }

    private void init() {

        services = ReusableFunctions.initiateRetrofit(getContext());
        toolbar = view.findViewById(R.id.custom_toolbar);
        // Set a custom close icon
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custon_action_bar);
        actionBar.show();
        title = view.findViewById(R.id.txtTitle);
        btnBackButton = view.findViewById(R.id.btnBack);
        btnWhatsappButton = view.findViewById(R.id.btnWhatsapp);

        surfaceView = view.findViewById(R.id.surface_view);
        retry = view.findViewById(R.id.btnRetry);
        upload = view.findViewById(R.id.btnUpload);
        onPreview = view.findViewById(R.id.lytonPreview);
        crdCaptureParcel = view.findViewById(R.id.crdCaptureParcel);
        getLocalData();
        cameraSurfaceView = view.findViewById(R.id.cameraSurfaceView);
        cameraSurfaceView.setWaybill("contactList.toString()");

//        cameraSurfaceView.openCamera();
        
        title.setText("Scan Parcels");
        btnBackButton.setOnClickListener(v -> {
            //cameraSurfaceView.closeCamera();
            dismiss();
        });


        cameraSurfaceView.setOnImageCapturedListener(new CameraSurfaceView.OnImageCapturedListener() {
            @Override
            public void onImageCaptured(Uri imageUri) {
                path = imageUri.getEncodedPath();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onPreview.setVisibility(View.VISIBLE);
                        crdCaptureParcel.setVisibility(View.GONE);
                    }
                });
            }
        });


        crdCaptureParcel.setOnClickListener(v -> {
            cameraSurfaceView.captureImage();
        });

        retry.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            onPreview.setVisibility(View.GONE);
            crdCaptureParcel.setVisibility(View.VISIBLE);
            cameraSurfaceView.closeCamera();
            cameraSurfaceView.openCamera();

        });

        upload.setOnClickListener(v -> {
            loader =ReusableFunctions.showLoader(getContext());
            uploadImage(path);
        });

    }



    /*==========================================================*/

    private void uploadImage(String uri) {
        try {

            File file = new File(uri);
            Bitmap bitmap = MediaStore.Images.Media
                    .getBitmap(getContext().getContentResolver(), Uri.fromFile(file));

            if (bitmap == null) {
                Log.d("SELECTED_RETURN", "============= /  // " + "An unknown problem occurred while taking an image.");
                ReusableFunctions.hideLoader(loader);
                return;
            }
            // myBitmap = getResizedBitmap(myBitmap, 500);


            File imageFile = new File(uri);

            Log.e("SELECTED_RETURN", "********************* This is the path ****************" + imageFile.canRead());
            RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), imageFile);
            // Create MultipartBody.Part using file request-body,file name and part name
            MultipartBody.Part part = MultipartBody.Part.createFormData("invoiceImage", "delivery-note.jpg", fileReqBody);
            //Create request body with text description and text media type
            RequestBody bookingOid = RequestBody.create(MediaType.parse("text/plain"), "currentBooking.getOid()");
            RequestBody bucketOid = RequestBody.create(MediaType.parse("text/plain"), "bucket.getOid()");
            RequestBody returnComment = RequestBody.create(MediaType.parse("text/plain"), "returnCommentValue");

            // RequestBody returnItems;

            Log.e("SELECTED_RETURN", "BEFORE SENDING Return item value: " + "isReturnItems");
            Log.e("SELECTED_RETURN", "BEFORE SENDING Return item value: " + "isReturnItems   =    " + token);
            Log.e("SELECTED_RETURN", "RETURN_COMMENT FROM DRIVER: " + returnComment.toString());
            //Sending the image here
            RequestBody returnItems = RequestBody.create(MediaType.parse("text/plain"), String.valueOf("isReturnItems"));
            Call<BucketBooking> call = services.uploadImage(token, part, bookingOid, bucketOid, returnItems, returnComment);

            Log.e("SELECTED_RETURN", "AFTER SENDING Return item value: " + "isReturnItems");

            call.enqueue(new Callback<BucketBooking>() {
                @Override
                public void onResponse(@NonNull Call<BucketBooking> call, @NonNull Response<BucketBooking> response) {

                    ReusableFunctions.hideLoader(loader);
                    if (response.code() == 200) {
                        showPopupDialog();
                        scanned = true;
                        Log.e("SELECTED_RETURN", "=======Ask if theres another POD then=========Open camera again==========");
                        cameraSurfaceView.openCamera();
                    } else {

                        showPopupDialog();
                        scanned = false;
                        Log.e("SELECTED_RETURN_", "================  " + response.code() + " An unknown problem occurred while completing booking " + response.message());
                        try {
                            Log.e("SELECTED_RETURN_", "================  " + response.errorBody().string() + " An unknown problem occurred while completing booking " + response.message());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        // snackBar("An unknown problem occurred while completing booking");
                    }
                }

                @Override
                public void onFailure(Call<BucketBooking> call, Throwable t) {

                    ReusableFunctions.hideLoader(loader);
                    Log.e("SELECTED_RETURN", "========= An unexpected problem occurred while completing booking =======" + t.getLocalizedMessage(), t);
                    // snackBar("An unexpected problem occurred while completing booking");
                }
            });

        } catch (Exception e) {

            ReusableFunctions.hideLoader(loader);
            e.getStackTrace();
            Log.e("SELECTED_RETURN", "Hello -> " + e.getMessage(), e);
        }
    }

    private void getLocalData() {
        user = LocalStorage.getUserAccount();
        driver = LocalStorage.getDriverAccount();
        token = LocalStorage.getToken(getContext());

        Log.e("SELECTED_RETURN", "BEFORE SENDING Return item value: " + "isReturnItems   =    " + token);
    }

    private void showPopupDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Upload was a success");
        builder.setMessage("Your Proof of delivery was is uploaded successfully, Would you like to capture another one?");
        // Positive button
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle positive button click
                cameraSurfaceView.openCamera();
                onPreview.setVisibility(View.GONE);
                crdCaptureParcel.setVisibility(View.VISIBLE);
                dialog.dismiss(); // Dismiss the dialog
            }
        });

        // Negative button
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                returnStatus.getStatus(scanned);
                dismiss();
                dialog.dismiss(); // Dismiss the dialog
            }
        });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public interface ReturnStatus{
        void getStatus(boolean status);
    }
}
