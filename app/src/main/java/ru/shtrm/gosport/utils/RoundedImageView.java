package ru.shtrm.gosport.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class RoundedImageView extends AppCompatImageView {

    public RoundedImageView(Context context) {
        super(context);
    }

    public RoundedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public static Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
        Bitmap sbmp;

        if (bmp.getWidth() != radius || bmp.getHeight() != radius) {
            float smallest = Math.min(bmp.getWidth(), bmp.getHeight());
            float factor = smallest / radius;
            sbmp = Bitmap.createScaledBitmap(bmp,
                    (int) (bmp.getWidth() / factor),
                    (int) (bmp.getHeight() / factor), false);
        } else {
            sbmp = bmp;
        }

        Bitmap output = Bitmap.createBitmap(radius, radius, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final String color = "#BAB399";
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, radius, radius);

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor(color));
        canvas.drawCircle(radius / 2 + 0.7f, radius / 2 + 0.7f,
                radius / 2 + 0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(sbmp, rect, rect, paint);

        return output;
    }

    public static Bitmap getRoundedBitmap(Bitmap bmp, int radius) {
        Bitmap imageRounded = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());
        Canvas canvas = new Canvas(imageRounded);
        Paint mpaint = new Paint();
        mpaint.setAntiAlias(true);
        mpaint.setShader(new BitmapShader(bmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        canvas.drawRoundRect((new RectF(0, 0, bmp.getWidth(), bmp.getHeight())), radius, radius, mpaint);
        return imageRounded;
    }

    public static Bitmap getRoundedBitmapByPath(String path, String filename, int radius) {
        File image_full = new File(path + filename);
        Bitmap bmp = BitmapFactory.decodeFile(image_full.getAbsolutePath());
        if (bmp != null) {
            final int width = bmp.getWidth() + 2;
            final int height = bmp.getHeight() + 2;
            Bitmap imageRounded = Bitmap.createBitmap(width, height, bmp.getConfig());
            BitmapShader shader = new BitmapShader(imageRounded, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setShader(shader);
            Canvas canvas = new Canvas(imageRounded);
            canvas.drawCircle(width / 2, height / 2, radius, paint);
            paint.setShader(null);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.BLUE);
            paint.setStrokeWidth(2);
            canvas.drawCircle(width / 2, height / 2, radius-1, paint);
            return imageRounded;
        }
        else return null;
    }

    public static Bitmap getResizedBitmap(String path, String filename, int newWidth, int newHeight, long changedAt) {
        Bitmap imageBitmap;
        Bitmap imageBitmap2;
        float scaleWidth;
        float scaleHeight;
        // /storage/sdcard1/Android/data/ru.toir.mobile/users/4CD4A64F-F6CB-4A7C-B5A6-42936E656F31.jpg
        if (path==null || filename==null) return null;

        File image = new File(path + filename.replace(".", "_m."));
        File image_full = new File(path + filename);

        Long last_modified = image.lastModified();
        if (image.exists() && changedAt <= last_modified) {
            // файл есть, преобразовывать не нужно
            imageBitmap2 = BitmapFactory.decodeFile(image.getAbsolutePath());
            if (imageBitmap2 != null) {
                return imageBitmap2;
            }
        }

        imageBitmap = BitmapFactory.decodeFile(image_full.getAbsolutePath());
        if (imageBitmap != null) {
            int width = imageBitmap.getWidth();
            int height = imageBitmap.getHeight();
            if ((newWidth == 0) && (newHeight == 0)) {
                return null;
            }

            if (newWidth > 0) {
                scaleWidth = (float) newWidth / (float) width;
                newHeight = (int) (height * scaleWidth);
            } else if (newHeight > 0) {
                scaleHeight = (float) newHeight / (float) height;
                newWidth = (int) (width * scaleHeight);
            }

            imageBitmap2 = Bitmap.createScaledBitmap(imageBitmap, newWidth, newHeight, false);
            FileOutputStream fOut;
            try {
                fOut = new FileOutputStream(image);
                imageBitmap2.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.flush();
                fOut.close();
                return imageBitmap2;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Drawable drawable = getDrawable();

        if (drawable == null) {
            return;
        }

        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        Bitmap b = ((BitmapDrawable) drawable).getBitmap();
        Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);

        int w = getWidth();
        @SuppressWarnings("unused")
        int h = getHeight();

        Bitmap roundBitmap = getCroppedBitmap(bitmap, w);
        canvas.drawBitmap(roundBitmap, 0, 0, null);

    }
}