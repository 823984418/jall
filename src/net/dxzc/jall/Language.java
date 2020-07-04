package net.dxzc.jall;

import java.lang.reflect.Method;
import java.util.*;

public class Language {

    public static Language fromClass(Class<?> c) {
        String[] ss = c.getAnnotation(Symbols.class).value();
        Language language = new Language();
        HashMap<String, Symbol> map = new HashMap<>();
        HashMap<String, Token> tokens = new HashMap<>();
        for (String s : ss) {
            String[] ts = s.substring(s.indexOf("(") + 1, s.lastIndexOf(")")).split(",");
            if (ts.length == 1 && ts[0].length() == 0) {
                ts = new String[0];
            }
            Symbol symbol = new Symbol(s.substring(0, s.indexOf("(")), s.substring(s.lastIndexOf(")") + 1), ts);
            language.symbols.add(symbol);
            map.put(symbol.name, symbol);
        }
        for (Method method : c.getDeclaredMethods()) {
            As as = method.getAnnotation(As.class);
            if (as != null) {
                String[] a = as.value();
                LinkedList<Node> list = new LinkedList<>();
                for (int i = 1; i < a.length; i++) {
                    String p = a[i];
                    Symbol s = map.get(p);
                    if (s != null) {
                        list.add(s);
                    } else {
                        Token t = tokens.get(p);
                        if (t == null) {
                            t = new Token(p);
                            tokens.put(p, t);
                        }
                        list.add(t);
                        list.add(Push.PUSH);
                    }
                }
                list.add(new Action(method.getReturnType().getTypeName(), method.getName(), method.getParameterCount()));
                map.get(a[0]).items.add(new Item(list.toArray(new Node[list.size()])));
            }
        }
        return language;
    }

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


}
