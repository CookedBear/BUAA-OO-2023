public class Request {
    private final String student;
    private final int date;
    private final int action;
    private final String book;

    public Request(String date, String student, String action, String book) {
        this.student = student;
        this.date = DateCal.getDate(date);
        this.action = (action.equals("borrowed")) ? 1 :
                      (action.equals("smeared")) ? 2 :
                      (action.equals("lost")) ? 3 : 4;
        this.book = book;
    }

}
