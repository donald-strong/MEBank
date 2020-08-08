package com.me;

import java.io.BufferedReader;
import java.io.File;
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
        int n = 0;
        if (args.length == 4) {
            setFilename(args[n++]);
            setAccountId(args[n++]);
            setFrom(args[n++]);
            setTo(args[n++]);
        } else {
            throw new IllegalArgumentException("Must be 4 command line arguments");
        }
    }
    
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        File file = new File(filename);
        if (file.exists()) {
            this.filename = filename;
        } else {
            throw new IllegalArgumentException("File not found: " + file.getAbsolutePath());
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
        System.err.println("Usage: java -jar <jarfile> <filename> <account_id> <from_date_time> <to_date_time>");
        System.err.println("\t<jarfile> the path to the jar file (Not really a parameter)");
        System.err.println("\t<filename> the path to the transaction file");
        System.err.println("\t<account_id> the account ID");
        System.err.println("\t<from_date_time> the time from which to include transations");
        System.err.println("\t<to_date_time> the time to which to include transactions");
        System.err.println("E.g. $ java -jar MEBank.jar Sample.csv ACC334455 '20/10/2018 12:00:00' '20/10/2018 19:00:00'");
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
        out.println("Relative balance for the period is: " + CsvStringUtils.currencyFormat(account.getBalance()));
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
