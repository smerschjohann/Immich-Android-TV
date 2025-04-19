package com.kitesystems.nix.frame;

import nl.giejay.android.tv.immich.sensors.HardwareSensor;
import timber.log.Timber;

public class MotionSensor implements HardwareSensor {
    private static boolean HAVE_GPIO = false;
    private static final String LIBRARY_NAME = "gpio_jni";

    public native int readMotionSensor();

    public native boolean readMotionSensorPower();

    public native void setMotionSensorPower(boolean b);

    public native int setWakeOnMotion(boolean b);

    public boolean isActivityDetected() {
        if (!getSensorEnabled()) {
            setSensorEnabled(true);
        }
        return isMotionDetectedNow();
    }

    static {
        HAVE_GPIO = false;
        try {
            System.loadLibrary(LIBRARY_NAME);
            HAVE_GPIO = true;
        } catch (UnsatisfiedLinkError x) {
            Timber.d("Could not load library %s: %s", LIBRARY_NAME, x.getMessage());
        }
    }

    private synchronized void setSensorEnabled(boolean enabled) {
        if (HAVE_GPIO) {
            setMotionSensorPower(enabled);
        }
    }

    private synchronized boolean isMotionDetectedNow() {
        if (HAVE_GPIO) {
            return readMotionSensor() > 0;
        }
        return false;
    }

    private synchronized boolean getSensorEnabled() {
        if (HAVE_GPIO) {
            return readMotionSensorPower();
        }
        return false;
    }
}
