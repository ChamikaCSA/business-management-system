package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class IDGenerator {
    public static String generateId(String prefix, int count) {
        return String.format("%s-%04d", prefix, count);
    }

    public static String generateDatedId(String prefix, int count) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String datePart = sdf.format(new Date());
        return String.format("%s-%s-%03d", prefix, datePart, count);
    }
}
