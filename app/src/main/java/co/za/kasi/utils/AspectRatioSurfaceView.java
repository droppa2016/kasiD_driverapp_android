package co.za.kasi.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import co.za.kasi.R;


public class AspectRatioSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private Path clipPath;
    private Paint paint;
    private float radius = 50.0f;
    private float aspectRatio = 3f / 9.0f; // Default aspect ratio (width /
    private Handler scanningHandler;
    private boolean isScanning = false;

    public AspectRatioSurfaceView(Context context) {
        super(context);
        getHolder().addCallback(this);
        init();
    }

    public AspectRatioSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        init();
    }

    public AspectRatioSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        getHolder().addCallback(this);
        init();
    }

    private void init() {
        clipPath = new Path();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(0xFFFFFFFF); // Set background color

        setWillNotDraw(false);

        scanningHandler = new Handler(Looper.getMainLooper());
    }

    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
        requestLayout();
    }

    public void setRadius(float radius) {
        this.radius = radius;
        redraw();
    }

    public void stopScanning() {
        isScanning = false;
        redraw(); // Redraw to reflect the change
    }

    public void startScanning() {
        isScanning = true;
        initiateScanning();
    }

    private void initiateScanning() {
        scanningHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isScanning) {
                    redraw();
                    initiateScanning(); // Schedule next scanning after 1.5 seconds
                }
            }
        }, 1500);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // Initialize drawing here
        if (isScanning) {
            initiateScanning(); // Start scanning if it was active before surface creation
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Handle changes in surface dimensions here
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Clean up resources here
        scanningHandler.removeCallbacksAndMessages(null); // Stop any pending scanning
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Create a rounded rectangle path based on the new width and height
        RectF rect = new RectF(0, 0, w, h);
        float radius = 40.0f; // Set the radius of the rounded corners
        clipPath.reset(); // Clear existing path
        clipPath.addRoundRect(rect, radius, radius, Path.Direction.CW);

        // Adjust aspect ratio here if needed
        aspectRatio = (float) w / 9.0f;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);

        // Calculate the height based on the aspect ratio
        int height = (int) (width / aspectRatio);

        // Ensure the calculated height is within the desired bounds
        int desiredHeight = (int) getResources().getDimension(R.dimen.surface_max_height);
        height = Math.min(height, desiredHeight);

        // Set the calculated width and height
        setMeasuredDimension(width, height);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.clipPath(clipPath);
        super.draw(canvas);
        redraw();
    }

    private void redraw() {
        SurfaceHolder holder = getHolder();
        Canvas canvas = holder.lockCanvas();
        if (canvas != null) {
            canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, radius, paint);
            holder.unlockCanvasAndPost(canvas);
        }
    }
}
