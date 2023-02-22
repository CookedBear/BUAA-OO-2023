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
            char now =input.charAt(position);
            if (Character.isDigit(now)) {
                currentContent = this.getNumber();
            } else if (now == '+' || now == '*' || now == '(' || now == ')' || now == 'x' || now == 'y' || now == 'z') {
                position++;
                currentContent =String.valueOf(now);
            }
        }
    }

    private String getNumber() {
        StringBuilder sb = new StringBuilder();
        while (position < input.length() && Character.isDigit(input.charAt(position))) {
            sb.append(input.charAt(position));
            ++position;
        }
        return sb.toString();
    }

    public String peek() {
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
