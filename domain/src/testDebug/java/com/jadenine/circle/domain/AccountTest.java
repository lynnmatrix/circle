package com.jadenine.circle.domain;

import com.jadenine.circle.domain.dagger.DaggerService;
import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.model.entity.UserApEntity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import domain.dagger.DaggerTestDomainComponent;
import domain.dagger.TestDomainModule;
import rx.Observer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Created by linym on 7/3/15.
 */
public class AccountTest {

    public static final String DEVICE_ID = "deviceID";
    private Account account;

    @Before
    public void setUp(){
        DaggerService.setComponent(DaggerTestDomainComponent.builder().testDomainModule(new
                TestDomainModule(DEVICE_ID)).build());
        account = DaggerService.getDomainComponent().getAccount();
        //TODO Waiting for gradle plugin 1.3+, walk around by define Test dagger in debug rather than test.
    }

    @Test
    public void testGetDeviceId() throws Exception {
        Assert.assertEquals(DEVICE_ID, account.getDeviceId());
    }

    @Test
    public void testAddUserAp() throws Exception {
        assertNotNull(account.getUserAps());
        Assert.assertTrue(account.getUserAps().isEmpty());

        final UserAp ap = new UserAp(new UserApEntity(DEVICE_ID, "mac", "ssid"));

        account.addUserAp(ap).subscribe(new Observer<List<UserAp>>() {
            @Override
            public void onCompleted() {
                Assert.assertEquals(1, account.getUserAps().size());

                Assert.assertEquals(ap, account.getUserAps().get(0));
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<UserAp> userAps) {

            }
        });

    }

    @Test
    public void testListAPs() throws Exception {

        final CountDownLatch latch = new CountDownLatch(1);
        account.listAPs().subscribe(new Observer<List<UserAp>>() {
            @Override
            public void onCompleted() {
                latch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                fail();
            }

            @Override
            public void onNext(List<UserAp> userAps) {

            }
        });

        Assert.assertTrue(latch.await(100, TimeUnit.SECONDS));

        assertFalse(account.getUserAps().isEmpty());
    }

    @Test
    public void testListChat() throws InterruptedException {
        /*final CountDownLatch latch = new CountDownLatch(1);
        account.listChat().subscribe(new Observer<List<Chat>>() {
            @Override
            public void onCompleted() {
                latch.countDown();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<Chat> chats) {

            }
        });
        assertTrue(latch.await(10, TimeUnit.SECONDS));*/
    }

    @Test
    public void testMyTopics() throws InterruptedException {
        assertNotNull(account.getMyTopics());

        final CountDownLatch latch = new CountDownLatch(1);
        account.refreshMyTopics().subscribe(new Observer<List<TimelineRange<Bomb>>>() {
            @Override
            public void onCompleted() {
                latch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                fail();
            }

            @Override
            public void onNext(List<TimelineRange<Bomb>> timelineRanges) {

            }
        });
        assertTrue(latch.await(10, TimeUnit.SECONDS));
    }
}