package net.dxzc.jall;

import java.util.LinkedList;

public abstract class BaseCodeBuilder implements CodeBuilder {

    private int inputCount;
    private int count;
    private StringBuilder sb;
    private LinkedList<Integer> stack;

    @Override
    public String build(Language language) {
        sb = new StringBuilder();
        buildLanguage(language);
        String r = sb.toString();
        sb = null;
        return r;
    }

    protected void code(String c) {
        sb.append(c);
    }

    protected void code(int c) {
        sb.append(c);
    }

    protected int push() {
        int c = count++;
        stack.push(c);
        return c;
    }

    protected int pop() {
        return stack.pop();
    }

    protected void buildLanguage(Language language) {
        for (Symbol symbol : language.symbols) {
            buildSymbol(symbol);
        }
    }

    protected void buildSymbol(Symbol symbol) {
        inputCount = symbol.input.length;
        for (Item item : symbol.items) {
            count = 0;
            stack = new LinkedList<>();
            for (int i = 0; i < inputCount; i++) {
                push();
            }
            int firstIndex = item.getFirstNameIndex();
            if (firstIndex < 0) {
                buildItem(null, item);
            } else {
                buildItem((Token) item.defines[firstIndex], item);
            }
        }
        count = 0;
        stack = null;
        inputCount = 0;
    }

    protected void buildItem(Token token, Item item) {
        boolean firstToken = true;
        for (Node node : item.defines) {
            if (node instanceof Token) {
                if (firstToken) {
                    firstToken = false;
                } else {
                    token((Token) node);
                }
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
    }

    protected abstract void token(Token token);

    protected abstract void push(Push push);

    protected abstract void symbol(Symbol symbol);

    protected abstract void action(Action action);


}
