package controller;

import service.AccountService;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

public class Server {

    private static final int PORT = 9000;

    public static void main(String[] args) {
        AccountService accountService = new AccountService();

        // try-with-resources ensures both the ServerSocket and ExecutorService are closed automatically
        try (ServerSocket serverSocket = new ServerSocket(PORT,1024);
             //  Executor that creates a new virtual thread for each submitted task
             var executor = Executors.newVirtualThreadPerTaskExecutor();) {

            System.out.println("Server started on port " + PORT);

            // Main loop: wait for new client connections
            while (true) {
                // Blocks until a client connects, returns a socket
                Socket client = serverSocket.accept();
                System.out.println("Client connected: " + client.getRemoteSocketAddress());

                // Handle each client in its own virtual thread
                executor.submit(() -> handleClient(client, accountService));
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Server error", e);
        }
    }

    // Handles communication with a single connected client
    // Each client runs in its own virtual thread
    private static void handleClient(Socket client, AccountService accountService) {
        // try-with-resources ensures the socket and streams are closed when the client disconnects
        try (client;
             BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()))) {

            String line;

            // Read messages from the client until the connection is closed
            while ((line = in.readLine()) != null) {
                System.out.println("Received: " + line);

                // Create a response using the AccountService
                String response = accountService.process(line);

                // Send the response back to the client
                out.write(response + "\n");
                // Ensure the response is sent immediately
                out.flush();
            }

        } catch (IOException e) {
        System.err.println("I/O error with client " + client.getRemoteSocketAddress());
        e.printStackTrace();
        } catch (Exception e) {
        System.err.println("Unexpected error handling client " + client.getRemoteSocketAddress());
        e.printStackTrace();
    }

        // Reached when a client disconnects
        System.out.println("Client disconnected.");
    }
}