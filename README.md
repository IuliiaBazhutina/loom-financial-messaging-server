# Loom Financial Messaging Server

A high‑concurrency financial messaging system built with Java 21 Virtual Threads (Project Loom).
This project demonstrates how to process thousands of simultaneous transaction requests with low latency.

---

## Overview
This project implements a lightweight, high‑performance client–server system designed for financial transaction processing. 

The application processes client requests for financial transactions such as deposits and withdrawals. Each request is validated, and the system ensures that the account exists and has sufficient funds when required.

Transactions are handled safely to maintain data consistency. The system returns clear responses with either the updated balance or an error message if the operation fails.

Technologies used:

- Java 21 (Project Loom virtual threads)

- Maven (client + server modules)

- SQLite

- IntelliJ IDEA (recommended)


---

## Architecture

### Server Module

- Listens for incoming client connections on a TCP ServerSocket bound to port 9000

- Accepts client's connection using serverSocket.accept() and logs the remote client address

- Creates one virtual thread per client using newVirtualThreadPerTaskExecutor()

- Communicates using a line‑based text protocol

- Reads client messages via BufferedReader.readLine() and writes responses with BufferedWriter

- Delegates all command processing to AccountService.process()

- Uses AccountRepository and Database to interact with an SQLite database

- Automatically closes sockets and streams using try‑with‑resources

### Client Module

- Simulates multiple concurrent clients

- Opens one TCP connection for each simulated client in its own virtual thread

- Sends text‑based commands following the server’s line‑oriented protocol

- Reads and logs server responses

---

## Project Structure

```
FinancialMessaging/
│
├── client/
│   ├── src/main/java/Client
│   └── pom.xml
│
├── server/
│   ├── src/main/java/controller/Server.java
│   ├── src/main/java/model/Account.java
│   ├── src/main/java/repository/AccountRepository.java
│   ├── src/main/java/repository/Database.java
│   ├── src/main/java/service/AccountService.java
│   └── pom.xml
│
└── pom.xml
```

---

## How to Run

### 1. Recommended: Run the Project from IntelliJ IDEA

- Open the project in IntelliJ
- Select Server from the Run/Debug configurations dropdown, click Run
- Select Client from the Run/Debug configurations dropdown, click Run
- Add this JVM flag to the Server Run Configuration to avoid warnings:

```--enable-native-access=ALL-UNNAMED```

### 2. Alternative: Run from Terminal (from the project root)

- Make sure you are in the directory FinancialMessaging/
- Start the Server:

```mvn -pl server exec:java```

- Start the client:

```mvn -pl client exec:java```

