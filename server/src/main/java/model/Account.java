package model;

public class Account {

    private final String accountNumber;
    private final long balance;

    public Account(String accountNumber, long balance) {
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Account{" + "accountNumber='" + accountNumber + '\'' + ", balance=" + balance + '}';
    }
}
