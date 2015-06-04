package com.jadenine.circle.entity;

/**
 * Created by linym on 6/3/15.
 */
public class UserAp {
    private final String user;
    private final String ap;

    public UserAp(String user, String ap) {
        this.user = user;
        this.ap = ap;
    }

    @Override
    public String toString(){
        return ap;
    }

    public String getAP() {
        return ap;
    }
}
