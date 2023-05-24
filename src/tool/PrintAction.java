package tool;

import instance.Book;
import instance.Student;

public class PrintAction {
    public static void queried(String dateOutput, Student student, Book book) {
        System.out.printf("[%s] %s queried %s from self-service machine\n",
                dateOutput, student.getName(), book.getName());
    }

    public static void rented(String dateOutput, Student student, Book book, String serviceName) {
        System.out.printf("[%s] %s borrowed %s from %s\n",
                dateOutput, student.getName(), book.getName(), serviceName);
    }
}
