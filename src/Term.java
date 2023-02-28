import java.math.BigInteger;
import java.util.HashSet;

public class Term {
    private final HashSet<Factor> factors;
    private HashSet<Values> values;

    public Term() {
        this.factors = new HashSet<Factor>();
        this.values = new HashSet<Values>();
    }

    public Term(HashSet<Values> values) {
        this.values = values;
        this.factors = new HashSet<>();
    }

    public void addFactor(Factor factor) {
        this.factors.add(factor);
        //this.values.addAll(factor.getValues());
        this.values = new calculator().multiValue(this.values, factor.getValues());
    }

    public void addFactorInit(Factor factor, Boolean status) {
        this.factors.add(factor);
        this.values = new calculator().getClone(factor.getValues());

        if (!status) {
            for (Values v : values) {
                //System.out.println(v.getConstValue());
                v.setConstValue(BigInteger.valueOf(0).subtract(v.getConstValue()));
            }
        }
    }

    public HashSet<Values> getValues() { return this.values; }
}
