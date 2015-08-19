package com.jadenine.circle.ui.menu;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.jadenine.circle.domain.Account;
import com.jadenine.circle.domain.Circle;
import com.jadenine.circle.eventbus.BusProvider;
import com.jadenine.circle.eventbus.EventProducer;
import com.jadenine.circle.model.entity.ApEntity;
import com.jadenine.circle.ui.topic.TopicListPath;
import com.jadenine.circle.ui.topic.top.TopPath;
import com.jadenine.circle.ui.welcome.WelcomePath;
import com.jadenine.circle.utils.ApUtils;
import com.squareup.otto.Subscribe;
import com.umeng.message.PushAgent;

import java.util.ArrayList;
import java.util.List;

import flow.Flow;
import flow.History;
import flow.path.Path;
import mortar.MortarScope;
import mortar.ViewPresenter;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

/**
 * Created by linym on 7/22/15.
 */
public class ApMenuPresenter extends ViewPresenter<ApMenuView>{
    private static final String BUNDLE_CURRENT_CIRCLE = "current_circle";
    private final Account account;
    private String currentCircle;

    public ApMenuPresenter(Account account) {
        this.account = account;
    }

    @Override
    protected void onEnterScope(MortarScope scope) {
        super.onEnterScope(scope);
        BusProvider.register(this);
    }

    @Override
    protected void onExitScope() {
        BusProvider.unregister(this);
        super.onExitScope();
    }

    @Override
    public void onLoad(Bundle savedInstanceState) {
        super.onLoad(savedInstanceState);
        if(null != savedInstanceState) {
            currentCircle = savedInstanceState.getString(BUNDLE_CURRENT_CIRCLE);
        }
        if (!hasView()) return;
        loadCircles();
        openDefaultItemIfNeed();
    }

    private void openDefaultItemIfNeed() {
        History history = Flow.get(getContext()).getHistory();
        Path top = history.top();
        if(top instanceof WelcomePath) {
            replaceWithPath(new TopPath());
        }
    }

    @Override
    protected void onSave(Bundle outState) {
        super.onSave(outState);
        outState.putString(BUNDLE_CURRENT_CIRCLE, currentCircle);
    }

    public boolean onCircleSelected(int position) {
        boolean validApPosition = position >= ApMenuAdapter.NON_AP_ITEM_COUNT;
        if(validApPosition) {
            Circle circle = getAdapter().getCircle(position);
            getAdapter().setSelected(position);
            onCircleSelected(circle);
        }
        return validApPosition;
    }

    private void onCircleSelected(@NonNull Circle circle) {
        if(null == circle) {
            return;
        }
        replaceWithPath(new TopicListPath(circle.getCircleId()));
        currentCircle = circle.getCircleId();
    }

    private void replaceWithPath(@NonNull Path path) {
        History.Builder historyBuilder = Flow.get(getContext()).getHistory().buildUpon();
        historyBuilder.pop();
        historyBuilder.push(path);

        Flow.get(getContext()).setHistory(historyBuilder.build(), Flow.Direction.REPLACE);
    }

    @Subscribe
    public void onApConnected(EventProducer.APConnectedEvent event) {
        addAPIfNot(event.getAP());
    }

    private Context getContext() {
        return getView().getContext();
    }

    private ApMenuAdapter getAdapter(){
        return getView().getAdapter();
    }

    private boolean alreadyAdded(ApUtils.AP currentAp) {
        boolean currentAPAlreadyAdded = false;
        List<Circle> circleList = account.getCircles();
        for (Circle circle : circleList) {
            if (currentAp.getBSSID().equals(circle.getCircleId()) && circle.getName().equals(currentAp.getSSID())) {
                currentAPAlreadyAdded = true;
                break;
            }
        }
        return currentAPAlreadyAdded;
    }

    private void addAPIfNot(ApUtils.AP ap) {
        if (null == ap || alreadyAdded(ap)) {
            return;
        }

        ApEntity apEntity = new ApEntity(ap.getBSSID(), ap.getSSID());

        account.addAp(apEntity).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<Circle>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(List<Circle> circles) {
                if (!hasView()) return;
                getAdapter().setCircles(circles);

                addTag(circles);
            }
        });
    }

    void loadCircles() {
        account.listCircles().observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List
                <Circle>>() {

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                Timber.w(e, "Failed to load aps.");
                getAdapter().setCircles(account.getCircles());
            }

            @Override
            public void onNext(List<Circle> circles) {
                if (!hasView()) return;
                getAdapter().setCircles(circles);

                addTag(circles);

                ApUtils.AP ap = ApUtils.getConnectedAP(getContext());
                addAPIfNot(ap);
            }
        });
    }

    private void addTag(final List<Circle> circles) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<Circle> tmpCircles = new ArrayList<>(circles);
                for (Circle circle : tmpCircles) {
                    addTag(circle.getCircleId());
                }
                addTag(account.getDeviceId());
            }
        });
    }

    private void addTag(String tag) {
        if(!hasView()) {
            return;
        }
        try {
            PushAgent.getInstance(getContext()).getTagManager().add(tag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
