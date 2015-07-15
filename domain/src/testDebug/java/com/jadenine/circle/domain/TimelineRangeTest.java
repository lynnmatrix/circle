package com.jadenine.circle.domain;

import android.support.annotation.NonNull;

import com.jadenine.circle.model.rest.JSONListWrapper;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by linym on 7/15/15.
 */
public class TimelineRangeTest {

    public static final String TIMELINE = "timeline";

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
    }

    private static class Loader implements RangeLoader<Id>{

        @Override
        public Observable<JSONListWrapper<Id>> refresh(Long top, Integer count) {
            return null;
        }

        @Override
        public Observable<JSONListWrapper<Id>> loadMore(Long bottom, Integer Count) {
            return null;
        }
    }
}