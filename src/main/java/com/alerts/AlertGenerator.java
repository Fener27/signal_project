package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.List;

/**
 * The AlertGenerator class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a DataStorage instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator {
    private DataStorage dataStorage;

    /**
     * Constructs an AlertGenerator with a specified DataStorage.
     * The DataStorage is used to retrieve patient data that this class
     * will monitor and evaluate.
     *
     * @param dataStorage the data storage system that provides access to patient
     *                    data
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    /**
     * Evaluates the specified patient's data to determine if any alert conditions
     * are met. If a condition is met, an alert is triggered.
     * @param patient the patient data to evaluate for alert conditions
     */
    public void evaluateData(Patient patient) {
        // Retrieve all records for this patient
        List<PatientRecord> records = patient.getRecords(0, Long.MAX_VALUE);

        if (records.isEmpty()) return;

        // Processing blood pressure alert
        checkBloodPressure(patient, records);

        // Processing blood saturation alert
        checkBloodSaturation(patient, records);

        // Processing saturation drop
        checkSaturationDrop(patient, records);
        
        // Processing hypotensive hypoxemia
        checkHypotensiveHypoxemia(patient, records);

        // Processing ECG
        checkECGAbnormal(patient, records);
    }


    /**
     * Evaluates the blood pressure of a Patient. If the value exceeds/meet a
     * certain threshold, it displays the type of alert.
     * @param patient the patient that needs the check
     * @param records the list of records of the other patients
     */
    private void checkBloodPressure(Patient patient, List<PatientRecord> records) {
        for (PatientRecord record : records) {
            double value = record.getMeasurementValue();
            String type = record.getRecordType();

            // Critical Threshold Alert 
            if (type.equals("SystolicBP")) {
                if (value > 180 || value < 90) {
                    triggerAlert(new Alert(String.valueOf(record.getPatientId()), "Critical Systolic BP", record.getTimestamp()));
                }
            } else if (type.equals("DiastolicBP")) {
                if (value > 120 || value < 60) {
                    triggerAlert(new Alert(String.valueOf(record.getPatientId()), "Critical Diastolic BP", record.getTimestamp()));
                }
            }
        }
    }

    /**
     * Evaluates the blood saturation of a Patient. If the value meet a
     * certain threshold, it displays the type of alert.
     * @param patient the patient that needs the check
     * @param records the list of records of the other patients
     */
    private void checkBloodSaturation(Patient patient, List<PatientRecord> records) {
        for (PatientRecord record : records) {
            if (record.getRecordType().equals("Saturation")) {
                // Low Saturation Alert
                if (record.getMeasurementValue() < 92.0) {
                    triggerAlert(new Alert(String.valueOf(record.getPatientId()), "Low Saturation", record.getTimestamp()));
                }
            }
        }
    }

    /**
     * Evaluates the saturation drop of a Patient. If the blood oxygen saturation
     * level drops by 5% or more, it triggers the alert.
     * @param patient the patient that needs the check
     * @param records the list of records of the other patients
     */
    private void checkSaturationDrop(Patient patient, List<PatientRecord> records) {
        List<PatientRecord> saturationRecords = records.stream()
            .filter(r -> r.getRecordType().equals("Saturation"))
            .sorted((r1, r2) -> Long.compare(r1.getTimestamp(), r2.getTimestamp()))
            .toList();

        for (int i = 0; i < saturationRecords.size(); i++) {
            PatientRecord current = saturationRecords.get(i);
            // Look back at previous records
            for (int j = 0; j < i; j++) {
                PatientRecord previous = saturationRecords.get(j);
                long timeDiff = current.getTimestamp() - previous.getTimestamp();

                if (timeDiff <= 600000) { // 10 minutes in milliseconds
                    double drop = previous.getMeasurementValue() - current.getMeasurementValue();
                    if (drop >= 5.0) {
                        triggerAlert(new Alert(String.valueOf(patient.getPatientId()), "Rapid Saturation Drop", current.getTimestamp()));
                        break; // Alert triggered
                    }
                }
            }
        }
    }

    /**
     * Evaluates the hypotensive hypoxemia of a Patient. If both Systolic blood pressure
     * is below 90 mmHg and Blood oxygen saturation falls below 92%, it triggers the alert.
     * @param patient the patient that needs the check
     * @param records the list of records of the other patients
     */
    private void checkHypotensiveHypoxemia(Patient patient, List<PatientRecord> records) {
        // We look at the most recent readings to see if they both violate the limits simultaneously
        PatientRecord latestSystolic = null;
        PatientRecord latestSaturation = null;

        for (PatientRecord record : records) {
            if (record.getRecordType().equals("SystolicBP")) latestSystolic = record;
            if (record.getRecordType().equals("Saturation")) latestSaturation = record;
        }

        if (latestSystolic != null && latestSaturation != null) {
            // Requirement: Systolic < 90 AND Saturation < 92%
            if (latestSystolic.getMeasurementValue() < 90 && latestSaturation.getMeasurementValue() < 92) {
                triggerAlert(new Alert(String.valueOf(patient.getPatientId()), "Hypotensive Hypoxemia", System.currentTimeMillis()));
            }
        }
    }

    /**
     * Evaluates the hypotensive hypoxemia of a Patient. If peaks occur
     * far beyond current average, it triggers the alert.
     * @param patient the patient that needs the check
     * @param records the list of records of the other patients
     */
    private void checkECGAbnormal(Patient patient, List<PatientRecord> records) {
        List<PatientRecord> ecgRecords = records.stream()
            .filter(r -> r.getRecordType().equals("ECG"))
            .sorted((r1, r2) -> Long.compare(r1.getTimestamp(), r2.getTimestamp()))
            .toList();

        // Need a sufficient number of records to establish a meaningful average
        int windowSize = 10; 
        if (ecgRecords.size() < windowSize) return;

        for (int i = windowSize; i < ecgRecords.size(); i++) {
            // Calculate the moving average of the previous windowSize records
            double sum = 0;
            for (int j = i - windowSize; j < i; j++) {
                sum += ecgRecords.get(j).getMeasurementValue();
            }
            double average = sum / windowSize;

            double currentVal = ecgRecords.get(i).getMeasurementValue();

            if (currentVal > average * 1.5) {
                triggerAlert(new Alert(String.valueOf(patient.getPatientId()), "Abnormal ECG Peak", ecgRecords.get(i).getTimestamp()));
            }
        }
    }

    /**
     * Triggers an alert for the monitoring system. This method can be extended to
     * notify medical staff, log the alert, or perform other actions. The method
     * currently assumes that the alert information is fully formed when passed as
     * an argument.
     *
     * @param alert the alert object containing details about the alert condition
     */
    private void triggerAlert(Alert alert) {
        System.out.println("ALERT TRIGGERED: " + alert.getCondition() + " for Patient " + alert.getPatientId());
    }
}
