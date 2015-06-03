package com.jadenine.circle.eventbus;

/**
 * Created by linym on 6/3/15.
 */
public class EventProducer {
    public static class APConnectedEvent{
        private String apMacAddrress;
        public APConnectedEvent(String apMacAddrress) {
            this.apMacAddrress = apMacAddrress;
        }

        public String getAP() {
            return apMacAddrress;
        }
    }
//
//    @Produce
//    public APConnectedEvent produceApConnectedEvent(){
//        return new APConnectedEvent(WifiScanner)
//    }
}
