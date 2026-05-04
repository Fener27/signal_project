package com.alerts.strategies;

import com.alerts.factories.AlertFactory;
import com.alerts.factories.ECGAlertFactory;
import com.alerts.Alert;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.List;

public class HeartRateStrategy implements AlertStrategy {
    private final AlertFactory factory = new ECGAlertFactory();

    @Override
    public void checkAlert(Patient patient, List<PatientRecord> records) {
        List<PatientRecord> ecgRecords = records.stream()
            .filter(r -> r.getRecordType().equals("ECG"))
            .toList();

        // Sliding window logic
        int windowSize = 10;
        if (ecgRecords.size() < windowSize) return;

        double sum = 0;
        for (int i = ecgRecords.size() - windowSize; i < ecgRecords.size(); i++) {
            sum += ecgRecords.get(i).getMeasurementValue();
        }
        double average = sum / windowSize;
        double latest = ecgRecords.get(ecgRecords.size() - 1).getMeasurementValue();

        if (latest > average * 1.5) {
            trigger(factory.createAlert(
                String.valueOf(patient.getPatientId()), 
                "Abnormal ECG Peak detected", 
                ecgRecords.get(ecgRecords.size() - 1).getTimestamp()
            ));
        }
    }

    private void trigger(Alert alert) {
        System.out.println("STRATEGY TRIGGER: " + alert.getCondition());
    }
}
