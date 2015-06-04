package com.jadenine.circle.eventbus;

import com.squareup.otto.Bus;

/**
 * Created by linym on 6/3/15.
 */
public class BusProvider {
    private static final Bus BUS = new Bus();

    public static Bus getInstance() {
        return BUS;
    }

    private BusProvider() {
        // No instances.
    }
}
