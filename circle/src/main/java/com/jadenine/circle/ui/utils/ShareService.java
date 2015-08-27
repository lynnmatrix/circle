package com.jadenine.circle.ui.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import com.jadenine.circle.R;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.ByteArrayOutputStream;

/**
 * Created by linym on 8/10/15.
 */
public class ShareService {
    private static final boolean ENABLE = true;
    private static final String WX_APP_ID = "wx22640278e820fdc8";//"wx26c018eb07d642cc"; //"wx4f4bf98ef21aba6b";//

    private IWXAPI wechatApi = null;
    private Context context;

    public boolean start(Context context) {
        this.context = context;
        if (null == wechatApi) {
            wechatApi = WXAPIFactory.createWXAPI(getContext(), WX_APP_ID, false);
        }
        // WXAppSupportAPI为0表示没有安装微信
        if (!wechatApi.registerApp(WX_APP_ID) || wechatApi.getWXAppSupportAPI() == 0) {
            wechatApi = null;
        }

        return weChatAvailable();
    }

    public void stop(){
        if (null != wechatApi) {
            wechatApi.unregisterApp();
            wechatApi.detach();
        }
    }

    public void share() {
        if(!ENABLE) return;
        Context context = getContext();

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);

        Bitmap bt = BitmapFactory.decodeResource(context.getResources(), R.drawable.starry_night);
        final Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver
                (), bt, null, null));

        String shareDescription = context.getString(R.string.share_message_description, context
                .getString(R.string.app_name), context.getString(R.string.share_link));

        shareIntent.setType("image*//*");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareDescription);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);

        context.startActivity(shareIntent);
    }

    public void shareToWeChatTimeline(){
        shareToWeChat(SendMessageToWX.Req.WXSceneTimeline);
    }

    public void shareToWeChat(int scene) {
        if(!weChatAvailable()) {
            return;
        }
        SendMessageToWX.Req req = buildWXMessage(scene);

        wechatApi.sendReq(req);
    }

    public boolean weChatAvailable(){
        return ENABLE && null != wechatApi;
    }

    private SendMessageToWX.Req buildWXMessage(int scene) {
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
        req.transaction = buildTransaction("circle");
        req.scene = scene;

        return req;
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

    private Context getContext() {
        return context;
    }

    private String getString(int id) {
        return getContext().getString(id);
    }

}
