package com.university.university_events.core.configuration;

public class Constants {
    public static final String SEQUENCE_NAME = "hibernate_sequence";

    public static final String API_URL = "/api/1.0";

    public static final int DEFUALT_PAGE_SIZE = 10;

    public static final String REDIRECT_VIEW = "redirect:";

    public static final String ADMIN_PREFIX = "/admin";

    public static final String DEFAULT_PASSWORD = "1234";

    //public static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[!@#\\$%\\^&\\*\\(\\)\\-_+=;:,\\./\\?\\\\|`~\\[\\]\\{\\}])(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z!@#\\$%\\^&\\*\\(\\)\\-_+=;:,\\./\\?\\\\|`~\\[\\]\\{\\}]{8,60}$";

    public static final String PHONE_PATTERN = "^((8|\\+374|\\+994|\\+995|\\+375|\\+7|\\+380|\\+38|\\+996|\\+998|\\+993)[\\- ]?)?\\(?\\d{3,5}\\)?[\\- ]?\\d{1}[\\- ]?\\d{1}[\\- ]?\\d{1}[\\- ]?\\d{1}[\\- ]?\\d{1}(([\\- ]?\\d{1})?[\\- ]?\\d{1})?$";

    private Constants() {
    }
}
