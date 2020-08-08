package com.me.csv;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CsvStringUtils {
    public static final String DATETIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DATETIME_FORMAT);

    public static LocalDateTime parseDateTime(String value) {
        if (value == null) {
            return null;
        } else {
            return LocalDateTime.parse(value, DATETIME_FORMATTER);
        }
    }
    
    public static String format(LocalDateTime value) {
        if (value == null) {
            return "";
        } else {
            return value.format(DATETIME_FORMATTER);
        }
    }
    
    public static BigDecimal parseAmount(String value) {
        if (value == null) {
            return null;
        } else {
            return new BigDecimal(value);
        }
    }
    
    public static String format(BigDecimal value) {
        if (value == null) {
            return "";
        } else {
            return value.toString();
        }
    }
    
    public static String currencyFormat(BigDecimal n) {
        return NumberFormat.getCurrencyInstance().format(n);
    }
}
