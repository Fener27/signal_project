package com;

import java.net.URI;

import java.net.URI;
import com.alerts.AlertGenerator;
import com.alerts.strategies.*;
import com.cardio_generator.HealthDataSimulator;
import com.data_management.DataStorage;
import com.data_management.MyWebSocketClient;
import com.data_management.Patient;

/**
 * Main entry point for the Healthcare Monitoring System.
 * Integrates the real-time Signal Generator with the WebSocket client and Alert System.
 */
public class Main {
    public static void main(String[] args) {
        try {
            // Initialize Signal Generator to act as the WebSocket server
            startSignalGenerator();

            // Allow the server time to bind to the port before client connection (for handshake)
            System.out.println("Initializing WebSocket server... please wait.");
            Thread.sleep(5000); 

            DataStorage storage = DataStorage.getInstance();
            MyWebSocketClient client = new MyWebSocketClient(new URI("ws://localhost:8080"));
            
            // Listening for real-time data
            client.readData(storage);
            AlertGenerator alertGenerator = setupAlertGenerator(storage);
            System.out.println("System fully integrated. Monitoring live patient data...");

            // Continuous evaluation loop (realtime)
            while (true) {
                for (Patient patient : storage.getAllPatients()) {
                    alertGenerator.evaluateData(patient);
                }
                // Preventing CPU overuse
                Thread.sleep(2000); 
            }
        } catch (Exception e) {
            System.err.println("Critical System Failure: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Configures the AlertGenerator with the strategies required for patient safety.
     */
    private static AlertGenerator setupAlertGenerator(DataStorage storage) {
        AlertGenerator alertGenerator = new AlertGenerator(storage);
        alertGenerator.addStrategy(new BloodPressureStrategy());
        alertGenerator.addStrategy(new OxygenSaturationStrategy());
        alertGenerator.addStrategy(new HeartRateStrategy());
        alertGenerator.addStrategy(new HypotensiveHypoxemiaStrategy());
        return alertGenerator;
    }

    /**
     * Starts the HealthDataSimulator in a daemon thread to provide a live data stream.
     */
    private static void startSignalGenerator() {
        Thread generatorThread = new Thread(() -> {
            try {
                HealthDataSimulator simulator = HealthDataSimulator.getInstance();
                String[] simulatorArgs = {"--patient-count", "5", "--output", "websocket:8080"};
                simulator.run(simulatorArgs);
            } catch (Exception e) {
                System.err.println("Simulator Error: " + e.getMessage());
            }
        });
        generatorThread.setDaemon(true); 
        generatorThread.start();
    }
}
