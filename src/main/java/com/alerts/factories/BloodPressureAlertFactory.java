package com.alerts.factories;

import com.alerts.Alert;

/**
 * Factory for generating alerts related to blood pressure anomalies.
 */
public class BloodPressureAlertFactory extends AlertFactory {
    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        // Returns an alert tagged for Blood Pressure
        return new Alert(patientId, "BP Alert: " + condition, timestamp);
    }
}
