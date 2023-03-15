import java.math.BigInteger;
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
                v1.put(vv.hashString(), vv);
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
            String s2 = v2.hashStringInValues();
            if (sanFuncs1.keySet().contains(s2)) {
                v2.setPower(v2.getPower().add(sanFuncs1.get(s2).getPower()));
                sanFuncs1.remove(s2);
                sanFuncs1.put(v2.hashStringInValues(), v2);
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

    public TreeMap<String, Values> getDao(Expr daoExpr, Character daoVar) {
        TreeMap<String, Values> exprValues = daoExpr.getValues();
        TreeMap<String, Values> daoExprValues = new TreeMap<>();
        for (Values values : exprValues.values()) {
            TreeMap<String, Values> daoValues = qiuDao(values, daoVar);
            for (String s : daoValues.keySet()) {   // put-in all
                if (daoExprValues.containsKey(s)) {
                    Values v = daoExprValues.get(s);
                    v.setConstValue(v.getConstValue().add(daoValues.get(s).getConstValue()));
                } else { daoExprValues.put(s, daoValues.get(s)); } } }
        return daoExprValues;
    }

    public TreeMap<String, Values> qiuDao(Values values, Character daoVar) {
        TreeMap<String, Values> daoValues = new TreeMap<>();
        boolean hasCharacter = !values.getCharPow(daoVar).equals(BigInteger.ZERO);
        boolean hasSanFunc = !values.getSanFuncs().isEmpty();
        boolean sanFuncSize = values.getSanFuncs().size() > 1;
        if (hasCharacter && !hasSanFunc) {  // x**b ok
            Values v = getClone(values);
            v.setConstValue(v.getConstValue().multiply(v.getCharPow(daoVar)));
            v.setCharPow(daoVar, v.getCharPow(daoVar).subtract(BigInteger.ONE));
            daoValues.put(v.hashString(), v);
        } else if (hasCharacter) {  // x**b*cos(x)*sin(x)   ok
            BigInteger xpow = values.getxPow();
            BigInteger ypow = values.getyPow();
            BigInteger zpow = values.getzPow();
            BigInteger pow = values.getCharPow(daoVar);
            Values values1 = getClone(values);
            values.setCharPow(daoVar, BigInteger.ZERO);
            values1.setCharPow(daoVar, pow.subtract(BigInteger.ONE));
            values1.setConstValue(values1.getConstValue().multiply(pow));
            daoValues.put(values1.hashString(), values1);
            TreeMap<String, Values> halfValues = qiuDao(values, daoVar);
            for (Values v : halfValues.values()) {
                v.setCharPow(daoVar, v.getCharPow(daoVar).add(pow));
                String s = v.hashString();
                if (daoValues.containsKey(s)) {
                    daoValues.get(s).setConstValue(daoValues.get(s).
                            getConstValue().add(v.getConstValue()));
                } else { daoValues.put(s, v); } }
        } else if (hasSanFunc && !sanFuncSize) {    //  sin(x) + sin(x)**2  ok
            daoValues = qiuDao2(values, daoVar);
        } else if (sanFuncSize) {   // sin(x)*cos(x)        ok
            BigInteger constValue = values.getConstValue();
            BigInteger xpow = values.getxPow();
            BigInteger ypow = values.getyPow();
            BigInteger zpow = values.getzPow();
            for (SanFunc sf : values.getSanFuncs().values()) {
                Values values0 = new Values(sf);
                Values values1 = getClone(values);
                values1.getSanFuncs().remove(sf.hashStringInValues());  // 其他三角函数项的集合
                TreeMap<String, Values> halfValues0 = qiuDao(values0, daoVar);
                for (Values v : halfValues0.values()) { //前导后不导
                    for (SanFunc sf1 : values1.getSanFuncs().values()) {
                        String s1 = sf1.hashStringInValues();
                        if (v.getSanFuncs().containsKey(s1)) {  // 更新指数
                            v.getSanFuncs().get(s1).setPower(v.getSanFuncs().
                                    get(s1).getPower().add(sf1.getPower()));
                        } else { v.getSanFuncs().put(s1, sf1); } }
                    if (daoValues.containsKey(v.hashString())) {
                        daoValues.get(v.hashString()).setConstValue(daoValues.get(
                                v.hashString()).getConstValue().add(v.getConstValue()));
                    } else { daoValues.put(v.hashString(), v); } } }
            TreeMap<String, Values> returnValues = new TreeMap<>();
            for (Values v : daoValues.values()) {
                v.addConstPow(constValue, xpow, ypow, zpow);
                returnValues.put(v.hashString(), v); }
            return returnValues;
        } else {    // a    ok
            Values v = new Values(new ZeroInt(BigInteger.ZERO));
            daoValues.put(v.hashString(), v); }
        return daoValues; }

    public TreeMap<String, Values> qiuDao2(Values values, Character daoVar) {
        TreeMap<String, Values> daoValues = new TreeMap<>();
        BigInteger constValue = values.getConstValue();
        BigInteger xpow = values.getxPow();
        BigInteger ypow = values.getyPow();
        BigInteger zpow = values.getzPow();
        SanFunc sf1 = null;
        for (SanFunc sf : values.getSanFuncs().values()) { sf1 = getClone(sf); }
        if (sf1.getPower().compareTo(BigInteger.ONE) <= 0) { // sin(x)
            boolean sin = sf1.getSin();
            sf1.setSin(!sin);
            TreeMap<String, Values> newDaoValues = getDao(new Expr(getClone(sf1.
                    getExprValues())), daoVar);
            for (Values v : newDaoValues.values()) {
                if (v.getSanFuncs().containsKey(sf1.hashStringInValues())) {
                    v.getSanFuncs().get(sf1.hashStringInValues()).setPower(
                            v.getSanFuncs().get(sf1.hashStringInValues())
                                    .getPower().add(sf1.getPower()));
                } else { v.getSanFuncs().put(sf1.hashStringInValues(), sf1); }
                if (!sin) {
                    v.setConstValue(BigInteger.ZERO.subtract(v.getConstValue())); }
                daoValues.put(v.hashString(), v); }
        } else {    // sin(x)**2
            SanFunc sf2 = getClone(sf1);
            boolean sin = sf1.getSin();
            sf1.setSin(!sin);
            sf1.setPower(BigInteger.ONE);
            BigInteger constV = sf2.getPower();
            sf2.setPower(sf2.getPower().subtract(BigInteger.ONE));
            TreeMap<String, Values> newDaoValues = getDao(new Expr(getClone(sf1.
                    getExprValues())), daoVar);
            for (Values v : newDaoValues.values()) {
                if (v.getSanFuncs().containsKey(sf1.hashStringInValues())) {
                    v.getSanFuncs().get(sf1.hashStringInValues()).setPower(v.
                            getSanFuncs().get(sf1.hashStringInValues()).
                            getPower().add(sf1.getPower()));
                } else {
                    v.getSanFuncs().put(sf1.hashStringInValues(), sf1);
                }
                if (v.getSanFuncs().containsKey(sf2.hashStringInValues())) {
                    v.getSanFuncs().get(sf2.hashStringInValues()).setPower(v.
                            getSanFuncs().get(sf2.hashStringInValues()).
                            getPower().add(sf2.getPower())); } else {
                    v.getSanFuncs().put(sf2.hashStringInValues(), sf2);
                }
                if (!sin) {
                    v.setConstValue(BigInteger.ZERO.subtract(v.getConstValue()));
                }
                v.setConstValue(v.getConstValue().multiply(constV));
                daoValues.put(v.hashString(), v);
            }
        }
        TreeMap<String, Values> returnValues = new TreeMap<>();
        for (Values v : daoValues.values()) {
            v.addConstPow(constValue, xpow, ypow, zpow);
            returnValues.put(v.hashString(), v);
        }
        return returnValues;
    }
}