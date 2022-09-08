package dev.nh7.javahttpserver;

import java.text.SimpleDateFormat;

public class Utils {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public static void log(String message) {
        String time = DATE_FORMAT.format(System.currentTimeMillis());
        System.out.println("[" + time + "] " + message);
    }

}
