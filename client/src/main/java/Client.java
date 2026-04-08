import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class Client {

    // Random object for generating message data
    private static final Random RANDOM = new Random();

    public static void main(String[] args) throws Exception {

        int numberOfClients = 1000;   // Number of simulated clients

        // Executor that creates a new virtual thread for each client
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

        // Launch all clients concurrently
        for (int i = 0; i < numberOfClients; i++) {
            executor.submit(() -> runClient());
            //Thread.sleep(1);
        }

        // Stop accepting new tasks and wait for all clients to finish
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
    }


    // Simulates a single client
    // Each virtual-thread client opens one TCP connection, sends a message over it and reads a response
    private static void runClient() {

        // try-with-resources ensures the socket is closed automatically
        try (Socket socket = new Socket("localhost", 9000);

             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String message = buildMessage();

            // Send request to server
            out.write(message);
            out.write("\n");
            out.flush();

            // Read server response
            String response = in.readLine();
            System.out.println("Server reply: " + response);

        } catch (Exception e) {
            System.err.println("Connection error: " + e.getMessage());
        }
    }

    // Builds a single transaction message
    // Format: TXN|<id>|<type>|<account>|<amount>
    private static String buildMessage() {
        final String[] ACCOUNTS = {
            // Valid account numbers
            "ACC001", "ACC002", "ACC003", "ACC004", "ACC005",
            "ACC006", "ACC007", "ACC008", "ACC009", "ACC010",
            // Invalid account number to check error validation
            // "ACC011"
        };

        // Generate a unique identifier so each transaction can be tracked
        String requestId = UUID.randomUUID().toString();
        String type = RANDOM.nextBoolean() ? "DEPOSIT" : "WITHDRAWAL";
        String account = ACCOUNTS[RANDOM.nextInt(ACCOUNTS.length)];
        long amount = RANDOM.nextInt(1000) + 1; // random amount between 1 and 1000

        return "TXN|" + requestId + "|" + type + "|" + account + "|" + amount;
    }
}
