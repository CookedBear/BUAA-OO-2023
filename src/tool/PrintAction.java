package tool;

import instance.Book;
import instance.Student;

public class PrintAction {
    public static void queried(int date, Student student, Book book) {
        System.out.printf("[%s] %s-%s queried %s from self-service machine\n",
                        DateCal.getDateOutput(date), student.getSchool(),
                        student.getName(), book.getName());
        message(DateCal.getDateOutput(date), ":School", ":Machine");
        System.out.printf("[%s] self-service machine provided information of %s\n",
                DateCal.getDateOutput(date), book.getName());
        message(DateCal.getDateOutput(date), ":Machine", ":School");
    }

    public static void failed(String dateOutput, Student student, Book book, String serviceName) {
        System.out.printf("[%s] %s refused lending %s-%s to %s-%s\n",
                dateOutput, serviceName, book.getSchool(), book.getName(), student.getSchool(),
                student.getName());
        stateTrans(dateOutput, book, "stored", "stored");
        message(dateOutput, ":Machine", ":School");
    }

    public static void rented(int date, Student student, Book book, String serviceName) {
        System.out.printf("[%s] %s lent %s-%s to %s-%s\n",
                DateCal.getDateOutput(date), serviceName, book.getSchool(), book.getName(),
                student.getSchool(), student.getName());
        stateTrans(DateCal.getDateOutput(date), book, "stored", "rented");
        message(DateCal.getDateOutput(date), ":Machine", ":School");
        System.out.printf("[%s] %s-%s borrowed %s-%s from %s\n",
                DateCal.getDateOutput(date), student.getSchool(), student.getName(),
                book.getSchool(), book.getName(), serviceName);
    }

    public static void ordered(String dateOutput, Student student, Book book) {
        System.out.printf("[%s] %s-%s ordered %s-%s from ordering librarian\n" +
                        "[%s] ordering librarian recorded %s-%s's order of %s-%s\n",
                dateOutput, student.getSchool(), student.getName(),
                book.getSchool(), book.getName(),
                dateOutput, student.getSchool(), student.getName(),
                book.getSchool(), book.getName());
        message(dateOutput, ":Reserve", ":School");
    }

    public static void returned(String dateOutput, Student student, Book book, String serviceName) {
        System.out.printf("[%s] %s-%s returned %s-%s to %s\n" +
                          "[%s] %s collected %s-%s from %s-%s\n",
                dateOutput, student.getSchool(), student.getName(),
                book.getSchool(), book.getName(), serviceName,
                dateOutput, serviceName, book.getSchool(), book.getName(),
                student.getSchool(), student.getName());
        stateTrans(dateOutput, book, "rented", "stored");
    }

    public static void punished(String dateOutput, Student student, String serviceName) {
        System.out.printf("[%s] %s-%s got punished by %s\n" +
                          "[%s] borrowing and returning librarian received %s-%s's fine\n",
                dateOutput, student.getSchool(), student.getName(), serviceName,
                dateOutput, student.getSchool(), student.getName());
    }

    public static void repaired(String dateOutput, Book book, String serviceName, String school) {
        System.out.printf("[%s] %S-%s got repaired by %s in %s\n",
                dateOutput, book.getSchool(), book.getName(), serviceName, school);
        stateTrans(dateOutput, book, "stored", "stored");
    }

    public static void bought(String dateOutput, Book book, String serviceName) {
        System.out.printf("[%s] %s-%s got purchased by %s in %s\n",
                dateOutput, book.getSchool(), book.getName(), serviceName, book.getSchool());
    }

    public static void transIn(String dateOutput, Book book, String serviceName, String school) {
        System.out.printf("[%s] %s-%s got received by %s in %s\n",
                dateOutput, book.getSchool(), book.getName(), serviceName, school);
        stateTrans(dateOutput, book, "stored", "stored");
    }

    public static void transOut(String dateOutput, Book book, String serviceName, String school) {
        System.out.printf("[%s] %s-%s got transported by %s in %s\n",
                dateOutput, book.getSchool(), book.getName(), serviceName, school);
        stateTrans(dateOutput, book, "stored", "stored");
    }

    public static void stateTrans(String dateOutput, Book book, String state1, String state2) {
        System.out.printf("(State) [%s] %s transfers from %s to %s\n",
                dateOutput, book.getName(), state1, state2);
    }

    public static void message(String dateOutput, String from, String to) {
        System.out.printf("(Sequence) [%s] %s sends a message to %s\n",
                dateOutput, from, to);
    }
}
