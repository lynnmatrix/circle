package com.jadenine.circle.domain.dagger;

import javax.net.ssl.SSLSocketFactory;

/**
 * Created by linym on 6/18/15.
 */
public class DaggerService {
    private static DomainComponent domainComponent ;

    public synchronized static void init(SSLSocketFactory socketFactory, String deviceId) {
        if(null == domainComponent) {
            domainComponent = DaggerDomainComponentProduction.builder()
                    .domainModule(new DomainModule(socketFactory, deviceId)).build();
        }
    }

    public static void setComponent(DomainComponent component) {
        domainComponent = component;
    }

    public static DomainComponent getDomainComponent(){
        return domainComponent;
    }
}
