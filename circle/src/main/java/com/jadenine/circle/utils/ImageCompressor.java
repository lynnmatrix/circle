package com.jadenine.circle.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * Created by linym on 7/31/15.
 */
public class ImageCompressor {

    public static final String UPLOAD_TEMP_DIR = "PicUploadTemp";
    public static final String TMP_UPLOAD_FILE_PREFIX = "tmp_upload_";

    public static Uri compress(Context context, Uri inputUri, int dstWidth, int dstHeight) throws
            IOException {

        ContentResolver contentResolver = context.getContentResolver();
        InputStream inputStream = contentResolver.openInputStream(inputUri);

        // decode image size (decode metadata only, not the whole image)
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, options);

        // save width and height
        int inWidth = options.outWidth;
        int inHeight = options.outHeight;

        // decode full image pre-resized
        options = new BitmapFactory.Options();

        // calc rough re-size (this is no exact resize)
        // Choose the largest ratio as inSampleSize value, this will guarantee
        // a final image with both dimensions smaller than or equal to the
        // requested height and width.
        options.inSampleSize = Math.max(inWidth / dstWidth, inHeight / dstHeight);
        // decode full image
        inputStream = contentResolver.openInputStream(inputUri);
        Bitmap roughBitmap = BitmapFactory.decodeStream(inputStream, null, options);

        // calc exact destination size
        Matrix m = new Matrix();
        RectF inRect = new RectF(0, 0, roughBitmap.getWidth(), roughBitmap.getHeight());
        RectF outRect = new RectF(0, 0, dstWidth, dstHeight);
        m.setRectToRect(inRect, outRect, Matrix.ScaleToFit.CENTER);
        float[] values = new float[9];
        m.getValues(values);

        // resize bitmap
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(roughBitmap, (int) (roughBitmap.getWidth
                () * values[0]), (int) (roughBitmap.getHeight() * values[4]), true);


        // save image
        File outputDir = context.getCacheDir(); // context being the Activity pointer
        File pngFile = File.createTempFile(TMP_UPLOAD_FILE_PREFIX, ".jpg", outputDir);

        FileOutputStream out = new FileOutputStream(pngFile);
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        return Uri.fromFile(pngFile);
    }

}
