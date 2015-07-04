package com.jadenine.circle.domain.dagger;

import javax.inject.Singleton;

/**
 * Created by linym on 6/18/15.
 */
@dagger.Component(modules = DomainModule.class)
@Singleton
public interface DomainComponentProduction extends DomainComponent {

}
