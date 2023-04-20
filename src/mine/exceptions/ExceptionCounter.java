package mine.exceptions;

import java.util.HashMap;

public class ExceptionCounter {
    private static final int CAUSES_TYPES = 4;
    private static int pinfCount = 0;
    private static int epiCount = 0;
    private static int rnfCount = 0;
    private static int erCount = 0;

    private static HashMap<String, HashMap<Integer, Integer>> causes = new HashMap<>();
    private static HashMap<String, Integer> counts = new HashMap<>();

    public static void initCauses() {
        initCause("pinf");
        initCause("epi");
        initCause("rnf");
        initCause("er");
    }

    private static void initCause(String type) {
        causes.put(type, new HashMap<>());
        counts.put(type, 0);
    }

    public static void adjustCause(String type, int causeId) {
        HashMap<Integer, Integer> cause = causes.get(type);
        if (cause.containsKey(causeId)) {
            cause.put(causeId, cause.get(causeId) + 1);
        } else {
            cause.put(causeId, 1);
        }
    }

    public static void adjustCount(String type) {
        counts.put(type, counts.get(type) + 1);
    }

    public static int getCount(String type) {
        return counts.get(type);
    }

    public static int getCause(String type, int id) {
        return causes.get(type).get(id);
    }
}
