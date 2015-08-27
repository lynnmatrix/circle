package com.jadenine.circle.ui.share;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jadenine.circle.R;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.message.proguard.O;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by linyb on 2015/8/27.
 */
public class ShareDialogFragment extends DialogFragment {

    private static final String WX_APP_ID = "wx22640278e820fdc8";
    private static String sShareText = null;

    private static IWXAPI sWXApi = null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context context = getActivity();
        sWXApi = WXAPIFactory.createWXAPI(getActivity(), null);
        // WXAppSupportAPI为0表示没有安装微信
        if(!sWXApi.registerApp(WX_APP_ID) || sWXApi.getWXAppSupportAPI() == 0) {
            sWXApi = null;
        }
        if (sShareText == null) {
            sShareText = context.getString(R.string.share_message_description, context
                    .getString(R.string.app_name), context.getString(R.string.share_link));
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        builder.setTitle(R.string.share)
               .setItems(createArray(getActivity()), 
                       new ShareItemClickListener(context, sWXApi != null));
        
        return builder.create();
    }

    private class ShareItemClickListener implements DialogInterface.OnClickListener {
        private Context context;
        private boolean wxAvailable;

        public ShareItemClickListener(Context context, boolean wxAvailable) {
            this.context = context;
            this.wxAvailable = wxAvailable;
        }
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch(which) {
                case 0: {
                    if (wxAvailable) {
                        shareToWeChat(SendMessageToWX.Req.WXSceneTimeline);
                    } else {
                        context.startActivity(createShareIntent());
                    }
                    break;
                }
                
                case 1 : {
                    shareToWeChat(SendMessageToWX.Req.WXSceneSession);
                    break;
                }
                case 2 : {
                    if (wxAvailable) {
                        context.startActivity(createShareIntent());
                    } 
                    break;
                }
            }
            
        }
    } 
    
    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    public void shareToWeChat(int scene) {
        SendMessageToWX.Req req = buildWXMessage(scene);

        sWXApi.sendReq(req);
    }

    private SendMessageToWX.Req buildWXMessage(int scene) {
        WXWebpageObject webPage = new WXWebpageObject();
        webPage.webpageUrl = getActivity().getString(R.string.share_link);
        WXMediaMessage weChatMessage = new WXMediaMessage(webPage);
        weChatMessage.title = getString(R.string.app_name);
        weChatMessage.description = getActivity().getString(R.string.share_message_description, 
                getActivity().getString(R.string.app_name), getActivity().getString(R.string.share_link));

        Bitmap thumb = BitmapFactory.decodeResource(getActivity().getResources(), R.mipmap
                .ic_launcher);
        weChatMessage.thumbData = bmpToByteArray(thumb, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.message = weChatMessage;
        req.transaction = buildTransaction(String.valueOf(System.currentTimeMillis()));
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
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public String[] createArray(Context context) {
        List<String> shareList = new ArrayList<>(3);
        if (sWXApi != null) {
            shareList.add(context.getString(R.string.share_to_moment));
            shareList.add(context.getString(R.string.share_to_wechat));
        }
        
        shareList.add(context.getString(R.string.share_to_other));
        
        String []array = new String[shareList.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = shareList.get(i);
        }
       
        return array;   
    }
    
    public Intent createShareIntent() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);

        intent.putExtra(Intent.EXTRA_TEXT, sShareText);
        intent.setType("text/plain");
        
        return intent;
    }
}
