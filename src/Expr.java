import java.math.BigInteger;
import java.util.HashSet;
import java.util.Iterator;

public class Expr implements Factor {
    private final HashSet<Term> terms;
    private HashSet<Values> values;
    private Boolean minus;

    public Expr() {
        this.terms = new HashSet<Term>();
        this.values = new HashSet<Values>();
        this.minus = false;
    }

    public void addTerm(Term term, Boolean status) {
        this.terms.add(term);
        //this.values.addAll(term.getValues());
        for (Values v2 : term.getValues()) {
            int flag = 1;
            for (Values v1 : values) {
                if (samePow(v1, v2)) {  //成功合并同类项
                    if (status) {
                        v1.setConstValue(v1.getConstValue().add(v2.getConstValue()));
                    } else {
                        v1.setConstValue(v1.getConstValue().subtract(v2.getConstValue()));
                    }
                    flag = 0;
                }
            }
            if (flag == 1) {    //无法合并同类项
                if (!status) {
                    v2.setConstValue(BigInteger.valueOf(0).subtract(v2.getConstValue()));
                }
                values.add(v2);
            }
        }
        values.removeIf(v -> v.getConstValue().equals(BigInteger.valueOf(0)));
        //删除系数为0的单项
    }

    public void pow(BigInteger pow) {
        HashSet<Values> newValues = new HashSet<>();
        BigInteger z = BigInteger.valueOf(0);
        if (pow.equals(z)) {
            newValues.add(new Values(z, z, z, BigInteger.valueOf(1)));
        } else if (pow.equals(BigInteger.valueOf(1))) {
            newValues = values;
        } else {
            for (Values v1 : values) {
                for (Values v2 : this.getValues()) {
                    newValues.add(multiValues(v1, v2));
                }
            }
            BigInteger ii = new BigInteger(pow.toString());
            ii = ii.subtract(new BigInteger("2"));
            for (; ii.compareTo(z) > 0; ii = ii.subtract(new BigInteger("1"))) {
                HashSet<Values> nnewValues = new HashSet<>();
                for (Values v1 : values) {
                    for (Values v2 : newValues) {
                        nnewValues.add(multiValues(v1, v2));
                    }
                }
                newValues = nnewValues;
            }
        }
        this.values = newValues;

    }

    private Values multiValues(Values value1, Values value2) {
        BigInteger constValue = value1.getConstValue().multiply(value2.getConstValue());
        BigInteger xpow = value1.getxPow().add(value2.getxPow());
        BigInteger ypow = value1.getyPow().add(value2.getyPow());
        BigInteger zpow = value1.getzPow().add(value2.getzPow());
        return new Values(xpow, ypow, zpow, constValue);
    }

    public Boolean samePow(Values v1, Values v2) {
        Boolean b1 = v1.getxPow().equals(v2.getxPow());
        Boolean b2 = v1.getyPow().equals(v2.getyPow());
        Boolean b3 = v1.getzPow().equals(v2.getzPow());
        return b1 && b2 && b3;
    }

    public Expr reverse() {
        for (Values v : values) {
            v.setConstValue(BigInteger.valueOf(0).subtract(v.getConstValue()));
        }
        return this;
    }

    @Override
    public HashSet<Values> getValues() {
        return values;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator<Values> iter = values.iterator();
        if (values.isEmpty()) { //expr sum is 0
            return "0";
        }
        //System.out.println(values);
        for (Values v : values) {
            if (v.getConstValue().compareTo(BigInteger.valueOf(0)) > 0) {
                sb.append(v);
            }
        }
        sb.append(iter.next().toString());
        while (iter.hasNext()) {
            sb.append(iter.next().toString());
        }
        String s = sb.toString();
        if (s.charAt(0) == '+') {
            return s.substring(1);
        } else {
            return s;
        }
    }
}

//addTerm-对Set进行遍历+删除时请注意，Iterator.remove()是在迭代过程中修改集合的唯一安全方法；如果在迭代过程中以任何其他方式修改基础集合，则未指定行为。
//+(+z**0--z**1*z**+2*x++3*-2*z-+x**+0--x*0*0-+z**1*0*+2--2+y*0*-4---2)**+0+z*(--y**1*0)+(+x*0*z+0+
// +z*x++0*+1*-4++0*y**+1*1-+0*y*922337203685+0*0+x*922337203685*0--y*0+y)*y**0*+2-922337203685+z-
// +z*y*(+-z**0+x**1+-0)**2--4


//2*x*y*z+z+2*x*z+2*y-922337203680-y*z-x**2*y*z
//-922337203680+2*x*y*z-y*z+z+2*x*z+2*y-x**2*y*z