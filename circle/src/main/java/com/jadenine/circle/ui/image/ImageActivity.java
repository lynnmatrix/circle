package com.jadenine.circle.ui.image;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.jadenine.circle.R;
import com.jadenine.circle.ui.widgets.TouchImageView;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by linym on 8/27/15.
 */
public class ImageActivity extends Activity {

    private static final String EXTRA_IMAGE = "image";

    @InjectView(R.id.image_view)
    TouchImageView imageView;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

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
        String imageUri = getIntent().getStringExtra(EXTRA_IMAGE);
        if(null != imageUri) {
            Picasso.with(this).load(imageUri).into(imageView);
        }

        toolbar.setNavigationIcon(R.drawable.ic_actionbar_back_light);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String imageUri = getIntent().getStringExtra(EXTRA_IMAGE);
        if(null != imageUri) {
            Picasso.with(this).load(imageUri).into(imageView);
        }
    }
}
