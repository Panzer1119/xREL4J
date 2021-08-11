package com.github.saftsau.xrel4j.converters;

import com.fasterxml.jackson.databind.util.StdConverter;
import com.github.saftsau.xrel4j.release.Size;

public class SizeNumberToStringConverter extends StdConverter<Integer, String> {
    
    @Override
    public String convert(Integer number) {
        if (number == null) {
            return null;
        }
        return switch (number) {
            case -1 -> "";
            case Size.SIZE_EQUALS -> "=";
            case Size.SIZE_GREATER_THAN -> ">";
            case Size.SIZE_GREATER_THAN_OR_EQUALS -> ">=";
            case Size.SIZE_LESS_THAN_OR_EQUALS -> "<=";
            case Size.SIZE_LESS_THAN -> "<";
            default -> Integer.toString(number);
        };
    }
    
}
