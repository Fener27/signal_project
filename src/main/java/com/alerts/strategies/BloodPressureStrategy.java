package com.alerts.strategies;

import com.alerts.Alert;
import com.alerts.factories.AlertFactory;
import com.alerts.factories.BloodPressureAlertFactory;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.List;

/**
 * Monitoring strategy that checks for trends and critical thresholds in blood pressure readings.
 */
public class BloodPressureStrategy implements AlertStrategy {
    private final AlertFactory FACTORY = new BloodPressureAlertFactory();

    @Override
    public void checkAlert(Patient patient, List<PatientRecord> records) {
        for (PatientRecord record : records) {
            double value = record.getMeasurementValue();
            String type = record.getRecordType();
            String pId = String.valueOf(patient.getPatientId());

            // Critical Thresholds 
            if (type.equals("SystolicBP") && (value > 180 || value < 90)) {
                trigger(FACTORY.createAlert(pId, "Critical Systolic BP: " + value, record.getTimestamp()));
            } else if (type.equals("DiastolicBP") && (value > 120 || value < 60)) {
                trigger(FACTORY.createAlert(pId, "Critical Diastolic BP: " + value, record.getTimestamp()));
            }
        }

        List<PatientRecord> systolicRecords = records.stream()
            .filter(r -> r.getRecordType().equals("SystolicBP"))
            .sorted((r1, r2) -> Long.compare(r1.getTimestamp(), r2.getTimestamp()))
            .toList();

        for (int i = 2; i < systolicRecords.size(); i++) {
            double v1 = systolicRecords.get(i-2).getMeasurementValue();
            double v2 = systolicRecords.get(i-1).getMeasurementValue();
            double v3 = systolicRecords.get(i).getMeasurementValue();

            if ((v2 - v1 > 10 && v3 - v2 > 10) || (v1 - v2 > 10 && v2 - v3 > 10)) {
                trigger(FACTORY.createAlert(String.valueOf(patient.getPatientId()), 
                    "Significant BP Trend Detected", systolicRecords.get(i).getTimestamp()));
            }
        }
    }

    private void trigger(Alert alert) {
        System.out.println("STRATEGY TRIGGER: " + alert.getCondition());
    }
}
