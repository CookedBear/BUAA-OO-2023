import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class Values { // constValue * x ** xpow * y ** ypow * z ** zpow * sin/cos(exprValue) ** pow
    private BigInteger constValue;
    private BigInteger xpow;
    private BigInteger ypow;
    private BigInteger zpow;
    private HashSet<SanFunc> sanFuncs;
    private Boolean print;

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Values values = (Values) o;
        return Objects.equals(constValue, values.constValue)
                && Objects.equals(xpow, values.xpow) && Objects.equals(ypow, values.ypow)
                && Objects.equals(zpow, values.zpow) && Objects.equals(sanFuncs, values.sanFuncs)
                && Objects.equals(print, values.print);
    }


    public Values(BigInteger constValue, BigInteger xpow, BigInteger ypow,
                  BigInteger zpow, HashSet<SanFunc> sanFuncs) {
        this.constValue = constValue;
        this.xpow = xpow;
        this.ypow = ypow;
        this.zpow = zpow;
        this.sanFuncs = sanFuncs;
        this.print = false;
    }

    public Values(BigInteger xpow, BigInteger ypow, BigInteger zpow, BigInteger constValue) {
        this.xpow = xpow;
        this.ypow = ypow;
        this.zpow = zpow;
        this.constValue = constValue;
        sanFuncs = new HashSet<>();
        this.print = false;
    }

    public Values(SanFunc sanFunc) {
        BigInteger z = BigInteger.ZERO;
        this.constValue = BigInteger.ONE;
        this.xpow = z;
        this.ypow = z;
        this.zpow = z;
        this.sanFuncs = new HashSet<>();
        this.print = false;

        this.sanFuncs.add(new Calculator().getClone(sanFunc));
    }

    public Values(ZeroInt zeroInt) {
        this.constValue = zeroInt.getInt();
        this.xpow = BigInteger.ZERO;
        this.ypow = BigInteger.ZERO;
        this.zpow = BigInteger.ZERO;
        this.sanFuncs = new HashSet<>();
        this.print = false;
    }

    public BigInteger getxPow() {
        return xpow;
    }

    public BigInteger getyPow() {
        return ypow;
    }

    public BigInteger getzPow() {
        return zpow;
    }

    public BigInteger getConstValue() {
        return constValue;
    }

    public HashSet<SanFunc> getSanFuncs() { return sanFuncs; }

    public void setConstValue(BigInteger constValue) {
        this.constValue = constValue;
    }

    private Boolean xishu1() {
        Boolean b1 = constValue.equals(BigInteger.valueOf(-1));
        Boolean b2 = constValue.equals(BigInteger.valueOf(1));
        return b1 || b2;
    }

    public String tostring() {  //+xxxxxx or +-yyyyyyyyy
        StringBuilder sb = new StringBuilder();
        if (print) {
            return sb.append("").toString();
        }
        print = true;
        if (constValue.compareTo(BigInteger.valueOf(0)) > 0) {
            sb.append('+');
        }
        BigInteger z = BigInteger.valueOf(0);
        if (xpow.equals(z) && ypow.equals(z) && zpow.equals(z) && sanFuncs.isEmpty()) {
            sb.append(constValue);
            return sb.toString();
        }
        if (!xishu1()) {
            sb.append(constValue);//System.out.println("zhiyin");
        } else {
            if (constValue.equals(BigInteger.valueOf(-1))) {
                sb.append("-");
            } }
        if (!this.xpow.equals(BigInteger.valueOf(0))) {
            if (!xishu1()) {
                sb.append("*");
            }
            sb.append("x");
            if (!xpow.equals(BigInteger.valueOf(1))) {
                if (xpow.equals(BigInteger.valueOf(2))) {
                    sb.append("*x");
                } else {
                    sb.append("**");
                    sb.append(xpow);
                } } }
        if (!this.ypow.equals(BigInteger.valueOf(0))) {
            if (!xpow.equals(z) || !xishu1()) { //x*y**2
                sb.append("*");
            }
            sb.append("y");
            if (!ypow.equals(BigInteger.valueOf(1))) {
                if (ypow.equals(BigInteger.valueOf(2))) {
                    sb.append("*y");
                } else {
                    sb.append("**");
                    sb.append(ypow);
                } } }
        if (!this.zpow.equals(BigInteger.valueOf(0))) {
            if (!(xpow.equals(z) && ypow.equals(z)) || !xishu1()) {
                sb.append("*");
            }
            sb.append("z");
            if (!zpow.equals(BigInteger.valueOf(1))) {
                if (zpow.equals(BigInteger.valueOf(2))) {
                    sb.append("*z");
                } else {
                    sb.append("**");
                    sb.append(zpow);
                } } }
        if (!this.sanFuncs.isEmpty()) {
            //System.out.println("printing");
            sb = printSanFuncs(sb);
        }
        return sb.toString();
    }

    public StringBuilder printSanFuncs(StringBuilder sb) {
        Boolean first = true;
        BigInteger z = BigInteger.ZERO;
        for (SanFunc s : sanFuncs) {
            if (!(xpow.equals(z) && ypow.equals(z) && zpow.equals(z)) || !xishu1() || !first) {
                sb.append("*");
            }
            first = false;
            String type = s.getSin() ? "sin" : "cos";
            if (onlyOneFactor(s.getExprValues())) {
                String exprString = new Expr(s.getExprValues()).tostring();
                exprString = exprString.replaceAll("x\\*x", "x**2");
                exprString = exprString.replaceAll("y\\*y", "y**2");
                exprString = exprString.replaceAll("z\\*z", "z**2");
                sb.append(type).append('(').append(exprString).append(')');
            } else {
                sb.append(type).append("((").append(new Expr(s.getExprValues()).
                        tostring()).append("))");
            }
            if (!s.getPower().equals(BigInteger.ONE)) {
                sb.append("**").append(s.getPower());
            }
        }
        return sb;
    }

    private Boolean onlyOneFactor(HashSet<Values> values) {
        if (values.size() != 1) {
            return false;
        } else {
            int count = 0;
            for (Values v : values) {
                BigInteger z = BigInteger.ZERO;
                if (!v.xpow.equals(z)) {
                    count++;
                }
                if (!v.ypow.equals(z)) {
                    count++;
                }
                if (!v.zpow.equals(z)) {
                    count++;
                }
                if (!v.sanFuncs.isEmpty()) {
                    count++;
                }
                return count == 1;
            }
        }
        return false;
    }
}


/*
表达式输出时的格式：
常数 [*因子 [**指数] ]
常数：
    1应省略
    -1应写为-

纯数字：
    直接输出
含指数：
    指数非1时直接追加，为1时不输出
一般式：
    1*x：省去1*

 */
