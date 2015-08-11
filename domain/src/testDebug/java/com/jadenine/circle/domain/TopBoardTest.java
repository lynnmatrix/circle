package com.jadenine.circle.domain;

import android.os.Build;

import com.jadenine.circle.domain.dagger.DaggerService;
import com.jadenine.circle.model.entity.Bomb;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import domain.dagger.DaggerTestDomainComponent;
import domain.dagger.TestDomainModule;
import rx.Observer;

import static org.junit.Assert.*;

/**
 * Created by linym on 8/11/15.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.JELLY_BEAN)
public class TopBoardTest {
    public static final String DEVICE_ID = "DEVICE_ID";
    TopBoard topBoard;

    @Before
    public void setup() {
        DaggerService.setComponent(DaggerTestDomainComponent.builder().testDomainModule(new
                TestDomainModule(DEVICE_ID)).build());

        TopLoader loader = new TopLoader(DEVICE_ID);
        DaggerService.getDomainComponent().inject(loader);
        topBoard = new TopBoard(loader, 10);
    }

    @Test
    public void testRefresh() throws Exception {
        assertNotNull(topBoard.getTops());

        final CountDownLatch latch = new CountDownLatch(1);
        topBoard.refresh().subscribe(new Observer<ArrayList<Group<Bomb>>>() {
            @Override
            public void onCompleted() {
                assertTrue(!topBoard.getTops().isEmpty());
                latch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                fail();
            }

            @Override
            public void onNext(ArrayList<Group<Bomb>> groups) {

            }
        });

        assertTrue(latch.await(10, TimeUnit.SECONDS));
    }
}