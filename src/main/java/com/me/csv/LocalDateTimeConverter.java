package com.me.csv;

import java.time.LocalDateTime;

import com.opencsv.bean.AbstractBeanField;

public class LocalDateTimeConverter<T,I> extends AbstractBeanField<T,I> {

        @Override
        public Object convert(String value) {
            if (value == null) {
                return null;
            } else {
                return CsvStringUtils.parseDateTime(value);
            }
        }

        @Override
        public String convertToWrite(Object value) {
            if (value == null) {
                return "null";
            } else if (value instanceof LocalDateTime) {
                return CsvStringUtils.format((LocalDateTime)value);
            } else {
                return value.toString();
            }
        }

}
