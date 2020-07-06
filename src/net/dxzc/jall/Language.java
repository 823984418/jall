package net.dxzc.jall;

import java.lang.reflect.Method;
import java.util.*;

public class Language {

    protected final List<Symbol> symbols = new ArrayList<>();

    public int compare(Symbol a, Symbol b) {
        if (a == b) {
            return 0;
        }
        for (Symbol symbol : symbols) {
            if (symbol == a) {
                return -1;
            }
            if (symbol == b) {
                return 1;
            }
        }
        return 0;
    }

    public String toLanguageString() {
        if (symbols.isEmpty()) {
            return "Empty language";
        } else {
            StringBuilder sb = new StringBuilder();
            for (Symbol symbol : symbols) {
                sb.append(symbol.toItemString());
                sb.append("\n");
            }
            return sb.toString();
        }
    }

    public String toSource() {
        StringBuilder sb = new StringBuilder();
        for (Symbol symbol : symbols) {
            sb.append(symbol.toSymbolString());
            sb.append(";\n");
        }
        sb.append("\n@language\n\n");
        for (Symbol symbol : symbols) {
            for (Item item : symbol.items) {
                sb.append(symbol.name);
                sb.append(" -> ");
                sb.append(item.toSource());
                sb.append(";\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }


}
