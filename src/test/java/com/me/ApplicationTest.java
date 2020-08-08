package com.me;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;

import org.junit.Test;

public class ApplicationTest {
    @Test
    public void processSampleOne() throws Exception {
        String[] args = {"src/test/resources/SampleOne.csv", "ACC334455", "20/10/2018 12:00:00", "20/10/2018 19:00:00"};
        Application app = new Application(args);
        assertEquals("Wrong account ID", "ACC334455", app.getAccountId());
        assertEquals("Wrong start time", LocalDateTime.of(2018, Month.OCTOBER, 20, 12, 00, 00), app.getStart());
        assertEquals("Wrong endTime time", LocalDateTime.of(2018, Month.OCTOBER, 20, 19, 00, 00), app.getEndTime());
    }

    @Test
    public void processSampleTransactions3() throws Exception {
        String[] args = {"src/test/resources/SampleTransactions.csv", "ACC778899", "20/10/2018 12:00:00", "21/10/2018 12:00:00"};
        Application app = new Application(args);
        AccountBalance accountBalance = app.createAccountBalance();
        app.process(accountBalance);
        app.report(System.out, accountBalance);
        assertEquals("Transaction count", 3, accountBalance.getTransactionCount());
        assertEquals("Account Balance", new BigDecimal("37.25"), accountBalance.getBalance());
    }

    @Test
    public void processSampleTransactions1() throws Exception {
        String[] args = {"src/test/resources/SampleTransactions.csv", "ACC334455", "20/10/2018 12:00:00", "20/10/2018 19:00:00"};
        Application app = new Application(args);
        AccountBalance accountBalance = app.createAccountBalance();
        app.process(accountBalance);
        app.report(System.out, accountBalance);
        assertEquals("Transaction count", 1, accountBalance.getTransactionCount());
        assertEquals("Account Balance", new BigDecimal("-25.00"), accountBalance.getBalance());
    }

    @Test
    public void fileNotFound() throws Exception {
        String[] args = {"dummy.csv", "ACC334455", "20/10/2018 12:00:00", "20/10/2018 19:00:00"};
        try {
            new Application(args);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertTrue("Should start with 'File not found:'", ex.getMessage().startsWith("File not found: "));
        }
    }
    
    @Test
    public void invalidFromDateTime() throws Exception {
        String[] args = {"src/test/resources/SampleOne.csv", "ACC334455", "20-10-2018 12:00:00", "20/10/2018 19:00:00"};
        try {
            new Application(args);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            String expected = "Expected date format (31/12/2020 12:34:56) not '20-10-2018 12:00:00'";
            assertEquals("Wrong error message", expected, ex.getMessage());
        }
    }
    
    @Test
    public void invalidToDateTime() throws Exception {
        String[] args = {"src/test/resources/SampleOne.csv", "ACC334455", "20/10/2018 12:00:00", "20-10-2018 19:00"};
        try {
            new Application(args);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            String expected = "Expected date format (31/12/2020 12:34:56) not '20-10-2018 19:00'";
            assertEquals("Wrong error message", expected, ex.getMessage());
        }
    }
    
    @Test
    public void usage() throws Exception {
        String[] args = {};
        try {
            new Application(args);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals("Must be 4 command line arguments", ex.getMessage());
        }
    }
    
    @Test
    public void processSampleFile() throws Exception {
        String[] args = {"src/test/resources/SampleOne.csv", "ACC334455", "20/10/2018 12:00:00", "20/10/2018 19:00:00"};
        Application app = new Application(args);
        assertEquals("Wrong account ID", "ACC334455", app.getAccountId());
        assertEquals("Wrong start time", LocalDateTime.of(2018, Month.OCTOBER, 20, 12, 00, 00), app.getStart());
        assertEquals("Wrong endTime time", LocalDateTime.of(2018, Month.OCTOBER, 20, 19, 00, 00), app.getEndTime());
    }

    @Test
    public void dateFormat() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime expected = LocalDateTime.of(2018, Month.OCTOBER, 20, 12, 00, 00);
        assertEquals("Wrong datetime format", "20/10/2018 12:00:00", expected.format(formatter));
        LocalDateTime local = LocalDateTime.parse("20/10/2018 12:00:00", formatter);
        assertEquals("Wrong date & time", expected, local);
    }
}
