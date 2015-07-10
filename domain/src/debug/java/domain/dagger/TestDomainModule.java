package domain.dagger;

import com.jadenine.circle.domain.Account;
import com.jadenine.circle.model.db.ApDBService;
import com.jadenine.circle.model.db.MessageDBService;
import com.jadenine.circle.model.db.TopicDBService;
import com.jadenine.circle.model.entity.Image;
import com.jadenine.circle.model.entity.MessageEntity;
import com.jadenine.circle.model.entity.TopicEntity;
import com.jadenine.circle.model.entity.UserApEntity;
import com.jadenine.circle.model.rest.ApService;
import com.jadenine.circle.model.rest.AzureBlobUploader;
import com.jadenine.circle.model.rest.ImageService;
import com.jadenine.circle.model.rest.JSONListWrapper;
import com.jadenine.circle.model.rest.MessageService;
import com.jadenine.circle.model.rest.TopicService;

import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import javax.inject.Singleton;

import dagger.Provides;
import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by linym on 7/3/15.
 */
@dagger.Module
public class TestDomainModule {

    private final String deviceId;

    public TestDomainModule(String deviceId) {
        this.deviceId = deviceId;
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
        when(mockService.listAPs(Matchers.anyString())).thenReturn(Observable.<JSONListWrapper<UserApEntity>>empty());
        when(mockService.addAP(Matchers.<UserApEntity>any())).thenReturn(Observable
                .<JSONListWrapper<UserApEntity>>empty());

        doAnswer(new Answer<Observable<JSONListWrapper<UserApEntity>>>() {
                        @Override
            public Observable<JSONListWrapper<UserApEntity>> answer(InvocationOnMock invocationOnMock)
                                throws
                                Throwable {
                            UserApEntity anyApEntity = (UserApEntity) invocationOnMock.getArguments()[0];
                            return Observable.just(new JSONListWrapper<>(Collections
                                    .singletonList(anyApEntity), false, null));
            }
        }).when(mockService).addAP(any(UserApEntity.class));


        return mockService;
    }

    @Provides
    @Singleton
    TopicService provideTopicRestService() {
        TopicService mockService = mock(TopicService.class);
        when(mockService.listTopics(Matchers.anyString(), Matchers.anyInt(), Matchers.anyLong()))
                .thenReturn(Observable.<JSONListWrapper<TopicEntity>>empty());
        return mockService;
    }

    @Provides
    @Singleton
    MessageService provideMessageService() {
        MessageService mockServcie = mock(MessageService.class);
        when(mockServcie.listMessages(Matchers.anyString(), Matchers.anyString(), Matchers
                .anyString())).thenReturn(Observable.<JSONListWrapper<MessageEntity>>empty());
        return mockServcie;
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
        when(mockUploader.upload(Matchers.anyString(), Matchers.<InputStream>any(), Matchers.anyString()))
                .thenReturn(true);

        return new AzureBlobUploader();
    }


    @Provides
    @Singleton
    ApDBService provideApDBService(){
        ApDBService mockService = mock(ApDBService.class);
        when(mockService.listAps()).thenReturn(Observable
                .<List<UserApEntity>>empty());
        return mockService;
    }

    @Provides
    @Singleton
    TopicDBService provideTopicDBService() {
        TopicDBService mockService = mock(TopicDBService.class);
        when(mockService.listTopics(anyString())).thenReturn(Observable.<List<TopicEntity>>empty
                ());
        return mockService;
    }

    @Provides
    @Singleton
    MessageDBService provideMessageDBService() {
        MessageDBService mockService = mock(MessageDBService.class);
        doReturn(Observable.<List<MessageEntity>>empty()).when(mockService).listMessages(anyString());
        return mockService;
    }
}
