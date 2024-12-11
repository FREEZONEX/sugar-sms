package org.niiish32x.sugarsms.common.supos.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * TimeUtil
 *
 * @author shenghao ni
 * @date 2024.12.11 17:01
 */
public class TimeUtil {
    public static String formatTimeStamp(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        String formattedDateTime = localDateTime.format(formatter);
        return formattedDateTime;
    }
}
