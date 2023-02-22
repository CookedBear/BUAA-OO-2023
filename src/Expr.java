import java.math.BigInteger;
import java.util.HashSet;
import java.util.Iterator;

public class Expr implements Factor{
    private final HashSet<Term> terms;
    private HashSet<Values> values;

    public Expr() {
        this.terms = new HashSet<Term>();
        this.values = new HashSet<Values>();
    }

    public void addTerm(Term term) {
        this.terms.add(term);
        //this.values.addAll(term.getValues());
        for (Values v2 : term.getValues()) {
            int flag = 1;
            for (Values v1 : values) {
                if (samePow(v1, v2)) {  //成功合并同类项
                    v1.setConstValue(v1.getConstValue().add(v2.getConstValue()));
                    flag = 0;
                }
            }
            if (flag == 1) {    //无法合并同类项
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
            newValues.add(new Values(z,z,z,BigInteger.valueOf(1)));
        } else {
            for (Values v1 : values) {
                for (Values v2 : this.getValues()) {
                    newValues.add(multiValues(v1, v2));
                }
            }
        }
        this.values = newValues;

    }

    private Values multiValues(Values value1, Values value2) {
        BigInteger constValue = value1.getConstValue().multiply(value2.getConstValue());
        BigInteger xPow = value1.getxPow().add(value2.getxPow());
        BigInteger yPow = value1.getyPow().add(value2.getyPow());
        BigInteger zPow = value1.getzPow().add(value2.getzPow());
        return new Values(xPow, yPow, zPow, constValue);
    }

    public Boolean samePow(Values v1, Values v2) {
        Boolean b1 = v1.getxPow().equals(v2.getxPow());
        Boolean b2 = v1.getyPow().equals(v2.getyPow());
        Boolean b3 = v1.getzPow().equals(v2.getzPow());
        return b1 && b2 && b3;
    }

    @Override
    public HashSet<Values> getValues() {
        return values;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator<Values> iter = values.iterator();
        //System.out.println(values);
        sb.append(iter.next().toString());
        if (iter.hasNext()) {
            sb.append(" ");
            sb.append(iter.next().toString());
            sb.append(" +");
            while (iter.hasNext()) {
                sb.append(" ");
                sb.append(iter.next().toString());
                sb.append(" +");
            }
        }
        return sb.toString();
    }
}

//addTerm-对Set进行遍历+删除时请注意，Iterator.remove()是在迭代过程中修改集合的唯一安全方法；如果在迭代过程中以任何其他方式修改基础集合，则未指定行为。
