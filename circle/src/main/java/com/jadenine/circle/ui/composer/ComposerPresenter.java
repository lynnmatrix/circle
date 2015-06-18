package com.jadenine.circle.ui.composer;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.Topic;
import com.jadenine.circle.domain.UserAp;

import flow.Flow;
import mortar.ViewPresenter;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by linym on 6/9/15.
 */
public class ComposerPresenter extends ViewPresenter<ComposerView>{
    private static final String BUNDLE_TYPED_CONTENT = "editor_content";

    private final UserAp userAp;

    public ComposerPresenter(UserAp userAp) {
        this.userAp = userAp;
    }

    @Override
    protected void onLoad(Bundle savedInstanceState) {
        super.onLoad(savedInstanceState);
        if(!hasView()){
            return;
        }

        getView().toolbar.setTitle(R.string.title_activity_topic_composer);

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
            return;
        }

        Topic topic = new Topic(userAp, content);

        topic.publish(userAp).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Topic>() {

            @Override
            public void onCompleted() {
                Toast.makeText(getView().getContext(), R.string.message_send_success, Toast
                        .LENGTH_SHORT).show();
                Flow.get(getView().getContext()).goBack();
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(getView().getContext(), R.string.message_send_fail, Toast
                        .LENGTH_LONG).show();
            }

            @Override
            public void onNext(Topic topic1) {

            }
        });
    }
}
