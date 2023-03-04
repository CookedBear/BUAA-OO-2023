import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

//cos((cos(x)**2-sin(x**2)**0+sin(x)**2))
public class Calculator {

    public TreeMap<String, Values> addValue(TreeMap<String, Values> v1,
                                            TreeMap<String, Values> v2, Boolean status) {
        for (String ss : v2.keySet()) {
            Values vv = getClone(v2.get(ss));
            String newS = vv.hashString();
            if (v1.containsKey(newS)) {
                Values v = v1.get(newS);
                if (status) {
                    v.setConstValue(v1.get(newS).getConstValue().add(vv.getConstValue()));
                } else {
                    v.setConstValue(v1.get(newS).getConstValue().subtract(vv.getConstValue()));
                }
                v1.put(newS, v);
            } else {
                if (status) {
                    vv.setConstValue(vv.getConstValue());
                } else {
                    vv.setConstValue(BigInteger.ZERO.subtract(vv.getConstValue()));
                }
                v1.put(newS, vv);
            }
        }
        return v1;
    }

    public Values multiValue(Values value1, Values value2) {

        BigInteger constValue = value1.getConstValue().multiply(value2.getConstValue());
        BigInteger xpow = value1.getxPow().add(value2.getxPow());
        BigInteger ypow = value1.getyPow().add(value2.getyPow());
        BigInteger zpow = value1.getzPow().add(value2.getzPow());
        TreeMap<String, SanFunc> sanFuncs2 = new Calculator().getSansClone(value2.getSanFuncs());
        TreeMap<String, SanFunc> sanFuncs1 = new Calculator().getSansClone(value1.getSanFuncs());
        for (SanFunc v2 : sanFuncs2.values()) {
            String s2 = v2.hashString();
            if (sanFuncs1.keySet().contains(s2)) {
                v2.setPower(v2.getPower().add(sanFuncs1.get(s2).getPower()));
                sanFuncs1.remove(s2);
                sanFuncs1.put(v2.hashString(), v2);
            } else {
                sanFuncs1.put(s2, v2);
            }
        }
        //迭代器删除SanFunc，待补
        //        for (SanFunc s : sanFuncs.values()) {
        //            if (s.getPower().equals(BigInteger.ZERO)) {
        //                sanFuncs.remove(s);
        //            }
        //        }
        //sanFuncs.removeIf(s -> s.getPower().equals(BigInteger.ZERO));   //幂为0即为常数
        return new Values(constValue, xpow, ypow, zpow, sanFuncs1);
    }

    public TreeMap<String, Values> multiValue(TreeMap<String, Values> v1,
                                              TreeMap<String, Values> v2) {
        TreeMap<String, Values> v3 = new TreeMap<>();
        for (Values v : v1.values()) {
            for (Values vv : v2.values()) {
                Values vvv = multiValue(v, vv);
                String key = vvv.hashString();
                if (v3.containsKey(key)) {
                    Values vTemp = v3.get(key);
                    vvv.setConstValue(v3.get(key).getConstValue().add(vvv.getConstValue()));
                    v3.remove(key);
                }
                v3.put(vvv.hashString(), vvv);
            }
        }
        return v3;
    }

    public TreeMap<String, Values> powerValue(TreeMap<String, Values> values, BigInteger power) {
        BigInteger z = BigInteger.valueOf(0);
        TreeMap<String, Values> newValues = new TreeMap<>();
        if (power.equals(z)) {
            Values zz = new Values(z, z, z, BigInteger.valueOf(1));
            newValues.put(zz.hashString(), zz);
        } else if (power.equals(BigInteger.valueOf(1))) {
            newValues = values;
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
        return new Values(v.getConstValue(), v.getxPow(), v.getyPow(),
                v.getzPow(), getSansClone(v.getSanFuncs()));
    }

    public TreeMap<String, Values> getClone(TreeMap<String, Values> values) {
        TreeMap<String, Values> newValues = new TreeMap<>();
        for (Values v : values.values()) {
            newValues.put(v.hashString(), getClone(v));
        }
        return newValues;
    }

    public SanFunc getClone(SanFunc sanFunc) {  // clone 默认获得表达式类型
        return new SanFunc(sanFunc.getSin() ? "sin" : "cos",new Expr(new Calculator().
                getClone(sanFunc.getExprValues())),new ZeroInt(sanFunc.getPower()));
    }

    public TreeMap<String, SanFunc> getSansClone(TreeMap<String, SanFunc> sanFuncs) {
        TreeMap<String, SanFunc> newSanFuncs = new TreeMap<>();
        sanFuncs.forEach((exprString, sanFunc) -> {
            newSanFuncs.put(exprString, getClone(sanFunc));
        });
        return newSanFuncs;
    }
}
