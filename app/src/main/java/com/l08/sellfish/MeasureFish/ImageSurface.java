package com.l08.sellfish.MeasureFish;

/**
 * Created by LauraCamila on 31/10/2016.
 */


        import android.annotation.SuppressLint;
        import android.content.Context;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.graphics.Canvas;
        import android.graphics.Color;
        import android.graphics.Paint;
        import android.graphics.PixelFormat;
        import android.view.SurfaceHolder;
        import android.view.SurfaceView;

        import java.io.File;
/**
 * Class used to handle displaying the photo/image to the user.
 */
public class ImageSurface extends SurfaceView implements SurfaceHolder.Callback {

    private Bitmap icon;
    private Paint paint;

    public ImageSurface(Context context, File image) {
        super(context);
        getHolder().addCallback(this);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);

        String imageFile = image.getAbsolutePath();
        icon = BitmapFactory.decodeFile(imageFile);
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAlpha(255);
        //setZOrderOnTop(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int newWidth = canvas.getHeight() * icon.getWidth() / icon.getHeight();
        icon = Bitmap.createScaledBitmap(icon, newWidth, canvas.getHeight(), false);
       canvas.drawColor(Color.WHITE);
        int cx = (canvas.getWidth() - icon.getWidth()) / 2;
        int cy = (canvas.getHeight() - icon.getHeight()) / 2;
        canvas.drawBitmap(icon, cx, cy, paint);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @SuppressLint("WrongCall")
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Canvas canvas = null;
        try {
            canvas = holder.lockCanvas(null);
            synchronized (holder) {
                onDraw(canvas);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (canvas != null) {
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }
}
