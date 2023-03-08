import java.math.BigInteger;
import java.util.HashMap;
import java.util.TreeMap;

public class Parser {
    private final Lexer lexer;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }
    //+800779086 +- ( y ++ +0 *-9646709*x *x+ +21)+-5 + +( -41186740406+ + y ** +7+ +-y *
    // * +0 )+ - y* +805495809- - +727361266 +  y - 602966266088732456 + - z * z ** 8*-58
    // * 4232545112188 -  +900 *x **+5 + + -65553902182 * y **0+ +y ** +7* z * +8362637576
    // 58524+- y**+6 -20384464919+- -( + +798577794609663)+--8 + -y+ +y* (+ +y++x** 7*-14
    // 1951037178294 *x ** 6+ - -+93000574 ) +   ( - -6668383 ++z **2 *y+++5649066 +- +92
    // 8493 *-210997156337362397+ ++138019420644634738 -y**8 +-- y * +175767510412771592
    // ) *+3828839 - + y + ++ +45248 *x-  ( + 739557607018 )+  x ** 5 *(- +619+ +x ** 6+
    // 764411398358068-+z *y+ -- 68538664190656+-64++ x* -146014201625562 ++ y*458 +- +-30
    // 123658651321* 57955132*945646286 *z **+1 ++ +12275+ - --9713 * -274313321 *6601+ x
    // **4 +-+52611018347 + +z ** +4 + + +y) "

    public Expr parseExpr(HashMap<Character, ArtiFunc> artiFunctions) {
        Expr expr = new Expr();

        String status = lexer.peek();
        if (status.equals("+") || status.equals("-")) {
            lexer.next();
        }
        Boolean ss = !status.equals("-");

        expr.addTerm(parseTerm(artiFunctions), ss);
        //System.out.println(lexer.peek());
        while (lexer.peek().equals("+") || lexer.peek().equals("-")) {
            Boolean sss = !lexer.peek().equals("-");
            lexer.next();
            expr.addTerm(parseTerm(artiFunctions), sss);
        }
        expr.merge();
        //expr.doubleCos();
        return expr;
    }

    public Term parseTerm(HashMap<Character, ArtiFunc> artiFunctions) {
        Term term = new Term();

        String status = lexer.peek();
        //System.out.println(status);
        if (status.equals("+") || status.equals("-")) {
            lexer.next();
        }
        Boolean ss = !status.equals("-");

        term.addFactorInit(parseFactor(artiFunctions), ss);

        while (lexer.peek().equals("*")) {
            lexer.next();
            term.addFactor(parseFactor(artiFunctions));
        }
        term.mergeInduction();
        //term.doubleSin();
        //term.doubleSin();
        return term;
    }

    public Factor parseFactor(HashMap<Character, ArtiFunc> artiFunctions) {
        String status = lexer.peek();
        Boolean s = !status.equals("-");
        String symbol = lexer.peek();
        if (symbol.equals("(")) {               //get ExprFunc now
            lexer.next();
            Expr expr = parseExpr(artiFunctions);
            lexer.next();//jump ')' to accept the correct Character
            if (lexer.hasPow()) {
                lexer.next();   //jump '**' to accept the pow (注意并没有处理'+'号情况)，大改！
                lexer.next();
                ZeroInt z = parseInt();
                BigInteger pow = z.getInt();
                expr.pow(pow);
            }
            return s ? expr : expr.reverse();
        } else if (symbol.equals("x") || symbol.equals("y") || symbol.equals("z")) {
            //get MiFunc now
            lexer.next();
            if (lexer.hasPow()) {
                lexer.next();   //jump '**' to accept the pow (注意并没有处理'+'号情况)，大改！
                lexer.next();
                ZeroInt z = parseInt();
                return new MiFunc(symbol, z.getInt(), s);
            } else {
                return new MiFunc(symbol, BigInteger.valueOf(1), s);
            }

        } else if (symbol.equals("sin") || symbol.equals("cos")) {  //sin( factor ) ** power
            return parseSanfunc(artiFunctions, symbol);
        } else if (symbol.equals("sum")) {
            return null;
        } else if (symbol.equals("f") || symbol.equals("g") || symbol.equals("h")) {    // f(f,f,f)
            lexer.next();
            lexer.next();
            //Factor factor1 = parseFactor(artiFunctions);
            ArtiFunc artiFunc = artiFunctions.get(symbol.charAt(0));
            int varNumber = artiFunc.getVarNumber();
            String var = artiFunc.getVar();
            String artiFuncExpr = artiFunc.getExpr();
            for (int i = 0; i < varNumber; i++) {
                TreeMap<String, Values> v = parseFactor(artiFunctions).getValues();
                lexer.next();
                Expr exprr = new Expr(new Calculator().getClone(v));
                String parse;
                parse = "(" + exprr.ttostring() + ")";
                artiFuncExpr = artiFuncExpr.replaceAll(String.valueOf(var.charAt(i)), parse);
                //System.out.println(artiFuncExpr);               System.out.println(var.charAt(i));
            }
            if (lexer.hasPow()) {
                lexer.next();
                lexer.next();   //power begin
                ZeroInt power = parseInt();
                Expr e = new Parser(new Lexer(artiFuncExpr)).parseExpr(artiFunctions);
                e.pow(power.getInt());
                return e;
            } else {
                return new Parser(new Lexer(artiFuncExpr)).parseExpr(artiFunctions);
            }
            //return new Parser(new Lexer(artiFuncExpr)).parseExpr(artiFunctions);
        } else {                                //get ZeroInt now
            return parseInt();//在peek后忘记使用next
        }
    } //

    public ZeroInt parseInt() {
        String status = lexer.peek();
        if (status.equals("+") || status.equals("-")) {
            lexer.next();
        }
        //System.out.println(lexer.peek());
        String peek = lexer.peek();
        lexer.next();
        return new ZeroInt(status.equals("-") ?
                new BigInteger("0").subtract(new BigInteger(peek)) :
                new BigInteger(peek));


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

    public SanFunc parseSanfunc(HashMap<Character, ArtiFunc> artiFunctions,
                                String symbol) {
        lexer.next();   // (
        lexer.next();   // factor begin
        Factor factor = parseFactor(artiFunctions);  // )
        lexer.next();   // * or fin
        if (lexer.hasPow()) {
            lexer.next();
            lexer.next();   //power begin
            ZeroInt power = parseInt();
            return new SanFunc(symbol, factor, power);
        } else {
            return new SanFunc(symbol, factor, new ZeroInt(BigInteger.ONE));
        }
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
