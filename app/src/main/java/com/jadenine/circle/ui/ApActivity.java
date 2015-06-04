package com.jadenine.circle.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jadenine.circle.R;
import com.jadenine.circle.entity.UserAp;

public class ApActivity extends AppCompatActivity implements ApFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ap);
    }

    @Override
    public void onApSelected(UserAp userAp) {
        Intent intent = MessageActivity.createMessageIntent(this, userAp.getAP());
        startActivity(intent);
    }
}
