package mine.exceptions;

import java.util.HashMap;

public class ExceptionCounter {
    private static final int CAUSES_TYPES = 9;
    private static final String[] CAUSES_NAMES = {"epi", "er", "pinf", "rnf", "anf", "emi", "egi", "ginf", "minf"};

    private static final HashMap<String, HashMap<Integer, Integer>> CAUSES = new HashMap<>();
    private static final HashMap<String, Integer> COUNTS = new HashMap<>();

    public static void initCauses() {
        for (int i = 0; i < CAUSES_TYPES; i++) {
            initCause(CAUSES_NAMES[i]);
        }
    }

    private static void initCause(String type) {
        CAUSES.put(type, new HashMap<>());
        COUNTS.put(type, 0);
    }

    public static void addCause(String type, int causeId) {
        HashMap<Integer, Integer> cause = CAUSES.get(type);
        if (cause.containsKey(causeId)) {
            cause.put(causeId, cause.get(causeId) + 1);
        } else {
            cause.put(causeId, 1);
        }
    }

    public static void addCount(String type) {
        COUNTS.put(type, COUNTS.get(type) + 1);
    }

    public static int getCount(String type) {
        return COUNTS.get(type);
    }

    public static int getCause(String type, int id) {
        return CAUSES.get(type).get(id);
    }
}
