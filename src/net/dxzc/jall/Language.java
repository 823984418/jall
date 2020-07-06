package net.dxzc.jall;

import java.lang.reflect.Method;
import java.util.*;

public class Language {

    /**
     * 语言包含的语法符号.
     */
    public final List<Symbol> symbols = new ArrayList<>();

    /**
     * 比较两个语法符号的先后顺序.
     *
     * @param a 语法符号
     * @param b 语法符号
     * @return {@code -1,0,1}
     */
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

    /**
     * 输出语法的描述.
     *
     * @return 描述的语法
     */
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

    /**
     * 输出对应的语法描述语言.
     *
     * @return 语法描述
     */
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
