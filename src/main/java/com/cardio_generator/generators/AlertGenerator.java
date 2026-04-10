package com.cardio_generator.generators;

import java.util.Random;
import com.cardio_generator.outputs.OutputStrategy;

/**
 * Simulates alert events for patients.
 * Uses a probabilistic model to determine when a patient condition triggers an alert
 * and when an existing alert is resolved.
 */
public class AlertGenerator implements PatientDataGenerator {

    // Changed variable name to UPPER_SNAKE_CASE
    /** Random number generator used for all probability calculations. */
    public static final Random RANDOM_GENERATOR = new Random();
    // Changed variable name to camelCase
    private boolean[] alertStates; // false = resolved, true = pressed

    /**
     * Constructs a new AlertGenerator.
     * All patients start in a "resolved" state (no active alerts).
     *
     * @param patientCount the total number of patients to simulate
     */
    public AlertGenerator(int patientCount) {
        alertStates = new boolean[patientCount + 1];
    }

    /**
     * Evaluates and generates alert status changes for a specific patient.
     * If an alert is active, there is a 90% chance it will resolve.
     *
     * @param patientId the unique identifier of the patient
     * @param outputStrategy the strategy used to transmit the alert status
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            if (alertStates[patientId]) {
                if (RANDOM_GENERATOR.nextDouble() < 0.9) { // 90% chance to resolve
                    alertStates[patientId] = false;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "resolved");
                }
            } else {
                // changed variable name to low_case
                double lambda = 0.1; // Average rate (alerts per period), adjust based on desired frequency
                double p = -Math.expm1(-lambda); // Probability of at least one alert in the period
                boolean alertTriggered = RANDOM_GENERATOR.nextDouble() < p;

                if (alertTriggered) {
                    alertStates[patientId] = true;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "triggered");
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred while generating alert data for patient " + patientId);
            e.printStackTrace();
        }
    }
}
