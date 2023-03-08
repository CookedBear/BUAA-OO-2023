import java.util.HashMap;

public class ArtiFunc {
    private String expr;
    private String var;
    private int varNumber = 0;

    public ArtiFunc(String expr, HashMap<Character, ArtiFunc> artifunctions) {
        var = "";
        String expr0 = expr.split("=")[0];
        int x = expr0.length();
        for (int i = 0; i < x; i++) {
            switch (expr0.charAt(i)) {
                case 'x':
                    var += "a";
                    varNumber++;
                    break;
                case 'y':
                    var += "b";
                    varNumber++;
                    break;
                case 'z':
                    var += "e";
                    varNumber++;
                    break;
                default:
                    break;
            }
        }
        String expr1 = expr.split("=")[1];
        String expr2 = new Parser(new Lexer(expr1)).parseExpr(artifunctions).ttostring();
        this.expr = expr2.replaceAll("x", "a").replaceAll("y", "b").replaceAll("z", "e");
    }

    public int getVarNumber() {
        return this.varNumber;
    }

    public String getVar() {
        return this.var;
    }

    public String getExpr() {
        return this.expr;
    }

}
