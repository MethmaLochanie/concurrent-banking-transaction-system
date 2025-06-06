package com.company;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TransactionSystem {
    // Stores bank accounts by their ID in a thread-safe map
    private final Map<Integer, BankAccount> accounts = new ConcurrentHashMap<>();

    // Initializes the TransactionSystem with a list of BankAccounts.
    public TransactionSystem(List<BankAccount> accountList) {
        // Populate the ConcurrentHashMap using each account's ID as the key
        for (BankAccount account : accountList) {
            accounts.put(account.getId(), account);
        }
    }

    public boolean transfer(int fromAccountId, int toAccountId, BigDecimal amount) {
        BankAccount fromAccount = accounts.get(fromAccountId);
        BankAccount toAccount   = accounts.get(toAccountId);

        // Check if the provided account IDs are valid
        if (fromAccount == null || toAccount == null) {
            System.out.println("[ERROR] Invalid account ID(s) in transfer.");
            return false;
        }

        System.out.printf("%n[TRANSFER] Attempting Rs%.2f from Account #%d to #%d...%n",
                amount, fromAccountId, toAccountId);

        // Determine lock order based on numeric IDs (avoid deadlock)
        BankAccount firstLock, secondLock;
        if (fromAccountId < toAccountId) {
            firstLock  = fromAccount;
            secondLock = toAccount;
        } else {
            firstLock  = toAccount;
            secondLock = fromAccount;
        }

        // Acquire locks in the chosen order
        firstLock.lock();
        secondLock.lock();
        try {
            // Check if the source account has enough balance
            BigDecimal fromBalance = fromAccount.getBalance();
            if (fromAccount.getBalance().compareTo(amount) < 0) {
                System.out.printf("[FAILED] Insufficient funds in Account #%d (Balance: Rs%.2f, Needed: Rs%.2f)%n",
                        fromAccountId, fromBalance, amount);
                return false;
            }

            // Withdraw from the source account
            fromAccount.withdraw(amount);
            fromAccount.logTransaction("Transferred Rs" + amount + " to Account " + toAccountId);

            // Deposit into the destination account
            try {
                toAccount.deposit(amount);
                toAccount.logTransaction("Received Rs" + amount + " from Account " + fromAccountId);
            } catch (Exception depositEx) {
                // If deposit fails, rollback the withdrawal to keep balances consistent
                fromAccount.deposit(amount);
                System.out.printf("[ROLLBACK] Deposit failed in Account #%d. Rolled back withdrawal in Account #%d.%n",
                        toAccountId, fromAccountId);
                reverseTransaction(fromAccountId, toAccountId, amount);
                // Rethrow to signal the error
                throw depositEx;
            }

            System.out.printf("[SUCCESS] Transferred Rs%.2f from Account #%d to #%d%n",
                    amount, fromAccountId, toAccountId);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            // Always release the locks in reverse order
            secondLock.unlock();
            firstLock.unlock();
        }
    }

    public void reverseTransaction(int fromAccountId, int toAccountId, BigDecimal amount) {
        System.out.printf("%n[REVERSAL] Reversing Rs%.2f from Account #%d back to #%d...%n",
                amount, toAccountId, fromAccountId);
        // Perform a transfer in the reverse direction
        transfer(toAccountId, fromAccountId, amount);
    }

    public BigDecimal getAccountBalance(int accountId) {
        BankAccount account = accounts.get(accountId);
        if (account != null) {
            return account.getBalance();
        }
        throw new IllegalArgumentException("Account ID not found: " + accountId);
    }

    public void printAccountBalance() {
        System.out.println("\n=== FINAL ACCOUNT BALANCES ===");
        for (BankAccount account : accounts.values()) {
            System.out.printf("  -> Account #%d | Balance: Rs%.2f%n",
                    account.getId(), account.getBalance());
        }
        System.out.println("================================\n");
    }

    public void getTransactionHistory(){
        System.out.println("\n=== TRANSACTION HISTORY ===");
        for (BankAccount account : accounts.values()) {
            System.out.println(account.getTransactionHistory().toString());
        }
        System.out.println("================================\n");
    }
}
