# Concurrent Banking Transaction System

This project implements a concurrent banking transaction system that demonstrates thread-safe operations for bank account transactions. The system handles multiple concurrent transactions, balance checks, and transaction reversals while maintaining data consistency.

## Features

- Concurrent bank account transactions
- Thread-safe balance operations
- Transaction history tracking
- Support for transaction reversals
- Random transaction generation for testing
- Balance inquiry operations

## Project Structure

```
src/
└── com/
    └── company/
        ├── Main.java              # Main application entry point
        ├── BankAccount.java       # Bank account implementation
        └── TransactionSystem.java # Transaction handling system
```

## Key Components

### BankAccount
- Represents a bank account with an ID and balance
- Implements thread-safe operations for balance modifications
- Maintains transaction history

### TransactionSystem
- Manages multiple bank accounts
- Handles concurrent transactions between accounts
- Provides methods for transfers and transaction reversals
- Maintains overall transaction history

### Main
- Demonstrates the system's functionality
- Creates multiple accounts and transactions
- Uses ExecutorService for concurrent operations
- Generates random transactions for testing

## How to Run

1. Ensure you have Java installed on your system
2. Compile the project:
   ```bash
   javac src/com/company/*.java
   ```
3. Run the application:
   ```bash
   java -cp src com.company.Main
   ```

## Implementation Details

- Uses `ExecutorService` for managing concurrent operations
- Implements thread-safe operations using synchronization
- Utilizes `BigDecimal` for precise financial calculations
- Maintains transaction history for auditing purposes
- Supports random transaction generation for testing

## Thread Safety

The implementation ensures thread safety through:
- Synchronized methods for balance modifications
- Thread-safe transaction history management
- Proper handling of concurrent transfers and reversals

## Testing

The system includes built-in testing capabilities:
- Random transaction generation
- Multiple concurrent operations
- Balance verification
- Transaction history tracking

## Usage Example

The system creates multiple bank accounts and performs concurrent transactions. Here's an example of how it works:

```java
// Create accounts with initial balance of 1000.00
List<BankAccount> accounts = new ArrayList<>();
accounts.add(new BankAccount(1, new BigDecimal("1000.00")));
accounts.add(new BankAccount(2, new BigDecimal("1000.00")));

// Initialize transaction system
TransactionSystem transactionSystem = new TransactionSystem(accounts);

// Perform a transfer
transactionSystem.transfer(1, 2, new BigDecimal("100.00"));

// Check account balances
System.out.println("Account 1 balance: " + transactionSystem.getAccountBalance(1));
System.out.println("Account 2 balance: " + transactionSystem.getAccountBalance(2));

// Reverse a transaction
transactionSystem.reverseTransaction(1, 2, new BigDecimal("100.00"));

// View transaction history
transactionSystem.getTransactionHistory();
```

When running the application, you'll see:
- Multiple concurrent transactions between accounts
- Random transfers and reversals
- Balance updates in real-time
- Complete transaction history
- Final account balances

## Requirements

- Java 8 or higher
- No external dependencies required 