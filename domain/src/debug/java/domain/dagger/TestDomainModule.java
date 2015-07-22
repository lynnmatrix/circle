package domain.dagger;

import com.jadenine.circle.domain.Account;
import com.jadenine.circle.model.db.ApDBService;
import com.jadenine.circle.model.db.MessageDBService;
import com.jadenine.circle.model.db.TopicDBService;
import com.jadenine.circle.model.db.impl.BombDBService;
import com.jadenine.circle.model.db.impl.DirectMessageDBService;
import com.jadenine.circle.model.db.impl.TimelineCursorDBService;
import com.jadenine.circle.model.db.impl.TimelineStateDBService;
import com.jadenine.circle.model.entity.DirectMessageEntity;
import com.jadenine.circle.model.entity.Image;
import com.jadenine.circle.model.entity.MessageEntity;
import com.jadenine.circle.model.entity.TopicEntity;
import com.jadenine.circle.model.entity.UserApEntity;
import com.jadenine.circle.model.rest.ApService;
import com.jadenine.circle.model.rest.AzureBlobUploader;
import com.jadenine.circle.model.rest.BombService;
import com.jadenine.circle.model.rest.DirectMessageService;
import com.jadenine.circle.model.rest.ImageService;
import com.jadenine.circle.model.rest.JSONListWrapper;
import com.jadenine.circle.model.rest.MessageService;
import com.jadenine.circle.model.rest.TimelineResult;
import com.jadenine.circle.model.rest.TopicService;

import org.mockito.Matchers;
import org.mockito.internal.matchers.GreaterOrEqual;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Singleton;

import dagger.Provides;
import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.longThat;
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
    private final TopicEntity topicEntity;
    private final MessageEntity messageEntity;
    private final List<UserApEntity> apList = new LinkedList<>();
    private final List<TopicEntity> topicList = new LinkedList<>();

    private final List<DirectMessageEntity> directMessageList= new LinkedList<>();
    public TestDomainModule(String deviceId) {
        this.deviceId = deviceId;

        userApEntity = new UserApEntity(deviceId, USER_AP_MAC, USER_AP_SSID);
        apList.add(userApEntity);

        topicEntity = spy(new TopicEntity(USER_AP_MAC, deviceId, "topic 1"));
        topicList.add(topicEntity);
        topicEntity.setMessageCount(1);
        topicEntity.setTimestamp(System.currentTimeMillis());
        doReturn("topicId").when(topicEntity).getTopicId();

        messageEntity = spy(new MessageEntity(USER_AP_MAC, topicEntity.getTopicId()));
        messageEntity.setContent("This is message.");
        messageEntity.setTimestamp(System.currentTimeMillis());
        doReturn("messageId").when(messageEntity).getMessageId();

        topicEntity.setMessages(Collections.singletonList(messageEntity));

        directMessageList.add(genTestDirectMessage(4));//id:6
        directMessageList.add(genTestDirectMessage(3));//id:7
        directMessageList.add(genTestDirectMessage(2));//id:8
        directMessageList.add(genTestDirectMessage(1));//id:9

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
    ApService provideApRestService() {
        ApService mockService = mock(ApService.class);
        when(mockService.listAPs(eq(deviceId))).thenReturn(Observable.just(new JSONListWrapper<>
                (apList, false, null)));

        when(mockService.addAP(Matchers.<UserApEntity>any())).thenReturn(Observable.just(new
                JSONListWrapper<>(apList, false, null)));

        doAnswer(new Answer<Observable<JSONListWrapper<UserApEntity>>>() {
                        @Override
            public Observable<JSONListWrapper<UserApEntity>> answer(InvocationOnMock invocationOnMock)
                                throws Throwable {
                            UserApEntity anyApEntity = (UserApEntity) invocationOnMock.getArguments()[0];
                            apList.add(anyApEntity);
                            return Observable.just(new JSONListWrapper<>(apList, false, null));
            }
        }).when(mockService).addAP(any(UserApEntity.class));


        return mockService;
    }

    @Provides
    @Singleton
    TopicService provideTopicRestService() {
        TopicService mockService = mock(TopicService.class);
        TimelineResult<TopicEntity> topics = new TimelineResult<>(topicList, Collections
                .<TopicEntity>emptyList(), false, null);

        when(mockService.refresh(eq(USER_AP_MAC), Matchers.anyInt(), Matchers.isNull(String
                .class), eq(-1l))).thenReturn(Observable.just(topics));

        String oldestTopicId = topicEntity.getTopicId();
        long latestTimestamp = topicEntity.getMessages().get(topicEntity.getMessages().size() -
                1).getTimestamp();
        when(mockService.refresh(eq(USER_AP_MAC), Matchers.anyInt(), eq(oldestTopicId), longThat
                (new GreaterOrEqual<>(latestTimestamp)))).thenReturn(Observable.just(new
                TimelineResult<TopicEntity>()));
        return mockService;
    }

    @Provides
    @Singleton
    MessageService provideMessageService() {
        MessageService mockService = mock(MessageService.class);
        when(mockService.listMessages(Matchers.anyString(), Matchers.anyString(), Matchers
                .anyString())).thenReturn(Observable.just(new JSONListWrapper<MessageEntity>()));
        return mockService;
    }

    @Provides
    @Singleton
    DirectMessageService provideChatService() {
        DirectMessageService mockService = mock(DirectMessageService.class);
        when(mockService.listMessages(Matchers.anyString(), eq(2), Matchers
                .isNull(Long.class), Matchers.isNull(Long.class)))
                .thenReturn(Observable.just(new JSONListWrapper<>(directMessageList.subList(0, 2),
                        true, "8")));

        when(mockService.listMessages(Matchers.anyString(), eq(2), Matchers.isNull(Long.class),
                eq(7l)))
                .thenReturn(Observable.just(new JSONListWrapper<>(directMessageList.subList(2, 4)
                        , false, null)));

        return mockService;
    }

    @Provides
    @Singleton
    BombService provideBombService() {
        return mock(BombService.class);
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
    TopicDBService provideTopicDBService() {
        TopicDBService mockService = mock(TopicDBService.class);
        when(mockService.listTopics(anyString())).thenReturn(Observable.<List<TopicEntity>>just
                (new ArrayList<TopicEntity>()));
        return mockService;
    }

    @Provides
    @Singleton
    MessageDBService provideMessageDBService() {
        MessageDBService mockService = mock(MessageDBService.class);
        doReturn(Observable.<List<MessageEntity>>just(new ArrayList<MessageEntity>())).when
                (mockService)
                .listMessages(anyString());
        return mockService;
    }

    @Provides
    @Singleton
    TimelineStateDBService provideTimelineStateService(){
        TimelineStateDBService mockService = mock(TimelineStateDBService.class);
        return mockService;
    }

    @Provides
    @Singleton
    DirectMessageDBService provideChatDBService() {
        return mock(DirectMessageDBService.class);
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
