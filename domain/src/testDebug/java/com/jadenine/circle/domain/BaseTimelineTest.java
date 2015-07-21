package com.jadenine.circle.domain;

import android.os.Build;

import com.jadenine.circle.domain.dagger.DaggerService;
import com.jadenine.circle.model.db.impl.DirectMessageDBService;
import com.jadenine.circle.model.db.impl.TimelineCursorDBService;
import com.jadenine.circle.model.entity.DirectMessageEntity;
import com.jadenine.circle.model.rest.DirectMessageService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import domain.dagger.DaggerTestDomainComponent;
import domain.dagger.TestDomainModule;
import rx.Observer;
import rx.functions.Action1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by linym on 7/20/15.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.JELLY_BEAN)
public class BaseTimelineTest {
    public static final String DEVICE_ID = "DEVICE_ID";
    BaseTimeline timeline;
    Account account;
    DirectMessageService messageService;
    DirectMessageDBService messageDBService;
    TimelineCursorDBService cursorDBService;

    @Before
    public void setUp(){
        DaggerService.setComponent(DaggerTestDomainComponent.builder().testDomainModule(new
                TestDomainModule(DEVICE_ID)).build());
        account = DaggerService.getDomainComponent().getAccount();
        messageService = DaggerService.getDomainComponent().getDirectMessageService();
        messageDBService = DaggerService.getDomainComponent().getDirectMessageDBService();
        cursorDBService = DaggerService.getDomainComponent().getTimelineRangeCursorDBService();
        timeline = new BaseTimeline("TEST", new ChatLoader(account, messageService,
                messageDBService, cursorDBService, 2));
    }

    @Test
    public void testGetFirstRange() throws Exception {


    }

    @Test
    public void testGetLastRange() throws Exception {

    }

    @Test
    public void testGetRange() throws Exception {

    }

    @Test
    public void testGetTimelineId() throws Exception {

    }

    @Test
    public void testRefresh() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        timeline.refresh().subscribe(new Observer<List<TimelineRange<DirectMessageEntity>>>() {
            @Override
            public void onCompleted() {
                assertNotNull(timeline.getFirstRange());
                latch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                fail();
            }

            @Override
            public void onNext(List<TimelineRange<DirectMessageEntity>> timelineRanges) {
                assertFalse(timelineRanges.isEmpty());
                assertEquals(1, timelineRanges.size());
                TimelineRange<DirectMessageEntity> range = timelineRanges.get(0);
                assertEquals(2, range.getAll().size());
                assertEquals(1, range.getAllGroups().size());

            }
        });
        assertTrue(latch.await(1, TimeUnit.SECONDS));
    }

    @Test
    public void testLoadMore() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        timeline.refresh().subscribe(new Action1() {
            @Override
            public void call(Object o) {
                timeline.loadMore().subscribe(new Observer<List<TimelineRange<DirectMessageEntity>>>() {
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
                    public void onNext(List<TimelineRange<DirectMessageEntity>> timelineRanges) {
                        assertFalse(timelineRanges.isEmpty());
                        assertEquals(1, timelineRanges.size());
                        TimelineRange<DirectMessageEntity> range = timelineRanges.get(0);
                        assertEquals(4, range.getAll().size());
                        assertEquals(1, range.getAllGroups().size());
                    }
                });
            }
        });

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    @Test
    public void testHasMore() throws Exception {

    }
}