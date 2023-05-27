package test;

public class DateCal {
    private static final int[] DATES = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    public static int getDate(String str) {
        int month = Integer.parseInt(str.substring(5, 7));
        int date = Integer.parseInt(str.substring(8));

        for (int i = 1; i < month; i++) {
            date += DATES[i - 1];
        }
        return date;
    }

    public static String getDateOutput(int date) {
        if (date < 32) {
            return "2023-01-" + String.format("%02d", date);
        } else if (date < 60) {
            return "2023-02-" + String.format("%02d", (date - 31));
        } else if (date < 91) {
            return "2023-03-" + String.format("%02d", (date - 59));
        } else if (date < 121) {
            return "2023-04-" + String.format("%02d", (date - 90));
        } else if (date < 152) {
            return "2023-05-" + String.format("%02d", (date - 120));
        } else if (date < 182) {
            return "2023-06-" + String.format("%02d", (date - 151));
        } else if (date < 213) {
            return "2023-07-" + String.format("%02d", (date - 181));
        } else if (date < 244) {
            return "2023-08-" + String.format("%02d", (date - 212));
        } else if (date < 274) {
            return "2023-09-" + String.format("%02d", (date - 243));
        } else if (date < 305) {
            return "2023-10-" + String.format("%02d", (date - 273));
        } else if (date < 335) {
            return "2023-11-" + String.format("%02d", (date - 304));
        } else {
            return "2023-12-" + String.format("%02d", (date - 334));
        }
    }
}
