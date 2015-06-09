package com.jadenine.circle.ui;

import com.jadenine.circle.R;
import com.jadenine.circle.mortar.MortarPathContainerView;
import com.jadenine.circle.ui.ap.ApListPath;

import butterknife.InjectView;
import flow.Flow;

@Container(R.layout.activity_home)
public class HomeActivity extends MortarActivity {

    @InjectView(R.id.container)
    MortarPathContainerView pathContainerView;

    @Override
    protected Object buildDaggerService() {
        return null;
    }

    @Override
    protected Object getFirstScreen() {
        return new ApListPath();
    }

    @Override
    protected Flow.Dispatcher getFlowDispatcher() {
        return new Flow.Dispatcher() {
            @Override
            public void dispatch(Flow.Traversal traversal, final Flow.TraversalCallback callback) {

                pathContainerView.dispatch(traversal, new Flow.TraversalCallback() {
                    @Override
                    public void onTraversalCompleted() {
                        callback.onTraversalCompleted();
                    }
                });
            }
        };
    }

    @Override
    public void onBackPressed() {
        if (pathContainerView.onBackPressed()) {
            return;
        }
        if (flowDelegate.onBackPressed()) {
            return;
        }

        super.onBackPressed();
    }
}