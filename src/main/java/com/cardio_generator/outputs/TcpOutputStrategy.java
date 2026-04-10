package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

/**
 * Implementation that streams patient data over a TCP network socket.
 * This strategy starts a server on a specified port and waits for a single client connection
 * to transmit data in real-time.
 */
public class TcpOutputStrategy implements OutputStrategy {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;

    /**
     * Constructs a new TcpOutputStrategy and initializes the server socket.
     * Starts a background thread to accept a client connection so that the main
     * initialization process is not blocked.
     *
     * @param port the TCP port number on which the server will listen for connections
     */
    public TcpOutputStrategy(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("TCP Server started on port " + port);

            // Accept clients in a new thread to not block the main thread
            Executors.newSingleThreadExecutor().submit(() -> {
                try {
                    clientSocket = serverSocket.accept();
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    System.out.println("Client connected: " + clientSocket.getInetAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends the patient data formatted as a comma-separated string to the connected TCP client.
     * If no client is connected, the data is ignored.
     *
     * @param patientId the unique identifier of the patient
     * @param timestamp the time the measurement was recorded
     * @param label the type of data being recorded (e.g., "ECG")
     * @param data the actual measurement value as a string
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        if (out != null) {
            String message = String.format("%d,%d,%s,%s", patientId, timestamp, label, data);
            out.println(message);
        }
    }
}
