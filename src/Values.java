import java.math.BigInteger;

public class Values {
    private BigInteger xpow;
    private BigInteger ypow;
    private BigInteger zpow;
    private BigInteger constValue;

    public Values(BigInteger xpow, BigInteger ypow, BigInteger zpow, BigInteger constValue) {
        this.xpow = xpow;
        this.ypow = ypow;
        this.zpow = zpow;
        this.constValue = constValue;
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

    public void setConstValue(BigInteger constValue) {
        this.constValue = constValue;
    }

    private Boolean xishu1() {
        Boolean b1 = constValue.equals(BigInteger.valueOf(-1));
        Boolean b2 = constValue.equals(BigInteger.valueOf(1));
        return b1 || b2;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (constValue.compareTo(BigInteger.valueOf(0)) > 0) {
            sb.append('+');
        }
        BigInteger z = BigInteger.valueOf(0);
        if (xpow.equals(z) && ypow.equals(z) && zpow.equals(z)) {
            sb.append(constValue);
            return sb.toString();
        }
        if (!xishu1()) {
            sb.append(constValue);
        } else {
            if (constValue.equals(BigInteger.valueOf(-1))) {
                sb.append("-");
            }
        }
        if (!this.xpow.equals(BigInteger.valueOf(0))) {
            if (!xishu1()) {
                sb.append("*");
            }
            sb.append("x");
            if (!xpow.equals(BigInteger.valueOf(1))) {
                sb.append("**");
                sb.append(xpow);
            }
        }
        if (!this.ypow.equals(BigInteger.valueOf(0))) {
            if (!xpow.equals(z) || !xishu1()) { //x*y**2
                sb.append("*");
            }
            sb.append("y");
            if (!ypow.equals(BigInteger.valueOf(1))) {
                sb.append("**");
                sb.append(ypow);
            }
        }
        if (!this.zpow.equals(BigInteger.valueOf(0))) {
            if (!(xpow.equals(z) && ypow.equals(z)) || !xishu1()) {
                sb.append("*");
            }
            sb.append("z");
            if (!zpow.equals(BigInteger.valueOf(1))) {
                sb.append("**");
                sb.append(zpow);
            }
        }
        return sb.toString();
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
