import java.math.BigInteger;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;

public class Term {
    private final HashSet<Factor> factors;
    private TreeMap<String, Values> values;

    public Term() {
        this.factors = new HashSet<>();
        this.values = new TreeMap<>();
    }

    public Term(TreeMap<String, Values> values) {
        this.values = values;
        this.factors = new HashSet<>();
    }

    public void addFactor(Factor factor) {
        //this.factors.add(factor);
        //this.values.addAll(factor.getValues());
        this.values = new Calculator().multiValue(this.values, factor.getValues());
    }

    public void addFactorInit(Factor factor, Boolean status) {
        //this.factors.add(factor);
        this.values = new Calculator().getClone(factor.getValues());

        if (!status) {
            for (Values v : values.values()) {
                //System.out.println(v.getConstValue());
                v.setConstValue(BigInteger.valueOf(0).subtract(v.getConstValue()));
            }
        }
    }

    public void mergeInduction() {  //诱导公式化简
        Iterator<String> it = values.keySet().iterator();
        while (it.hasNext()) {
            String s0 = it.next();
            Values v = values.get(s0);
            Boolean reverse = false;
            for (SanFunc s : v.getSanFuncs().values()) {    //单项式的每个三角函数
                TreeMap<String, Values> expr = s.getExprValues();
                for (Values vv : expr.values()) {   //三角函数内第一个单项式
                    if (vv.getConstValue().compareTo(BigInteger.ZERO) < 0) {
                        for (Values vvv : expr.values()) {
                            vvv.setConstValue(BigInteger.ZERO.subtract(vvv.getConstValue()));
                        }   //内部全部系数取反，更新单项式的键值对
                        //expr在三角函数中作为一部分时需要带系数，避免合并函数
                        //expr在内部键值对没必要带系数，便于其他项化简
                        if (!s.getSin()) {
                            break;
                        }
                        for (BigInteger i = BigInteger.ZERO; i.compareTo(s.getPower()) < 0; i = i.add(BigInteger.ONE)) {
                            reverse = !reverse;
                        }
                    }
                    break;
                }
                //if (s.getExprValues())
            }
            if (reverse) {
                v.setConstValue(BigInteger.ZERO.subtract(v.getConstValue()));
            }
            values.put(v.hashString(), new Calculator().getClone(v));
            it.remove();
        }

//        for (Values v : values.values()) {  //项的每个单项式
//            Boolean reverse = false;
//            for (SanFunc s : v.getSanFuncs().values()) {    //单项式的每个三角函数
//                TreeMap<String, Values> expr = s.getExprValues();
//                for (Values vv : expr.values()) {   //三角函数内第一个单项式
//                    if (vv.getConstValue().compareTo(BigInteger.ZERO) < 0) {
//                        for (Values vvv : expr.values()) {
//                            vvv.setConstValue(BigInteger.ZERO.subtract(vvv.getConstValue()));
//                        }   //内部全部系数取反，更新单项式的键值对
//                        //expr在三角函数中作为一部分时需要带系数，避免合并函数
//                        //expr在内部键值对没必要带系数，便于其他项化简
//                        if (!s.getSin()) {
//                            break;
//                        }
//                        for (BigInteger i = BigInteger.ZERO; i.compareTo(s.getPower()) < 0; i = i.add(BigInteger.ONE)) {
//                            reverse = !reverse;
//                        }
//                    }
//                    break;
//                }
//                //if (s.getExprValues())
//            }
//            if (reverse) {
//            v.setConstValue(BigInteger.ZERO.subtract(v.getConstValue()));
//            }
//         //三角内部必须体现所有项的系数属性（完整展开），否则会错误地合并同类项：sin(x)+sin((2*x))
//        }//考虑拉一套特别的输出expr的hashStringInSan方法，把系数作为属性输出在string内
         //三角函数就对expr使用新方法，其余场景使用原本的hashString便于合并
    }
//if (v.getConstValue().mod(new BigInteger("2")).equals(BigInteger.ONE)) {
//        continue;
//    }
    public TreeMap<String, Values> getValues() { return this.values; }
}
