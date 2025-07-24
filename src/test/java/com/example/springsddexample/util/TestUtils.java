package com.example.springsddexample.util;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class TestUtils {
    
    public static final String DEFAULT_EMAIL = "test@example.com";
    public static final String DEFAULT_USERNAME = "testUser";
    public static final String DEFAULT_FIRST_NAME = "John";
    public static final String DEFAULT_LAST_NAME = "Doe";
    
    public static ZonedDateTime fixedDateTime() {
        return ZonedDateTime.of(2024, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
    }
    
    public static String emailWithSuffix(String suffix) {
        return "test" + suffix + "@example.com";
    }
    
    public static String usernameWithSuffix(String suffix) {
        return DEFAULT_USERNAME + suffix;
    }
}