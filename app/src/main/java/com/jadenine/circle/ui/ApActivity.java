package com.jadenine.circle.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jadenine.circle.R;

public class ApActivity extends AppCompatActivity implements APFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ap);
    }

    @Override
    public void onFragmentInteraction(String id) {

    }


}
