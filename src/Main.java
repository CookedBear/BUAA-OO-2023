import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) throws IOException {

        for (int i = 1; i <= 66; i++) {
            System.out.printf("[49.4]%d-FROM-11-TO-1\n", i);
        }

        Scanner stop = new Scanner(System.in);
        double inputFinTime = 1;
        double processFinTime = 1;
        ArrayList<Elevator> elevatorAddList = new ArrayList<>();
        ArrayList<Double> elevatorMainTime = new ArrayList<>();
        ArrayList<Integer> elevatorMainId = new ArrayList<>();
        BufferedReader br = null;
        BufferedReader br2 = null;
        int turns = 0;
        if (args.length > 1) {
            if (args[0].equals("-single")) {
                br = new BufferedReader(new FileReader("./single/" + args[1] + "/stdin.txt"));
                br2 = new BufferedReader(new FileReader("./single/" + args[1] + "/javaOut.txt"));
            } else if (args[0].equals("-normal")) {
                turns = Integer.parseInt(args[1]);
            } else if (args[0].equals("-direct")) {
                br = new BufferedReader(new FileReader(args[1]));
                br2 = new BufferedReader(new FileReader(args[2]));
            }
        } else if (args.length == 1) {
            br = new BufferedReader(new FileReader("./stdin.txt"));
            br2 = new BufferedReader(new FileReader("javaOut"+args[0]+".txt"));
            System.out.println("javaOut"+args[0]+".txt");
        } else {
            br = new BufferedReader(new FileReader("./stdin.txt"));
            br2 = new BufferedReader(new FileReader("./javaOut.txt"));
        }
        String s, ss;
        HashMap<Integer, Request> requestList = new HashMap<>();
        ElevatorList elevatorList = new ElevatorList();
        while ((s = br.readLine()) != null) {
            String p;
            if (s.contains("MAINTAIN")) {

                p = "\\[(.+)]MAINTAIN-Elevator-(\\d+)";
                Pattern pattern = Pattern.compile(p);
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    double time = Double.parseDouble(matcher.group(1));
                    int elevatorId = Integer.parseInt(matcher.group(2));
                    inputFinTime = time;
                    elevatorMainTime.add(time);
                    elevatorMainId.add(elevatorId);
                    //System.out.println("maintain"+elevatorId);
                }
            } else if (s.contains("ADD")) {
                //System.out.println("found");
                p = "\\[(.+)]ADD-Elevator-(\\d+)-(\\d+)-(\\d+)-(.+)";
                Pattern pattern = Pattern.compile(p);
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    //System.out.println("found");
                    double time = Double.parseDouble(matcher.group(1));
                    int elevatorId = Integer.parseInt(matcher.group(2));
                    int initFloor = Integer.parseInt(matcher.group(3));
                    int capacity = Integer.parseInt(matcher.group(4));
                    double movingTime = Double.parseDouble(matcher.group(5));
                    Elevator e = new Elevator(elevatorId, initFloor, capacity, (int) (movingTime * 100));
                    e.addTime = time;
                    inputFinTime = time;
                    elevatorAddList.add(e);
                }
            } else {
                p = "\\[(.+)](\\d+)-FROM-(\\d+)-TO-(\\d+)";
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
        }
        double ele = 0;
        double movingTime = 0;
        for (Request r : requestList.values()) {
            ele += r.calculateElc();
            movingTime += r.calculateTime();
        }

        elevatorList.elevatorArrayList.addAll(elevatorAddList);
        //System.out.println(elevatorAddList);

        int line = 0;
        int maintainCnt = -1;
        while ((ss = br2.readLine()) != null) {
            line++;
            boolean warn = false;
            if (ss.contains("ACCEPT")) {
                String pp = "\\[(.+)]MAINTAIN_ACCEPT-(\\d+)";
                Pattern pattern2 = Pattern.compile(pp);
                Matcher matcher2 = pattern2.matcher(ss);
                if (matcher2.find()) {
                    double time = Double.parseDouble(matcher2.group(1));
                    int elevatorId = Integer.parseInt(matcher2.group(2));
                    Elevator elevator = null;
                    for (Elevator e : elevatorList.elevatorArrayList) {
                        if (e.elevatorId == elevatorId) {
                            elevator = e;
                        }
                    }
                    elevator.maintainCnt = 3;
                    elevatorList.acceptedtimes++;
                }
            } else if (ss.contains("ABLE")) {
                String pp = "\\[(.+)]MAINTAIN_ABLE-(\\d+)";
                Pattern pattern2 = Pattern.compile(pp);
                Matcher matcher2 = pattern2.matcher(ss);
                if (matcher2.find()) {
                    double time = Double.parseDouble(matcher2.group(1));
                    int elevatorId = Integer.parseInt(matcher2.group(2));
                    elevatorList.checkMaintain(elevatorId);
                    elevatorList.abletimes++;
                }
            } else {
                String pp = "\\[(.+)](\\w+)-(\\d+)-(\\d+)(-(\\d+))?";
                Pattern pattern2 = Pattern.compile(pp);
                Matcher matcher2 = pattern2.matcher(ss);
                if (matcher2.find()) {
                    double time = Double.parseDouble(matcher2.group(1));
                    processFinTime = time;
                    String operation = matcher2.group(2);

                    if (operation.equals("IN") || operation.equals("OUT")) {    // request
                        int requestId = Integer.parseInt(matcher2.group(3));
                        int floor = Integer.parseInt(matcher2.group(4));
                        int elevatorId = Integer.parseInt(matcher2.group(6));
                        int action = (operation.equals("IN")) ? 1 :             // IN = 1
                                2;                                              // OUT = 2
                        warn = elevatorList.requestAction(action, elevatorId, floor, time, requestId, requestList);
                    } else {                                                    // elevator
                        int floor = Integer.parseInt(matcher2.group(3));
                        int elevatorId = Integer.parseInt(matcher2.group(4));
                        int action = (operation.equals("ARRIVE")) ? 3 :          // ARRIVE = 3
                                     (operation.equals("OPEN")) ? 4 :            // OPEN = 4
                                     5;                                          // CLOSE = 5
                        warn = elevatorList.elevatorAction(action, elevatorId, floor, time);
                    }
                }
            }
            if (warn) {
                System.out.println("-------------------------------------------------------\n\n");
                System.out.println("jar Error occurred at Request " + line);
                //errorPrint("Error occurred at Line " + line, args[0]);
                return;
            }
        }
        for (int i = 0; i < elevatorList.elevatorArrayList.size(); i++) {
            Elevator elevator = elevatorList.elevatorArrayList.get(i);
            if (elevator.openDoor) {
                System.out.println("-------------------------------------------------------\n\n");
                System.out.println("jar Elevator " + (i + 1) + " stopped without closing door!\n");
                //errorPrint("Elevator " + (i + 1) + " stopped without closing door!", args[0]);
                return;
            }
            if (!elevator.requests.isEmpty()) {
                System.out.println("-------------------------------------------------------\n\n");
                System.out.println("jar Elevator " + (i + 1) + " stopped without finishing requests!\n");
                //errorPrint("Elevator " + (i + 1) + " stopped without finishing requests!", args[0]);
                return;
            }
        }
        if (!requestList.isEmpty()) {
            System.out.println("-------------------------------------------------------\n\n");
            System.out.println("jar Elevator stopped without finishing:");
            for (Integer rid : requestList.keySet()) {
                System.out.println("Request ID: "+rid);
            }
            //errorPrint("Elevator stopped without finishing!", args[0]);
            return;
        }
        if (processFinTime - inputFinTime > 170.0) {
            System.out.println("-------------------------------------------------------\n\n");
            System.out.println("jar Have the risk of RTLE!\n");
            //errorPrint("Have the risk of RTLE!", args[0]);
            return;
        }
        if (elevatorList.abletimes != elevatorList.acceptedtimes) {
            System.out.println("Has elevator accepted but not able!");
            return;
        }
        System.out.println("-------------------------------------------------------\n");
        System.out.println("jar Correct Check Success!\n");

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