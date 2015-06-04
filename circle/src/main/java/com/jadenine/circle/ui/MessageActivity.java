package com.jadenine.circle.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.jadenine.circle.R;
import com.jadenine.circle.entity.Message;
import com.jadenine.circle.request.JSONListWrapper;
import com.jadenine.circle.request.ServiceProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MessageActivity extends AppCompatActivity {

    public final static String PARAM_AP = "ap";

    private MessageFragment messageFragment;
    private ArrayAdapter<Message> messageAdapter;
    private String ap;
    public static Intent createMessageIntent(Context context, String ap) {
        Intent intent = new Intent(context, MessageActivity.class);
        intent.putExtra(PARAM_AP, ap);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        messageFragment = (MessageFragment) getFragmentManager()
                .findFragmentById(R.id.fragment);

        messageAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new
                ArrayList<Message>(0));
        messageFragment.setListAdapter(messageAdapter);

        if(null == savedInstanceState) {
            Intent intent = getIntent();
            ap = intent.getStringExtra(PARAM_AP);
        }
        setTitle(R.string.title_activity_message);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMessages();
    }

    private void loadMessages() {
        ServiceProvider.provideMessageService().listMessages(ap, new Callback<JSONListWrapper<Message>>() {

            @Override
            public void success(JSONListWrapper<Message> messageJSONListWrapper, Response
                    response) {
                messageAdapter.clear();
                List<Message> messages = messageJSONListWrapper.getAll();
                Collections.sort(messages, new Comparator<Message>() {
                    @Override
                    public int compare(Message lhs, Message rhs) {
                        return (int) (rhs.getTimestamp() - lhs.getTimestamp());
                    }
                });
                messageAdapter.addAll(messages);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            loadMessages();
            return true;
        } else if(id == R.id.action_add) {
            Intent addIntent = MessageAddActivity.createMessageAddIntent(this, ap);
            startActivity(addIntent);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
}
