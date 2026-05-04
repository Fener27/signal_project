package com.data_management;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;

/**
 * A real-time data reader that connects to a WebSocket server to receive 
 * patient health data continuously.
 */
public class MyWebSocketClient extends WebSocketClient implements DataReader {
    private DataStorage storage;

    /**
     * Constructs a new MyWebSocketClient instance.
     * 
     * @param serverUri The URI of the WebSocket server
     */
    public MyWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    /**
     * Starts the real-time data streaming process.
     * @param storage The DataStorage instance where received data will be saved.
     */
    @Override
    public void readData(DataStorage storage) {
        this.storage = storage;
        this.connect(); // Asynchronously connects to the server
    }

    /**
     * Executes when the connection to the server is established
     */
    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connection established with Signal Generator.");
    }

    /**
     * Handles incoming data messages from the server.
     * Parses the CSV-formatted string and stores the resulting data point.
     */
    @Override
    public void onMessage(String message) {
        try {
            parseAndStore(message);
        } catch (Exception e) {
            // Managing corrupted or malformed data
            System.err.println("Skipping malformed message: " + message + " Error: " + e.getMessage());
        }
    }

    /**
     * Gets the message, parse it and store it with the relative data
     * @param message the data send as message
     */
    private void parseAndStore(String message) {
        String[] parts = message.split(",");
        if (parts.length == 4) {
            int patientId = Integer.parseInt(parts[0]);
            long timestamp = Long.parseLong(parts[1]);
            String label = parts[2];
            double value = Double.parseDouble(parts[3]);

            // Storing real-time information
            storage.addPatientData(patientId, value, label, timestamp);
        }
    }

    /**
     * Triggers when the connection is lost.
     */
    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection lost: " + reason);
        new Thread(() -> {
            try {
                Thread.sleep(5000); // Waiting some time before trying again
                System.out.println("Attempting to reconnect...");
                this.reconnect(); 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Handling connection errors
     */
    @Override
    public void onError(Exception ex) {
        // Handling interruptions in the data stream
        System.err.println("WebSocket Error occurred: " + ex.getMessage());
    }
}
