package com.company;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {

        // Number of accounts and threads
        int numAccounts = 4;
        int numTransactions = 6;

        // Create accounts
        List<BankAccount> accounts = new ArrayList<>();
        for (int i = 1; i < numAccounts+1; i++) {
            accounts.add(new BankAccount(i, new BigDecimal("1000.00")));
        }

        // Create transaction system
        TransactionSystem transactionSystem = new TransactionSystem(accounts);

        // Create a thread pool using ExecutorService
        ExecutorService executor = Executors.newFixedThreadPool(numAccounts);

        // Random number generator for transactions
        Random random = new Random();
        // Submit random transfer tasks
        for (int i = 0; i < numTransactions; i++) {
            executor.submit(() -> {
                int fromAccount = random.nextInt(numAccounts+1);
                int toAccount;
                do {
                    toAccount = random.nextInt(numAccounts+1);
                    // Ensure fromAccount != toAccount
                } while (fromAccount == toAccount);
                // Random amount between 1 and 500
                BigDecimal transferAmount = BigDecimal.valueOf(random.nextInt(500) + 1);
                transactionSystem.transfer(fromAccount, toAccount, transferAmount);
            });
        }

        // Submit random reverse transactions
        for (int i = 0; i < numTransactions / 2; i++) {
            executor.submit(() -> {
                int fromAccount = random.nextInt(numAccounts+1);
                int toAccount;
                do {
                    toAccount = random.nextInt(numAccounts+1);
                    // Ensure fromAccount != toAccount
                } while (fromAccount == toAccount);
                // Random amount between 1 and 500
                BigDecimal reverseAmount = BigDecimal.valueOf(random.nextInt(500) + 1);
                transactionSystem.reverseTransaction(fromAccount, toAccount, reverseAmount);
            });
        }
        // Submit random balance check tasks
        for (int i = 0; i < numTransactions; i++) {
            executor.submit(() -> {
                int accountId = random.nextInt(numAccounts);
                System.out.println("Account ID " + accountId + " balance: " + transactionSystem.getAccountBalance(accountId));
            });
        }

        // Shutdown the executor service after all tasks are submitted
        executor.shutdown();
        try {
            if (!executor.awaitTermination(600, java.util.concurrent.TimeUnit.SECONDS)) {
                System.err.println("Executor did not terminate in the specified time.");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // print transaction history
        transactionSystem.getTransactionHistory();
        // print the final account balances
        transactionSystem.printAccountBalance();
    }
}
