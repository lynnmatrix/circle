package com.jadenine.circle.domain.dagger;

/**
 * Created by linym on 6/18/15.
 */
public class DaggerService {
    private static final DomainComponent domainComponent = DaggerDomainComponent.builder()
            .domainModule(new DomainModule()).build();
    public static DomainComponent getDomainComponent(){
        return domainComponent;
    }
}
