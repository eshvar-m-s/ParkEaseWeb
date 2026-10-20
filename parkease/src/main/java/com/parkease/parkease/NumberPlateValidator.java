package com.parkease.parkease;

public final class NumberPlateValidator {
    private NumberPlateValidator() { }

    public static boolean isValid(String numberPlate) {
        if (numberPlate == null || numberPlate.length() < 6 || numberPlate.length() > 12) {
            return false;
        }

        for (int index = 0; index < numberPlate.length(); index++) {
            if (!Character.isLetterOrDigit(numberPlate.charAt(index))) {
                return false;
            }
        }
        return true;
    }
}
