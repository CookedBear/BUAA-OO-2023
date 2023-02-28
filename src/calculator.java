import java.math.BigInteger;
import java.util.HashSet;

public class calculator {

    public HashSet<Values> addValue(HashSet<Values> v1, HashSet<Values> v2, Boolean status) {
        HashSet<Values> v3 = getClone(v1);
        for (Values v : v2) {
            Values vv = getClone(v);
            int flag = 1;

            for (Values vvv : v3) {
                if (!status) {
                    vv.setConstValue(BigInteger.valueOf(0).subtract(vv.getConstValue()));
                }

                if (samePow(vvv, vv)) {  //成功合并同类项
                        vvv.setConstValue(vvv.getConstValue().add(vv.getConstValue()));
                    flag = 0;
                }
            }
            if (flag == 1) {    //无法合并同类项
                v3.add(new calculator().getClone(vv));
            }
        }

        v3.removeIf(vx -> vx.getConstValue().equals(BigInteger.valueOf(0)));
        return v3;
    }

    public Values multiValue(Values value1, Values value2) {
        BigInteger constValue = value1.getConstValue().multiply(value2.getConstValue());
        BigInteger xpow = value1.getxPow().add(value2.getxPow());
        BigInteger ypow = value1.getyPow().add(value2.getyPow());
        BigInteger zpow = value1.getzPow().add(value2.getzPow());
        return new Values(xpow, ypow, zpow, constValue);
    }

    public HashSet<Values> multiValue(HashSet<Values> v1, HashSet<Values> v2) {
        HashSet<Values> v3 = new HashSet<>();
        for (Values v : v1) {
            for (Values vv : v2) {
                v3.add(multiValue(v, vv));
            }
        }
        return v3;
    }

    public HashSet<Values> powerValue(HashSet<Values> values, BigInteger power) {
        BigInteger z = BigInteger.valueOf(0);
        HashSet<Values> newValues = new HashSet<>();
        if (power.equals(z)) {
            newValues.add(new Values(z, z, z, BigInteger.valueOf(1)));
        } else if (power.equals(BigInteger.valueOf(1))) {
            newValues = getClone(values);
        } else {
            newValues = multiValue(values, values);

            BigInteger ii = new BigInteger(power.toString());
            ii = ii.subtract(new BigInteger("2"));
            for (; ii.compareTo(z) > 0; ii = ii.subtract(new BigInteger("1"))) {
                newValues = multiValue(newValues, values);
            }
        }
        return newValues;
    }

    public Values getClone(Values v) {
        return new Values(v.getxPow(), v.getyPow(), v.getzPow(), v.getConstValue());
    }

    public HashSet<Values> getClone(HashSet<Values> values) {
        HashSet<Values> newValues = new HashSet<>();
        for (Values v : values) {
            newValues.add(getClone(v));
        }
        return newValues;
    }

    public Boolean samePow(Values v1, Values v2) {
        Boolean b1 = v1.getxPow().equals(v2.getxPow());
        Boolean b2 = v1.getyPow().equals(v2.getyPow());
        Boolean b3 = v1.getzPow().equals(v2.getzPow());
        return b1 && b2 && b3;
    }

}
