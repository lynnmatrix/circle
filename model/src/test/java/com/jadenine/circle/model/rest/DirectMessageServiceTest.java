package com.jadenine.circle.model.rest;

import com.jadenine.circle.model.entity.DirectMessageEntity;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;
import rx.Observable;
import rx.Observer;
import rx.functions.Action1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by linym on 7/20/15.
 */
public class DirectMessageServiceTest {
    public static final String ENDPOINT_LOCAL = "http://192.168.9.117:8080";
    DirectMessageService service;
    @Before
    public void setUp() throws Exception {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(ENDPOINT_LOCAL)
                .build();
        service = restAdapter.create(DirectMessageService.class);
    }

    @Test
    public void testListMessages() throws Exception {
        Observable<TimelineRangeResult<DirectMessageEntity>> observable = service.listMessages
                ("test_to", null, null, null);
        final CountDownLatch latch = new CountDownLatch(1);
        observable.subscribe(new Observer<TimelineRangeResult<DirectMessageEntity>>() {
            @Override
            public void onCompleted() {
                latch.countDown();
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(TimelineRangeResult<DirectMessageEntity>
                                       directMessageEntityTimelineRangeResult) {
                assertFalse(directMessageEntityTimelineRangeResult.getAll().isEmpty());
            }
        });
        assertTrue(latch.await(10, TimeUnit.SECONDS));
    }

    @Test
    public void testAddMessage() throws Exception {
        DirectMessageEntity messageEntity = new DirectMessageEntity("test_ap", "test_topic",
                "test_from", "test_to");
        messageEntity.setContent("test content");
        final CountDownLatch latch = new CountDownLatch(1);
        service.addMessage(messageEntity).subscribe(new Action1<DirectMessageEntity>() {
            @Override
            public void call(DirectMessageEntity directMessageEntity) {
                assertNotNull(directMessageEntity.getRootMessageId());
                assertEquals(directMessageEntity.getRootMessageId(), directMessageEntity
                        .getMessageId());
                assertEquals("test_from", directMessageEntity.getFrom());
                assertEquals("test_to", directMessageEntity.getTo());
                assertEquals(directMessageEntity.getRootUser(), directMessageEntity.getFrom());
                latch.countDown();
            }
        });
        assertTrue(latch.await(10, TimeUnit.SECONDS));
    }

    @Test
    public void testReply() throws InterruptedException {
        final String rootMessageId = String.valueOf(9223370599474055111l);

        final DirectMessageEntity messageEntity = new DirectMessageEntity("test_ap", "test_topic",
                "test_from", "test_to");
        messageEntity.setRootMessageId(rootMessageId);
        messageEntity.setRootUser("test_from");
        messageEntity.setContent("test content");
        final CountDownLatch latch = new CountDownLatch(1);
        service.addMessage(messageEntity).subscribe(new Action1<DirectMessageEntity>() {
            @Override
            public void call(DirectMessageEntity directMessageEntity) {
                assertEquals(messageEntity.getRootMessageId(), directMessageEntity.getRootMessageId());
                assertNotEquals(directMessageEntity.getRootMessageId(), directMessageEntity.getMessageId());

                assertEquals(messageEntity.getRootUser(), directMessageEntity.getRootUser());
                latch.countDown();
            }
        });
        assertTrue(latch.await(10, TimeUnit.SECONDS));
    }
}