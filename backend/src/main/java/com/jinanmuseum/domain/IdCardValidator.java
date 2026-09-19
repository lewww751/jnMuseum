package com.jinanmuseum.domain;

public class IdCardValidator {

    private static final int LENGTH = 18;
    private static final int[] WEIGHTS = {
            7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2
    };
    private static final String[] CHECK_CHARS = {
            "1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"
    };

    public static boolean isValid(String idCard) {
        if (idCard == null || idCard.trim().isEmpty()) {
            return false;
        }

        String normalized = idCard.trim();
        if (normalized.length() != LENGTH) {
            return false;
        }

        // Check if first 17 characters are digits
        for (int i = 0; i < 17; i++) {
            if (!Character.isDigit(normalized.charAt(i))) {
                return false;
            }
        }

        // Check last character (digit or X/x)
        char lastChar = normalized.charAt(17);
        if (!Character.isDigit(lastChar) && lastChar != 'X' && lastChar != 'x') {
            return false;
        }

        return validateChecksum(normalized);
    }

    public static String normalize(String idCard) {
        if (idCard == null || !isValid(idCard)) {
            return idCard;
        }
        return idCard.trim().toUpperCase();
    }

    private static boolean validateChecksum(String idCard) {
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += (idCard.charAt(i) - '0') * WEIGHTS[i];
        }

        int checkIndex = sum % 11;
        String expectedCheck = CHECK_CHARS[checkIndex];
        String actualCheck = String.valueOf(idCard.charAt(17)).toUpperCase();

        return expectedCheck.equals(actualCheck);
    }
}