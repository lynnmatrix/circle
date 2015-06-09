package com.jadenine.circle.ui;

import android.app.ActionBar;
import android.view.MenuItem;

import com.jadenine.circle.R;
import com.jadenine.circle.mortar.MortarPathContainerView;
import com.jadenine.circle.ui.ap.ApListPath;

import butterknife.InjectView;
import flow.Flow;
import flow.path.Path;

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
                Path path = traversal.destination.top();
                setTitle(path.getClass().getSimpleName());
                ActionBar actionBar = HomeActivity.this.getActionBar();
                boolean canGoBack = traversal.destination.size() > 1;
                actionBar.setDisplayHomeAsUpEnabled(canGoBack);
                actionBar.setHomeButtonEnabled(canGoBack);

                pathContainerView.dispatch(traversal, new Flow.TraversalCallback() {
                    @Override
                    public void onTraversalCompleted() {
                        invalidateOptionsMenu();
                        callback.onTraversalCompleted();
                    }
                });
            }
        };
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
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