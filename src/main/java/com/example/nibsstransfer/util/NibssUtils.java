package com.example.nibsstransfer.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * @author Peace Obute
 * @since 09/06/2024
 */
public class NibssUtils {

    private static final Random RANDOM = new Random();
    private static final int MAX_LENGTH = 10;

    public static String generateRandomNumberString(int length) {
        if (length <= 0 || length > MAX_LENGTH) {
            throw new IllegalArgumentException("Length must be between 1 and " + MAX_LENGTH);
        }

        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int digit = RANDOM.nextInt(10);  // Generates a random digit between 0 and 9
            stringBuilder.append(digit);
        }
        return stringBuilder.toString();
    }

    public static Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

}
