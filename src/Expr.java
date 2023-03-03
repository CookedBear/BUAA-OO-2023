import java.math.BigInteger;
import java.util.HashSet;
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
        for (Values v : values.values()) {
            v.setConstValue(BigInteger.valueOf(0).subtract(v.getConstValue()));
        }
        return this;
    }

    public void merge() {
        values.entrySet().removeIf(stringValuesEntry -> stringValuesEntry.
                getValue().getConstValue().equals(BigInteger.ZERO));
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