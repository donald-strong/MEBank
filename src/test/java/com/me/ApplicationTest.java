package com.me;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;

import org.junit.Test;

public class ApplicationTest {
    @Test
    public void commandLine() throws Exception {
        String[] args = {"ACC334455", "20/10/2018 12:00:00", "20/10/2018 19:00:00"};
        Application app = new Application(args);
        assertEquals("Wrong account ID", "ACC334455", app.getAccountId());
        assertEquals("Wrong start time", LocalDateTime.of(2018, Month.OCTOBER, 20, 12, 00, 00), app.getStart());
        assertEquals("Wrong endTime time", LocalDateTime.of(2018, Month.OCTOBER, 20, 19, 00, 00), app.getEndTime());
    }

    @Test
    public void usage() throws Exception {
        String[] args = {};
        Application app = new Application(args);
        app.toString();
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
