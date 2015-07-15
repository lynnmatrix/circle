package com.jadenine.circle.model.state;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by linym on 7/15/15.
 */
public class TimelineRangeCursorTest {

    @Test
    public void testDefaultValue() throws Exception {
        String timeline = "timeline";
        TimelineRangeCursor cursor = new TimelineRangeCursor(timeline);
        assertEquals(timeline, cursor.getTimeline());
        assertNull(cursor.getTop());
        assertNull(cursor.getBottom());
        assertTrue(cursor.hasMore);
    }

    @Test
    public void testSetter(){
        TimelineRangeCursor cursor = new TimelineRangeCursor("timeline");
        cursor.setTop(1l);
        cursor.setBottom(2l);

        assertEquals(Long.valueOf(1l), cursor.getTop());
        assertEquals(Long.valueOf(2l), cursor.getBottom());
    }

    @Test
    public void testCascadeSetter(){
        TimelineRangeCursor cursor1 = new TimelineRangeCursor("timeline");
        cursor1.setTop(1l);
        assertEquals(Long.valueOf(1l), cursor1.getBottom());

        TimelineRangeCursor cursor2 = new TimelineRangeCursor("timeline");
        cursor2.setBottom(2l);
        assertEquals(Long.valueOf(2l), cursor2.getTop());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidBottom(){
        TimelineRangeCursor cursor = new TimelineRangeCursor("timeline");
        cursor.setTop(2l);
        cursor.setBottom(1l);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidTop(){
        TimelineRangeCursor cursor = new TimelineRangeCursor("timeline");
        cursor.setBottom(2l);
        cursor.setTop(3l);
    }

    @Test
    public void testSetHasMore(){
        TimelineRangeCursor cursor = new TimelineRangeCursor("timeline");
        assertTrue(cursor.getHasMore());
        cursor.setHasMore(false);
        assertFalse(cursor.getHasMore());
    }

}