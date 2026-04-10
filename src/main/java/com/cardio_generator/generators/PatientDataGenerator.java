package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Defines the contract for classes that simulate or generate patient health data.
 */
public interface PatientDataGenerator {

    /**
     * Generates health data for a specific patient and sends it to the provided 
     * output destination.
     *
     * @param patientId the unique identifier of the patient for whom data is generated
     * @param outputStrategy the strategy implementation used to output the generated data
     */
    void generate(int patientId, OutputStrategy outputStrategy);
}
