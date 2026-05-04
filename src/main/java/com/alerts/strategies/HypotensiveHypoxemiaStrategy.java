package com.alerts.strategies;

import com.alerts.factories.AlertFactory;
import com.alerts.factories.BloodPressureAlertFactory;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.List;

/**
 * Monitoring strategy that analyzes Hypotensive hypoxemia levels.
 */
public class HypotensiveHypoxemiaStrategy implements AlertStrategy {
    private final AlertFactory FACTORY = new BloodPressureAlertFactory();

    @Override
    public void checkAlert(Patient patient, List<PatientRecord> records) {
        PatientRecord latestSystolic = null;
        PatientRecord latestSaturation = null;

        for (PatientRecord record : records) {
            if (record.getRecordType().equals("SystolicBP")) latestSystolic = record;
            if (record.getRecordType().equals("Saturation")) latestSaturation = record;
        }

        if (latestSystolic != null && latestSaturation != null) {
            if (latestSystolic.getMeasurementValue() < 90 && latestSaturation.getMeasurementValue() < 92) {
                System.out.println("STRATEGY TRIGGER: Hypotensive Hypoxemia");
            }
        }
    }
}
