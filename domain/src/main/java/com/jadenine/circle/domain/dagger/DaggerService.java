package com.jadenine.circle.domain.dagger;

/**
 * Created by linym on 6/18/15.
 */
public class DaggerService {
    private static DomainComponent domainComponent ;

    public synchronized static void init(String deviceId) {
        if(null == domainComponent) {
            domainComponent = DaggerDomainComponentProduction.builder()
                    .domainModule(new DomainModule(deviceId)).build();
        }
    }

    public static void setComponent(DomainComponent component) {
        domainComponent = component;
    }

    public static DomainComponent getDomainComponent(){
        return domainComponent;
    }
}
