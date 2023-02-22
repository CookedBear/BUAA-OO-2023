import java.math.BigInteger;

public class Parser {
    private final Lexer lexer;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    public Expr parseExpr() {
        Expr expr =new Expr();
        expr.addTerm(parseTerm());
        //System.out.println(lexer.peek());
        while (lexer.peek().equals("+")) {
            lexer.next();
            expr.addTerm(parseTerm());
        }

        return expr;
    }

    public Term parseTerm() {
        Term term = new Term();
        term.addFactorInit(parseFactor());

        while (lexer.peek().equals("*")) {
            lexer.next();
            term.addFactor(parseFactor());
        }

        return term;
    }

    public Factor parseFactor() {
        String symbol = lexer.peek();
        //System.out.println(symbol);
//可以使用switch并优化为parse各部分的函数，简单调用
        if (symbol.equals("(")) {               //get ExprFunc now
            lexer.next();
            Expr expr = parseExpr();
            lexer.next();//jump ')' to accept the correct Character

            //lexer.next();//simulate the MiFunc
            if (lexer.hasPow()) {

                //System.out.println("has");
                lexer.next();   //jump '**' to accept the pow (注意并没有处理'+'号情况)，大改！
                lexer.next();
                BigInteger pow = new BigInteger(lexer.peek());
                expr.pow(pow);
                lexer.next();
                return expr;
            } else {
                return expr;
            }

        } else if (symbol.equals("x") || symbol.equals("y") || symbol.equals("z")) {
                                                //get MiFunc now
            lexer.next();
            if (lexer.hasPow()) {
                lexer.next();   //jump '**' to accept the pow (注意并没有处理'+'号情况)，大改！
                lexer.next();
                MiFunc mi = new MiFunc(symbol, new BigInteger(lexer.peek()));//只能处理数字的指数
                lexer.next();
                return mi;
            } else {
                return new MiFunc(symbol, BigInteger.valueOf(1));
            }

        } else {                                //get ZeroInt now
            ZeroInt z = new ZeroInt(new BigInteger(lexer.peek()));
            lexer.next();
            return z;//在peek后忘记使用next
        }
    }
}

//parseFactor-可以使用switch并优化为parse各部分的函数，简单调用
//parseFactor-MiFunc内没有处理指数的'+'号