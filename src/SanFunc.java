import java.math.BigInteger;
import java.util.HashSet;

public class SanFunc implements Factor{ //保留Factor元素做类型判断
    private Boolean sin;
    private HashSet<Values> exprValues;
    private BigInteger power;

    public SanFunc(String type, Factor factor, ZeroInt zeroInt) {
        this.sin = type.equals("sin");
        this.exprValues = new calculator().getClone(factor.getValues());
        this.power = zeroInt.getInt();
    }

    public HashSet<Values> getValues() {
        HashSet<Values> values = new HashSet<>();
        //HashSet<Values> tempExprValues = new calculator().getClone(exprValues);
        HashSet<Values> zero = new HashSet<>();
        zero.add(new Values(new ZeroInt(BigInteger.ZERO)));
        if (!sin && new calculator().addValue(exprValues, zero, false).isEmpty()) {
            values.add(new Values(new ZeroInt(BigInteger.ONE)));
        } else if (sin && new calculator().addValue(exprValues, zero, true).isEmpty()) {
            values.add(new Values(new ZeroInt(BigInteger.ZERO)));
        } else {
            values.add(new Values(this));
        }
        return values;
    }

    public Boolean getSin() { return this.sin; }

    public HashSet<Values> getExprValues() { return this.exprValues; }

    public BigInteger getPower() { return this.power; }

    public void setPower(BigInteger power) { this.power = power; }
}
