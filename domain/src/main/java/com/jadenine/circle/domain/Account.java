package com.jadenine.circle.domain;

import com.jadenine.circle.domain.dagger.DaggerService;
import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.model.entity.DirectMessageEntity;
import com.jadenine.circle.model.state.TimelineType;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by linym on 6/17/15.
 */
public class Account {
    public static final String MY_CHATS_TIME_LINE = "my_chats";

    private final String deviceId;

    private final ApSource apSource;

    private final BaseTimeline<DirectMessageEntity> chatTimeline;
    private final BaseTimeline<Bomb> myTopicsTimeline;
    @Inject
    ChatComposer bombComposer;

    public Account(String deviceId) {
        this.deviceId = deviceId;

        DaggerService.getDomainComponent().inject(this);

        ChatLoader chatLoader = new ChatLoader(deviceId, Constants.PAGE_SIZE);

        this.chatTimeline = new BaseTimeline<>(MY_CHATS_TIME_LINE, TimelineType.CHAT, chatLoader);
        this.apSource = new ApSource(deviceId);

        MyTopicLoader myTopicLoader = new MyTopicLoader(deviceId, Constants.PAGE_SIZE);
        DaggerService.getDomainComponent().inject(myTopicLoader);
        this.myTopicsTimeline = new BaseTimeline<>("my_topics", TimelineType.MY_TOPIC, myTopicLoader);
    }

    public String getDeviceId() {
        return deviceId;
    }

    public List<UserAp> getUserAps(){
        return apSource.getAps();
    }

    public UserAp getUserAp(String ap) {
        return apSource.getUserAp(ap);
    }

    public Observable<List<UserAp>> listAPs() {
        return apSource.list();
    }

    Observable<List<UserAp>> addUserAp(final UserAp userAp) {
        return apSource.addUserAp(userAp);
    }

    public UserAp getDefaultAp() {
        //TODO
        UserAp ap = null;
        if(getUserAps().size() > 0) {
            ap = getUserAps().get(0);
        }
        return ap;
    }

    //<editor-fold desc="chat">
    public Group<DirectMessageEntity> getChat(String ap, Long bombGroupId, String rootUser, Long
            rootMessageId) {
        if(null != rootMessageId) {
            return chatTimeline.getRange(rootMessageId).getGroup(rootMessageId);
        }

        for(TimelineRange<DirectMessageEntity> range : chatTimeline.getAllRanges()) {
            for(Group<DirectMessageEntity> group : range.getAllGroups()) {
                List<DirectMessageEntity> messages = group.getEntities();
                if(messages.size() > 0) {
                    DirectMessageEntity firstMessage = messages.get(0);
                    if(firstMessage.getAp().equals(ap)
                            && Long.valueOf(firstMessage.getTopicId()).equals(bombGroupId)
                            && firstMessage.getRootUser().equals(rootUser)){
                        return group;
                    }
                }
            }
        }
        return null;
    }

    public Observable<List<TimelineRange<DirectMessageEntity>>> refreshChats(){
        return chatTimeline.refresh();
    }

    public Observable<List<TimelineRange<DirectMessageEntity>>> loadMoreChat() {
        return chatTimeline.loadMore();
    }

    public Observable<List<TimelineRange<DirectMessageEntity>>> loadMoreChat(TimelineRange range) {
        return chatTimeline.loadMore(range);
    }

    public Observable<DirectMessageEntity> publish(DirectMessageEntity chatMessage) {
        Observable<DirectMessageEntity> observable = bombComposer.send(chatMessage)
                .map(new Func1<DirectMessageEntity, DirectMessageEntity>() {
                    @Override
                    public DirectMessageEntity call(DirectMessageEntity bomb1) {
                        chatTimeline.addPublished(bomb1);
                        return bomb1;
                    }
                });

        return observable;
    }

    public List<TimelineRange<DirectMessageEntity>> getAllChats() {
        return chatTimeline.getAllRanges();
    }

    public boolean hasMoreChat() {
        return chatTimeline.hasMore();
    }

    public void setHasUnreadChat(boolean hasUnread) {
        chatTimeline.setHasUnread(hasUnread);
    }

    public boolean hasUnreadChat() {
        return chatTimeline.getHasUnread();
    }
    //</editor-fold>

    //<editor-fold desc="my topics">

    public Observable<List<TimelineRange<Bomb>>> refreshMyTopics(){
        return myTopicsTimeline.refresh();
    }

    public Observable<List<TimelineRange<Bomb>>> loadMoreMyTopics(){
        return myTopicsTimeline.loadMore();
    }
    public Observable<List<TimelineRange<Bomb>>> loadMoreMyTopics(TimelineRange<Bomb> range) {
        return myTopicsTimeline.loadMore(range);
    }

    public boolean hasMoreMyTopic(){
        return myTopicsTimeline.hasMore();
    }

    public List<TimelineRange<Bomb>> getMyTopics(){
        return myTopicsTimeline.getAllRanges();
    }

    public List<TimelineRange<Bomb>> getAllMyTopics() {
        return myTopicsTimeline.getAllRanges();
    }
    //</editor-fold>
}
