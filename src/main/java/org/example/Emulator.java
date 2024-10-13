package org.example;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Emulator {
    private JToggleButton listenBtn;
    private JTextField addressField;
    private JTextField portField;
    private JTextArea log;
    private JPanel mainPanel;
    private ControlBoard controlBoard;
    private ServerSocket serverSocket;
    private boolean isListening = false;
    private ScheduledExecutorService scheduler;
    private Socket clientSocket;  // Store the connected client socket
    private PrintWriter writer;  // Output stream to send messages to the client
    private boolean isSampling = false;  // To track whether sampling is active


    public Emulator() {
        // Other initialization code if necessary
        log.setEditable(false);
        showMessage("The Emulator is running.");
        showMessage("Waiting for connection...");
        // Initialize the button click event handler
        // Listen button action
        // Set up any listeners or custom behavior for the toggle button
        listenBtn.addActionListener(e -> {
            if (listenBtn.isSelected()) {
                startServer();
            } else {
                if (isSampling) {
                    stopSampling();
                }
                stopServer();
            }
        });
    }

    public void createUIComponents() {
        controlBoard = new ControlBoard();
    }

    // Method to start the TCP server
    private void startServer() {
        int port = Integer.parseInt(portField.getText());  // Get port from field
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(port);  // Create the ServerSocket
                isListening = true;
                showMessage("Server started and listening on port: " + addressField.getText() + ":" + port);

                // Continuously listen for client connections
                while (isListening) {
                    try {
                        Socket clientSocket = serverSocket.accept();  // Accept incoming client connection
                        showMessage("Client connected: " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());

                        // Handle client communication in a separate thread
                        new Thread(() -> handleClient(clientSocket)).start();
                    } catch (IOException e) {
                        if (isListening) {  // Ignore if server was intentionally stopped
                            showMessage("Error accepting client: " + e.getMessage());
                        }
                    }
                }
            } catch (IOException ex) {
                showMessage("Error starting server: " + ex.getMessage());
            }
        }).start();
    }

    // Method to stop the TCP server
    private void stopServer() {
        isListening = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();  // This will stop the server from accepting new connections
                controlBoard = new ControlBoard();
                showMessage("Server stopped.");
            } catch (IOException e) {
                showMessage("Error stopping server: " + e.getMessage());
            }
        }

    }

    // Example usage in your server when receiving a message
    private void handleClient(Socket clientSocket) {
        try {
            this.clientSocket = clientSocket;
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
            String message;
            Set<String> requiredKeys = Set.of("SurgicalTools", "Anatomy");  // Define required keys

            while ((message = reader.readLine()) != null) {
                if (isValidJSONWithRequiredKeys(message, requiredKeys)) {
                    showMessage("Received valid JSON with required keys: " + message);
                    initializeControls(new JSONObject(message));
                } else {
                    showMessage("Received invalid or incomplete JSON message.");
                }

                // Echo the message back to the client
                writer.println("Server received: " + message);
            }
        } catch (IOException ex) {
            showMessage("Client communication error: " + ex.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException ex) {
                showMessage("Error closing client connection: " + ex.getMessage());
            }
        }
    }


    // Method to start the sampler (periodically send messages to the client)
    public void startSampling(long periodInMilliSeconds) {
        if (isSampling || clientSocket == null || clientSocket.isClosed()) {
            showMessage("Sampling already running or no client connected.");
            return;
        }

        // Create a scheduler that runs tasks at a fixed period
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::sendSampleMessage, 0, periodInMilliSeconds, TimeUnit.MILLISECONDS);
        isSampling = true;
        showMessage("Started sampling, sending messages every " + periodInMilliSeconds + " seconds.");
    }

    // Method to stop the sampler
    public void stopSampling() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            isSampling = false;
            showMessage("Stopped sampling.");
        }
    }

    // Method to send a message to the client
    private void sendSampleMessage() {
        if (writer != null) {
            writer.println(controlBoard.getControlData());  // Send the message to the client
        }
    }

    private void initializeControls(JSONObject message) {
        JSONArray surgicalTools = message.getJSONArray("SurgicalTools");
        JSONObject anatomy = message.getJSONObject("Anatomy");
        controlBoard.addUnitControl(anatomy.getString("Name"));
        for (int i = 0; i < surgicalTools.length(); i++) {
            JSONObject surgicalTool = surgicalTools.getJSONObject(i);
            controlBoard.addUnitControl(surgicalTool.getString("Name"));
        }
        startSampling(50);
    }

    public void showMessage(String text) {
        // Get the current date and time
        LocalDateTime now = LocalDateTime.now();
        // Define a printable datetime format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // Format the current date and time
        String formattedDateTime = now.format(formatter);
        log.append("[" + formattedDateTime + "] " + text + "\n");
    }


    // Method to check if the message is valid JSON and contains required keys
    private boolean isValidJSONWithRequiredKeys(String message, Set<String> requiredKeys) {
        try {
            // Parse the message as a JSON object
            JSONObject jsonObject = new JSONObject(message);

            // Check if all required keys are present
            for (String key : requiredKeys) {
                if (!jsonObject.has(key)) {
                    showMessage("Missing key: " + key + "\n");
                    return false;  // Key is missing
                }
            }
            return true;  // All required keys are present
        } catch (JSONException ex) {
            showMessage("Invalid JSON format: " + ex.getMessage());
            return false;  // Invalid JSON
        }
    }



    public static void main(String[] args) {
        JFrame frame = new JFrame("Emulator");
        frame.setContentPane(new Emulator().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}

