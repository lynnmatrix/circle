package com.jadenine.circle.domain.dagger;

import com.google.gson.Gson;
import com.jadenine.circle.domain.Account;
import com.jadenine.circle.domain.ApSource;
import com.jadenine.circle.domain.BombLoader;
import com.jadenine.circle.domain.ChatLoader;
import com.jadenine.circle.domain.MyTopicLoader;
import com.jadenine.circle.domain.TopLoader;
import com.jadenine.circle.domain.UserAp;

/**
 * Created by linym on 7/3/15.
 */
public interface DomainComponent {
    void inject(Account account);
    void inject(ApSource apSource);
    void inject(UserAp userAp);

    void inject(BombLoader loader);

    void inject(ChatLoader loader);

    void inject(MyTopicLoader loader);

    void inject(TopLoader loader);

    Account getAccount();

    Gson gson();

}
