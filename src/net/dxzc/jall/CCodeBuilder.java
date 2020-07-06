package net.dxzc.jall;

import java.util.LinkedList;

public class CCodeBuilder extends BaseCodeBuilder {

    public static final String CODE = "";

    protected boolean code = true;
    protected String indent = "    ";
    protected boolean use = true;
    protected String tokenType = "char*";

    protected void indent(int c) {
        for (int i = 0; i < c; i++) {
            code(indent);
        }
    }

    @Override
    protected void buildLanguage(Language language) {
        if (code) {
            code(CODE);
        }
        if (use) {
            Symbol symbol = language.symbols.get(0);
            indent(0);
            code(symbol.type);
            code(" parser(");
            if (symbol.input.length != 0) {
                code(symbol.input[0]);
                code(" v0");
                for (int t = 1; t < symbol.input.length; t++) {
                    code(", ");
                    code(symbol.input[0]);
                    code(" v");
                    code(t);
                }
            }
            code(") {\n");
            indent(1);
            code("next();\n");
            indent(1);
            code("return ");
            code(symbol.name);
            code("(");
            if (symbol.input.length != 0) {
                indent(2);
                code("v0");
                for (int t = 1; t < symbol.input.length; t++) {
                    code(", v");
                    code(t);
                }
            }
            code(");\n");
            indent(0);
            code("}\n\n");
        }
        super.buildLanguage(language);
    }

    @Override
    protected void buildSymbol(Symbol symbol) {
        indent(0);
        code(symbol.type);
        code(" ");
        code(symbol.name);
        code("(");
        if (symbol.input.length != 0) {
            code(symbol.input[0]);
            code(" v0");
            for (int i = 1; i < symbol.input.length; i++) {
                code(", ");
                code(symbol.input[i]);
                code(" v");
                code(i);
            }
        }
        code(") {\n");
        indent(1);
        code("switch(token) {\n");
        super.buildSymbol(symbol);
        indent(1);
        code("}\n");
        boolean nu = false;
        LinkedList<Token> tokens = new LinkedList<>();
        for (Item item : symbol.items) {
            int firstIndex = item.getFirstNameIndex();
            if (firstIndex < 0) {
                nu = true;
            } else {
                tokens.add((Token) item.defines[firstIndex]);
            }
        }
        if (!nu) {
            indent(1);
            code("return need(token");
            while (!tokens.isEmpty()) {
                code(", ");
                code(tokens.pop().name);
            }
            code(");\n");
        }
        indent(0);
        code("}\n\n");
    }

    @Override
    protected void buildItem(Token token, Item item) {
        indent(2);
        if (token != null) {
            code("case ");
            code(token.name);
            code(": {\n");
        } else {
            code("default: {\n");
        }
        super.buildItem(token, item);
        indent(3);
        code("return v");
        code(pop());
        code(";\n");
        indent(2);
        code("}\n");
    }

    @Override
    protected void token(Token token) {
        indent(3);
        code("check(");
        code(token.name);
        code(");\n");
    }

    @Override
    protected void push(Push push) {
        for (int i = 0; i < push.count; i++) {
            indent(3);
            code(tokenType);
            code(" v");
            code(push());
            code(" = push();\n");
        }
    }

    @Override
    protected void symbol(Symbol symbol) {
        StringBuilder sb = new StringBuilder();
        LinkedList<Integer> buff = new LinkedList<>();
        for (int i = 0; i < symbol.input.length; i++) {
            buff.push(pop());
        }
        if (buff.size() != 0) {
            sb.append("v");
            sb.append(buff.pop());
            while (buff.size() != 0) {
                sb.append(", v");
                sb.append(buff.pop());
            }
        }
        indent(3);
        code(symbol.type);
        code(" v");
        code(push());
        code(" = ");
        code(symbol.name);
        code("(");
        code(sb.toString());
        code(");\n");
    }

    @Override
    protected void action(Action action) {
        StringBuilder sb = new StringBuilder();
        LinkedList<Integer> buff = new LinkedList<>();
        for (int i = 0; i < action.count; i++) {
            buff.push(pop());
        }
        if (buff.size() != 0) {
            sb.append("v");
            sb.append(buff.pop());
            while (buff.size() != 0) {
                sb.append(", v");
                sb.append(buff.pop());
            }
        }
        indent(3);
        code(action.type);
        code(" v");
        code(push());
        code(" = ");
        code(action.method);
        code("(");
        code(sb.toString());
        code(");\n");
    }

}
