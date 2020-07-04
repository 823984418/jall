package net.dxzc.jall;


import java.util.LinkedList;

public class MethodBuilder {

    public MethodBuilder(String head, String name, String type, String... input) {
        sb.append(head);
        sb.append(" ");
        sb.append(type);
        sb.append(" ");
        sb.append(name);
        sb.append("(");
        if (input.length != 0) {
            sb.append(input[0]);
            sb.append(" v");
            sb.append(count++);
            for (int i = 1; i < input.length; i++) {
                sb.append(", ");
                sb.append(input[i]);
                sb.append(" v");
                sb.append(count++);
            }
        }
        sb.append(") {\n");
        inputCount = input.length;
    }

    int inputCount;

    public final StringBuilder sb = new StringBuilder();

    int count = 0;

    LinkedList<Integer> stack = new LinkedList<>();

    public String end() {
        sb.append("}\n\n");
        return sb.toString();
    }

    public void item(Item item) {
        count = inputCount;
        stack.clear();
        for (int i = 0; i < count; i++) {
            stack.add(i);
        }
        for (Node node : item.defines) {
            if (node instanceof Token) {
                token((Token) node);
            } else if (node instanceof Push) {
                push((Push) node);
            } else if (node instanceof Symbol) {
                symbol((Symbol) node);
            } else if (node instanceof Action) {
                action((Action) node);
            }
        }
        if (stack.size() != 1) {
            throw new RuntimeException();
        }
        sb.append("return v").append(stack.pollFirst()).append(";\n");
    }

    public void token(Token token) {
        sb.append("check(");
        sb.append(token);
        sb.append(");\n");
    }

    public void push(Push push) {
        for (int i = 0; i < push.count; i++) {
            sb.append("String v");
            sb.append(count);
            sb.append(" = push();\n");
            stack.add(count);
            count++;
        }
    }

    public void symbol(Symbol symbol) {
        sb.append(symbol.type);
        sb.append(" v");
        sb.append(count);
        sb.append(" = ");
        sb.append(symbol.name);
        sb.append("(");
        LinkedList<Integer> buff = new LinkedList<>();
        for (int i = 0; i < symbol.input.length; i++) {
            buff.addFirst(stack.pollLast());
        }
        if (buff.size() != 0) {
            sb.append("v");
            sb.append(buff.pollFirst());
            while (buff.size() != 0) {
                sb.append(", v");
                sb.append(buff.pollFirst());
            }
        }
        sb.append(");\n");
        stack.add(count);
        count++;
    }

    public void action(Action action) {
        sb.append(action.type);
        sb.append(" v");
        sb.append(count);
        sb.append(" = ");
        sb.append(action.method);
        sb.append("(");
        LinkedList<Integer> buff = new LinkedList<>();
        for (int i = 0; i < action.count; i++) {
            buff.addFirst(stack.pollLast());
        }
        if (buff.size() != 0) {
            sb.append("v");
            sb.append(buff.pollFirst());
            while (buff.size() != 0) {
                sb.append(", v");
                sb.append(buff.pollFirst());
            }
        }
        sb.append(");\n");
        stack.add(count);
        count++;
    }

}
