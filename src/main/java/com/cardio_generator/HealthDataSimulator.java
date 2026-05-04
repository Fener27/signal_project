package com.cardio_generator;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.cardio_generator.generators.AlertGenerator;

import com.cardio_generator.generators.BloodPressureDataGenerator;
import com.cardio_generator.generators.BloodSaturationDataGenerator;
import com.cardio_generator.generators.BloodLevelsDataGenerator;
import com.cardio_generator.generators.ECGDataGenerator;
import com.cardio_generator.outputs.ConsoleOutputStrategy;
import com.cardio_generator.outputs.FileOutputStrategy;
import com.cardio_generator.outputs.OutputStrategy;
import com.cardio_generator.outputs.TcpOutputStrategy;
import com.cardio_generator.outputs.WebSocketOutputStrategy;

// Deleted java.util.Collections
import java.util.List;
import java.util.Random;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * The main driver class for the Healthcare Data Simulator.
 * This class parses command-line configurations, initializes the chosen 
 * output method, and schedules periodic data generation tasks for multiple patients.
 */
public class HealthDataSimulator {

    private static HealthDataSimulator instance;

    /** Default number of patients to simulate if not specified. */
    private static int patientCount = 50; // Default number of patients

    /** Service used to schedule and execute background data generation tasks. */
    private static ScheduledExecutorService scheduler;

    /** The strategy currently used to output generated data. */
    private static OutputStrategy outputStrategy = new ConsoleOutputStrategy(); // Default output strategy
    
    /** Random number generator used for introducing initial delays in task scheduling. */
    // Modified the final variable name to UPPER_SNAKE_CASE
    private static final Random RANDOM = new Random();

    // Private constructor 
    private HealthDataSimulator() {}

    /**
     * Provides a global point of access to the single instance of this class,
     * ensuring that only one instance exists.
     * @return the singleton instance
     */
    public static synchronized HealthDataSimulator getInstance() {
        if (instance == null) {
            instance = new HealthDataSimulator();
        }
        return instance;
    }

    /**
     * Main entry point for the simulator application.
     * It handles the setup process including argument parsing, thread pool initialization, 
     * and starting the patient simulation.
     *
     * @param args command-line arguments for configuration (e.g., --patient-count, --output).
     * @throws IOException if there is an error setting up file-based output directories.
     */
    public static void main(String[] args) throws IOException {
        /* System.out.println("DEBUG: Starting main method...");
        
        parseArguments(args);
        
        System.out.println("DEBUG: Arguments parsed successfully.");
        System.out.println("DEBUG: Patient Count: " + patientCount);
        System.out.println("DEBUG: Output Strategy: " + outputStrategy.getClass().getSimpleName());

        //scheduler = Executors.newScheduledThreadPool(patientCount * 4);
        scheduler = Executors.newScheduledThreadPool(10);
        
        List<Integer> patientIds = initializePatientIds(patientCount);
        System.out.println("DEBUG: IDs initialized. Starting scheduler...");

        scheduleTasksForPatients(patientIds); */
        HealthDataSimulator simulator = HealthDataSimulator.getInstance();
        simulator.run(args);

    }

    public void run(String[] args) throws IOException {
        parseArguments(args);
        scheduler = Executors.newScheduledThreadPool(10);
        List<Integer> patientIds = initializePatientIds(patientCount);
        scheduleTasksForPatients(patientIds);
    }

    /**
     * Parses command-line arguments to configure the simulation.
     * Supports configuration for patient count and various output methods (console, file, 
     * websocket, tcp).
     *
     * @param args the raw array of command-line arguments.
     * @throws IOException if directory creation for file output fails.
     */
    private void parseArguments(String[] args) throws IOException {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-h":
                    printHelp();
                    System.exit(0);
                    break;
                case "--patient-count":
                    if (i + 1 < args.length) {
                        try {
                            patientCount = Integer.parseInt(args[++i]);
                        } catch (NumberFormatException e) {
                            System.err
                                    .println("Error: Invalid number of patients. Using default value: " + patientCount);
                        }
                    }
                    break;
                case "--output":
                    if (i + 1 < args.length) {
                        String outputArg = args[++i];
                        if (outputArg.equals("console")) {
                            outputStrategy = new ConsoleOutputStrategy();
                        } else if (outputArg.startsWith("file:")) {
                            String baseDirectory = outputArg.substring(5);
                            Path outputPath = Paths.get(baseDirectory);
                            System.out.println("Simulation started for " + patientCount + " patients.");
                            System.out.println("Output is being sent to: " + outputStrategy.getClass().getSimpleName());
                            if (!Files.exists(outputPath)) {
                                Files.createDirectories(outputPath);
                            }
                            outputStrategy = new FileOutputStrategy(baseDirectory);
                        } else if (outputArg.startsWith("websocket:")) {
                            try {
                                int port = Integer.parseInt(outputArg.substring(10));
                                // Initialize your WebSocket output strategy here
                                outputStrategy = new WebSocketOutputStrategy(port);
                                System.out.println("WebSocket output will be on port: " + port);
                            } catch (NumberFormatException e) {
                                System.err.println(
                                        "Invalid port for WebSocket output. Please specify a valid port number.");
                            }
                        } else if (outputArg.startsWith("tcp:")) {
                            try {
                                int port = Integer.parseInt(outputArg.substring(4));
                                // Initialize your TCP socket output strategy here
                                outputStrategy = new TcpOutputStrategy(port);
                                System.out.println("TCP socket output will be on port: " + port);
                            } catch (NumberFormatException e) {
                                System.err.println("Invalid port for TCP output. Please specify a valid port number.");
                            }
                        } else {
                            System.err.println("Unknown output type. Using default (console).");
                        }
                    }
                    break;
                default:
                    System.err.println("Unknown option '" + args[i] + "'");
                    printHelp();
                    System.exit(1);
            }
        }
    }

    /**
     * Prints the help information and usage examples to the console.
     */
    private static void printHelp() {
        System.out.println("Usage: java HealthDataSimulator [options]");
        System.out.println("Options:");
        System.out.println("  -h                       Show help and exit.");
        System.out.println(
                "  --patient-count <count>  Specify the number of patients to simulate data for (default: 50).");
        System.out.println("  --output <type>          Define the output method. Options are:");
        System.out.println("                             'console' for console output,");
        System.out.println("                             'file:<directory>' for file output,");
        System.out.println("                             'websocket:<port>' for WebSocket output,");
        System.out.println("                             'tcp:<port>' for TCP socket output.");
        System.out.println("Example:");
        System.out.println("  java HealthDataSimulator --patient-count 100 --output websocket:8080");
        System.out.println(
                "  This command simulates data for 100 patients and sends the output to WebSocket clients connected to port 8080.");
    }

    /**
     * Initializes a list of unique patient identifiers ranging from 1 to the specified count.
     *
     * @param patientCount the number of IDs to generate.
     * @return a list containing integers from 1 to patientCount.
     */
    private List<Integer> initializePatientIds(int patientCount) {
        List<Integer> patientIds = new ArrayList<>();
        for (int i = 1; i <= patientCount; i++) {
            patientIds.add(i);
        }
        return patientIds;
    }

    /**
     * Initializes data generators and schedules periodic tasks for every patient.
     * Tasks include ECG, Blood Saturation, Blood Pressure, Blood Levels, and Alert generation.
     *
     * @param patientIds the list of patient IDs for whom tasks will be scheduled.
     */
    private void scheduleTasksForPatients(List<Integer> patientIds) {
        ECGDataGenerator ecgDataGenerator = new ECGDataGenerator(patientCount);
        BloodSaturationDataGenerator bloodSaturationDataGenerator = new BloodSaturationDataGenerator(patientCount);
        BloodPressureDataGenerator bloodPressureDataGenerator = new BloodPressureDataGenerator(patientCount);
        BloodLevelsDataGenerator bloodLevelsDataGenerator = new BloodLevelsDataGenerator(patientCount);
        AlertGenerator alertGenerator = new AlertGenerator(patientCount);

        for (int patientId : patientIds) {
            scheduleTask(() -> ecgDataGenerator.generate(patientId, outputStrategy), 1, TimeUnit.SECONDS);
            scheduleTask(() -> bloodSaturationDataGenerator.generate(patientId, outputStrategy), 1, TimeUnit.SECONDS);
            scheduleTask(() -> bloodPressureDataGenerator.generate(patientId, outputStrategy), 1, TimeUnit.MINUTES);
            scheduleTask(() -> bloodLevelsDataGenerator.generate(patientId, outputStrategy), 2, TimeUnit.MINUTES);
            scheduleTask(() -> alertGenerator.generate(patientId, outputStrategy), 20, TimeUnit.SECONDS);
        }
    }

    /**
     * Schedules a single task to run at a fixed rate with a random initial delay.
     *
     * @param task the logic to execute.
     * @param period the frequency of execution.
     * @param timeUnit the time unit for the period and delay.
     */
    private static void scheduleTask(Runnable task, long period, TimeUnit timeUnit) {
        scheduler.scheduleAtFixedRate(task, RANDOM.nextInt(5), period, timeUnit);
    }
}
