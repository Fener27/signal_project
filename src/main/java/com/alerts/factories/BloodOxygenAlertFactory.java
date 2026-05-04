package com.alerts.factories;

import com.alerts.Alert;

/**
 * Factory for generating alerts related to changes in oxygen levels.
 */
public class BloodOxygenAlertFactory extends AlertFactory {
    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        // Returns an alert for changes in oxygen levels
        return new Alert(patientId, "Oxygen Alert: " + condition, timestamp);
    }
}
