import java.math.BigInteger;

public class Parser {
    private final Lexer lexer;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    public Expr parseExpr() {
        Expr expr = new Expr();

        String status = lexer.peek();
        if (status.equals("+") || status.equals("-")) {
            lexer.next();
        }
        Boolean ss = !status.equals("-");

        expr.addTerm(parseTerm(),ss);
        //System.out.println(lexer.peek());
        while (lexer.peek().equals("+") || lexer.peek().equals("-")) {
            Boolean sss = !lexer.peek().equals("-");
            lexer.next();
            expr.addTerm(parseTerm(),sss);
        }

        return expr;
    }

    public Term parseTerm() {
        Term term = new Term();

        String status = lexer.peek();
        //System.out.println(status);
        if (status.equals("+") || status.equals("-")) {
            lexer.next();
        }
        Boolean ss = !status.equals("-");

        term.addFactorInit(parseFactor(),ss);

        while (lexer.peek().equals("*")) {
            lexer.next();
            term.addFactor(parseFactor());
        }

        return term;
    }

    public Factor parseFactor() {
        String status = lexer.peek();
        //if (status.equals("+") || status.equals("-")) {
        //    lexer.next();
        //}
        Boolean s = !status.equals("-");
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
                ZeroInt z = parseInt();
                BigInteger pow = z.getInt();
                expr.pow(pow);
                lexer.next();

                return s ? expr : expr.reverse();
            } else {
                return s ? expr : expr.reverse();
            }

        } else if (symbol.equals("x") || symbol.equals("y") || symbol.equals("z")) {
            //get MiFunc now
            lexer.next();
            if (lexer.hasPow()) {
                lexer.next();   //jump '**' to accept the pow (注意并没有处理'+'号情况)，大改！
                lexer.next();
                ZeroInt z = parseInt();
                MiFunc mi = new MiFunc(symbol, z.getInt(), s);//只能处理数字的指数
                lexer.next();
                return mi;
            } else {
                return new MiFunc(symbol, BigInteger.valueOf(1), s);
            }

        } else {                                //get ZeroInt now
            ZeroInt z = parseInt();
            lexer.next();
            return z;//在peek后忘记使用next
        }
    }

    public ZeroInt parseInt() {
        String status = lexer.peek();
        if (status.equals("+") || status.equals("-")) {
            lexer.next();
        }
        System.out.println(lexer.peek());
        return new ZeroInt(status.equals("-") ?
               new BigInteger("0").subtract(new BigInteger(lexer.peek())) :
               new BigInteger(lexer.peek()));


//        if (lexer.peek().equals("+")) {
//            System.out.println(lexer.peek());
//            lexer.next();
//
//            return new ZeroInt(new BigInteger(lexer.peek()));
//        } else if (lexer.peek().equals("-")) {
//            lexer.next();
//            System.out.println("yyy");
//            return new ZeroInt(new BigInteger("0").subtract(new BigInteger(lexer.peek())));
//        } else if (Character.isDigit(lexer.peek().charAt(0))) {
//            System.out.println("zzz");
//            return new ZeroInt(new BigInteger(lexer.peek()));
//        }
//        return new ZeroInt(BigInteger.valueOf(0));
    }
}

//parseFactor-可以使用switch并优化为parse各部分的函数，简单调用
//parseFactor-MiFunc内没有处理指数的'+'号

//借助parseInt()对正负号的处理办法，对Expr和Term也进行改写



//if(符号){
//    next
//}
//addterm(符号);
//while(符号) {
//    addterm(符号)
//}
