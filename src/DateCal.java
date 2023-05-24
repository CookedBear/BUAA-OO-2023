public class DateCal {
    private static final int[] dates = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    public static int getDate(String str) {
        int month = Integer.parseInt(str.substring(5, 7));
        int date = Integer.parseInt(str.substring(8));

        for (int i = 1; i < month; i++) {
            date += dates[i - 1];
        }
        return date;
    }
}
