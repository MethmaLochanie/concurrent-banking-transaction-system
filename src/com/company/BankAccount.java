package com.company;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.List;

public class BankAccount {
    private final int id;
    private BigDecimal balance;

    // Fair read-write lock ensures FIFO ordering for waiting threads
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
    private final List<String> transactionHistory = new ArrayList<>();

    public BankAccount(int id, BigDecimal initialBalance) {
        this.id = id;
        this.balance = initialBalance;
    }

    public int getId() {
        // return the unique identifier of this bank account
        return id;
    }

    public BigDecimal getBalance() {
        //Acquires a read lock, allowing concurrent reads as long as no write lock is held.
        lock.readLock().lock();
        try {
            // return the current balance
            return balance;
        } finally {
            // releases the lock
            lock.readLock().unlock();
        }
    }

    public void deposit(BigDecimal amount) {
        // Acquires the write-lock exclusively until the deposit is complete.
        lock.writeLock().lock();
        try {
            // Deposits the specified amount into this account.
            balance = balance.add(amount) ;
        } finally {
            // releases the lock
            lock.writeLock().unlock();
        }
    }

    public void withdraw(BigDecimal amount) throws Exception {
        // Acquires the write-lock exclusively
        lock.writeLock().lock();
        try {
            // if amount exceeds the current balance throws an exception
            if (balance.compareTo(amount) < 0) {
                throw new Exception("Insufficient balance in account #" + id);
            }
            // withdraw the amount from the current account
            balance = balance.subtract(amount) ;
        } finally {
            // releases the lock
            lock.writeLock().unlock();
        }
    }

    public List<String> getTransactionHistory() {
        lock.readLock().lock();
        try {
            return Collections.unmodifiableList(transactionHistory);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void logTransaction(String transaction) {
        lock.writeLock().lock();
        try {
            transactionHistory.add(transaction);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void lock() {
        lock.writeLock().lock();
    }

    public void unlock() {
        lock.writeLock().unlock();
    }
}
