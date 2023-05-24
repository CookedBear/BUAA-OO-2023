package instance;

import tool.DateCal;

public class Request {
    private final String student;
    private final int date;
    private final String dateOutput;
    private final int action;
    /*
     * borrow = 1
     * smeared = 2
     * lost = 3
     * return = 4
     */
    private final String book;

    public Request(String date, String student, String action, String book) {
        this.student = student;
        this.date = DateCal.getDate(date);
        this.dateOutput = date;
        this.action = (action.equals("borrowed")) ? 1 :
                      (action.equals("smeared")) ? 2 :
                      (action.equals("lost")) ? 3 : 4;
        this.book = book;
    }

    public Request(String date, String student, String book) {
        this.student = student;
        this.date = DateCal.getDate(date);
        this.dateOutput = date;
        this.action = 1;
        this.book = book;
    }

    public int getDate() { return date; }

    public String getDateOutput() { return dateOutput; }

    public int getAction() { return action; }

    public String getStudent() { return student; }

    public String getBook() { return book; }

}
