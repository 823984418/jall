package net.dxzc.jall;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 非终结符号.
 */
public class Symbol extends Name {

    public Symbol(String name, String type, String... input) {
        super(name);
        if (type == null) {
            throw new RuntimeException();
        }
        this.input = input.clone();
        this.type = type;
    }

    protected final String[] input;

    protected final String type;

    protected final ArrayList<Item> items = new ArrayList<>();

    public String toSymbolString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append("(");
        if (input.length > 0) {
            sb.append(input[0]);
            for (int i = 1; i < input.length; i++) {
                sb.append(",");
                sb.append(input[i]);
            }
        }
        sb.append(")");
        sb.append(type);
        return sb.toString();
    }

    public String toItemString() {
        if (items.isEmpty()) {
            return "Error symbol";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(toSymbolString());
            sb.append("\n");
            for (Item item : items) {
                sb.append(name);
                sb.append(" -> ");
                sb.append(item.toString());
                sb.append("\n");
            }
            return sb.toString();
        }
    }

}
