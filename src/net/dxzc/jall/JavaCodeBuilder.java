package net.dxzc.jall;

import java.util.LinkedList;

public class JavaCodeBuilder extends BaseCodeBuilder {

    protected String packageName;
    protected String className = "Parser";
    protected String indent = "    ";
    protected String heads = "private";
    protected boolean use = true;
    protected String tokenType = "String";

    protected String exceptions = "";

    protected void indent(int c) {
        for (int i = 0; i < c; i++) {
            code(indent);
        }
    }

    @Override
    protected void buildLanguage(Language language) {
        if (packageName != null) {
            code("package ");
            code(packageName);
            code(";\n");
        }
        code("public class ");
        code(className);
        code(" {\n\n");
        if (use) {
            Symbol symbol = language.symbols.get(0);
            indent(1);
            code("public ");
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
            code(") ");
            code(exceptions);
            if (!exceptions.isEmpty()) {
                code(" ");
            }
            code("{\n");
            indent(2);
            code("next();\n");
            indent(2);
            code("return ");
            code(symbol.name);
            code("(");
            if (symbol.input.length != 0) {
                indent(3);
                code("v0");
                for (int t = 1; t < symbol.input.length; t++) {
                    code(", v");
                    code(t);
                }
            }
            code(");\n");
            indent(1);
            code("}\n\n");
        }
        super.buildLanguage(language);
        code("}\n");
    }

    @Override
    protected void buildSymbol(Symbol symbol) {
        indent(1);
        code(heads);
        code(" ");
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
        code(") ");
        code(exceptions);
        if (!exceptions.isEmpty()) {
            code(" ");
        }
        code("{\n");
        indent(2);
        code("switch(token) {\n");
        super.buildSymbol(symbol);
        indent(2);
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
            indent(2);
            code("throw need(token");
            while (!tokens.isEmpty()) {
                code(", ");
                code(tokens.pop().name);
            }
            code(");\n");
        }
        indent(1);
        code("}\n\n");
    }

    @Override
    protected void buildItem(Token token, Item item) {
        indent(3);
        if (token != null) {
            code("case ");
            code(token.name);
            code(": {\n");
        } else {
            code("default: {\n");
        }
        super.buildItem(token, item);
        indent(4);
        code("return v");
        code(pop());
        code(";\n");
        indent(3);
        code("}\n");
    }

    @Override
    protected void token(Token token) {
        indent(4);
        code("check(");
        code(token.name);
        code(");\n");
    }

    @Override
    protected void push(Push push) {
        for (int i = 0; i < push.count; i++) {
            indent(4);
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
        indent(4);
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
        indent(4);
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
