package com.cardio_generator.outputs;

/**
 * Defines the contract for outputting patient data to various destinations.
 * Implementations of this interface handle the actual transmission or storage
 * of data, such as printing to a console, writing to a file, or streaming over 
 * network protocols like TCP or WebSockets.
 */
public interface OutputStrategy {
    
    /**
     * Outputs a single data point for a specific patient.
     *
     * @param patientId the unique identifier of the patient associated with the data
     * @param timestamp the time at which the measurement was recorded, in milliseconds 
     * since the Unix epoch
     * @param label the type of measurement being recorded (e.g., "ECG", "HeartRate")
     * @param data the actual value of the measurement formatted as a string
     */
    void output(int patientId, long timestamp, String label, String data);
}
