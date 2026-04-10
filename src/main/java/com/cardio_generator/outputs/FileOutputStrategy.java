package com.cardio_generator.outputs;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation that saves patient data into text files.
 * Each data label (e.g., "ECG", "BloodPressure") is saved into its own specific file 
 * within a designated base directory.
 */
public class FileOutputStrategy implements OutputStrategy {
    // Modified the variable name to camelCase
    private String baseDirectory;
    // Modified the final variable name to UPPER_SNAKE_CASE
    /** * A map to cache file paths based on data labels to avoid repeated string concatenations. */
    public final ConcurrentHashMap<String, String> FILE_MAP = new ConcurrentHashMap<>();

    /**
     * Constructs a new FileOutputStrategy with a specified directory for storage.
     *
     * @param baseDirectory the path to the directory where data files will be created
     */
    public FileOutputStrategy(String baseDirectory) {
        // Modified the assigned variables
        this.baseDirectory = baseDirectory;
    }

    /**
     * Writes a single patient record to a text file corresponding to the data label.
     * The record is appended to the file in a human-readable format.
     *
     * @param patientId the unique identifier of the patient
     * @param timestamp the time the measurement was recorded
     * @param label the type of data being recorded, used to determine the filename
     * @param data the actual measurement value
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        // Modified the variable name to camelCase
        String filePath = FILE_MAP.computeIfAbsent(label, k -> Paths.get(baseDirectory, label + ".txt").toString());
        try (PrintWriter out = new PrintWriter(new FileWriter(filePath, true))) {
            out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timestamp, label, data);
            out.flush(); // Force the computer to write to disk RIGHT NOW
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Write the data to the file
        try (PrintWriter out = new PrintWriter(
            Files.newBufferedWriter(Paths.get(filePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
            out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timestamp, label, data);
        } catch (Exception e) {
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());
        }
    }
}