package com.jadenine.circle.domain;

import com.jadenine.circle.domain.dagger.DaggerService;
import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.model.entity.DirectMessageEntity;
import com.jadenine.circle.model.state.TimelineType;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by linym on 6/17/15.
 */
public class Account {
    public static final String TIME_LINE_MY_CHATS = "my_chats";
    public static final String TIME_LINE_MY_TOPICS = "my_topics";

    private final String deviceId;

    private final ApSource apSource;

    private final BaseTimeline<DirectMessageEntity> chatTimeline;
    private final BaseTimeline<Bomb> myTopicsTimeline;
    private final TopBoard topBoard;

    @Inject
    ChatComposer chatComposer;
    @Inject
    BombComposer bombComposer;

    public Account(String deviceId) {
        this.deviceId = deviceId;

        DaggerService.getDomainComponent().inject(this);

        ChatLoader chatLoader = new ChatLoader(deviceId, Constants.PAGE_SIZE);

        this.chatTimeline = new BaseTimeline<>(TIME_LINE_MY_CHATS, TimelineType.CHAT, chatLoader);
        this.apSource = new ApSource(deviceId);

        MyTopicLoader myTopicLoader = new MyTopicLoader(deviceId, Constants.PAGE_SIZE);
        DaggerService.getDomainComponent().inject(myTopicLoader);
        this.myTopicsTimeline = new BaseTimeline<>(TIME_LINE_MY_TOPICS, TimelineType.MY_TOPIC, myTopicLoader);

        TopLoader topLoader = new TopLoader(deviceId);
        DaggerService.getDomainComponent().inject(topLoader);
        this.topBoard = new TopBoard(topLoader, Constants.TOP_K);
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

    public Group<Bomb> getMyTopic(Long groupId){
        return myTopicsTimeline.getRange(groupId).getGroup(groupId);
    }

    public List<TimelineRange<Bomb>> getAllMyTopics() {
        return myTopicsTimeline.getAllRanges();
    }
    //</editor-fold>

    //<editor-fold desc="publisher">

    public Observable<DirectMessageEntity> publish(DirectMessageEntity chatMessage) {
        return chatComposer.send(chatMessage);
    }

    public Observable<Bomb> publish(final Bomb bomb) {
        return bombComposer.send(bomb);
    }

    //</editor-fold>

    //<editor-fold desc="top">
    public Observable<ArrayList<Group<Bomb>>> refreshTop(){
        return topBoard.refresh();
    }

    public List<Group<Bomb>> getTops(){
        return topBoard.getTops();
    }
    //</editor-fold>
}
