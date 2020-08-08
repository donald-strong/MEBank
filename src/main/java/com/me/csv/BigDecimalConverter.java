package com.me.csv;

import java.math.BigDecimal;

import com.opencsv.bean.AbstractBeanField;

public class BigDecimalConverter<T,I> extends AbstractBeanField<T,I> {
        
        @Override
        public Object convert(String value) {
            if (value == null) {
                return null;
            } else {
                return new BigDecimal(value);
            }
        }

        @Override
        public String convertToWrite(Object value) {
            if (value == null) {
                return "null";
            } else if (value instanceof BigDecimal) {
                return value.toString();
            } else {
                return value.toString();
            }
        }

}
