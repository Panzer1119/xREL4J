package com.github.saftsau.xrel4j.converters;

import com.fasterxml.jackson.databind.util.StdConverter;
import com.github.saftsau.xrel4j.release.Size;

public class StringToSizeNumberConverter extends StdConverter<String, Integer> {
    
    @Override
    public Integer convert(String number) {
        if (number == null) {
            return null;
        }
        return switch (number) {
            case "" -> -1;
            case "=" -> Size.SIZE_EQUALS;
            case ">" -> Size.SIZE_GREATER_THAN;
            case ">=" -> Size.SIZE_GREATER_THAN_OR_EQUALS;
            case "<=" -> Size.SIZE_LESS_THAN_OR_EQUALS;
            case "<" -> Size.SIZE_LESS_THAN;
            default -> Integer.parseInt(number);
        };
    }
    
}
