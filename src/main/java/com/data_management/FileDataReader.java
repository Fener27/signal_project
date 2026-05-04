package com.data_management;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Implementation of DataReader that reads patient data from files in a specified directory.
 */
public class FileDataReader implements DataReader {

    private final String outputDirectory;

    /**
     * Constructs a FileDataReader pointing to a specific directory.
     * * @param outputDirectory The path to the directory containing the simulation files.
     */
    public FileDataReader(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    /**
     * Reads all files in the directory, parses each line, and stores it in DataStorage.
     * * @param dataStorage the storage where data will be stored.
     * @throws IOException if the directory cannot be accessed or a file cannot be read.
     */
    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        File folder = new File(outputDirectory);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null) {
            throw new IOException("Directory does not exist or is not a directory: " + outputDirectory);
        }

        for (File file : listOfFiles) {
            if (file.isFile()) {
                parseFile(file, dataStorage);
            }
        }
    }

    /**
     * Parses an individual file line by line.
     */
    private void parseFile(File file, DataStorage dataStorage) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                parseAndStore(line, dataStorage);
            }
        }
    }

    /**
     * Parses a single line from the file and adds it to DataStorage.
     */
    private void parseAndStore(String line, DataStorage dataStorage) {
        try {
            // Logic to strip labels and split by commas based on simulation output format
            String cleanLine = line.replace("Patient ID: ", "")
                                  .replace("Timestamp: ", "")
                                  .replace("Label: ", "")
                                  .replace("Data: ", "");
            
            String[] parts = cleanLine.split(", ");
            
            if (parts.length == 4) {
                int patientId = Integer.parseInt(parts[0].trim());
                long timestamp = Long.parseLong(parts[1].trim());
                String label = parts[2].trim();
                String data = parts[3].trim();

                // Check if data is a percentage
                double value;
                if (data.endsWith("%")) {
                    value = Double.parseDouble(data.replace("%", ""));
                } else {
                    value = Double.parseDouble(data);
                }

                dataStorage.addPatientData(patientId, value, label, timestamp);
            }
        } catch (Exception e) {
            System.err.println("Skipping malformed line: " + line);
        }
    }
}