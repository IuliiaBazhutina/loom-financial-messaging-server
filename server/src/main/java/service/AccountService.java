package service;

import repository.AccountRepository;
import repository.Database;
import java.sql.Connection;
import java.sql.SQLException;

public class AccountService {

    // Main entry point for processing incoming messages
    // Expected format:
    // TXN|<id>|<type>|<account>|<amount>

    public String process(String message) {
        try {
            // Split the message by the '|' delimiter
            String[] parts = message.split("\\|");
            // Validate message structure
            if (parts.length != 5) {
                return "ERROR | INVALID_FORMAT";
            }

            // Extract fields from the message
            String id = parts[1];
            String type = parts[2];
            String account = parts[3];
            long amount = Long.parseLong(parts[4]);

            // Check if the account exists in the database
            if (!AccountRepository.isAccountExists(account)) {
                return "ERROR | " + account + " | ACCOUNT_NOT_FOUND |" + id;
            }

            // Switch expression to run the correct operation
            return switch (type) {
                case "DEPOSIT" -> deposit(id, account, amount);
                case "WITHDRAWAL" -> withdraw(id, account, amount);
                default -> "ERROR | " + account + " | UNKNOWN_TYPE | " + id;
            };

        } catch (Exception e) {
            return "ERROR | " + e.getMessage();
        }
    }

    // -----------------------------
    // BUSINESS LOGIC
    // -----------------------------

     // Handle a deposit transaction
    private String deposit(String id, String account, long amount) throws SQLException {
        Connection conn = Database.getConnection();

        try {
            // Start transaction
            conn.setAutoCommit(false);
            long balance = AccountRepository.getBalance(account);
            long newBalance = balance + amount;

            AccountRepository.updateBalance(account, newBalance);
            // Commit update
            conn.commit();
            return "OK    | " + account + " | BALANCE=" + newBalance + " | " + id;

        } catch (Exception e) {
            // Undo changes
            conn.rollback();
            return "ERROR | " + account + " | " + e.getMessage() + " | " + id;
        }
    }

    // Handles a withdrawal transaction
    private String withdraw(String id, String account, long amount) throws SQLException {
        Connection conn = Database.getConnection();

        try {
            // Start transaction
            conn.setAutoCommit(false);
            long balance = AccountRepository.getBalance(account);

            // Check if sufficient funds exist
            if (balance < amount) {
                return "ERROR | " + account + " | INSUFFICIENT_FUNDS" + " | " + id;
            }

            long newBalance = balance - amount;
            AccountRepository.updateBalance(account, newBalance);

            // Commit update
            conn.commit();
            return "OK    | " + account + " | BALANCE=" + newBalance + " | " + id;

        } catch (Exception e) {
            // Undo changes
            conn.rollback();
            return "ERROR | " + account + " | " + e.getMessage() + " | " + id;
        }
    }
}

