package com.me;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.junit.Test;

import com.me.csv.CsvStringUtils;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;


public class TransactionTest {
    @Test
    public void readLinesAsString() throws Exception {
        InputStream input = getClass().getClassLoader().getResourceAsStream("SampleOne.csv");
        assertNotNull("Cannot find Sample.csv as resource", input);
        try (CSVReader reader = new CSVReader(new BufferedReader(new InputStreamReader(input)))) {
            List<String[]> myEntries = reader.readAll();
            assertEquals(2, myEntries.size());
        }
    }
    
    @Test
    public void readTransaction() throws Exception {
        InputStream input = getClass().getClassLoader().getResourceAsStream("SampleOne.csv");
        assertNotNull("Cannot find Sample.csv as resource", input);
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        CsvToBean<Transaction> csvToBean = new CsvToBeanBuilder<Transaction>(reader)
                .withType(Transaction.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build();
        List<Transaction> myEntries = csvToBean.parse();
        assertEquals(1, myEntries.size());
        Transaction trans = myEntries.get(0);
        assertNotNull("Transaction not found", trans);
        // TX10001, ACC334455, ACC778899, 20/10/2018 12:47:55, 25.00, PAYMENT
        assertEquals("TX10001", trans.getTransactionId());
        assertEquals("ACC334455", trans.getFromAccountId());
        assertEquals("ACC778899", trans.getToAccountId());
        assertEquals("20/10/2018 12:47:55", CsvStringUtils.format(trans.getCreatedAt()));
        assertEquals("25.00", trans.getAmount().toString());
        assertEquals("PAYMENT", trans.getTransactionType());
        assertEquals("", trans.getRelatedTransaction());
    }
}
