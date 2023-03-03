import java.math.BigInteger;
import java.util.HashSet;
import java.util.TreeMap;

public class Term {
    private final HashSet<Factor> factors;
    private TreeMap<String, Values> values;

    public Term() {
        this.factors = new HashSet<>();
        this.values = new TreeMap<>();
    }

    public Term(TreeMap<String, Values> values) {
        this.values = values;
        this.factors = new HashSet<>();
    }

    public void addFactor(Factor factor) {
        //this.factors.add(factor);
        //this.values.addAll(factor.getValues());
        this.values = new Calculator().multiValue(this.values, factor.getValues());
    }

    public void addFactorInit(Factor factor, Boolean status) {
        //this.factors.add(factor);
        this.values = new Calculator().getClone(factor.getValues());

        if (!status) {
            for (Values v : values.values()) {
                //System.out.println(v.getConstValue());
                v.setConstValue(BigInteger.valueOf(0).subtract(v.getConstValue()));
            }
        }
    }

    public TreeMap<String, Values> getValues() { return this.values; }
}
