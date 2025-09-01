package com.hamster.ecommerce.util;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class TimeUtil
{
    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");

    public static String getFormattedDuration(LocalDateTime beginTime, LocalDateTime endTime)
    {
        Duration duration = Duration.between(beginTime, endTime);
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static String getFormattedDateTime(LocalDateTime dateTime)
    {
        return dateTime.format(dateTimeFormatter);
    }
}
