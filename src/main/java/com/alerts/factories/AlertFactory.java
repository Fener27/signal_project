package com.alerts.factories;

import com.alerts.Alert;

/**
 * Base factory class for creating Alert objects.
 */
public abstract class AlertFactory {
    /**
     * Factory method to create a specific Alert.
     * @param patientId The ID of the patient.
     * @param condition The description of the medical condition.
     * @param timestamp The time the alert was triggered.
     * @return A specific instance or subclass of Alert.
     */
    public abstract Alert createAlert(String patientId, String condition, long timestamp);
}
