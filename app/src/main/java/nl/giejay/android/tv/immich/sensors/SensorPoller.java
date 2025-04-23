package nl.giejay.android.tv.immich.sensors;

import nl.giejay.android.tv.immich.shared.prefs.PreferenceManager;
import timber.log.Timber;

public class SensorPoller {
    public static boolean sleepModeActive = false;
    long durationBeforeSleep;
    protected boolean disabled = false;
    public long idleDuration = 0;

    private final HardwareSensor hardwareSensor;

    public SensorPoller(HardwareSensor hardwareSensor) {
        this.hardwareSensor = hardwareSensor;

        updateSettings();
        PreferenceManager.sharedPreference.registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> {
            updateSettings();
        });
    }

    public boolean isSleepModeActive() {
        return sleepModeActive;
    }

    public void checkIfActivityDetected(SensorServiceCallback callback) {
        if (this.disabled || this.durationBeforeSleep < 1) {
            return;
        }
        if (!hardwareSensor.isActivityDetected()) {
            if (this.idleDuration % 30000 == 0) {
                Timber.d("No activity detected for %d minutes, already in sleep mode: %b", this.idleDuration / 1000 / 60, sleepModeActive);
            }
            this.idleDuration += 1000;

            boolean noMotionForFullDuration = this.idleDuration > this.durationBeforeSleep;
            if (noMotionForFullDuration) {
                if(!sleepModeActive) {
                    Timber.d("-------Going to sleep--------");
                }
                callback.sleep();
                sleepModeActive = true;
                return;
            }
            return;
        }
        this.idleDuration = 0L;
        callback.wakeUp();
    }

    public synchronized void updateSettings() {
        try {
            var wakeLockMinutes = PreferenceManager.INSTANCE.wakeLockMinutes();
            this.durationBeforeSleep = wakeLockMinutes * 60 * 1000L;
        } catch (Exception e) {
            Timber.e(e, "Error updating settings");
        }
    }

    public void resetMotionSensor() {
        this.idleDuration = 0L;
        sleepModeActive = false;
    }
}
