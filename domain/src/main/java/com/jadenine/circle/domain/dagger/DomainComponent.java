package com.jadenine.circle.domain.dagger;

import com.jadenine.circle.domain.Account;
import com.jadenine.circle.domain.Message;
import com.jadenine.circle.domain.Topic;
import com.jadenine.circle.domain.UserAp;

import javax.inject.Singleton;

/**
 * Created by linym on 6/18/15.
 */
@dagger.Component(modules = DomainModule.class)
@Singleton
public interface DomainComponent {
    void inject(Account account);
    void inject(UserAp userAp);
    void inject(Topic topic);
    void inject(Message message);
    Account getAccount();
}
