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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (constValue.compareTo(BigInteger.valueOf(0)) > 0) {
            sb.append('+');
        }
        sb.append(constValue.toString());
        if (!this.xpow.equals(BigInteger.valueOf(0))) {
            sb.append("*x");
            if (!xpow.equals(BigInteger.valueOf(1))) {
                sb.append(xpow);
            }
        }
        if (!this.ypow.equals(BigInteger.valueOf(0))) {
            sb.append("*y");
            if (!ypow.equals(BigInteger.valueOf(1))) {
                sb.append(ypow);
            }
        }
        if (!this.zpow.equals(BigInteger.valueOf(0))) {
            sb.append("*z");
            if (!zpow.equals(BigInteger.valueOf(1))) {
                sb.append(zpow);
            }
        }
        return sb.toString();
    }
}
