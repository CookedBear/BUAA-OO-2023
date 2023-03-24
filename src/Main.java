import java.io.*;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) throws IOException {

        BufferedReader br = new BufferedReader(new FileReader(new File("./src/stdin.txt")));
        String s, ss;
        HashMap<Integer, Request> requestList = new HashMap<>();
        ElevatorList elevatorList = new ElevatorList();
        while ((s = br.readLine()) != null) {
            String p = "\\[(.+)\\](\\d+)-FROM-(\\d+)-TO-(\\d+)";
            Pattern pattern = Pattern.compile(p);
            Matcher matcher = pattern.matcher(s);
            if (matcher.find()) {
                double time = Double.parseDouble(matcher.group(1));
                int id = Integer.parseInt(matcher.group(2));
                int from = Integer.parseInt(matcher.group(3));
                int to = Integer.parseInt(matcher.group(4));
                Request r = new Request(from, to, id, time);
                requestList.put(id, r);
            }
        }
        double ele = 0;
        double movingTime = 0;
        for (Request r : requestList.values()) {
            ele += r.calculateElc();
            movingTime += r.calculateTime();
        }
        BufferedReader br2 = new BufferedReader(new FileReader(new File("./src/javaOut.txt")));
        // int line = 0;
        while ((ss = br2.readLine()) != null) {
            // System.out.println(++line);
            String pp = "\\[(.+)\\](\\w+)-(\\d+)-(\\d+)(-(\\d+))?";
            Pattern pattern2 = Pattern.compile(pp);
            Matcher matcher2 = pattern2.matcher(ss);
            if (matcher2.find()) {
                double time = Double.parseDouble(matcher2.group(1));
                String operation = matcher2.group(2);
                boolean warn = false;
                if (operation.equals("IN") || operation.equals("OUT")) {    // request
                    int requestId = Integer.parseInt(matcher2.group(3));
                    int floor = Integer.parseInt(matcher2.group(4));
                    int elevatorId = Integer.parseInt(matcher2.group(6));
                    int action = (operation.equals("IN")) ? 1 :             // IN = 1
                                 2;                                         // OUT = 2
                    warn = elevatorList.requestAction(action, elevatorId, floor, time, requestId, requestList);
                } else {                                                    // elevator
                    int floor = Integer.parseInt(matcher2.group(3));
                    int elevatorId = Integer.parseInt(matcher2.group(4));
                    int action = (operation.equals("ARRIVE")) ? 3 :         // ARRIVE = 3
                                 (operation.equals("OPEN"))   ? 4 :         // OPEN = 4
                                 5;                                         // CLOSE = 5
                    warn = elevatorList.elevatorAction(action, elevatorId, floor, time);
                }
                if (warn) {
                    return;
                }
            }
        }
        for (int i = 0; i < 6; i++) {
            Elevator elevator = elevatorList.elevatorArrayList.get(i);
            if (elevator.openDoor) {
                System.out.println("Elevator " + (i + 1) + " stopped without closing door!");
                return;
            }
            if (!elevator.requests.isEmpty()) {
                System.out.println("Elevator " + (i + 1) + " stopped without finishing requests!");
                return;
            }
        }
        if (!requestList.isEmpty()) {
            System.out.println("Elevator stopped without finishing!");
            return;
        }

        System.out.println("Success!");

        System.out.printf("Strict electricity: %.2f, strict moving time: %.03f\n", ele, movingTime);
        System.out.printf("Your electricity:   %.2f, your total time:    %.03f", elevatorList.ele, elevatorList.time);
    }
}