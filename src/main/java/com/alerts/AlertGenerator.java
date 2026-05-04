package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import com.alerts.strategies.AlertStrategy;
import java.util.ArrayList;
import java.util.List;

/**
 * The AlertGenerator class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a DataStorage instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator {
    private DataStorage dataStorage;
    private List<AlertStrategy> strategies;

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
        this.strategies = new ArrayList<>();
    }

    /**
     * Constructs an AlertGenerator with a specified DataStorage and a list
     * of strategies that will be used.
     * @param dataStorage the data storage system that provides access to patient
     *                    data
     * @param strategies  the list of strategies that will be used
     */
    public AlertGenerator(DataStorage dataStorage, List<AlertStrategy> strategies) {
        this.dataStorage = dataStorage;
        this.strategies = strategies;
    }

    /**
     * Adds a monitoring strategy to the generator.
     * This allows dynamic selection of algorithms at runtime.
     * @param strategy BP, Oxygen, or Heart Rate
     */
    public void addStrategy(AlertStrategy strategy) {
        this.strategies.add(strategy);
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

        for (AlertStrategy strategy : strategies) {
            strategy.checkAlert(patient, records);
        }
    }
}
