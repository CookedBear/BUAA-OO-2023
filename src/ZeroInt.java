import java.math.BigInteger;
import java.util.HashSet;

public class ZeroInt implements Factor {
    private HashSet<Values> values;

    public ZeroInt(BigInteger num) {
        this.values = new HashSet<Values>();
        BigInteger z = BigInteger.valueOf(0);
        Values value1 = new Values(z,z,z,num);
        //System.out.println(num);
        values.add(value1);
    }

    public HashSet<Values> getValues() {
        return this.values;
    }
}
