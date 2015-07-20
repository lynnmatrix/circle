package com.jadenine.circle.domain;

import com.jadenine.circle.domain.dagger.DaggerService;
import com.jadenine.circle.model.rest.DirectMessageService;

import org.junit.Before;
import org.junit.Test;

import domain.dagger.DaggerTestDomainComponent;
import domain.dagger.TestDomainModule;

/**
 * Created by linym on 7/20/15.
 */
public class BaseTimelineTest {
    public static final String DEVICE_ID = "DEVICE_ID";
    BaseTimeline timeline;
    Account account;
    DirectMessageService messageService;

    @Before
    public void setUp(){
        DaggerService.setComponent(DaggerTestDomainComponent.builder().testDomainModule(new
                TestDomainModule(DEVICE_ID)).build());
        account = DaggerService.getDomainComponent().getAccount();
        messageService = DaggerService.getDomainComponent().getDirectMessageService();
        timeline = new BaseTimeline(new ChatLoader(account, messageService)) {
            @Override
            protected String getTimelineId() {
                return "test";
            }
        };
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

    }

    @Test
    public void testLoadMore() throws Exception {

    }

    @Test
    public void testHasMore() throws Exception {

    }
}