import java.io.*;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner stop = new Scanner(System.in);
        double inputFinTime = 1;
        double processFinTime = 1;

        BufferedReader br = new BufferedReader(new FileReader(new File("stdin.txt")));
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
                inputFinTime = time;
                requestList.put(id, r);
            }
        }
        double ele = 0;
        double movingTime = 0;
        for (Request r : requestList.values()) {
            ele += r.calculateElc();
            movingTime += r.calculateTime();
        }
        BufferedReader br2 = new BufferedReader(new FileReader(new File("./javaOut" + args[0] + ".txt")));
        int line = 0;
        while ((ss = br2.readLine()) != null) {
            line++;
            String pp = "\\[(.+)\\](\\w+)-(\\d+)-(\\d+)(-(\\d+))?";
            Pattern pattern2 = Pattern.compile(pp);
            Matcher matcher2 = pattern2.matcher(ss);
            if (matcher2.find()) {
                double time = Double.parseDouble(matcher2.group(1));
                processFinTime = time;
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
                    System.out.println("-------------------------------------------------------\n\n");
                    System.out.println("jar " + args[0] + " Error occurred at Request " + line);
                    errorPrint("Error occurred at Line " + line, args[0]);
                    return;
                }
            }
        }
        for (int i = 0; i < 6; i++) {
            Elevator elevator = elevatorList.elevatorArrayList.get(i);
            if (elevator.openDoor) {
                System.out.println("-------------------------------------------------------\n\n");
                System.out.println("jar " + args[0] + " Elevator " + (i + 1) + " stopped without closing door!\n");
                errorPrint("Elevator " + (i + 1) + " stopped without closing door!", args[0]);
                return;
            }
            if (!elevator.requests.isEmpty()) {
                System.out.println("-------------------------------------------------------\n\n");
                System.out.println("jar " + args[0] + " Elevator " + (i + 1) + " stopped without finishing requests!\n");
                errorPrint("Elevator " + (i + 1) + " stopped without finishing requests!", args[0]);
                return;
            }
        }
        if (!requestList.isEmpty()) {
            System.out.println("-------------------------------------------------------\n\n");
            System.out.println("jar " + args[0] + " Elevator stopped without finishing!\n");
            errorPrint("Elevator stopped without finishing!", args[0]);
            return;
        }
        if (processFinTime - inputFinTime > 70.0) {
            System.out.println("-------------------------------------------------------\n\n");
            System.out.println("jar " + args[0] + " Have the risk of RTLE!\n");
            errorPrint("Have the risk of RTLE!", args[0]);
            return;
        }
        System.out.println("-------------------------------------------------------\n");
        System.out.println("jar " + args[0] + " Correct Check Success!\n");

        System.out.printf("Strict electricity: %.2f, your electricity:   %.2f.\n", ele, elevatorList.ele);
        System.out.printf("Single moving time: %-6.03f, your time after LAST input:    %-6.03f.\n", movingTime, processFinTime - inputFinTime);
        System.out.printf("Your average moving time: %.4f. You moved %d times in total.\n", elevatorList.moveUsedTime / elevatorList.moveTimes * 1000, elevatorList.moveTimes);
        System.out.printf("Your average door time:   %.4f.\n\n", elevatorList.doorUsedTime / elevatorList.doorTimes * 1000);
        //System.out.println("-------------------------------------------------------\n\n");
    }

    public static void errorPrint(String errorInformation, String i) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter("./errorOut"+i+".txt",true));
        out.write(errorInformation);
        out.write("\nInput:\n");
        BufferedReader br = new BufferedReader(new FileReader(new File("stdin.txt")));
        String s;
        while ((s = br.readLine()) != null) {
            out.write(s + "\n");
        }
        out.write("-------------------------------------------------------\n\n");
        out.close();
    }
}