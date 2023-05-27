package service;

import instance.Book;
import tool.PrintAction;

public class Back {
    private static final String NAME = "logistics division";

    public static void repair(String dateOutput, Book book) {
        PrintAction.repaired(dateOutput, book, NAME);
    }
}
