package com.jadenine.circle.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.jadenine.circle.R;
import com.jadenine.circle.app.CircleApplication;
import com.jadenine.circle.entity.Message;
import com.jadenine.circle.mortar.DaggerService;
import com.jadenine.circle.request.MessageService;
import com.jadenine.circle.utils.Device;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MessageAddActivity extends AppCompatActivity {

    private final static String PARAM_AP = "ap";

    private EditText editTextView;
    private String ap;

    @Inject
    MessageService messageService;

    public static Intent createMessageAddIntent(Context context, String ap) {
        Intent intent = new Intent(context, MessageAddActivity.class);
        intent.putExtra(PARAM_AP, ap);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_add);

        DaggerService.<CircleApplication.AppComponent>getDaggerComponent(this).inject(this);

        editTextView = (EditText) findViewById(R.id.message_edit);

        if(null == savedInstanceState) {
            Intent intent = getIntent();
            ap = intent.getStringExtra(PARAM_AP);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_message_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send) {
            send();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void send() {
        String content = editTextView.getText().toString();
        if(TextUtils.isEmpty(content)){
            Snackbar.make(editTextView, R.string.message_invalid_empty, Snackbar.LENGTH_LONG);
            return;
        }
        Message message = new Message();
        message.setAp(ap);
        message.setUser(Device.getDeviceId(this));
        message.setContent(content);

        messageService.addMessage(message, new Callback<Message>() {
            @Override
            public void success(Message message, Response response) {
                Toast.makeText(MessageAddActivity.this, R.string.message_send_success, Toast
                        .LENGTH_SHORT).show();
                onBackPressed();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(MessageAddActivity.this, R.string.message_send_fail, Toast
                        .LENGTH_LONG).show();
            }
        });
    }
}
