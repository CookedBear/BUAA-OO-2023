public class OutputFormat {
    public static void errorPrint(double time, int errorType, String errorMessage) {
        System.out.printf("Error occurred at %.4f%n", time);
        System.out.printf("Error typeCode is %d: %s%n", errorType, errorMessage);
        System.exit(-1);
    }
}
