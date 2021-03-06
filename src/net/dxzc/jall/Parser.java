package net.dxzc.jall;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * 语法描述语言的解析器.
 */
public class Parser {

    private char[] buff;
    private int pos;

    /**
     * 从语法描述语言中得到描述的语法.
     *
     * @param code 描述语言
     * @return 语法
     */
    public Language parser(String code) {
        buff = code.toCharArray();
        pos = 0;
        return parser();
    }

    private void skip() {
        for (; ; ) {
            if (pos == buff.length) {
                return;
            }
            char c = buff[pos];
            if (c == '#') {
                for (; ; ) {
                    pos++;
                    if (pos == buff.length) {
                        return;
                    }
                    c = buff[pos];
                    if (c == '\n') {
                        break;
                    }
                }
            } else if (c != ' ' && c != '\r' && c != '\n') {
                return;
            }
            pos++;
        }
    }

    private String readUtil(String c) {
        char[] cs = c.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (; ; ) {
            if (pos == buff.length) {
                return sb.toString();
            }
            char n = buff[pos];
            for (int i = 0; i < cs.length; i++) {
                if (n == cs[i]) {
                    return sb.toString();
                }
            }
            sb.append(n);
            pos++;
        }
    }

    private Language parser() {
        Language language = new Language();
        HashMap<String, Symbol> map = new HashMap<>();
        HashMap<String, Token> tokens = new HashMap<>();
        for (; ; ) {
            skip();
            if (buff[pos] == '@') {
                pos++;
                String cmd = readUtil("\r\n");
                if (cmd.equals("language")) {
                    break;
                }
                throw new RuntimeException();
            }
            String name = readUtil("(");
            LinkedList<String> inputs = new LinkedList<>();
            pos++;
            while (buff[pos] != ')') {
                inputs.add(readUtil(",)"));
            }
            pos++;
            String ret = readUtil("#\r\n");
            Symbol symbol = new Symbol(name, ret, inputs.toArray(new String[inputs.size()]));
            map.put(name, symbol);
            language.symbols.add(symbol);
        }
        for (; ; ) {
            skip();
            if (pos == buff.length) {
                break;
            }
            if (buff[pos] == '@') {
                pos++;
                String cmd = readUtil("\r\n");
                throw new RuntimeException();
            }
            LinkedList<Node> nodes = new LinkedList<>();
            String name = readUtil(" -");
            skip();
            if (buff[pos++] != '-' || buff[pos++] != '>') {
                throw new RuntimeException();
            }
            for (; ; ) {
                skip();
                if (buff[pos] == ';') {
                    pos++;
                    break;
                } else if (buff[pos] == '>') {
                    pos++;
                    nodes.add(Push.PUSH);
                } else if (buff[pos] == '{') {
                    pos++;
                    String call = readUtil("(");
                    pos++;
                    String as = readUtil(")");
                    pos++;
                    String type = readUtil("}");
                    pos++;
                    nodes.add(new Action(type, call, Integer.parseInt(as)));
                } else {
                    String p = readUtil(" >{;\r\n");
                    Symbol symbol = map.get(p);
                    if (symbol != null) {
                        nodes.add(symbol);
                    } else {
                        Token token = tokens.get(p);
                        if (token == null) {
                            token = new Token(p);
                            tokens.put(p, token);
                        }
                        nodes.add(token);
                    }
                }
            }
            Symbol symbol = map.get(name);
            if (symbol == null) {
                throw new RuntimeException("符号" + name + "未声明");
            }
            symbol.items.add(new Item(nodes.toArray(new Node[nodes.size()])));
        }
        return language;
    }

}
