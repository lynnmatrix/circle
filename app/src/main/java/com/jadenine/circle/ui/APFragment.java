package com.jadenine.circle.ui;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jadenine.circle.R;
import com.jadenine.circle.entity.UserAp;
import com.jadenine.circle.eventbus.BusProvider;
import com.jadenine.circle.eventbus.EventProducer;
import com.jadenine.circle.request.ApService;
import com.jadenine.circle.request.JSONListWrapper;
import com.jadenine.circle.request.ServiceProvider;
import com.jadenine.circle.utils.ApUtils;
import com.jadenine.circle.utils.Device;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A fragment representing a list of AP.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class ApFragment extends ListFragment {

    private OnFragmentInteractionListener mListener;

    private ArrayAdapter<UserAp> userApAdapter;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ApFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        userApAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, new ArrayList<UserAp>(0));

        setListAdapter(userApAdapter);

        loadAPList();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        BusProvider.getInstance().register(this);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_ap, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id ==R.id.action_list_ap) {
            loadAPList();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (null != mListener) {
            UserAp userAp = userApAdapter.getItem(position);
            if(null != userAp) {
                mListener.onApSelected(userAp);
            }
        }
    }

    @Subscribe
    public void onApConnected(EventProducer.APConnectedEvent event) {
        addAPIfNot(event.getAP());
    }

    private boolean alreadyAdded(String currentAp) {
        boolean currentAPAlreadyAdded = false;

        if(!TextUtils.isEmpty(currentAp)) {
            int count = userApAdapter.getCount();
            for (int i = 0; i< count; i++ ) {
                UserAp userAp = userApAdapter.getItem(i);
                if(null != userAp) {
                    if(currentAp.equals(userAp.getAP())) {
                        currentAPAlreadyAdded = true;
                        break;
                    }
                }else {
                    break;
                }
            }
        }
        return currentAPAlreadyAdded;
    }

    private void addAPIfNot(String mac){
        if(alreadyAdded(mac)) {
           return;
        }

        ApService apService = ServiceProvider.provideApService();

        apService.addAP(Device.getDeviceId(getActivity()), mac, new Callback<JSONListWrapper<UserAp>>() {
            @Override
            public void success(JSONListWrapper<UserAp> userAps, Response response) {
                userApAdapter.clear();
                userApAdapter.addAll(userAps.getAll());
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void loadAPList(){
        ApService apService = ServiceProvider.provideApService();

        apService.listAPs(Device.getDeviceId(getActivity()), new Callback<JSONListWrapper<UserAp>>() {
            @Override
            public void success(JSONListWrapper<UserAp> userAps, Response response) {

                userApAdapter.clear();
                userApAdapter.addAll(userAps.getAll());

                String currentAp = ApUtils.getConnectedAP(getActivity());
                addAPIfNot(currentAp);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onApSelected(UserAp userAp);
    }

}
