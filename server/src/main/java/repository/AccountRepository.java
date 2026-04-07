package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountRepository {


    // Ensure an account exists. Returns TRUE if the account exists, otherwise FALSE
    public static boolean isAccountExists(String accountNumber) throws SQLException {
        Connection conn = Database.getConnection();
        String query = "SELECT 1 FROM accounts WHERE account_number = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, accountNumber);
            ResultSet rs = stmt.executeQuery();

            // true if exists, otherwise false
            return rs.next();
        }
    }


    // Returns the balance of an account. If the account does not exist, returns -1.
    public static long getBalance(String accountNumber) throws SQLException {
        Connection conn = Database.getConnection();
        String selectBalance = "SELECT balance FROM accounts WHERE account_number = ?";

        try (PreparedStatement stmt = conn.prepareStatement(selectBalance)) {
            stmt.setString(1, accountNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getLong("balance");
            }
            // account not found → return -1
            return -1;
        }
    }


    //Update the balance of an account.
    public static void updateBalance(String accountNumber, long newBalance) throws SQLException {
        Connection conn = Database.getConnection();
        String updateBalance = "UPDATE accounts SET balance = ? WHERE account_number = ?";

        try (PreparedStatement stmt = conn.prepareStatement(updateBalance)) {
            stmt.setLong(1, newBalance);
            stmt.setString(2, accountNumber);
            stmt.executeUpdate();
        }
    }
}
