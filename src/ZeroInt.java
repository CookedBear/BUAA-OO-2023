import java.math.BigInteger;
import java.util.HashSet;

public class ZeroInt implements Factor {
    private HashSet<Values> values;
    private BigInteger bigInt;

    public ZeroInt(BigInteger num) {
        this.values = new HashSet<Values>();
        BigInteger z = BigInteger.valueOf(0);
        Values value1 = new Values(z,z,z,num);
        //System.out.println(num);
        bigInt = num;
        values.add(value1);
    }

    public HashSet<Values> getValues() {
        return this.values;
    }

    public BigInteger getInt() {

        return this.bigInt;
    }
}
