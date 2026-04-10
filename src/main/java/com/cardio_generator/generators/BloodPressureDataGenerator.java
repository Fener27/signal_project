package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

public class BloodPressureDataGenerator implements PatientDataGenerator {
    // Changed name variable to UPPER_SNAKE_CASE
    private static final Random RANDOM = new Random();

    private int[] lastSystolicValues;
    private int[] lastDiastolicValues;

    public BloodPressureDataGenerator(int patientCount) {
        lastSystolicValues = new int[patientCount + 1];
        lastDiastolicValues = new int[patientCount + 1];

        // Initialize with baseline values for each patient
        for (int i = 1; i <= patientCount; i++) {
            lastSystolicValues[i] = 110 + RANDOM.nextInt(20); // Random baseline between 110 and 130
            lastDiastolicValues[i] = 70 + RANDOM.nextInt(15); // Random baseline between 70 and 85
        }
    }

    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            int systolicVariation = RANDOM.nextInt(5) - 2; // -2, -1, 0, 1, or 2
            int diastolicVariation = RANDOM.nextInt(5) - 2;
            int newSystolicValue = lastSystolicValues[patientId] + systolicVariation;
            int newDiastolicValue = lastDiastolicValues[patientId] + diastolicVariation;
            // Ensure the blood pressure stays within a realistic and safe range
            newSystolicValue = Math.min(Math.max(newSystolicValue, 90), 180);
            newDiastolicValue = Math.min(Math.max(newDiastolicValue, 60), 120);
            lastSystolicValues[patientId] = newSystolicValue;
            lastDiastolicValues[patientId] = newDiastolicValue;

            outputStrategy.output(patientId, System.currentTimeMillis(), "SystolicPressure",
                    Double.toString(newSystolicValue));
            outputStrategy.output(patientId, System.currentTimeMillis(), "DiastolicPressure",
                    Double.toString(newDiastolicValue));
        } catch (Exception e) {
            System.err.println("An error occurred while generating blood pressure data for patient " + patientId);
            e.printStackTrace(); // This will print the stack trace to help identify where the error occurred.
        }
    }
}
