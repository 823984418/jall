package net.dxzc.jall;

import java.util.ArrayList;
import java.util.List;

/**
 * 非终结符号.
 */
public class Symbol extends Name {

    /**
     * 构建一个非终结符号.
     *
     * @param name  名字
     * @param type  类型
     * @param input 需要的参数
     */
    public Symbol(String name, String type, String... input) {
        super(name);
        if (type == null) {
            throw new RuntimeException();
        }
        this.input = input.clone();
        this.type = type;
    }

    /**
     * 需要的参数类型.
     */
    protected final String[] input;

    /**
     * 返回的类型.
     */
    public final String type;

    /**
     * 产生式集.
     */
    public final List<Item> items = new ArrayList<>();

    /**
     * 得到需要参数类型的拷贝.
     *
     * @return 需要的参数类型
     */
    public String[] getInput() {
        return input.clone();
    }

    /**
     * 得到符号的声明.
     *
     * @return 符号声明
     */
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

    /**
     * 得到符号语言的表述.
     *
     * @return 描述
     */
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
