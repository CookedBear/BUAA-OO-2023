import java.util.HashMap;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        //System.out.println("Hello world!");
        Scanner input = new Scanner(System.in);
        int functionNumber = Integer.parseInt(input.nextLine());
        HashMap<Character, ArtiFunc> artiFunctions = new HashMap<>();
        for (int i = 0; i < functionNumber; i++) {
            String artiFunction = input.nextLine();
            artiFunction = artiFunction.replaceAll("[ \t]", "");
            artiFunctions.put(artiFunction.charAt(0), new ArtiFunc(artiFunction));
        }
        String inputLine = input.nextLine();

        long startTime = System.nanoTime();
        inputLine = inputLine.replaceAll("[ \t]", "");

        Lexer lexer = new Lexer(inputLine);
        Parser parser = new Parser(lexer);

        long overTime0 = System.nanoTime();
        Expr exprMain = parser.parseExpr(artiFunctions);
        long overTime1 = System.nanoTime();
        System.out.println(exprMain.ttostring());
        long overTime2 = System.nanoTime();      //获取结束时间
        //System.out.println("Time is: "+(overTime0-startTime)+"(Initialization)  "
        //+(overTime1-startTime)+"(Parse)  "+(overTime2-startTime)+"(PrintOut)");
    }
}

//优化记录：
//增加一个工具类，用于进行values计算与数据结构的深克隆              (√)
//  深克隆方法：调用 new Type(new calculator().getClone(Type.getValues()));
//改进lexer.next()，以支持更多输入类型                          (√)
//  在parseFactor()中进行测试，保证功能的可行性，拆分各函数代码      (√)
//重构values结构，并适配已有代码：重写clone和add和multi方法
/*
public class Values{

}
 */
//添加三角函数因子SanFunc类，适配parseSanFunc()方法
//含有三角函数的项的合并——cal类中编写equal方法
//添加自定义函数ArtiFunc类，支持自定义函数输入，适配parseArtiFunc()方法——预计递归实现
//输出优化
//诱导公式：term返回时，遍历每个三角函数，使得treemap第一项constValue为正，保证内部格式一致   (√)
//  sin二倍角：term返回时对每个Values的每个三角进行遍历（迭代器），如
//      系数%2==0 + s->c、c->s后string是contain的；取出两项生成二倍角并立即检查系数条件+查询合成后的角（需要while
//平方和=1：expr返回时，遍历Values，取出并替换每个二次方的三角函数，通过string回查expr能否合并，while合并项有二次三角函数，递归
//cos二倍角：和上一步同步完成

//---------------------------------------------------------------------------------

//错误记录：
//向一个空白的Term中添加Factor时仍然使用乘法添加Values，导致所有Values归零，输出时报错
//  创建一个独特的init的添加方法

//幂函数缺省指数时会错误地向后跳跃两个词，导致存储时出错
//  创建一个Lexer显示后两个字符的方法，对缺省的"**"进行检测
//      在创建hasPow方法时，没有考虑如果遇到表达式末尾的处理方法
//          对表达式是否结束进行判断

//当含有括号时，忘记在parseFactor()中为表达式函数分支使用next()跳过括号，导致运行时爆栈
//  添加一个next()再运行
//  忘记给Expr类Override的getValues()方法提供values作为返回值，导致在运行时getValues()时报错NullPointException
//      修改getValues()方法返回值
//表达式因子可以包含指数，没有设置相关方法



//重构问题：
//'+'报错
//0报错
//sin(x)*cos(x)=sin(x)**2