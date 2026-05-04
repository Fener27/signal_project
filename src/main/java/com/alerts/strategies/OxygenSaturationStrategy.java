package com.alerts.strategies;

import com.alerts.factories.AlertFactory;
import com.alerts.factories.BloodOxygenAlertFactory;
import com.alerts.Alert;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.List;

/**
 * Monitoring strategy that observes oxygen levels for critical drops and low saturation thresholds.
 */
public class OxygenSaturationStrategy implements AlertStrategy {
    private final AlertFactory FACTORY = new BloodOxygenAlertFactory();

    @Override
    public void checkAlert(Patient patient, List<PatientRecord> records) {
        for (PatientRecord record : records) {
            if (record.getRecordType().equals("Saturation")) {
                double value = record.getMeasurementValue();
                String pId = String.valueOf(patient.getPatientId());

                // Low saturation threshold 
                if (value < 92.0) {
                    trigger(FACTORY.createAlert(pId, "Low Saturation: " + value + "%", record.getTimestamp()));
                }
            }
        }

        List<PatientRecord> satRecords = records.stream()
            .filter(r -> r.getRecordType().equals("Saturation"))
            .sorted((r1, r2) -> Long.compare(r1.getTimestamp(), r2.getTimestamp()))
            .toList();

        for (int i = 0; i < satRecords.size(); i++) {
            for (int j = 0; j < i; j++) {
                long timeDiff = satRecords.get(i).getTimestamp() - satRecords.get(j).getTimestamp();
                if (timeDiff <= 600000) { // 10 minutes
                    double drop = satRecords.get(j).getMeasurementValue() - satRecords.get(i).getMeasurementValue();
                    if (drop >= 5.0) {
                        trigger(FACTORY.createAlert(String.valueOf(patient.getPatientId()), 
                            "Rapid Saturation Drop", satRecords.get(i).getTimestamp()));
                        break; 
                    }
                }
            }
        }
    }

    private void trigger(Alert alert) {
        System.out.println("STRATEGY TRIGGER: " + alert.getCondition());
    }
}
