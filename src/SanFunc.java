import java.math.BigInteger;
import java.util.TreeMap;

public class SanFunc implements Factor { //保留Factor元素做类型判断
    private Boolean sin;
    private TreeMap<String, Values> exprValues;
    private BigInteger power;

    public SanFunc(String type, Factor factor, ZeroInt zeroInt) {
        if (!zeroInt.getInt().equals(BigInteger.ZERO)) {
            this.sin = type.equals("sin");
            this.exprValues = new Calculator().getClone(factor.getValues());
            this.power = zeroInt.getInt();
        } else {
            this.sin = true;
            this.exprValues = new TreeMap<>();
            this.power = BigInteger.ZERO;
            exprValues.put("0,0,0,", new Values(new ZeroInt(BigInteger.ZERO)));
        }
    }

    public String hashString() {
        StringBuilder sb = new StringBuilder();
        sb.append(sin ? "s" : "c");
        sb.append(power);
        sb.append("(");
        sb.append(new Expr(exprValues).hashString());
        sb.append(")");
        return sb.toString();
    }

    public TreeMap<String, Values> getValues() {    //封装为Values的Map返回
        TreeMap<String, Values> values = new TreeMap<>();
        //HashSet<Values> tempExprValues = new calculator().getClone(exprValues);
        TreeMap<String, Values> zero = new TreeMap<>();
        zero.put("0,0,0,", new Values(new ZeroInt(BigInteger.ZERO)));
        if (!sin && !power.equals(BigInteger.ZERO) && (exprValues.containsKey("0,0,0,")
                && exprValues.get("0,0,0,").getConstValue().equals(BigInteger.ZERO)
                && exprValues.size() == 1)) {
            values.put("0,0,0,", new Values(new ZeroInt(BigInteger.ONE)));
        } else if (sin && !power.equals(BigInteger.ZERO) && (exprValues.containsKey("0,0,0,")
                && exprValues.get("0,0,0,").getConstValue().equals(BigInteger.ZERO)
                && exprValues.size() == 1)) {
            values.put("0,0,0,", new Values(new ZeroInt(BigInteger.ZERO)));
        } else {

            StringBuilder sb = new StringBuilder();
            sb.append("0,0,0,");
            sb.append(sin ? "s" : "c");
            sb.append(power);
            StringBuilder esb = new StringBuilder();
            for (String ss : exprValues.keySet()) {
                esb.append(ss);
            }
            sb.append("(");
            sb.append(esb);
            sb.append("),");
            Values v = new Values(this);
            values.put(sb.toString(), v);   //存储好s1(X+Y)的形式在三角函数的
        }
        return values;
    }

    public Boolean getSin() { return this.sin; }

    public TreeMap<String, Values> getExprValues() { return this.exprValues; }

    public BigInteger getPower() { return this.power; }

    public void setSin(Boolean sin) { this.sin = sin; }

    public void setPower(BigInteger power) { this.power = power; }
}
