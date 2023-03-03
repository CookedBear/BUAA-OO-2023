import java.math.BigInteger;
import java.util.TreeMap;

public class MiFunc implements Factor {
    private TreeMap<String, Values> values;

    public MiFunc(String va, BigInteger pow, Boolean status) {
        char var = va.charAt(0);
        this.values = new TreeMap<>();
        BigInteger z = BigInteger.valueOf(0);
        BigInteger big = BigInteger.valueOf(1);
        if (!status) {
            big = BigInteger.valueOf(0).subtract(big);
        }
        if (pow.equals(BigInteger.valueOf(0))) {

            Values value1 = new Values(z, z, z, big);
            values.put("0,0,0,", value1);
        } else {
            switch (var) {
                case 'x':
                    //Values value1 = new Values(pow, z, z, big);
                    values.put(pow + ",0,0,", new Values(pow, z, z, big));
                    break;
                case 'y':
                    Values value2 = new Values(z, pow, z, big);
                    values.put("0," + pow + ",0,", value2);
                    break;
                case 'z':
                    Values value3 = new Values(z, z, pow, big);
                    values.put("0,0," + pow + ",", value3);
                    break;
                default:
            }
        }
    }

    public MiFunc(TreeMap<String, Values> values) {
        this.values = values;
    }

    public TreeMap<String, Values> getValues() {
        return this.values;
    }
}
