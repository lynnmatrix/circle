package com.jadenine.circle.domain.dagger;

import com.google.gson.Gson;
import com.jadenine.circle.domain.Account;
import com.jadenine.circle.domain.Constants;
import com.jadenine.circle.model.db.ApDBService;
import com.jadenine.circle.model.db.BombDBService;
import com.jadenine.circle.model.db.DirectMessageDBService;
import com.jadenine.circle.model.db.TimelineCursorDBService;
import com.jadenine.circle.model.db.TimelineDBService;
import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.model.entity.DirectMessageEntity;
import com.jadenine.circle.model.entity.Image;
import com.jadenine.circle.model.entity.UserApEntity;
import com.jadenine.circle.model.rest.ApService;
import com.jadenine.circle.model.rest.AzureBlobUploader;
import com.jadenine.circle.model.rest.BombService;
import com.jadenine.circle.model.rest.DirectMessageService;
import com.jadenine.circle.model.rest.ImageService;
import com.jadenine.circle.model.rest.TimelineRangeResult;

import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Singleton;

import dagger.Provides;
import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Created by linym on 7/3/15.
 */
@dagger.Module
public class TestDomainModule {

    private final String deviceId;
    private final String USER_AP_MAC = "test_ap";
    private final String USER_AP_SSID = "test_ssid";

    private final UserApEntity userApEntity;
    private final List<UserApEntity> apList = new LinkedList<>();

    private final List<DirectMessageEntity> directMessageList= new LinkedList<>();

    private final List<Bomb> bombList = new LinkedList<>();

    public TestDomainModule(String deviceId) {
        this.deviceId = deviceId;

        userApEntity = new UserApEntity(deviceId, USER_AP_MAC, USER_AP_SSID);
        apList.add(userApEntity);

        directMessageList.add(genTestDirectMessage(4));//id:6
        directMessageList.add(genTestDirectMessage(3));//id:7
        directMessageList.add(genTestDirectMessage(2));//id:8
        directMessageList.add(genTestDirectMessage(1));//id:9

        bombList.add(genMyTopicBomb(1));
        bombList.add(genMyTopicBomb(2));
        bombList.add(genMyTopicBomb(3));
        bombList.add(genMyTopicBomb(4));
        bombList.add(genMyTopicBomb(5));

        bombList.add(genSomeoneStartedBomb(11));
        bombList.add(genSomeoneStartedBomb(12));
        bombList.add(genSomeoneStartedBomb(13));
        bombList.add(genSomeoneStartedBomb(14));
        bombList.add(genSomeoneStartedBomb(15));
        bombList.add(genSomeoneStartedBomb(16));
    }

    private Bomb genMyTopicBomb(int id) {
        String someone = "someone";
        Bomb bomb = spy(new Bomb(USER_AP_MAC, 1== id%2?deviceId:someone));
        doReturn(""+id).when(bomb).getMessageId();
        bomb.setRootMessageId("1");
        bomb.setRootUser(deviceId);
        bomb.setTo(1 == id % 2 ? someone : deviceId);
        bomb.setContent("message " + id);
        return bomb;
    }

    private Bomb genSomeoneStartedBomb(int id) {
        String someone = "someone";
        Bomb bomb = spy(new Bomb(USER_AP_MAC, 1== id%2?someone:deviceId));
        doReturn(""+id).when(bomb).getMessageId();
        bomb.setRootMessageId("1");
        bomb.setRootUser(someone);
        bomb.setTo(1 == id % 2 ? deviceId : someone);
        bomb.setContent("message " + id);
        return bomb;
    }

    private DirectMessageEntity genTestDirectMessage(int number) {
        DirectMessageEntity chatMessage = spy(new DirectMessageEntity(USER_AP_MAC, "test_topic_id",
                1==number%2?"test_from":"test_from", 1==number % 2 ? "test_to":"test_from"));
        chatMessage.setRootMessageId("9");
        chatMessage.setRootUser("test_from");
        doReturn(String.valueOf(10-number)).when(chatMessage).getMessageId();
        chatMessage.setContent("message " + number);

        return chatMessage;
    }

    @Provides
    @Singleton
    Account provideAccount() {
        return new Account(deviceId);
    }

    @Provides
    @Singleton
    Gson provideGson() {
        return new Gson();
    }

    @Provides
    @Singleton
    ApService provideApRestService() {
        ApService mockService = mock(ApService.class);
        when(mockService.listAPs(eq(deviceId))).thenReturn(Observable.just(new TimelineRangeResult<>
                (apList, false, null)));

        when(mockService.addAP(Matchers.<UserApEntity>any())).thenReturn(Observable.just(new TimelineRangeResult<>(apList, false, null)));

        doAnswer(new Answer<Observable<TimelineRangeResult<UserApEntity>>>() {
                        @Override
            public Observable<TimelineRangeResult<UserApEntity>> answer(InvocationOnMock invocationOnMock)
                                throws Throwable {
                            UserApEntity anyApEntity = (UserApEntity) invocationOnMock.getArguments()[0];
                            apList.add(anyApEntity);
                            return Observable.just(new TimelineRangeResult<>(apList, false, null));
            }
        }).when(mockService).addAP(any(UserApEntity.class));


        return mockService;
    }


    @Provides
    @Singleton
    DirectMessageService provideChatService() {
        DirectMessageService mockService = mock(DirectMessageService.class);
        when(mockService.listMessages(Matchers.anyString(), eq(2), Matchers.isNull(Long.class),
                Matchers.isNull(Long.class)))
                .thenReturn(Observable.just(new TimelineRangeResult<>(directMessageList.subList
                        (0, 2), true, "8")));

        when(mockService.listMessages(Matchers.anyString(), eq(2), Matchers.isNull(Long.class),
                eq(7l)))
                .thenReturn(Observable.just(new TimelineRangeResult<>(directMessageList.subList
                        (2, 4), false, null)));

        return mockService;
    }

    @Provides
    @Singleton
    BombService provideBombService() {
        BombService mockService = mock(BombService.class);

        when(mockService.myTopicsTimeline(Matchers.anyString(), eq(Constants.PAGE_SIZE), Matchers.isNull(Long.class),
                Matchers.isNull(Long.class)))
                .thenReturn(Observable.just(new TimelineRangeResult<>(bombList, false, null)));

        return mockService;
    }

    @Provides
    @Singleton
    ImageService provideImageService() {
        ImageService mockService = mock(ImageService.class);
        when(mockService.getWritableSas()).thenReturn(Observable.<Image>empty());
        return mockService;
    }

    @Provides
    @Singleton
    AzureBlobUploader provideBlobUploader(){
        AzureBlobUploader mockUploader = mock(AzureBlobUploader.class);
        when(mockUploader.upload(Matchers.anyString(), Matchers.<InputStream>any(), Matchers
                .anyString()))
                .thenReturn(true);

        return new AzureBlobUploader();
    }

    @Provides
    @Singleton
    ApDBService provideApDBService(){
        ApDBService mockService = mock(ApDBService.class);
        when(mockService.listAps()).thenReturn(Observable.<List<UserApEntity>>just(new
                ArrayList<UserApEntity>()));
        return mockService;
    }

    @Provides
    @Singleton
    DirectMessageDBService provideChatDBService() {
        return mock(DirectMessageDBService.class);
    }


    @Provides
    @Singleton
    TimelineDBService provideTimelineDBService() {
        return mock(TimelineDBService.class);
    }


    @Provides
    @Singleton
    TimelineCursorDBService provideTimelineCursorDBService() {
        return mock(TimelineCursorDBService.class);
    }

    @Provides
    @Singleton
    BombDBService provideBombDBService() {
        return mock(BombDBService.class);
    }


}
