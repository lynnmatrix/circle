package com.jadenine.circle.domain.dagger;

import com.jadenine.circle.domain.Account;
import com.jadenine.circle.domain.BombLoader;
import com.jadenine.circle.domain.Chat;
import com.jadenine.circle.domain.ChatLoader;
import com.jadenine.circle.domain.Message;
import com.jadenine.circle.domain.Topic;
import com.jadenine.circle.domain.TopicTimeline;
import com.jadenine.circle.domain.UserAp;
import com.jadenine.circle.model.db.impl.DirectMessageDBService;
import com.jadenine.circle.model.db.impl.TimelineCursorDBService;
import com.jadenine.circle.model.rest.DirectMessageService;

/**
 * Created by linym on 7/3/15.
 */
public interface DomainComponent {
    void inject(Account account);
    void inject(UserAp userAp);
    void inject(Topic topic);
    void inject(Message message);
    void inject(Chat chat);
    void inject(TopicTimeline timeline);

    void inject(BombLoader loader);

    void inject(ChatLoader loader);

    Account getAccount();

    DirectMessageService getDirectMessageService();

    DirectMessageDBService getDirectMessageDBService();

    TimelineCursorDBService getTimelineRangeCursorDBService();
}
