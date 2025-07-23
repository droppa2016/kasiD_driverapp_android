package co.za.kasi.dialogs;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.textview.MaterialTextView;

import java.io.IOException;
import java.util.ArrayList;

import co.za.kasi.R;
import co.za.kasi.enums.BookingActivities;
import co.za.kasi.utils.AspectRatioSurfaceView;

public class ScanWaybilDialog extends DialogFragment {

    private AppCompatImageButton btnBackButton, btnWhatsappButton;
    private MaterialTextView title;
    View view;
    Toolbar toolbar;

    private AspectRatioSurfaceView surfaceView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    //This class provides methods to play DTMF tones
    private ToneGenerator toneGen1;
    private String barcodeData;
    private RelativeLayout surfaceBackGround;
    private ScanningListener scanningListener;

    //    private CardView crdFailed, crdSuccess;
    private LottieAnimationView lottieAnimationView;
    private MaterialTextView tvCounter, errorMessage;
    private MaterialTextView tvResults, tvStatus, tvTotal;
    private AppCompatButton done;

    ArrayList<String> bucketBookings;
    ArrayList<String> scannedBucketBookings;
    ArrayList<String> canAddParcelBookings;
    ArrayList<String> contactList;
    int bucketSize = 0, parcelNumber = 1;
    String activityType;
    String bookingOid;
    String bucketOid;

    private boolean scanned = false;

    private boolean isInbound = false;
    String transportType;
    private boolean isBucketPrioratized = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = getActivity().getLayoutInflater().inflate(R.layout.scanning_waybill, container, true);
        setStyle(DialogFragment.STYLE_NORMAL,
                android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        Window window = getDialog().getWindow();
        getDialog().setCancelable(false);
        window.setBackgroundDrawable(getContext().getDrawable(R.drawable.dialog_transparent_backgroudn));
        init();

        bucketBookings = new ArrayList<>();
        scannedBucketBookings = new ArrayList<>();
        canAddParcelBookings = new ArrayList<>();
        contactList = new ArrayList<>();
        isBucketPrioratized = getArguments().getBoolean("isBucketPrioratized", false);

        if (isBucketPrioratized) {
            contactList = getArguments().getStringArrayList("contactList");
            bucketSize = getArguments().getInt("bucketSize");
            isInbound = getArguments().getBoolean("inbound", false);
            activityType = getArguments().getString("activityType");
            bookingOid = getArguments().getString("bookingOid");
            bucketBookings = getArguments().getStringArrayList("bucket");
            bucketOid = getArguments().getString("bucketOid");

            for (int i = 0; i < bucketBookings.size(); i++) {
                String booking = bucketBookings.get(i).substring(0, bucketBookings.get(i).indexOf("/"));
                Log.d("SCANNED_BOOKINGS", "adding: " + booking + " to list");
                Log.d("SCANNED_BOOKINGS", "adding: " + bucketBookings + " to list");
                canAddParcelBookings.add(booking);
            }

            Log.d("SCANNED_BOOKINGS", bucketBookings.toString());
            Log.d("SCANNED_BOOKINGS", canAddParcelBookings.toString());
            Log.d("SCANNED_BOOKINGS", contactList.toString());
            Log.d("SCANNED_BOOKINGS", activityType);
            Log.d("SCANNED_BOOKINGS", "adding: " + isInbound + " to list");

            tvTotal.setText(String.valueOf(bucketBookings.size()));
        } else {
            activityType = getArguments().getString("activityType");
            bookingOid = getArguments().getString("bookingOid");
            transportType = getArguments().getString("transportType");

            contactList = new ArrayList<>();
            contactList = getArguments().getStringArrayList("contactList");

            Log.d("SCANNED_BOOKINGS", bookingOid);
            Log.d("SCANNED_BOOKINGS", contactList.toString());
            tvTotal.setText(String.valueOf(contactList.size()));
        }

        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    // Handle the back button press here

                    getDialog().dismiss();
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
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    private void init() {

        toolbar = view.findViewById(R.id.custom_toolbar);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custon_action_bar);
        actionBar.show();
        title = view.findViewById(R.id.txtTitle);
        btnBackButton = view.findViewById(R.id.btnBack);
        btnWhatsappButton = view.findViewById(R.id.btnWhatsapp);

        surfaceView = view.findViewById(R.id.surface_view);
//        crdFailed = view.findViewById(R.id.crdFailedStatus);
//        crdSuccess = view.findViewById(R.id.crdSuccessfull);
        errorMessage = view.findViewById(R.id.txtError);
        tvCounter = view.findViewById(R.id.tvCounter);
        tvResults = view.findViewById(R.id.tvResultsr);
        tvStatus = view.findViewById(R.id.tvStatus);
        tvTotal = view.findViewById(R.id.tvParcels);
        done = view.findViewById(R.id.btnDone);
        surfaceBackGround = view.findViewById(R.id.surfaceBackGround);
        surfaceBackGround.setBackground(getContext().getDrawable(R.drawable.failed_surface_background_));
        
        btnBackButton.setOnClickListener(v -> {
            scanningListener.returnStatus(false);
            dismiss();
        });
        toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 20);
        //surfaceView.setAspectRatio(  5.0f);
        scanningListener = (ScanningListener) view.getContext();
        barcodeDetector = new BarcodeDetector.Builder(getContext())
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();
        initialiseDetectorsAndSources();

        done.setOnClickListener(v -> {

            initialiseDetectorsAndSources();

        });

/*        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() { scanningListener.returnStatus(false);
                dismiss();
                Log.d("OnBackPressedDispatcher", "handleOnBackPressed: pressed");
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(onBackPressedCallback);*/

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
//        try {
//            if (context instanceof CaptureDocumentsDialog.PhotoUri) {
//                scanningListener = (ScanningListener) context;
//            } else {
//                throw new ClassCastException("The attached context is not of type PhotoUri");
//            }
//        } catch (ClassCastException e) {
//            e.printStackTrace();
//        }
    }

    /*==========================================================*/


    private void initialiseDetectorsAndSources() {

        //Toast.makeText(getContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();

        cameraSource = new CameraSource.Builder(getContext(), barcodeDetector)
                .setRequestedPreviewSize(820, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(getActivity(), new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                // Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                Log.d("TAG_barcode", "receiveDetections:  ========================================================================");
                Log.i("TAG_barcode", "receiveDetections: ========  " + barcodes.size());
                Log.d("TAG_barcode", "receiveDetections:  ========================================================================");

                if (barcodes.size() != 0) {

                    surfaceBackGround.setBackground(getContext().getDrawable(R.drawable.surface_background));

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // UI-related code here, for example:

                                if (barcodes.valueAt(0).email != null) {
                                        barcodeData = barcodes.valueAt(0).email.address;
                                        verifirdDecodedTrackNumber(barcodeData);
                                        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                                        scanned = true;
                                        surfaceView.stopScanning();
                                } else {
                                        scanningListener.returnStatus(true);
                                        barcodeData = barcodes.valueAt(0).displayValue;
                                        verifirdDecodedTrackNumber(barcodeData);
                                        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                                        scanned = true;
                                        surfaceView.stopScanning();

                                }
                            }
                    });

//                    tvResults.post(new Runnable() {
//                        @Override
//                        public void run() {
//
//                        }
//
//                    });

                } else {

                    surfaceBackGround.setBackground(getContext().getDrawable(R.drawable.failed_surface_background_));

                }
            }

        });
    }

    private void verifirdDecodedTrackNumber(String results) {

        tvResults.setText(results);
        if (isBucketPrioratized) {
            if (bucketBookings.contains(results.toString()) || canAddParcelBookings.contains(results.toString())) {

                if (scannedBucketBookings.size() > 0 && scannedBucketBookings.contains(results.toString())) {
                    int parcelRemaining = bucketBookings.size() - scannedBucketBookings.size();

                    tvResults.setText(results);
                    tvStatus.setText("Booking already scanned.");
                    tvStatus.setTextColor(getContext().getColor(R.color.skynet_red));

                } else {
                    tvCounter.setText(scannedBucketBookings.size() + " of " + canAddParcelBookings.size());
                    scannedBucketBookings.add(results.toString());
                    Log.d("RESULTS", scannedBucketBookings.toString());
                    Log.d("RESULTS", bucketBookings.toString());
                    boolean exist = scannedBucketBookings.contains(results.toString());
                    Log.d("RESULTS", String.valueOf(exist));


                    int parcelRemaining = bucketBookings.size() - scannedBucketBookings.size();

                    if (activityType.equals(BookingActivities.DROP_OFF.name())) {
                        errorMessage.setText("Scanned successfully. " + parcelRemaining + " Parcels remaining to scan");

                    }

                    if (activityType.equals("PICK_UP")) {
                        errorMessage.setText("Booking is Added. " + parcelRemaining + " Parcels remaining to scan");

                    }

                    //onDoneScanning();

                    Log.d("SCANNED_BOOKINGS", String.valueOf(scannedBucketBookings.size()));
                    Log.d("SCANNED_BOOKINGS", String.valueOf(bucketBookings.size()));

                    tvStatus.setText("Scanned booking invalid.");
                    tvStatus.setTextColor(getContext().getColor(R.color.skynet_red));

                    if (scannedBucketBookings.size() == bucketBookings.size()) {
                        Log.d("SCANNED_BOOKINGS", "DONE SCANNING");

                        String trackNo;

                        if (results.toString().contains("/")) {
                            trackNo = results.toString().substring(0, results.toString().indexOf("/"));
                        } else {
                            trackNo = results.toString();
                        }


                        scanningListener.returnStatus(true);
                        dismiss();
                    }

                    tvTotal.setText(String.valueOf(bucketBookings.size()));
                    tvCounter.setText(String.valueOf(scannedBucketBookings.size()));

                }

            } else {

                if (activityType.equals(BookingActivities.DROP_OFF.name())) {

                }
                if (activityType.equals(BookingActivities.PICK_UP.name())) {
                }

                tvStatus.setText("Scanned booking invalid.");
                tvStatus.setTextColor(getContext().getColor(R.color.skynet_red));
            }


        } else {

            if (results.toString().length() == 12) {
                // getBookingByWaybillNo(result.toString());
                //Log.d("RESULTS", results.toString());
            } else {
                try {
                    Log.d("IDS_", results.toString() + " /=/ " + bookingOid);
                    if (results.toString().equals(bookingOid)) {
                        scanningListener.returnStatus(true);
                        tvStatus.setText("Booking scanned Successfully. ");
                        tvStatus.setTextColor(getContext().getColor(R.color.snackbar_red));
                        dismiss();
                    } else {
                        Log.d("IDS_", "Id: " + results.toString() + " does not match with booking");
                        tvResults.setText(results);
                        tvStatus.setText("Scanned booking invalid.");
                        tvStatus.setTextColor(getContext().getColor(R.color.skynet_red));


                    }

                } catch (Exception e) {
                    Log.e("RESULTS", e.toString());

                }
            }

        }

    }

//    @NonNull
//    @Override
//    public OnBackPressedDispatcher getOnBackPressedDispatcher() {
//        return getOnBackPressedDispatcher();
//    }

    public interface ScanningListener {
        void returnStatus(boolean isClientVerified);
    }
}
