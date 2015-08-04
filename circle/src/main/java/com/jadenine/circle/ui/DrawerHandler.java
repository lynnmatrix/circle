package com.jadenine.circle.ui;

/**
 * Created by linym on 8/4/15.
 */
public interface DrawerHandler {
    boolean isDrawerOpen(int gravity);

    void openDrawer(int gravity);

    void closeDrawer(int gravity);

    void setDrawerLockMode(int lockMode);
}
