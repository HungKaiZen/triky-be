package vn.tayjava.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    private static final DateTimeFormatter UI_FORMAT = DateTimeFormatter.ofPattern("yyy-MM-dd");
    private static final DateTimeFormatter DTO_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public static String convertUiToDtoFormat(String uiDate) {
        LocalDateTime localDateTime = LocalDateTime.parse(uiDate, UI_FORMAT);
        return localDateTime.format(DTO_FORMAT);
    }
}
