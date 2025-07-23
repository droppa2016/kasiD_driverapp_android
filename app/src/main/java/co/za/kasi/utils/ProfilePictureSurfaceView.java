package co.za.kasi.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class ProfilePictureSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private CameraManager cameraManager;
    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraCaptureSession;
    private CaptureRequest.Builder captureRequestBuilder;
    private CaptureRequest captureRequest;
    private SurfaceHolder surfaceHolder;
    private Context context;

    private ImageReader imageReader;


    private int currentCameraFacing = CameraCharacteristics.LENS_FACING_FRONT; // Initial camera facing

    // ... (your existing methods)

    public void switchCamera() {
        // Close the current camera
        closeCamera();

        // Toggle between front and back cameras
        if (currentCameraFacing == CameraCharacteristics.LENS_FACING_BACK) {
            currentCameraFacing = CameraCharacteristics.LENS_FACING_FRONT;
            openCamera();
        } else {
            currentCameraFacing = CameraCharacteristics.LENS_FACING_BACK;
            openCamera();
        }

        // Open the new camera
    }

    // Callback for capturing image URI
    public interface OnImageCapturedListener {
        void onImageCaptured(Uri imageUri);
    }

    private OnImageCapturedListener onImageCapturedListener;

    public void setOnImageCapturedListener(OnImageCapturedListener listener) {
        this.onImageCapturedListener = listener;
    }

    // Handler for camera operations
    private Handler cameraHandler;
    private HandlerThread cameraThread;

    public ProfilePictureSurfaceView(Context context) {
        super(context);
        this.context = context;
        initialize();
    }

    public ProfilePictureSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initialize();
    }

    public ProfilePictureSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        initialize();
    }



    private void initialize() {
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        // Create a background thread for camera operations
        cameraThread = new HandlerThread("CameraThread");
        cameraThread.start();
        cameraHandler = new Handler(cameraThread.getLooper());

        imageReader = ImageReader.newInstance(820, 1200, ImageFormat.JPEG, 1);
        imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                Image image = reader.acquireLatestImage();
                if (image != null) {
                    // Process the captured image as needed
                    Uri imageUri = saveCapturedImage(image);
                    onImageCapturedListener.onImageCaptured(imageUri);
                    image.close();
                }
            }
        }, cameraHandler);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // Open the camera when the surface is created
        openCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Handle changes in surface dimensions
        if (surfaceHolder.getSurface() == null) {
            return;
        }

        openCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Release the camera when the surface is destroyed
        closeCamera();
        // Quit the background thread
        cameraThread.quitSafely();
        try {
            cameraThread.join();
            cameraThread = null;
            cameraHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        // Draw the circular shape
        int radius = Math.min(getWidth(), getHeight()) / 2;
        Path path = new Path();
        path.addCircle(getWidth() / 2f, getHeight() / 2f, radius, Path.Direction.CW);
        canvas.clipPath(path);

        // Your existing drawing code
        // ...
    }

    // Capture an image and return the URI
    public void captureImage() {
        if (cameraDevice != null) {
            try {
                cameraDevice.createCaptureSession(Arrays.asList(surfaceHolder.getSurface(), imageReader.getSurface()),
                        new CameraCaptureSession.StateCallback() {
                            @Override
                            public void onConfigured(CameraCaptureSession session) {
                                try {
                                    cameraCaptureSession = session;
                                    captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                                    captureRequestBuilder.addTarget(imageReader.getSurface());

                                    cameraCaptureSession.capture(captureRequestBuilder.build(),
                                            new CameraCaptureSession.CaptureCallback() {
                                                @Override
                                                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                                                    super.onCaptureCompleted(session, request, result);
                                                    // Image will be processed in onImageAvailable callback
                                                }
                                            }, null);
                                } catch (CameraAccessException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onConfigureFailed(CameraCaptureSession session) {
                                // Handle configuration failures
                            }
                        }, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
                Log.e("TAG_image_uri", "onImageCaptured: === " + e.getMessage(), e);
            }
        }
    }

    private Uri saveCapturedImage(Image image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            return null;
        }

        try {
            // Convert Image to byte array
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);

            // Rotate the image to vertical
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            Matrix matrix = new Matrix();
            matrix.postRotate(getRotationCompensation()); // Adjust the rotation angle as needed
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

            // Add watermark to the image
            // Bitmap watermarkedBitmap = addWatermark(bitmap, "Your Name", "Your Address", "Date", "Time", "Waybill Number");

            // Save the watermarked image to the file
            FileOutputStream fos = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();

            // Notify the system that a new photo has been taken
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(pictureFile);
            mediaScanIntent.setData(contentUri);
            context.sendBroadcast(mediaScanIntent);

            Log.d("TAG_image_uri", "onImageCaptured: === " + contentUri);

            return Uri.fromFile(pictureFile);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("TAG_image_uri", "onImageCaptured: === " + e.getMessage(), e);
        } finally {
            // Always close the Image to release its resources, even in case of an exception
            image.close();
        }

        return null;
    }

    private Bitmap addWatermark(Bitmap originalBitmap, String name, String address, String date, String time, String waybillNumber) {
        Bitmap watermarkedBitmap = Bitmap.createBitmap(originalBitmap);
        Canvas canvas = new Canvas(watermarkedBitmap);

        // Watermark text settings
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(30);
        paint.setAntiAlias(true);

        // Watermark position
        float x = 50; // Adjust the x-coordinate as needed
        float y = 50; // Adjust the y-coordinate as needed

        // Draw watermark text on the image
        canvas.drawText("Name: " + name, x, y, paint);
        y += 40;
        canvas.drawText("Address: " + address, x, y, paint);
        y += 40;
        canvas.drawText("Date: " + date, x, y, paint);
        y += 40;
        canvas.drawText("Time: " + time, x, y, paint);
        y += 40;
        canvas.drawText("Waybill Number: " + waybillNumber, x, y, paint);

        return watermarkedBitmap;
    }



    private int getRotationCompensation() {
        int rotation = ((Activity) context).getWindowManager().getDefaultDisplay().getRotation();
        int sensorOrientation = getSensorOrientation();

        // Get the difference between the device rotation and the sensor orientation
        int rotationCompensation = (rotation + sensorOrientation + 0) % 360;

        return rotationCompensation;
    }

    private int getSensorOrientation() {
        try {
            CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraDevice.getId());
            return cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        return 0;
    }



    public void openCamera() {
        cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraId = cameraManager.getCameraIdList()[currentCameraFacing]; // Use the first camera
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);

            Size[] jpegSizes = null;
            if (characteristics != null) {
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                        .getOutputSizes(ImageFormat.JPEG);
            }
            int width = 640;
            int height = 480;
            if (jpegSizes != null && jpegSizes.length > 0) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            }

            cameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(CameraDevice camera) {
                    cameraDevice = camera;
                    createCameraPreview();
                    Log.d("ProfilePictureSurfaceView", "Camera Opened");
                }

                @Override
                public void onDisconnected(CameraDevice camera) {
                    closeCamera();
                    Log.d("ProfilePictureSurfaceView", "Camera Disconnected");
                }

                @Override
                public void onError(CameraDevice camera, int error) {
                    closeCamera();
                    Log.e("ProfilePictureSurfaceView", "Camera Error: " + error);
                }

            }, cameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void createCameraPreview() {
        try {
            Surface surface = surfaceHolder.getSurface();
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    if (cameraDevice == null) {
                        return;
                    }

                    cameraCaptureSession = session;
                    try {
                        captureRequest = captureRequestBuilder.build();
                        cameraCaptureSession.setRepeatingRequest(captureRequest, null, cameraHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                    // Handle configuration failures
                }
            }, cameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void closeCamera() {
        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
        if (cameraCaptureSession != null) {
            cameraCaptureSession.close();
            cameraCaptureSession = null;
        }
    }

    private File getOutputMediaFile() {
        // Create a directory to save the image
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraApp/droppa");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }
}