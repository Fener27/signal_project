package com.alerts.factories;

import com.alerts.Alert;

/**
 * Factory for generating alerts related to irregular heart rates.
 */
public class ECGAlertFactory extends AlertFactory {
    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        // Returns an alert for irregular heart rates
        return new Alert(patientId, "ECG Alert: " + condition, timestamp);
    }
}
