import java.math.BigInteger;
import java.util.ArrayList;
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
        TreeMap<String, Values> newMap = new TreeMap<>();

        if (!status) {
            for (Values v : values.values()) {
                //System.out.println(v.getConstValue());
                v.setConstValue(BigInteger.valueOf(0).subtract(v.getConstValue()));
                newMap.put(v.hashString(), v);
            }
            this.values = newMap;
        }
    }

    public void mergeInduction() {  //诱导公式化简
        TreeMap<String, Values> tempValues = new TreeMap<>();
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
                        for (BigInteger i = BigInteger.ZERO;
                             i.compareTo(s.getPower()) < 0; i = i.add(BigInteger.ONE)) {
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
            tempValues.put(v.hashString(), new Calculator().getClone(v));
            it.remove();
        }
        values.putAll(tempValues);

        //三角内部必须体现所有项的系数属性（完整展开），否则会错误地合并同类项：sin(x)+sin((2*x))
        //考虑拉一套特别的输出expr的hashStringInSan方法，把系数作为属性输出在string内
        //三角函数就对expr使用新方法，其余场景使用原本的hashString便于合并
    }

    public void doubleSin() {
        for (String s0 : values.keySet()) {
            Values values1 = values.get(s0);
            if (values1.getConstValue().mod(BigInteger.valueOf(2)).equals(BigInteger.ZERO)) {
                TreeMap<String, SanFunc> sans = values1.getSanFuncs();
                TreeMap<String, SanFunc> addSans = new TreeMap<>();
                Iterator<String> it = sans.keySet().iterator();
                ArrayList<String> removeSans = new ArrayList<>();
                while (it.hasNext()) {
                    String s = it.next();
                    if (!sans.get(s).getPower().equals(BigInteger.ONE)) {
                        continue;
                    }
                    String c;
                    if (s.charAt(0) == 's') {
                        c = "c" + s.substring(1);
                    } else {
                        c = "s" + s.substring(1);
                    }
                    if (sans.containsKey(c)) {
                        values1.setConstValue(values1.getConstValue().
                                divide(BigInteger.valueOf(2)));
                        removeSans.add(c);// 也有直接替换就查找的bug，会覆盖原有内容

                        //  先暂存索引，待全部流程结束后进行string更新

                        SanFunc doub = new Calculator().getClone(sans.get(s));
                        doub.getDouble(true);
                        addSans.put(doub.hashStringInValues(), doub);
                        it.remove();
                    }
                }
                for (String s : addSans.keySet()) {
                    if (sans.containsKey(s)) {
                        SanFunc addSan = addSans.get(s);
                        addSan.setPower(addSan.getPower().add(sans.get(s).getPower()));
                        sans.remove(s);
                        sans.put(addSan.hashStringInValues(), addSan);
                    } else {
                        sans.put(s, addSans.get(s));
                    }
                }
                for (String c : removeSans) {
                    sans.remove(c);
                }
            }
        }
        //cos((2*x))**2+4*sin(x)**2*cos(x)*cos(x)
    }

    public TreeMap<String, Values> getValues() { return this.values; }
}
