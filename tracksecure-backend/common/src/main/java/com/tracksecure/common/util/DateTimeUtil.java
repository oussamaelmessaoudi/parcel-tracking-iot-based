package com.tracksecure.common.util;

import org.springframework.cglib.core.Local;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DateTimeUtil {
    private DateTimeUtil(){
        throw new UnsupportedOperationException("Utility class");
    }

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    public static String format(LocalDateTime dateTime){
        return dateTime != null ? dateTime.format(ISO_FORMATTER) : null;
    }

    public static LocalDateTime parse(String dateTimeStr){
        return dateTimeStr != null ? LocalDateTime.parse(dateTimeStr, ISO_FORMATTER) : null;
    }

    public static boolean isExpired(LocalDateTime timestamp, Duration ttl){
        return timestamp != null && timestamp.plus(ttl).isBefore(LocalDateTime.now());
    }
}
