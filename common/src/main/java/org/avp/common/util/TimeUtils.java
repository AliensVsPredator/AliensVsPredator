package org.avp.common.util;

import java.time.LocalDate;
import java.time.Month;

public class TimeUtils {

    public static final int ONE_MINUTE_IN_TICKS = 20 * 60;

    public static final int FIVE_MINUTES_IN_MILLIS = 5 * 60 * 1000;

    public static boolean isHalloween() {
        LocalDate now = LocalDate.now();
        return now.getMonth() == Month.OCTOBER && now.getDayOfMonth() == 31;
    }

    private TimeUtils() {
        throw new UnsupportedOperationException();
    }
}
