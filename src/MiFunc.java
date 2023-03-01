import java.math.BigInteger;
import java.util.HashSet;

public class MiFunc implements Factor {
    private HashSet<Values> values;

    public MiFunc(String va, BigInteger pow, Boolean status) {
        char var = va.charAt(0);
        this.values = new HashSet<Values>();
        BigInteger z = BigInteger.valueOf(0);
        BigInteger big = BigInteger.valueOf(1);
        if (!status) {
            big = BigInteger.valueOf(0).subtract(big);
        }
        if (pow.equals(BigInteger.valueOf(0))) {

            Values value1 = new Values(z, z, z, big);
            values.add(value1);
        } else {
            switch (var) {
                case 'x':
                    //Values value1 = new Values(pow, z, z, big);
                    values.add(new Values(pow, z, z, big));
                    break;
                case 'y':
                    Values value2 = new Values(z, pow, z, big);
                    values.add(value2);
                    break;
                case 'z':
                    Values value3 = new Values(z, z, pow, big);
                    values.add(value3);
                    break;
                default:
            }
        }
    }

    public MiFunc(HashSet<Values> values) {
        this.values = values;
    }

    public HashSet<Values> getValues() {
        return this.values;
    }
}
