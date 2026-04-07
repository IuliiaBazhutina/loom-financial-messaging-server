package repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private static final String URL = "JDBC:sqlite:database.db";
    private static Connection connection;

    static {
        try {
            connection = DriverManager.getConnection(URL);
            connection.setAutoCommit(false); // we control commits manually
            initializeSchema();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize SQLite database", e);
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    // Create the table if it doesn't exist
    // Insert initial accounts and balances if they don't exist
    private static void initializeSchema() throws SQLException {
        try (Statement stmt = connection.createStatement()) {

            // Create Table for account balances
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS accounts (
                    account_number TEXT PRIMARY KEY,
                    balance INTEGER NOT NULL CHECK (balance >= 0)
                );
            """);

            // Insert initial Accounts (only if they don’t exist)
            stmt.execute("""
                INSERT OR IGNORE INTO accounts (account_number, balance) VALUES
                    ('ACC001', 1000),
                    ('ACC002', 1000),
                    ('ACC003', 1000),
                    ('ACC004', 1000),
                    ('ACC005', 1000),
                    ('ACC006', 1000),
                    ('ACC007', 1000),
                    ('ACC008', 1000),
                    ('ACC009', 1000),
                    ('ACC010', 1000);
            """);

            connection.commit();
        }
    }
}