package com.alerts.strategies;

import com.data_management.Patient;
import java.util.List;
import com.data_management.PatientRecord;

/**
 * Interface that allows the alert generation algorithm's behavior to be selected
 * at runtime based on the health metric being monitored.
 */
public interface AlertStrategy {
    /**
     * Encapsulating the algorithm to determine if an alert should trigger
     */
    void checkAlert(Patient patient, List<PatientRecord> records);
}
