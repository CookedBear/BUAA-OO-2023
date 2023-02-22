import java.math.BigInteger;
import java.util.HashSet;

public class MiFunc implements Factor{
    private HashSet<Values> values;

    public MiFunc(String va, BigInteger pow) {
        char var = va.charAt(0);
        this.values = new HashSet<Values>();
        BigInteger z = BigInteger.valueOf(0);
        if (pow.equals(BigInteger.valueOf(0))) {
            Values value1 = new Values(z, z, z, BigInteger.valueOf(1));
            values.add(value1);
        } else {
            switch (var) {
                case 'x':
                    Values value1 = new Values(pow, z, z, BigInteger.valueOf(1));
                    values.add(value1);
                    break;
                case 'y':
                    Values value2 = new Values(z, pow, z, BigInteger.valueOf(1));
                    values.add(value2);
                    break;
                case 'z':
                    Values value3 = new Values(z, z, pow, BigInteger.valueOf(1));
                    values.add(value3);
                    break;
                default:
            }
        }
    }

    public HashSet<Values> getValues() {
        return this.values;
    }
}
