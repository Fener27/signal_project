package com.cardio_generator.outputs;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;

public class FileOutputStrategy implements OutputStrategy {
    // Modified the variable name to camelCase
    private String baseDirectory;
    // Modified the final variable name to UPPER_SNAKE_CASE
    public final ConcurrentHashMap<String, String> FILE_MAP = new ConcurrentHashMap<>();

    public FileOutputStrategy(String baseDirectory) {
        // Modified the assigned variables
        this.baseDirectory = baseDirectory;
    }

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