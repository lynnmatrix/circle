package com.jadenine.circle.domain.dagger;

import javax.inject.Singleton;

/**
 * Created by linym on 7/3/15.
 */
@dagger.Component(modules = TestDomainModule.class)
@Singleton
public interface TestDomainComponent extends DomainComponent {
}
