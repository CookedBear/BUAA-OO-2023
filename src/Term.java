import java.math.BigInteger;
import java.util.HashSet;

public class Term {
    private final HashSet<Factor> factors;
    private HashSet<Values> values;

    public Term() {
        this.factors = new HashSet<Factor>();
        this.values = new HashSet<Values>();
    }

    public void addFactor(Factor factor) {
        this.factors.add(factor);
        //this.values.addAll(factor.getValues());
        HashSet<Values> newValues = new HashSet<Values>();
        for (Values v1 : values) {
            for (Values v2 : factor.getValues()) {
                newValues.add(multiValues(v1, v2));
            }
        }
        this.values = newValues;
    }

    public void addFactorInit(Factor factor, Boolean status) {
        this.factors.add(factor);
        this.values.addAll(factor.getValues());
        if (!status) {
            for (Values v : values) {
                //System.out.println(v.getConstValue());
                v.setConstValue(BigInteger.valueOf(0).subtract(v.getConstValue()));
            }
        }
    }

    private Values multiValues(Values value1, Values value2) {
        BigInteger constValue = value1.getConstValue().multiply(value2.getConstValue());
        BigInteger xpow = value1.getxPow().add(value2.getxPow());
        BigInteger ypow = value1.getyPow().add(value2.getyPow());
        BigInteger zpow = value1.getzPow().add(value2.getzPow());
        return new Values(xpow, ypow, zpow, constValue);
    }

    public HashSet<Values> getValues() {
        return this.values;
    }
}
