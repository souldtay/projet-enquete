package model;

import com.opencsv.bean.AbstractBeanField;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ListToStringConverter extends AbstractBeanField<List<String>, String> {

    @Override
    protected Object convert(String value) {
        if (value == null || value.isBlank()) return List.of();
        return Arrays.stream(value.split(";"))
                     .map(String::trim)
                     .collect(Collectors.toList());
    }

    @Override
    protected String convertToWrite(Object value) {
        if (value instanceof List) {
            return String.join(";", (Iterable<? extends CharSequence>) value);
        }
        return "";
    }
}
