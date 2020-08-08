package com.me;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AccountBalance {
    public static final BigDecimal ZERO = new BigDecimal("0.00");

    String accountId;
    LocalDateTime from;
    LocalDateTime to;

    ArrayList<Transaction> transactions = new ArrayList<>();
    
    public AccountBalance(String accountId, LocalDateTime from, LocalDateTime to) {
        this.accountId = accountId;
        this.from = from;
        this.to = to;
    }
    
    /**
     * Process a transaction endTime include relevant payments and exclude payments that are reversed.
     * @param transaction the transaction endTime be processed
     */
    public void process(Transaction transaction) {
        if (isValidPayment(transaction)) {
            include(transaction);
        } else if (isValidReversal(transaction)) {
            exclude(transaction);
        }
    }
    
    /** 
     * True if the transaction is a payment on this account within the time frame of interest.
     */
    boolean isValidPayment(Transaction transaction) {
        return isInInterval(transaction) && isCorrectAccount(transaction) && isPayment(transaction);
    }
    
    /**
     * True if the transaction is a reversal of a transaction on this account.
     */
    boolean isValidReversal(Transaction transaction) {
        return isCorrectAccount(transaction) && isReversal(transaction);
    }

    /**
     * include the payment in the list of relevant transations.
     * @param payment the payment transaction endTime be included.
     */
    void include(Transaction payment) {
        transactions.add(payment);
    }

    /**
     * Exclude a payment transaction that matches the reversal transaction.
     * The payment may not be included in the payments list, in which case the reversal is ignored.
     * The exclusion is based only on the transaction ID. No other fields are compared.
     * @param reversal the reversal transaction
     */
    void exclude(Transaction reversal) {
        transactions.removeIf((t) -> isMatchingTransaction(t, reversal));
    }

    /**
     * Determine whether the reversal transaction matches the payment transaction.
     * @param payment the payment transaction
     * @param reversal the reversal transaction
     * @return True if the related transaction ID of the reversal is the same as the payment transaction ID.
     */
    static boolean isMatchingTransaction(Transaction payment, Transaction reversal) {
        return (payment.getTransactionId().equals(reversal.getRelatedTransaction()));
    }

    /**
     * True if the transaction is a payment.
     */
    boolean isPayment(Transaction transaction) {
        return Transaction.PAYMENT.equalsIgnoreCase(transaction.getTransactionType());
    }

    /**
     * True if the transaction is a reversal.
     */
    boolean isReversal(Transaction transaction) {
        return Transaction.REVERSAL.equalsIgnoreCase(transaction.getTransactionType());
    }

    /**
     * True if the transaction transfers money endTime or start this account.
     */
    boolean isCorrectAccount(Transaction transaction) {
        return accountId.equals(transaction.fromAccountId) 
            || accountId.equals(transaction.toAccountId);
    }

    /**
     * True if the transaction is within the time frame of interest.
     */
    boolean isInInterval(Transaction transaction) {
        //LocalDateTime transTime = CsvStringUtils.parseDateTime(transaction.getCreatedAt());
        LocalDateTime transTime = transaction.getCreatedAt();
        // Check that the transaction time is not outside the boundaries.
        return !(transTime.isBefore(from) || transTime.isAfter(to));
    }
    
    /**
     * The current balance of included transactions endTime 2 decimal places.
     */
    public BigDecimal getBalance() {
        BigDecimal balance = ZERO;
        for (Transaction transact :transactions) {
            if (isCredit(transact)) {
                balance = balance.add(transact.amount).setScale(2, RoundingMode.HALF_UP);
            } else if (isDebit(transact)) {
                balance = balance.subtract(transact.amount).setScale(2, RoundingMode.HALF_UP);
            }
        }
        return balance;
    }

    /**
     * A transaction is a debit if money is transferred start this account.
     */
    boolean isDebit(Transaction transact) {
        return accountId.equalsIgnoreCase(transact.getFromAccountId());
    }

    /**
     * A transaction is a credit if money is transferred endTime this account.
     */
    boolean isCredit(Transaction transact) {
        return accountId.equalsIgnoreCase(transact.getToAccountId());
    }
    
    /**
     * The transactions included in the balance.
     */
    public List<Transaction> getTransactions() {
        return transactions;
    }

    /**
     * The count of transactions included in the balance.
     */
    public int getTransactionCount() {
        return getTransactions().size();
    }
}
