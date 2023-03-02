public class ArtiFunc {
    private String expr;
    private String var;
    private int varNumber = 0;


    public ArtiFunc(String expr) {
        var = "";
//        if (expr.contains("x")) {
//            var += "a";
//            varNumber++;
//        }
//        if (expr.contains("y")) {
//            var += "b";
//            varNumber++;
//        }
//        if (expr.contains("z")) {
//            var += "d";
//            varNumber++;
//        }
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
                    var += "d";
                    varNumber++;break;
                default:
                    break;
            }
        }
        String expr1 = expr.split("=")[1];
        this.expr = expr1.replaceAll("x", "a").replaceAll("y", "b").replaceAll("z" ,"d");
    }

    public int getVarNumber() { return this.varNumber; }

    public String getVar() { return this.var; }

    public String getExpr() { return this.expr; }

}
