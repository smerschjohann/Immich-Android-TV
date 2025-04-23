package com.kitesystems.nix.frame;

import nl.giejay.android.tv.immich.sensors.HardwareSensor;
import timber.log.Timber;

public class MotionSensor implements HardwareSensor {
    private static boolean LIBRARY_LOADED;
    private static final String LIBRARY_NAME = "gpio_jni";

    public native int readMotionSensor();

    public native boolean readMotionSensorPower();

    public native void setMotionSensorPower(boolean b);

    public native int setWakeOnMotion(boolean b);

    public synchronized boolean isActivityDetected() {
        if(LIBRARY_LOADED) {
            if(!readMotionSensorPower()) {
                setMotionSensorPower(true);
            }
            return readMotionSensor() > 0;
        }
        return false;
    }

    static {
        LIBRARY_LOADED = false;
        try {
            System.loadLibrary(LIBRARY_NAME);
            LIBRARY_LOADED = true;
        } catch (UnsatisfiedLinkError e) {
            Timber.d("native library %s could not be loaded: %s", LIBRARY_NAME, e.getMessage());
        }
    }
}
