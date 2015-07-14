package com.jadenine.circle.domain;

import com.jadenine.circle.domain.dagger.DaggerService;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import domain.dagger.DaggerTestDomainComponent;
import domain.dagger.TestDomainModule;
import rx.Observer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by linym on 7/13/15.
 */
public class UserApTest {
    public static final String DEVICE_ID = "deviceID";
    private Account account;
    private UserAp userAp;
    @Before
    public void setUp() throws InterruptedException {
        DaggerService.setComponent(DaggerTestDomainComponent.builder().testDomainModule(new
                TestDomainModule(DEVICE_ID)).build());
        account = DaggerService.getDomainComponent().getAccount();
        final CountDownLatch latch = new CountDownLatch(1);
        account.listAPs().subscribe(new Observer<List<UserAp>>() {
            @Override
            public void onCompleted() {
                latch.countDown();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<UserAp> userAps) {

            }
        });

        assertTrue(latch.await(1, TimeUnit.SECONDS));
        userAp = account.getUserAps().get(0);
    }

    @Test
    public void testEntity(){
        assertNotNull(userAp.getEntity());
        assertEquals("TMAC", userAp.getAP());
        assertEquals(DEVICE_ID, userAp.getUser());
        assertEquals("TSSID", userAp.getSSID());
    }

    @Test
    public void testMerge() throws Exception {

    }

    @Test
    public void testRefreshTopic() throws Exception {

    }

    @Test
    public void testLoadMore() throws Exception {

    }

    @Test
    public void testGetTopic() throws Exception {
        assertNotNull(userAp.getTopic("topicId"));
    }

    @Test
    public void testConnect() throws Exception {

    }
}