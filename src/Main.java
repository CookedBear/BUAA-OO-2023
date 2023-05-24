import jdk.nashorn.internal.parser.Lexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static final HashMap<Book, Integer> bookPool = new HashMap<>();
    private static final HashMap<String, Student> studentPool = new HashMap<>();
    private static final ArrayList<Request> requestList = new ArrayList<>();
    private static final HashMap<Book, Integer> rentFailPool = new HashMap<>();
    public static void main(String[] args) {
        System.out.println("Hello world!");

        Scanner scanner = new Scanner(System.in);
        initBooks(scanner);
        initRequests(scanner);


    }

    private static void initBooks(Scanner scanner) {
        int people = scanner.nextInt();
        String patternString = "([\\w-]+) (\\d+)";
        Pattern pattern = Pattern.compile(patternString);
        String requestLine;
        for (int i = 0; i <= people; i++) {
            requestLine = scanner.nextLine();
            Matcher matcher = pattern.matcher(requestLine);
            if (matcher.find()) {
                String bookName = matcher.group(1);
                int count = Integer.parseInt(matcher.group(2));
                // System.out.println("store book " + bookName + " has " + count);
                bookPool.put(new Book(bookName), count);
            }
        }
    }

    private static void initRequests(Scanner scanner) {
        String patternString = "\\[([\\w-]+)] (\\d+) (\\w+) ([\\w-]+)";
        Pattern pattern = Pattern.compile(patternString);
        int requests = scanner.nextInt();
        String requestLine;
        for (int i = 0; i <= requests; i++) {
            requestLine = scanner.nextLine();
            Matcher matcher = pattern.matcher(requestLine);
            if (matcher.find()) {
                String date = matcher.group(1);
                String student = matcher.group(2);
                String action = matcher.group(3);
                String book = matcher.group(4);
                // System.out.println(date+student+action+book);
                requestList.add(new Request(date, student, action, book));
            }
        }
    }
}

