package com.jadenine.circle.eventbus;

import com.jadenine.circle.utils.ApUtils;

/**
 * Created by linym on 6/3/15.
 */
public class EventProducer {
    public static class APConnectedEvent{
        private ApUtils.AP ap;
        public APConnectedEvent(ApUtils.AP ap) {
            this.ap = ap;
        }

        public ApUtils.AP getAP() {
            return ap;
        }
    }
    public static class DrawerOpenEvent{}

}
