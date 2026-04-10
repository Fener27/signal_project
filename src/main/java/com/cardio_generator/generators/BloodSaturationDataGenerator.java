package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Generates simulated blood oxygen saturation data for patients.
 * This generator maintains a baseline saturation level for each patient and applies
 * small random fluctuations to simulate realistic health monitoring.
 */
public class BloodSaturationDataGenerator implements PatientDataGenerator {
    // Changed name variable to UPPER_SNAKE_CASE
    private static final Random RANDOM = new Random();
    private int[] lastSaturationValues;

    /**
     * Constructs a new BloodSaturationDataGenerator.
     * Initializes baseline SpO2 values between 95% and 100% for each patient.
     *
     * @param patientCount the total number of patients to simulate
     */
    public BloodSaturationDataGenerator(int patientCount) {
        lastSaturationValues = new int[patientCount + 1];

        // Initialize with baseline saturation values for each patient
        for (int i = 1; i <= patientCount; i++) {
            lastSaturationValues[i] = 95 + RANDOM.nextInt(6); // Initializes with a value between 95 and 100
        }
    }

    /**
     * Generates a new blood saturation reading for a specific patient.
     * The value is clamped between 90% and 100%.
     *
     * @param patientId the unique identifier of the patient
     * @param outputStrategy the strategy used to transmit the generated data
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            // Simulate blood saturation values
            int variation = RANDOM.nextInt(3) - 1; // -1, 0, or 1 to simulate small fluctuations
            int newSaturationValue = lastSaturationValues[patientId] + variation;

            // Ensure the saturation stays within a realistic and healthy range
            newSaturationValue = Math.min(Math.max(newSaturationValue, 90), 100);
            lastSaturationValues[patientId] = newSaturationValue;
            outputStrategy.output(patientId, System.currentTimeMillis(), "Saturation",
                    Double.toString(newSaturationValue) + "%");
        } catch (Exception e) {
            System.err.println("An error occurred while generating blood saturation data for patient " + patientId);
            e.printStackTrace(); // This will print the stack trace to help identify where the error occurred.
        }
    }
}
