package com.me;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;

public class Transaction {
    public static final String PAYMENT = "PAYMENT";
    public static final String REVERSAL = "REVERSAL";

    @CsvBindByName
    String transactionId;
    @CsvBindByName
    String fromAccountId;
    @CsvBindByName
    String toAccountId;
    @CsvBindByName
    @CsvDate("dd/MM/yyyy HH:mm:ss")
    LocalDateTime createdAt;
    @CsvBindByName
    BigDecimal amount;
    @CsvBindByName
    String transactionType;
    @CsvBindByName
    String relatedTransaction;

    public Transaction() {
        super();
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(String fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public String getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(String toAccountId) {
        this.toAccountId = toAccountId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getRelatedTransaction() {
        return relatedTransaction;
    }

    public void setRelatedTransaction(String relatedTransaction) {
        this.relatedTransaction = relatedTransaction;
    }
}
