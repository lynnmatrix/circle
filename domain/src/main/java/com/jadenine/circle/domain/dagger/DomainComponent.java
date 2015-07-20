package com.jadenine.circle.domain.dagger;

import com.jadenine.circle.domain.Account;
import com.jadenine.circle.domain.Chat;
import com.jadenine.circle.domain.Message;
import com.jadenine.circle.domain.Topic;
import com.jadenine.circle.domain.TopicTimeline;
import com.jadenine.circle.domain.UserAp;
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
    Account getAccount();

    DirectMessageService getDirectMessageService();
}
