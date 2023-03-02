import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;

//cos((cos(x)**2-sin(x**2)**0+sin(x)**2))
public class calculator {

    public HashSet<Values> addValue(HashSet<Values> v1, HashSet<Values> v2, Boolean status) {
        HashSet<Values> v3 = getClone(v1);
        for (Values vvv : v2) {
            Values vv = getClone(vvv);
            int flag = 1;

            if (!status) {
                vv.setConstValue(BigInteger.valueOf(0).subtract(vv.getConstValue()));
            }

            for (Values v : v3) {

                if (samePow(v, vv)) {  //成功合并同类项
                        v.setConstValue(v.getConstValue().add(vv.getConstValue()));
                    flag = 0;
                }
//                SanFunc delete = shrinkSan(v, vv);
//                if (delete != null) {
//                    System.out.println(delete.getSin());
//                    v.getSanFuncs().remove(delete);
//                    delete.setSin(!delete.getSin());
//                    vv.getSanFuncs().remove(delete);
//
//                    //flag = 0;
//                }

//                if (contrast(vvv, vv)) {                 //找到a * A * sin^2 + b * A * cos^2 = b * A + (a-b) * A * sin^2
//                    BigInteger a = vvv.getConstValue();
//                    BigInteger b = vv.getConstValue();
////                    System.out.println(a);
////                    System.out.println(b);
//                    vvv.setConstValue(a.subtract(b));
//                    v3.add(new Values(new ZeroInt(b)));
//                    flag = 0;
//                }
            }
            if (flag == 1) {    //无法合并同类项
                v3.add(new calculator().getClone(vv));
            }
        }

        v3.removeIf(vx -> vx.getConstValue().equals(BigInteger.valueOf(0)));
        for (Values v : v3) {
            v.getSanFuncs().removeIf(s -> s.getPower().equals(BigInteger.ZERO));
        }
        return v3;
    }

    public Values multiValue(Values value1, Values value2) {
        BigInteger constValue = value1.getConstValue().multiply(value2.getConstValue());
        BigInteger xpow = value1.getxPow().add(value2.getxPow());
        BigInteger ypow = value1.getyPow().add(value2.getyPow());
        BigInteger zpow = value1.getzPow().add(value2.getzPow());
        HashSet<SanFunc> sanFuncs = getSansClone(value1.getSanFuncs());
        HashSet<SanFunc> sanFuncs2 = value2.getSanFuncs();
//        sanFuncs.addAll(getSanClone(value2.getSanFuncs()));
        for (SanFunc s2 : sanFuncs2) {
            Boolean insert = false;
            for (SanFunc s1 : sanFuncs) {
                s1.hashCode();
                HashSet<Values> v2 = s2.getExprValues();
                if (s1.getSin() == s2.getSin() && addValue(v2, s1.getExprValues(), false).isEmpty()) {    //合并同三角项
                    s1.setPower(s1.getPower().add(s2.getPower()));
                    insert = true;
                    break;
                }   //  错误判断会把同内容的sin和cos合并为s1的种类
            }
            if (!insert) {
                sanFuncs.add(getClone(s2));
            }
        }
        sanFuncs.removeIf(s -> s.getPower().equals(BigInteger.ZERO));   //幂为0即为常数
        return new Values(constValue, xpow, ypow, zpow, sanFuncs);
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
        return new Values(v.getConstValue(), v.getxPow(), v.getyPow(), v.getzPow(), getSansClone(v.getSanFuncs()));
    }

    public HashSet<Values> getClone(HashSet<Values> values) {
        HashSet<Values> newValues = new HashSet<>();
        for (Values v : values) {
            newValues.add(getClone(v));
        }
        return newValues;
    }

    public SanFunc getClone(SanFunc sanFunc) {  // clone 默认获得表达式类型
        return new SanFunc(sanFunc.getSin() ? "sin" : "cos",new Expr(new calculator().getClone(sanFunc.getExprValues())),new ZeroInt(sanFunc.getPower()));
    }

    public HashSet<SanFunc> getSansClone(HashSet<SanFunc> sanFuncs) {
        HashSet<SanFunc> newSanFuncs = new HashSet<>();
        for (SanFunc s : sanFuncs) {
            newSanFuncs.add(getClone(s));
        }
        return newSanFuncs;
    }

    public Boolean samePow(Values v1, Values v2) {
        Boolean b1 = v1.getxPow().equals(v2.getxPow());
        Boolean b2 = v1.getyPow().equals(v2.getyPow());
        Boolean b3 = v1.getzPow().equals(v2.getzPow());
        Boolean b4;
        if (v1.getSanFuncs().isEmpty() && v2.getSanFuncs().isEmpty()) {
            b4 = true;
        } else {
            b4 = sameSan(v1.getSanFuncs(), v2.getSanFuncs(), true);
        }
        //System.out.println(b4);
        return b1 && b2 && b3 && b4;
    }

    public SanFunc shrinkSan(Values v1, Values v2) {
        HashSet<SanFunc> sv1 = getSansClone(v1.getSanFuncs());
        HashSet<SanFunc> sv2 = getSansClone(v2.getSanFuncs());
        for (SanFunc s2 : sv2) {
            Boolean s2Sin = s2.getSin();
            HashSet<Values> s2ExprValues = s2.getExprValues();
            BigInteger s2Power = s2.getPower();

            for (SanFunc s1 : sv1) {
                Boolean s1Sin = s1.getSin();
                HashSet<Values> s1ExprValues = s1.getExprValues();
                BigInteger s1Power = s1.getPower();
                if (s1Sin == s2Sin && s1Power.equals(s2Power) && addValue(s1ExprValues, s2ExprValues, false).isEmpty()) {
                    sv1.remove(s1);
                    sv2.remove(s2);
                    break;
                }
            }
            // not found
        }
        if (sv1.size()!=1 || sv2.size()!=1) {
            return null;
        }
        for (SanFunc s1 : sv1) {
            for (SanFunc s2 : sv2) {
                Boolean b1 = s1.getSin()!=s2.getSin();
                Boolean b2 = s1.getPower().equals(s2.getPower()) && s1.getPower().equals(BigInteger.valueOf(2));
                Boolean b3 = addValue(s1.getExprValues(), s2.getExprValues(), false).isEmpty();
                if (b1 && b2 && b3) {
                    return s1;
                }
            }
        }
        return null;
    }


    public Boolean sameSan(HashSet<SanFunc> sanFuncs1, HashSet<SanFunc> sanFuncs2, Boolean same) {    //三角函数集合相同（遍历）：类型一致 + 指数一致 + ExprValue一致
        HashSet<SanFunc> sanFuncs3 = getSansClone(sanFuncs1);
        for (SanFunc s2 : sanFuncs2) {
            Boolean s2Sin = same == s2.getSin();
            HashSet<Values> s2ExprValues = s2.getExprValues();
            BigInteger s2Power = s2.getPower();
            Boolean found = false;

            for (SanFunc s1 : sanFuncs3) {
                Boolean s1Sin = s1.getSin();
                HashSet<Values> s1ExprValues = s1.getExprValues();
                BigInteger s1Power = s1.getPower();
                if (s1Sin == s2Sin && s1Power.equals(s2Power) && addValue(s1ExprValues, s2ExprValues, false).isEmpty()) {
                    sanFuncs3.remove(s1);
                    found = true;
                    break;
                }
            }
            // not found
            if (!found) {
                return false;
            }
        }
        return sanFuncs3.isEmpty();

    }
}
