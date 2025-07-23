package co.za.kasi.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SignaturePadView extends View {

    private Paint paint;
    private Path path;
    private Bitmap signatureBitmap;
    private Canvas signatureCanvas;

    public SignaturePadView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(5f);

        path = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Create a transparent bitmap if not already created
        if (signatureBitmap == null) {
            signatureBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            signatureCanvas = new Canvas(signatureBitmap);
        }

        // Draw the signature path on the transparent canvas
        signatureCanvas.drawColor(Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR);
        signatureCanvas.drawPath(path, paint);

        // Draw the bitmap on the view's canvas
        canvas.drawBitmap(signatureBitmap, 0, 0, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                return true;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(x, y);
                break;
            case MotionEvent.ACTION_UP:
                // You can handle the end of drawing here if needed
                break;
            default:
                return false;
        }

        // Force a redraw of the view
        invalidate();
        return true;
    }

    // Save the signature as an image file and get its URI
    public String saveSignature() {
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "SIGNATURE_" + timeStamp + ".png";
        File imageFile = new File(storageDir, imageFileName);

        try {
            FileOutputStream fos = new FileOutputStream(imageFile);
            signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            return imageFile.getAbsolutePath(); // Return the file path or URI as needed
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Clear the signature pad
    public void clear() {
        path.reset();
        if (signatureBitmap != null) {
            signatureBitmap.recycle();
            signatureBitmap = null;
            signatureCanvas = null;
        }
        invalidate();
    }

    // Check if the signature pad is empty
    public boolean isEmpty() {
        return path.isEmpty();
    }

    // Check if the signature pad contains a straight line
    public boolean isStraightLine() {
        // Adjust the threshold as needed
        float threshold = 500f;

        // Get the bounding box of the drawn path
        android.graphics.RectF bounds = new android.graphics.RectF();
        path.computeBounds(bounds, true);

        // Check if the bounding box is essentially a straight line
        return (bounds.width() < threshold && bounds.height() < threshold);
    }
}
