package com.jadenine.circle.eventbus;

import com.squareup.otto.Bus;

/**
 * Created by linym on 6/3/15.
 */
public class BusProvider {
    private static final Bus BUS = new Bus();

    private static Bus getInstance() {
        return BUS;
    }

    private BusProvider() {
        // No instances.
    }

    public static void register(Object object) {
        getInstance().register(object);
    }

    public static void unregister(Object object) {
        getInstance().unregister(object);
    }

    public static void post(Object object) {
        getInstance().post(object);
    }
}
