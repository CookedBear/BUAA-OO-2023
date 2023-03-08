import java.math.BigInteger;
import java.util.TreeMap;

public class SanFunc implements Factor { //保留Factor元素做类型判断
    private Boolean sin;
    private TreeMap<String, Values> exprValues;
    private BigInteger power;

    public SanFunc(String type, Factor factor, ZeroInt zeroInt) {
        exprValues = new TreeMap<>();
        if (!zeroInt.getInt().equals(BigInteger.ZERO)) {
            this.sin = type.equals("sin");
            if (factor.getValues().isEmpty()) {
                Values nv = new Values(new ZeroInt(BigInteger.ZERO));
                exprValues.put(nv.hashString(), nv);
            } else {
                this.exprValues = new Calculator().getClone(factor.getValues());
            }
            this.power = zeroInt.getInt();
        } else {
            this.sin = type.equals("sin");
            this.power = BigInteger.ZERO;
            Values nv = new Values(new ZeroInt(BigInteger.ZERO));
            exprValues.put(nv.hashString(), nv);
        }
    }

    public void getDouble(Boolean sin) {
        TreeMap<String, Values> newValues = new TreeMap<>();
        for (Values v : this.exprValues.values()) {
            Values vv = new Calculator().getClone(v);
            vv.setConstValue(vv.getConstValue().multiply(BigInteger.valueOf(2)));
            newValues.put(vv.hashString(), vv);
        }
        this.exprValues = newValues;

        if (sin) {
            this.sin = true;
        } else {
            this.sin = false;
            this.power = this.power.divide(BigInteger.valueOf(2));
        }
    }

    public String hashString() {
        StringBuilder sb = new StringBuilder();
        sb.append(sin ? "s" : "c");
        sb.append(power);
        sb.append("(");
        sb.append(new Expr(exprValues).hashStringInSan());
        sb.append(")");
        return sb.toString();
    }

    public String hashStringInValues() {
        StringBuilder sb = new StringBuilder();
        sb.append(sin ? "s" : "c");
        //sb.append(power);
        sb.append("(");
        sb.append(new Expr(exprValues).hashStringInSan());
        sb.append(")");
        return sb.toString();
    }

    public TreeMap<String, Values> getValues() {    //封装为Values的Map返回
        TreeMap<String, Values> values = new TreeMap<>();
        //HashSet<Values> tempExprValues = new calculator().getClone(exprValues);
        TreeMap<String, Values> zero = new TreeMap<>();
        zero.put("0,0,0,", new Values(new ZeroInt(BigInteger.ZERO)));

        Boolean flag = false;
        for (String s : exprValues.keySet()) {
            flag = exprValues.get(s).getConstValue().equals(BigInteger.ZERO);
            break;
        }

        if (!sin && exprValues.size() <= 1 && !power.equals(BigInteger.ZERO) && flag) {
            Values nv = new Values(new ZeroInt(BigInteger.ONE));
            values.put(nv.hashString(), nv);
        } else if (sin && exprValues.size() <= 1 && !power.equals(BigInteger.ZERO) && flag) {
            Values nv = new Values(new ZeroInt(BigInteger.ZERO));
            values.put(nv.hashString(), nv);
        } else if (power.equals(BigInteger.ZERO)) {
            Values nv = new Values(new ZeroInt(BigInteger.ONE));
            values.put(nv.hashString(), nv);
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

    public void setExprValues(TreeMap<String, Values> exprValues) { this.exprValues = exprValues; }

    public BigInteger getPower() { return this.power; }

    public void setSin(Boolean sin) { this.sin = sin; }

    public void setPower(BigInteger power) { this.power = power; }
}
