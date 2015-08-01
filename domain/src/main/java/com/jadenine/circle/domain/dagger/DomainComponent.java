package com.jadenine.circle.domain.dagger;

import com.jadenine.circle.domain.Account;
import com.jadenine.circle.domain.ApSource;
import com.jadenine.circle.domain.BombLoader;
import com.jadenine.circle.domain.ChatLoader;
import com.jadenine.circle.domain.UserAp;

/**
 * Created by linym on 7/3/15.
 */
public interface DomainComponent {
    void inject(ApSource apSource);
    void inject(UserAp userAp);

    void inject(BombLoader loader);

    void inject(ChatLoader loader);

    Account getAccount();

}
