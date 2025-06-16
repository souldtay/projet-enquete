// src/util/LocalDateConverter.java
package util;

import com.opencsv.bean.AbstractBeanField;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class LocalDateConverter
        extends AbstractBeanField<LocalDate, String> {

    /** CSV ▸ Bean */
    @Override
    protected LocalDate convert(String value) {
        if (value == null || value.isBlank())
            return null;
        try {
            return LocalDate.parse(value);              // ISO-8601: yyyy-MM-dd
        } catch (DateTimeParseException ex) {           // header or bad value
            return null;                                // silently ignore
        }
    }

    /** Bean ▸ CSV */
    @Override
    protected String convertToWrite(Object value) {
        return value == null ? "" : value.toString();
    }
}
