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

    public static void ordered(String dateOutput, Student student, Book book) {
        System.out.printf("[%s] %s ordered %s from ordering librarian\n",
                dateOutput, student.getName(), book.getName());
    }

    public static void returned(String dateOutput, Student student, Book book, String serviceName) {
        System.out.printf("[%s] %s returned %s to %s\n",
                dateOutput, student.getName(), book.getName(), serviceName);
    }

    public static void punished(String dateOutput, Student student, String serviceName) {
        System.out.printf("[%s] %s got punished by %s\n",
                dateOutput, student.getName(), serviceName);
    }

    public static void repaired(String dateOutput, Book book, String serviceName) {
        System.out.printf("[%s] %s got repaired by %s\n",
                dateOutput, book.getName(), serviceName);
    }
}
