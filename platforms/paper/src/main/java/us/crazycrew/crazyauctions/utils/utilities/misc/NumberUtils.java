package us.crazycrew.crazyauctions.utils.utilities.misc;

public class NumberUtils {

    public static boolean isInt(String value) {
        try {
            Integer.parseInt(value);
        } catch (NumberFormatException nfe) {
            return false;
        }

        return true;
    }

    /**
     * This converts a String into a number if using a roman numeral from I-X.
     * @param level The string you want to convert.
     * @return The roman numeral as a number.
     */
    public static int convertLevelInteger(String level) {
        switch (level) {
            case "I" -> {
                return 1;
            }
            case "II" -> {
                return 2;
            }
            case "III" -> {
                return 3;
            }
            case "IV" -> {
                return 4;
            }
            case "V" -> {
                return 5;
            }
            case "VI" -> {
                return 6;
            }
            case "VII" -> {
                return 7;
            }
            case "VIII" -> {
                return 8;
            }
            case "IX" -> {
                return 9;
            }
            case "X" -> {
                return 10;
            }

            default -> {
                if (isInt(level)) {
                    return Integer.parseInt(level);
                } else {
                    return 0;
                }
            }
        }
    }
}