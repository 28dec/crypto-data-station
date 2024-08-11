package me.longnh.cryptodatastation.common.model;

import java.time.format.DateTimeFormatter;

public class CdsConstant {
    private CdsConstant(){};

    public static DateTimeFormatter FORMATTER_UTC_DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
}
