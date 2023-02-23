import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        //System.out.println("Hello world!");
        Scanner input = new Scanner(System.in);
        String inputLine = input.nextLine();

        Lexer lexer = new Lexer(inputLine);
        Parser parser = new Parser(lexer);

        Expr exprMain = parser.parseExpr();
        System.out.println(exprMain);
    }
}

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