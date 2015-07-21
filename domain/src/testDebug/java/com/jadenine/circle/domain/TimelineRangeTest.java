package com.jadenine.circle.domain;

import android.support.annotation.NonNull;

import com.jadenine.circle.domain.dagger.DaggerService;
import com.jadenine.circle.model.Identifiable;
import com.jadenine.circle.model.entity.DirectMessageEntity;
import com.jadenine.circle.model.rest.DirectMessageService;
import com.jadenine.circle.model.rest.JSONListWrapper;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import domain.dagger.DaggerTestDomainComponent;
import domain.dagger.TestDomainModule;
import rx.Observable;
import rx.Observer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by linym on 7/15/15.
 */
public class TimelineRangeTest {

    public static final String TIMELINE = "timeline";
    public static final String DEVICE_ID = "DEVICE_ID";

    @Test
    public void testConstructorWithEmptyRange() {
        List list = new ArrayList<>();
        TimelineRange range = new TimelineRange(TIMELINE, list, new Loader());
        assertTrue(range.hasMore());
        assertTrue(range.getAll().isEmpty());
    }

    @Test
    public void testConstructor() {
        List list = new ArrayList<>();
        list.add(new Id(1l));
        list.add(new Id(2l));

        TimelineRange<Id> range = new TimelineRange(TIMELINE, list, new Loader());
        assertTrue(range.hasMore());
        assertFalse(range.getAll().isEmpty());

        assertEquals(Long.valueOf(1l), range.getAll().get(0).getId());
        assertEquals(Long.valueOf(2l), range.getAll().get(1).getId());
    }

    @Test
    public void testContact(){
        List list1 = new ArrayList<>();
        list1.add(new Id(1l));
        list1.add(new Id(2l));

        TimelineRange<Id> range1 = new TimelineRange<>(TIMELINE, list1, new Loader());
        range1.cursor.setHasMore(false);

        List list2 = new ArrayList<>();
        list1.add(new Id(3l));
        list1.add(new Id(4l));

        TimelineRange<Id> range2 = new TimelineRange<>(TIMELINE, list2, new Loader());

        range1.contact(range2);
        assertTrue(range1.hasMore());
        assertEquals(range2.cursor.getBottom(), range1.cursor.getBottom());
        assertEquals(4, range1.getAll().size());
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalContact(){
        List list1 = new ArrayList<>();
        list1.add(new Id(1l));
        list1.add(new Id(2l));

        TimelineRange<Id> range1 = new TimelineRange<>(TIMELINE, list1, new Loader());

        List list2 = new ArrayList<>();
        list1.add(new Id(3l));
        list1.add(new Id(4l));

        TimelineRange<Id> range2 = new TimelineRange<>(TIMELINE, list2, new Loader());

        range1.contact(range2);

    }

    @Test
    public void testSubRange(){
        List list = new ArrayList<>();
        list.add(new Id(1l));
        list.add(new Id(2l));

        list.add(new Id(4l));
        list.add(new Id(5l));

        list.add(new Id(8l));
        list.add(new Id(10l));

        TimelineRange<Id> range = new TimelineRange<>(TIMELINE, list, new Loader());

        assertEquals(5, range.getSubRange(1l, 10l).size());
        assertEquals(6, range.getSubRange(1l, 11l).size());
        assertEquals(0, range.getSubRange(1l, 1l).size());

        List<Id> range2_8 = range.getSubRange(2l, 8l);
        assertEquals(Long.valueOf(2l), range2_8.get(0).getId());
        assertEquals(Long.valueOf(5l), range2_8.get(range2_8.size() - 1).getId());

        List<Id> range3_7 = range.getSubRange(3l, 7l);
        assertEquals(Long.valueOf(4l), range3_7.get(0).getId());
        assertEquals(Long.valueOf(5l), range3_7.get(range3_7.size() - 1).getId());
    }

    @Test
    public void testRefresh() throws InterruptedException {
        DaggerService.setComponent(DaggerTestDomainComponent.builder().testDomainModule(new
                TestDomainModule(DEVICE_ID)).build());
        Account account = DaggerService.getDomainComponent().getAccount();
        DirectMessageService messageService = DaggerService.getDomainComponent()
                .getDirectMessageService();

        TimelineRange<DirectMessageEntity> range = new TimelineRange<>(TIMELINE, new
                ArrayList<DirectMessageEntity>(), new ChatLoader(account, messageService, 2));
        final CountDownLatch latch = new CountDownLatch(1);
        range.refresh().subscribe(new Observer<TimelineRange<DirectMessageEntity>>() {
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
            public void onNext(TimelineRange<DirectMessageEntity>
                                       directMessageEntityTimelineRange) {

            }
        });

        assertTrue(latch.await(1, TimeUnit.SECONDS));
    }

    @Test
    public void testLoadMore() throws InterruptedException {
        DaggerService.setComponent(DaggerTestDomainComponent.builder().testDomainModule(new
                TestDomainModule(DEVICE_ID)).build());
        Account account = DaggerService.getDomainComponent().getAccount();
        DirectMessageService messageService = DaggerService.getDomainComponent()
                .getDirectMessageService();

        TimelineRange<DirectMessageEntity> range = new TimelineRange<>(TIMELINE, new
                ArrayList<DirectMessageEntity>(), new ChatLoader(account, messageService, 2));
        final CountDownLatch latch = new CountDownLatch(1);
        range.loadMore().subscribe(new Observer<TimelineRange<DirectMessageEntity>>() {
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
            public void onNext(TimelineRange<DirectMessageEntity>
                                       directMessageEntityTimelineRange) {

            }
        });

        assertTrue(latch.await(1, TimeUnit.SECONDS));

    }

    private class Id implements Identifiable<Long> {
        private final Long id;
        public Id(Long id){
            this.id = id;
        }

        @NonNull
        @Override
        public Long getId() {
            return id;
        }

        @NonNull
        @Override
        public Long getGroupId() {
            return id;
        }
    }

    private static class Loader implements RangeLoader<Id>{

        @Override
        public Observable<JSONListWrapper<Id>> refresh(Long top) {
            return null;
        }

        @Override
        public Observable<JSONListWrapper<Id>> loadMore(Long bottom) {
            return null;
        }
    }
}