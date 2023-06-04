package tool;

import instance.Book;
import instance.Student;

public class PrintAction {
    public static void queried(int date, Student student, Book book) {
        System.out.printf("[%s] %s-%s queried %s from self-service machine\n" +
                          "[%s] self-service machine provided information of %s\n",
                        DateCal.getDateOutput(date), student.getSchool(),
                        student.getName(), book.getName(),
                        DateCal.getDateOutput(date), book.getName());
    }

    public static void failed(String dateOutput, Student student, Book book, String serviceName) {
        System.out.printf("[%s] %s refused lending %s-%s to %s-%s\n",
                dateOutput, serviceName, book.getSchool(), book.getName(), student.getSchool(),
                student.getName());
    }

    public static void rented(int date, Student student, Book book, String serviceName) {
        System.out.printf("[%s] %s lent %s-%s to %s-%s\n",
                DateCal.getDateOutput(date), serviceName, book.getSchool(), book.getName(),
                student.getSchool(), student.getName());
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
    }

    public static void returned(String dateOutput, Student student, Book book, String serviceName) {
        System.out.printf("[%s] %s-%s returned %s-%s to %s\n" +
                          "[%s] %s collected %s-%s from %s-%s\n",
                dateOutput, student.getSchool(), student.getName(),
                book.getSchool(), book.getName(), serviceName,
                dateOutput, serviceName, book.getSchool(), book.getName(),
                student.getSchool(), student.getName());
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
    }

    public static void bought(String dateOutput, Book book, String serviceName) {
        System.out.printf("[%s] %s-%s got purchased by %s in %s\n",
                dateOutput, book.getSchool(), book.getName(), serviceName, book.getSchool());
    }

    public static void transIn(String dateOutput, Book book, String serviceName, String school) {
        System.out.printf("[%s] %s-%s got received by %s in %s\n",
                dateOutput, book.getSchool(), book.getName(), serviceName, school);
    }

    public static void transOut(String dateOutput, Book book, String serviceName, String school) {
        System.out.printf("[%s] %s-%s got transported by %s in %s\n",
                dateOutput, book.getSchool(), book.getName(), serviceName, school);
    }

    public static void stateTrans(String dateOutput, Book book, String state1, String state2) {
        System.out.printf("(State) [%s] %s transfers from %s to %s\n",
                dateOutput, book.getName(), state1, state2);
    }
}
