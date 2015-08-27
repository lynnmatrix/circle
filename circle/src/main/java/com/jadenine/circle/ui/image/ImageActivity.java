package com.jadenine.circle.ui.image;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.jadenine.circle.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import uk.co.senab.photoview.PhotoView;

/**
 * Created by linym on 8/27/15.
 */
public class ImageActivity extends Activity {

    private static final String EXTRA_IMAGE = "image";

    @InjectView(R.id.image_view)
    PhotoView imageView;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    private String mImageUri;

    public static void actionOpenFullImage(Context context, String imageUri) {
        Intent intent = new Intent(context, ImageActivity.class);
        intent.putExtra(EXTRA_IMAGE, imageUri);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ButterKnife.inject(this);
        mImageUri = getIntent().getStringExtra(EXTRA_IMAGE);
        if(null != mImageUri) {
            Picasso.with(this).load(mImageUri).into(imageView);
        }

        toolbar.setNavigationIcon(R.drawable.ic_actionbar_back_light);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showSaveImageDialog();
                return true;
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        mImageUri = getIntent().getStringExtra(EXTRA_IMAGE);
        if(null != mImageUri) {
            Picasso.with(this).load(mImageUri).into(imageView);
        }
    }

    private void showSaveImageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage(R.string.dialog_save_image_message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveImage(mImageUri);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //DO NOTHING
                    }
                });
        builder.create().show();
    }

    private void saveImage(final String imageUri) {
        new Thread() {
            @Override
            public void run() {
                Bitmap bmp = null;
                try {
                    bmp = Picasso.with(ImageActivity.this).load(imageUri).get();
                } catch (IOException ignore) {
                }
                if(bmp != null) {
                    File savePath = Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS);
                    savePath.mkdirs();
                    Date dt = new Date(System.currentTimeMillis());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss", Locale.getDefault());
                    String name = sdf.format(dt);
                    File file = new File(savePath.getAbsolutePath(), name + ".png");
                    int offset = 1;
                    while(file.exists()) {
                        offset++;
                        file = new File(savePath.getAbsolutePath(), name + "_" + offset + ".png");
                    }
                    OutputStream out = null;
                    boolean success;
                    try {
                        out = new FileOutputStream(file);
                        success = bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
                    } catch (FileNotFoundException e) {
                        success = false;
                    } finally {
                        try {
                            out.close();
                        } catch (IOException e) {
                        }
                    }
                    if(success) {
                        DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                        if(dm != null) {
                            dm.addCompletedDownload(file.getName(), file.getName(),
                                    false , "image/png",
                                    file.getAbsolutePath(), file.length(), true);
                        }
                        showToast(R.string.save_image_success);
                    } else {
                        showToast(R.string.save_image_fail);
                    }
                } else {
                    showToast(R.string.save_image_fail);
                }
            }
        }.start();
    }

    private void showToast(final int resId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ImageActivity.this, resId, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
