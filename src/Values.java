import java.math.BigInteger;

public class Values {
    private BigInteger xPow;
    private BigInteger yPow;
    private BigInteger zPow;
    private BigInteger constValue;

    public Values(BigInteger xPow, BigInteger yPow, BigInteger zPow, BigInteger constValue) {
        this.xPow = xPow;
        this.yPow = yPow;
        this.zPow = zPow;
        this.constValue = constValue;
    }

    public BigInteger getxPow() {
        return xPow;
    }


    public BigInteger getyPow() {
        return yPow;
    }


    public BigInteger getzPow() {
        return zPow;
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
        sb.append(constValue.toString());
        if (!this.xPow.equals(BigInteger.valueOf(0))) {
            sb.append("x");
            sb.append(xPow);
        }
        if (!this.yPow.equals(BigInteger.valueOf(0))) {
            sb.append("y");
            sb.append(yPow);
        }
        if (!this.zPow.equals(BigInteger.valueOf(0))) {
            sb.append("z");
            sb.append(zPow);
        }
        return sb.toString();
    }
}
