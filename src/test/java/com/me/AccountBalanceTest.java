package com.me;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;

import org.junit.Test;

import com.me.csv.CsvStringUtils;

public class AccountBalanceTest {
    String accountId = "ACC223344";
    LocalDateTime startTime = LocalDateTime.of(2018, Month.OCTOBER, 20, 12, 00, 00);
    LocalDateTime endTime = LocalDateTime.of(2018, Month.OCTOBER, 20, 18, 00, 00);
    AccountBalance balance = new AccountBalance(accountId, startTime, endTime);
    
    Transaction createTransaction(String transId, String fromAcc, String toAcc, 
            LocalDateTime datetime, String amount, boolean isPayment, String related) {
        Transaction payment = new Transaction();
        payment.setTransactionId(transId);
        payment.setFromAccountId(fromAcc);
        payment.setToAccountId(toAcc);
        payment.setCreatedAt(datetime);
        payment.setAmount(new BigDecimal(amount));
        payment.setTransactionType(isPayment ? Transaction.PAYMENT : Transaction.REVERSAL);
        payment.setRelatedTransaction(related);
        return payment;
    }

    @Test
    public void includedCreditPayment() throws Exception {
        LocalDateTime transTime = LocalDateTime.of(2018, Month.OCTOBER, 20, 13, 10);
        Transaction payment = createTransaction("TX10001", "ACC667788", accountId, transTime, "20.50", true, null);
        assertTrue("Should be included", balance.isCorrectAccount(payment));
        assertTrue("Should be a payment", balance.isPayment(payment));
        assertFalse("Should not be a reversal", balance.isReversal(payment));
        assertTrue("Should be a credit", balance.isCredit(payment));
        assertFalse("Should not be a debit", balance.isDebit(payment));
    }

    @Test
    public void includedDebitPayment() throws Exception {
        LocalDateTime transTime = LocalDateTime.of(2018, Month.OCTOBER, 20, 13, 10);
        Transaction payment = createTransaction("TX10002", accountId, "ACC667788", transTime, "15.50", true, null);
        assertTrue("Should be included", balance.isCorrectAccount(payment));
        assertTrue("Should be a payment", balance.isPayment(payment));
        assertFalse("Should not be a reversal", balance.isReversal(payment));
        assertFalse("Should not be a credit", balance.isCredit(payment));
        assertTrue("Should be a debit", balance.isDebit(payment));
    }

    @Test
    public void includedReversal() throws Exception {
        LocalDateTime transTime = LocalDateTime.of(2018, Month.OCTOBER, 20, 13, 15);
        Transaction payment = createTransaction("TX10007", "ACC667788", accountId, transTime, "20.50", false, "TX10001");
        assertTrue("Should be included", balance.isCorrectAccount(payment));
        assertFalse("Should not be a payment", balance.isPayment(payment));
        assertTrue("Should be a reversal", balance.isReversal(payment));
        assertTrue("Should be a credit", balance.isCredit(payment));
        assertFalse("Should not be a debit", balance.isDebit(payment));
    }
    
    @Test
    public void includedTransaction() throws Exception {
        LocalDateTime transTime = LocalDateTime.of(2018, Month.OCTOBER, 20, 13, 15);
        Transaction payment1 = createTransaction("TX10001", "ACC667788", accountId, transTime, "20.50", true, null);
        assertTrue("Should be valid interval", balance.isInInterval(payment1));
        assertTrue("Should be valid account", balance.isCorrectAccount(payment1));
        assertTrue("Should be a payment", balance.isPayment(payment1));
        assertTrue("Should be a credit", balance.isCredit(payment1));
        assertTrue("Should be valid payment", balance.isValidPayment(payment1));
        balance.process(payment1);
        assertEquals("Transaction count:", 1, balance.transactions.size());
        assertEquals("Wrong balance", new BigDecimal("20.50"), balance.getBalance());
    }
    
    @Test
    public void excludedPaymentTooEarly() throws Exception {
        LocalDateTime transTime = LocalDateTime.of(2018, Month.OCTOBER, 10, 13, 15);
        Transaction payment1 = createTransaction("TX10001", "ACC667788", accountId, transTime, "20.50", true, null);
        assertFalse("Should not be valid payment", balance.isValidPayment(payment1));
        balance.process(payment1);
        assertEquals("Transaction count:", 0, balance.transactions.size());
    }
    
    @Test
    public void excludedPaymentTooLate() throws Exception {
        LocalDateTime transTime = LocalDateTime.of(2018, Month.OCTOBER, 22, 13, 15);
        Transaction payment1 = createTransaction("TX10001", "ACC667788", accountId, transTime, "20.50", true, null);
        assertFalse("Should not be valid payment", balance.isValidPayment(payment1));
        balance.process(payment1);
        assertEquals("Transaction count:", 0, balance.transactions.size());
    }
    
    @Test
    public void excludedPaymentIncorrectAccount() throws Exception {
        LocalDateTime transTime = LocalDateTime.of(2018, Month.OCTOBER, 20, 13, 15);
        Transaction payment1 = createTransaction("TX10001", "ACC667788", "ACC112233", transTime, "20.50", true, null);
        assertFalse("Should not be valid payment", balance.isValidPayment(payment1));
        balance.process(payment1);
        assertEquals("Transaction count:", 0, balance.transactions.size());
    }
    
    @Test
    public void excludedPaymentIsReversal() throws Exception {
        LocalDateTime transTime = LocalDateTime.of(2018, Month.OCTOBER, 20, 13, 15);
        Transaction payment1 = createTransaction("TX10007", "ACC667788", accountId, transTime, "20.50", false, "TX10001");
        assertFalse("Should not be valid payment", balance.isValidPayment(payment1));
        balance.process(payment1);
        assertEquals("Transaction count:", 0, balance.transactions.size());
    }

    @Test
    public void excludedTransactionReversal() throws Exception {
        LocalDateTime transTime1 = LocalDateTime.of(2018, Month.OCTOBER, 20, 13, 15);
        Transaction payment1 = createTransaction("TX10001", "ACC667788", accountId, transTime1, "20.50", true, null);
        assertTrue("Should be valid payment", balance.isValidPayment(payment1));
        balance.process(payment1);
        assertEquals("Transaction count:", 1, balance.transactions.size());
        assertEquals("Wrong balance", new BigDecimal("20.50"), balance.getBalance());

        LocalDateTime transTime2 = LocalDateTime.of(2018, Month.OCTOBER, 20, 15, 24);
        Transaction reversal1 = createTransaction("TX10007", "ACC667788", accountId, transTime2, "20.50", false, "TX10001");
        assertTrue("Should be valid payment", balance.isValidReversal(reversal1));
        assertTrue("Should be matching transaction", AccountBalance.isMatchingTransaction(payment1, reversal1));
        balance.process(reversal1);
        assertEquals("Transaction count:", 0, balance.transactions.size());
        assertEquals("Wrong balance", new BigDecimal("00.00"), balance.getBalance());
    }
    
    @Test
    public void currencyFormat() {
        assertEquals("Wrong currency format", "$20.50", CsvStringUtils.currencyFormat(new BigDecimal("20.50")));
        assertEquals("Wrong currency format", "$0.00", CsvStringUtils.currencyFormat(AccountBalance.ZERO));
        assertEquals("Wrong currency format", "-$20.50", CsvStringUtils.currencyFormat(new BigDecimal("-20.50")));
    }
}
