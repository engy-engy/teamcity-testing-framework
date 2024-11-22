package com.example.teamcity.api.generators;

import org.apache.commons.lang3.RandomStringUtils;

public final class RandomData {

    private static final String TEST_PREFIX = "test_";
    private static final int MAX_LENGTH = 10;
    private static final long MIN_LONG = 1L;
    private static final long MAX_LONG = 10L;


    public static String getString() {
        return TEST_PREFIX + RandomStringUtils.randomAlphabetic(MAX_LENGTH);
    }

    public static String getString(int length) {
        return TEST_PREFIX + RandomStringUtils
                .randomAlphabetic(Math.max(length - TEST_PREFIX.length(), MAX_LENGTH));
    }
    public static long getLong() {
        return MIN_LONG + (long) (Math.random() * (MAX_LONG - MIN_LONG));  // Генерация случайного long
    }
}
