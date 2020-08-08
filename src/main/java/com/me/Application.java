package com.me;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.List;

import com.me.csv.CsvStringUtils;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvException;

public class Application {
    
    String filename;
    String accountId;
    LocalDateTime startTime;
    LocalDateTime endTime;
    
    public Application(String[] args) {
        setArguments(args);
    }
    
    public void setArguments(String[] args) {
        if (args.length == 3) {
            setAccountId(args[0]);
            setFrom(args[1]);
            setTo(args[2]);
        } else {
            usage();
        }
    }
    
    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public LocalDateTime getStart() {
        return startTime;
    }

    public void setFrom(String from) {
        this.startTime = CsvStringUtils.parseDateTime(from);
    }

    public void setFrom(LocalDateTime from) {
        this.startTime = from;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setTo(String to) {
        this.endTime = CsvStringUtils.parseDateTime(to);
    }

    public void setTo(LocalDateTime to) {
        this.endTime = to;
    }
    
    AccountBalance createAccountBalance() {
        return new AccountBalance(getAccountId(), startTime, endTime);
    }
    
    public static void usage() {
        System.err.println("Usage: Application <account_id> <from_date_time> <to_date_time>");
    }

    public void process(AccountBalance accountBalance) throws IOException, CsvException {
        FileInputStream input = new FileInputStream(filename);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            CsvToBean<Transaction> csvToBean = new CsvToBeanBuilder<Transaction>(reader)
                    .withType(Transaction.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            List<Transaction> transactions = csvToBean.parse();
            for (Transaction trans : transactions) {
                accountBalance.process(trans);
            }
        } finally {
            input.close();
        }
    }

    public void report(PrintStream out, AccountBalance account) {
        out.println("Relative balance for the period is: " + account.getBalance());
        out.println("Number of transactions included is: " + account.getTransactionCount());
    }

    public static void main(String [] args) {
        try {
            Application app = new Application(args);
            AccountBalance accountBalance = app.createAccountBalance();
            app.process(accountBalance);
            app.report(System.out, accountBalance);
        } catch (Exception ex) {
            System.err.println("Error: " + ex.getMessage());
            Application.usage();
        }
    }
}
