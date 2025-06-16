package util;

import com.opencsv.bean.AbstractBeanField;
import java.time.*;
import java.time.format.*;

public class LocalDateTimeConverter
        extends AbstractBeanField<LocalDateTime, String> {

    // we’ll try ISO first, then fallback to M/d/yyyy at start-of-day
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_DATE_TIME;
    private static final DateTimeFormatter MDY = DateTimeFormatter.ofPattern("M/d/yyyy");

    @Override
    protected LocalDateTime convert(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        // Try full ISO date-time
        try {
            return LocalDateTime.parse(value, ISO);
        } catch (DateTimeParseException ignored) { }

        // Fallback to month/day/year (no time) → midnight
        try {
            LocalDate d = LocalDate.parse(value, MDY);
            return d.atStartOfDay();
        } catch (DateTimeParseException ex) {
            // if you get here, the value really isn’t parseable
            throw new RuntimeException(
                "Cannot parse timestamp '" + value +
                "': expected ISO or M/d/yyyy", ex);
        }
    }

    @Override
    protected String convertToWrite(Object value) {
        if (value instanceof LocalDateTime ldt) {
            // write back as ISO so round-trip works
            return ISO.format(ldt);
        }
        return "";
    }
}
