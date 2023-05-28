import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    static BufferedReader br = null;
    static BufferedReader br2 = null;
    static HashMap<Integer, Elevator> elevatorMap = new HashMap<>();
    static HashMap<Integer, Request> requestMap = new HashMap<>();
    static HashMap<Integer, Floor> floorMap = new HashMap<>();
    static double timeStamp = -1;

    public static void main(String[] args) throws IOException {
        System.out.println("Hello world!");

        setBuffer(args);
        initElevator();
        initFloor();

        readStdin();
        readStdout();

        checkFloor();
        checkRequest();
        checkElevator();

        System.out.println("Success");
    }

    public static void initElevator() {
        for (int i = 1; i <= 6; i++) {
            elevatorMap.put(i, new Elevator(0, i, 6, 0.4, 2047, 1));
        }
    }

    public static void initFloor() {
        for (int i = 1; i <= 11; i++) {
            floorMap.put(i, new Floor(i));
        }
    }

    public static void setBuffer(String[] args) throws FileNotFoundException {
        if (args.length > 1) {
            switch (args[0]) {
                case "-single":
                    // java -jar test2-3.jar -single inputName.txt
                    // 同一jar多输入文件测试
                    br = new BufferedReader(new FileReader("./single/" + args[1] + "/stdin.txt"));
                    br2 = new BufferedReader(new FileReader("./single/" + args[1] + "/javaOut.txt"));
                    break;
                case "-normal":
                    // java -jar test2-3.jar -normal 1
                    // 同目录下stdin与javaOut1
                    br = new BufferedReader(new FileReader("./stdin.txt"));
                    br2 = new BufferedReader(new FileReader("javaOut" + args[1] + ".txt"));
                    break;
                case "-direct":
                    // java -jar test2-3.jar -direct stdin.txt stdout.txt
                    br = new BufferedReader(new FileReader(args[1]));
                    br2 = new BufferedReader(new FileReader(args[2]));
                    break;
            }
        } else if (args.length == 1) {

            br = new BufferedReader(new FileReader("./stdin.txt"));
            br2 = new BufferedReader(new FileReader("javaOut" + args[0] + ".txt"));
            System.out.println("javaOut" + args[0] + ".txt");
        } else {
            // java -jar test2-3.jar
            // 同目录下stdin与javaOut
            br = new BufferedReader(new FileReader("./stdin.txt"));
            br2 = new BufferedReader(new FileReader("./javaOut.txt"));
        }
    }

    public static void checkFloor() {
        for (Floor floor : floorMap.values()) {
            int loading = 0;
            for (int j = 0; j < floor.visitedRecord.size(); j++) {
                if (floor.visitedRecord.get(j).type == 2) {
                    loading++;
                } else if (floor.visitedRecord.get(j).type == -2) {
                    loading--;
                }
                if (loading > 2) {
                    OutputFormat.errorPrint(floor.visitedRecord.get(j).time, 23, "floor overcrowd by loading");
                }
            }
        }
    }

    public static void checkElevator() {
        for (Elevator elevator : elevatorMap.values()) {
            if (!elevator.requestList.isEmpty()) {
                for (int r : elevator.requestList.keySet()) {
                    System.out.print(r + " ");
                }
                System.out.print("\n");
                OutputFormat.errorPrint(0, 24, "request left in elevator");
            } else if (elevator.open) {
                OutputFormat.errorPrint(0, 22, "elevator stopped without closing door");
            }
        }
    }

    public static void checkRequest() {
        int f = 0;
        for (Request request : requestMap.values()) {
            if (request.from != request.to) {
                f = 1;
                System.out.println(request.requestId);
            }
        }
        if (f == 1) {
            OutputFormat.errorPrint(-1, 25, "request left");
        }
    }

    public static void readStdin() throws IOException {
        String s;
        while ((s = br.readLine()) != null) {
            String pattern;
            Pattern p;
            Matcher m;
            if (s.contains("MAINTAIN")) {
                pattern = "\\[(.+)]MAINTAIN-Elevator-(\\d+)";
                p = Pattern.compile(pattern);
                m = p.matcher(s);
                if (m.find()) {
                    double time = Double.parseDouble(m.group(1));
                    int elevatorId = Integer.parseInt(m.group(2));
                    elevatorMap.get(elevatorId).maintainTime = time;
                }
            } else if (s.contains("ADD")) {
                pattern = "\\[(.+)]ADD-Elevator-(\\d+)-(\\d+)-(\\d+)-(.+)-(\\d+)";
                p = Pattern.compile(pattern);
                m = p.matcher(s);
                if (m.find()) {
                    double time = Double.parseDouble(m.group(1));
                    int elevatorId = Integer.parseInt(m.group(2));
                    int from = Integer.parseInt(m.group(3));
                    int capacity = Integer.parseInt(m.group(4));
                    double mvTime = Double.parseDouble(m.group(5));
                    int arrival = Integer.parseInt(m.group(6));
                    Elevator elevator = new Elevator(time, elevatorId, capacity, mvTime, arrival, from);
                    elevatorMap.put(elevatorId, elevator);
                }
            } else {
                pattern = "\\[(.+)](\\d+)-FROM-(\\d+)-TO-(\\d+)";
                p = Pattern.compile(pattern);
                m = p.matcher(s);
                if (m.find()) {
                    double time = Double.parseDouble(m.group(1));
                    int id = Integer.parseInt(m.group(2));
                    int from = Integer.parseInt(m.group(3));
                    int to = Integer.parseInt(m.group(4));
                    Request request = new Request(id, from, to, time);
                    requestMap.put(id, request);
                }
            }
        }
    }

    public static void readStdout() throws IOException {
        String s;
        while ((s = br2.readLine()) != null) {
            String pattern;
            Pattern p;
            Matcher m;
            if (s.contains("MAINTAIN_ACCEPT")) {
                pattern = "\\[(.+)]MAINTAIN_ACCEPT-(\\d+)";
                p = Pattern.compile(pattern);
                m = p.matcher(s);
                if (m.find()) {
                    double time = Double.parseDouble(m.group(1));
                    int elevatorId = Integer.parseInt(m.group(2));
                    checkTimeStamp(time);
                    elevatorMap.get(elevatorId).readyMaintain(time);
                }
            } else if (s.contains("MAINTAIN_ABLE")) {
                pattern = "\\[(.+)]MAINTAIN_ABLE-(\\d+)";
                p = Pattern.compile(pattern);
                m = p.matcher(s);
                if (m.find()) {
                    double time = Double.parseDouble(m.group(1));
                    int elevatorId = Integer.parseInt(m.group(2));
                    checkTimeStamp(time);
                    elevatorMap.get(elevatorId).finishMaintain(time);
                    elevatorMap.remove(elevatorId);
                }
            } else if (s.contains("ARRIVE")) {
                pattern = "\\[(.+)]ARRIVE-(\\d+)-(\\d+)";
                p = Pattern.compile(pattern);
                m = p.matcher(s);
                if (m.find()) {
                    double time = Double.parseDouble(m.group(1));
                    int elevatorId = Integer.parseInt(m.group(3));
                    int floor = Integer.parseInt(m.group(2));
                    checkTimeStamp(time);
                    elevatorMap.get(elevatorId).move(time, floor);
                }
            } else if (s.contains("OPEN")) {
                pattern = "\\[(.+)]OPEN-(\\d+)-(\\d+)";
                p = Pattern.compile(pattern);
                m = p.matcher(s);
                if (m.find()) {
                    double time = Double.parseDouble(m.group(1));
                    int elevatorId = Integer.parseInt(m.group(3));
                    int floor = Integer.parseInt(m.group(2));
                    checkTimeStamp(time);
                    elevatorMap.get(elevatorId).openDoor(time, true, floor, floorMap);
                }
            } else if (s.contains("CLOSE")) {
                pattern = "\\[(.+)]CLOSE-(\\d+)-(\\d+)";
                p = Pattern.compile(pattern);
                m = p.matcher(s);
                if (m.find()) {
                    double time = Double.parseDouble(m.group(1));
                    int elevatorId = Integer.parseInt(m.group(3));
                    int floor = Integer.parseInt(m.group(2));
                    checkTimeStamp(time);
                    elevatorMap.get(elevatorId).openDoor(time, false, floor, floorMap);
                }
            } else if (s.contains("IN")) {
                pattern = "\\[(.+)]IN-(\\d+)-(\\d+)-(\\d+)";
                p = Pattern.compile(pattern);
                m = p.matcher(s);
                if (m.find()) {
                    double time = Double.parseDouble(m.group(1));
                    int requestId = Integer.parseInt(m.group(2));
                    int floor = Integer.parseInt(m.group(3));
                    int elevatorId = Integer.parseInt(m.group(4));
                    checkTimeStamp(time);
                    elevatorMap.get(elevatorId).addRequest(time, requestMap, requestId, floor);

                }
            } else {
                pattern = "\\[(.+)]OUT-(\\d+)-(\\d+)-(\\d+)";
                p = Pattern.compile(pattern);
                m = p.matcher(s);
                if (m.find()) {
                    double time = Double.parseDouble(m.group(1));
                    int requestId = Integer.parseInt(m.group(2));
                    int floor = Integer.parseInt(m.group(3));
                    int elevatorId = Integer.parseInt(m.group(4));
                    checkTimeStamp(time);
                    elevatorMap.get(elevatorId).minusRequest(time, requestMap, requestId, floor);
                }
            }
        }
    }

    public static void checkTimeStamp(double time) {
        if (timeStamp > time) {
            OutputFormat.errorPrint(time, 27, "timeStamp failed");
        } else {
            timeStamp = time;
        }
    }
}
