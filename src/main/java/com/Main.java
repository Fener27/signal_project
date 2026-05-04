package com;

import com.cardio_generator.HealthDataSimulator;
import com.data_management.DataStorage;

/**
 * Main entry point that allows selecting between the Simulator and DataStorage.
 */
public class Main {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("DataStorage")) {
            // Allowing to run the DataStorage class
            DataStorage.main(new String[]{});
        } else {
            // Run the HealthDataSimulator as default
            try {
                HealthDataSimulator.main(new String[]{});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
