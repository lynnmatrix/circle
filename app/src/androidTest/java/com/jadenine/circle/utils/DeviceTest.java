package com.jadenine.circle.utils;

import android.test.InstrumentationTestCase;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;

/**
 * Created by linym on 6/3/15.
 */
public class DeviceTest extends InstrumentationTestCase {

    public void testGetDeviceId() throws IOException {
        String deviceId = Device.getDeviceId(getInstrumentation().getTargetContext());

        Log.i("TEST", "device id: " + deviceId);

        assertFalse(TextUtils.isEmpty(deviceId));
    }
}