import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;

public class Expr implements Factor {
    private HashSet<Term> terms = null;
    private TreeMap<String, Values> values;

    public Expr() {
        this.terms = new HashSet<Term>();
        this.values = new TreeMap<>();
    }

    public Expr(TreeMap<String, Values> values) {
        this.values = values;
    }

    public void addTerm(Term term, Boolean status) {
        this.terms.add(term);
        //this.values.addAll(term.getValues());
        this.values = new Calculator().addValue(values, term.getValues(), status);
    }

    public void pow(BigInteger pow) {
        values = new Calculator().powerValue(values, pow);
    }

    public Expr reverse() {
        TreeMap<String, Values> newMap = new TreeMap<>();
        for (Values v : values.values()) {
            v.setConstValue(BigInteger.valueOf(0).subtract(v.getConstValue()));
            newMap.put(v.hashString(), v);
        }
        this.values = newMap;
        return this;
    }

    public void merge() {
        values.entrySet().removeIf(stringValuesEntry -> stringValuesEntry.
                getValue().getConstValue().equals(BigInteger.ZERO));
    }

    public void doubleCos() {
        //遍历二次方三角函数
        //取出values、去掉这一项、换成另一个三角项
        //用新values.hashString查询整个expr
        //如果有（指数、三角函数均相等）匹配，取出两项再比较系数
        //  相等 -> 平方和，存入临时集合
        //  相反 -> 二倍角，也存
        //  ELSE p都不做
        TreeMap<String, Values> tempValues = new TreeMap<>();
        ArrayList<String> remove = new ArrayList<>();
        Iterator<String> it = values.keySet().iterator();
        while (it.hasNext()) {
            String s0 = it.next();
            Values v0 = values.get(s0);
            for (String s1 : v0.getSanFuncs().keySet()) {
                if (v0.getSanFuncs().get(s1).getPower().
                        equals(BigInteger.valueOf(2))) {    //只对 [2] 次方，进行 [移除] 操作
                    Values v1 = new Calculator().getClone(v0);
                    SanFunc sf1 = new Calculator().getClone(v0.getSanFuncs().get(s1));

                    Values vrenew = new Calculator().getClone(v0);                  // 保存剩余部分
                    vrenew.getSanFuncs().remove(sf1.hashStringInValues());


                    sf1.setSin(!sf1.getSin());                                      // 反转后存入
                    v1.getSanFuncs().remove(s1);//sin(x)**2*cos(x)**2+sin(x)**2
                    if (v1.getSanFuncs().containsKey(sf1.hashStringInValues())) {
                        SanFunc p = v1.getSanFuncs().get(sf1.hashStringInValues());
                        p.setPower(p.getPower().add(sf1.getPower()));
                    } else {
                        v1.getSanFuncs().put(sf1.hashStringInValues(), sf1);
                    }
                    if (values.containsKey(v1.hashString())) {                      // 还真能找到
                        Values v2 = values.get(v1.hashString());
                        if (v1.getConstValue().equals(v2.getConstValue())) {         // 平方和，放回去除后的项
                            remove.add(v1.hashString());
                            tempValues.put(vrenew.hashString(), vrenew);
                            it.remove();
                        } else if (v1.getConstValue().add(v2.getConstValue()).
                                equals(BigInteger.ZERO)) {
                            remove.add(v1.hashString());
                            Boolean sin = !sf1.getSin(); //反转过一次
                            sf1.getDouble(false);
                            vrenew.getSanFuncs().put(sf1.hashStringInValues(), sf1);
                            if (sin) {  // 反着的二倍角
                                vrenew.setConstValue(BigInteger.ZERO.
                                        subtract(vrenew.getConstValue()));
                            }
                            tempValues.put(vrenew.hashString(), vrenew);
                            it.remove();
                        }
                    }
                }
            }
        }
        for (String s : remove) {
            values.remove(s);
        }
        for (String s : tempValues.keySet()) {
            if (values.containsKey(s)) {
                Values v = values.get(s);
                v.setConstValue(v.getConstValue().add(tempValues.get(s).getConstValue()));
            } else {
                values.put(s, tempValues.get(s));
            }
        }
    }

    @Override
    public TreeMap<String, Values> getValues() {
        return values;
    }

    public String hashString() {
        StringBuilder sb = new StringBuilder();
        for (Values v : values.values()) {
            sb.append(v.hashString());
            sb.append("+");
        }
        return sb.toString();
    }

    public String hashStringInSan() {
        StringBuilder sb = new StringBuilder();
        for (Values v : values.values()) {
            sb.append("[");
            sb.append(v.getConstValue());
            sb.append("]");
            sb.append(v.hashString());
            sb.append("+");
        }
        return sb.toString();
    }

    public String ttostring() {
        StringBuilder sb = new StringBuilder();
        if (values.isEmpty()) { //expr sum is 0
            return "0";
        }
        //System.out.println(values);
        for (Values v : values.values()) {
            if (v.getConstValue().compareTo(BigInteger.valueOf(0)) > 0) {
                sb.append(v.ttostring());
            }
        }
        for (Values v : values.values()) {
            sb.append(v.ttostring());
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