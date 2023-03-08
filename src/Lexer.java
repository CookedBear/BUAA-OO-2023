public class Lexer {
    private final String input;
    private int position;
    private String currentContent;

    public Lexer(String input) {
        this.input = input;
        this.next();
    }

    public void next() {
        if (position == this.input.length()) {
            return;
        } else {
            char now;
            while ((now = input.charAt(position)) == ' ' ||
                   (now = input.charAt(position)) == '\t') {
                position++;
            }
            if (Character.isDigit(now)) {
                currentContent = this.getNumber();
            } else if (isOp(now)) {
                position++;
                currentContent = String.valueOf(now);
            } else if (now == 'c' || now == 's') {
                switch (input.substring(position, position + 3)) {
                    case "sin":
                        currentContent = "sin";
                        break;
                    case "cos":
                        currentContent = "cos";
                        break;
                    case "sum":
                        currentContent = "sum";
                        break;
                    default:
                        currentContent = "jile";
                }
                position += 3;
                //System.out.println(currentContent);
            } else if (now == 'f' || now == 'g' || now == 'h') {
                currentContent = String.valueOf(now);
                position++;
            } else if (now == ',' || now == 'd') {
                currentContent = String.valueOf(now);
                position++;
            }
        }
    }

    private Boolean isOp(char now) {
        Boolean b1 = now == '+' || now == '-' || now == '*';
        return b1 || now == '(' || now == ')'
                  || now == 'x' || now == 'y' || now == 'z';
    }
    //(x+1)**3+1
    //2x1 1x2 + 2 +

    private String getNumber() {
        StringBuilder sb = new StringBuilder();
        while (position < input.length() && Character.isDigit(input.charAt(position))) {
            sb.append(input.charAt(position));
            ++position;
        }
        return sb.toString();
    }

    public String peek() {
        //System.out.println(currentContent);
        return this.currentContent;
    }

    public Boolean hasPow() {
        int pos = position;
        if (pos >= input.length()) {
            return false;
        }
        while (input.charAt(pos) == ' ') {
            pos++;
        }
        if (pos >= input.length() - 1) {
            return false;
        }
        return (input.charAt(pos) == '*' && input.charAt(pos - 1) == '*');
    }
}
