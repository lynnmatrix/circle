package com.jadenine.circle.ui.message.composer;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.jadenine.circle.R;
import com.jadenine.circle.entity.Message;
import com.jadenine.circle.entity.UserAp;
import com.jadenine.circle.request.MessageService;
import com.jadenine.circle.utils.Device;

import flow.Flow;
import mortar.ViewPresenter;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by linym on 6/9/15.
 */
public class ComposerPresenter extends ViewPresenter<ComposerView>{
    private static final String BUNDLE_TYPED_CONTENT = "editor_content";

    final MessageService messageService;
    final UserAp userAp;

    public ComposerPresenter(MessageService messageService, UserAp userAp) {
        this.messageService = messageService;
        this.userAp = userAp;
    }

    @Override
    protected void onLoad(Bundle savedInstanceState) {
        super.onLoad(savedInstanceState);
        if(!hasView()){
            return;
        }

        if(null != savedInstanceState) {
            String content = savedInstanceState.getString(BUNDLE_TYPED_CONTENT, "");
            getView().editor.setText(content);
        }
    }

    @Override
    protected void onSave(Bundle outState) {
        super.onSave(outState);
        outState.putString(BUNDLE_TYPED_CONTENT, getView().editor.getText().toString());
    }


    void send(final String content) {
        if(TextUtils.isEmpty(content)){
            Toast.makeText(getView().getContext(), R.string.message_invalid_empty, Toast
                    .LENGTH_SHORT).show();
//            Snackbar.make(getView().editor, R.string.message_invalid_empty, Snackbar.LENGTH_LONG);
            return;
        }
        Message message = new Message();
        message.setAp(userAp.getAP());
        message.setUser(Device.getDeviceId(getView().getContext()));
        message.setContent(content);

        messageService.addMessage(message, new Callback<Message>() {
            @Override
            public void success(Message message, Response response) {
                Toast.makeText(getView().getContext(), R.string.message_send_success, Toast
                        .LENGTH_SHORT).show();
                Flow.get(getView().getContext()).goBack();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getView().getContext(), R.string.message_send_fail, Toast
                        .LENGTH_LONG).show();
            }
        });
    }
}
