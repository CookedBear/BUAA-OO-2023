import java.math.BigInteger;
import java.util.TreeMap;

public class ZeroInt implements Factor {
    private TreeMap<String, Values> values;
    private BigInteger bigInt;

    public ZeroInt(BigInteger num) {
        this.values = new TreeMap<>();
        BigInteger z = BigInteger.valueOf(0);
        Values value1 = new Values(z,z,z,num);
        //System.out.println(num);
        bigInt = num;
        values.put(value1.hashString(), value1);
    }

    public ZeroInt(TreeMap<String, Values> values) {
        this.values = values;
    }

    public TreeMap<String, Values> getValues() {
        return this.values;
    }

    public BigInteger getInt() {

        return this.bigInt;
    }
}
