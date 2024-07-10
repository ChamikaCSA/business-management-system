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

    public static String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int index = (int) (Math.random() * chars.length());
            password.append(chars.charAt(index));
        }
        return password.toString();
    }
}
