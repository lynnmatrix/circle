package com.jadenine.circle.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;

import com.jadenine.circle.R;
import com.jadenine.circle.ui.scanner.WifiPath;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.ByteArrayOutputStream;

import flow.Flow;
import mortar.Presenter;
import mortar.bundler.BundleService;

/**
 * Created by linym on 6/15/15.
 */
public class HomePresenter extends Presenter<HomeActivity> {

    private static final String WX_APP_ID = "wx4f4bf98ef21aba6b";
    private IWXAPI wechatApi = null;

    @Override
    protected BundleService extractBundleService(HomeActivity activity) {
        return BundleService.getBundleService(activity);
    }

    @Override
    protected void onLoad(Bundle savedInstanceState) {
        super.onLoad(savedInstanceState);

        if (null == wechatApi) {
            wechatApi = WXAPIFactory.createWXAPI(getContext(), WX_APP_ID);
        }
        // WXAppSupportAPI为0表示没有安装微信
        if (!wechatApi.registerApp(WX_APP_ID) || wechatApi.getWXAppSupportAPI() == 0) {
            wechatApi = null;
        }

        if(!weChatAvaliable()) {
            Menu menu = getView().navigationView.getMenu();
            MenuItem menuItem = menu.findItem(R.id.item_share_wechat);
            if(null != menuItem) {
                menuItem.setVisible(false);
            }
        }
    }

    @Override
    protected void onExitScope() {
        if (null != wechatApi) {
            wechatApi.unregisterApp();
        }
        super.onExitScope();
    }

    void share() {
        Context context = getContext();

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);

        Bitmap bt = BitmapFactory.decodeResource(context.getResources(), R.drawable.starry_night);
        final Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver
                (), bt, null, null));

        String shareDescription = context.getString(R.string.share_message_description, context
                .getString(R.string.app_name), context.getString(R.string.share_link));

        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareDescription);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);

        context.startActivity(shareIntent);
    }

    void shareToWeChat(){
        shareToWeChat(SendMessageToWX.Req.WXSceneSession);
    }

    void shareToWeChat(int scene) {
        if(!weChatAvaliable()) {
            return;
        }
        SendMessageToWX.Req req = buildWXMessage();

        req.transaction = buildTransaction("wechat");
        req.scene = scene;
        wechatApi.sendReq(req);
    }

    void scanWifi() {
        Flow.get(getContext()).set(new WifiPath());
    }

    private boolean weChatAvaliable(){
        return null != wechatApi;
    }

    private SendMessageToWX.Req buildWXMessage() {
        WXWebpageObject webPage = new WXWebpageObject();
        webPage.webpageUrl = getContext().getString(R.string.share_link);
        WXMediaMessage weChatMessage = new WXMediaMessage(webPage);
        weChatMessage.title = getString(R.string.app_name);
        weChatMessage.description = getContext().getString(R.string.share_message_description,
                getContext().getString(R.string.app_name), getString(R.string.share_link));

        Bitmap thumb = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap
                .ic_launcher);
        weChatMessage.thumbData = bmpToByteArray(thumb, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.message = weChatMessage;
        return req;
    }

    private Context getContext() {
        return getView();
    }

    private String getString(int id) {
        return getContext().getString(id);
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System
                .currentTimeMillis();
    }

    private static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
