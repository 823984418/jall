package net.dxzc.jall;

import java.util.LinkedList;
import java.util.List;

/**
 * 非终结符号产生式.
 */
public final class Item {

    public Item(Node... defines) {
        if (defines == null) {
            throw new RuntimeException();
        }
        LinkedList<Node> nodes = new LinkedList<>();
        int push = 0;
        for (int i = 0; i < defines.length; i++) {
            Node node = defines[i];
            if (node instanceof Push) {
                push += ((Push) node).count;
            } else {
                if (push != 0) {
                    nodes.add(new Push(push));
                    push = 0;
                }
                nodes.add(node);
            }
        }
        this.defines = nodes.toArray(new Node[nodes.size()]);
    }

    protected final Node[] defines;

    protected int getFirstNameIndex() {
        for (int i = 0; i < defines.length; i++) {
            if (defines[i] instanceof Name) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 内联指定符号.
     *
     * @param index 指定符号
     * @return 内联结果
     */
    protected List<Item> inline(int index) {
        Symbol inlineSymbol = (Symbol) defines[index];
        LinkedList<Item> list = new LinkedList<>();
        for (int i = 0; i < inlineSymbol.items.size(); i++) {
            Item inlineItem = inlineSymbol.items.get(i);
            LinkedList<Node> nodes = new LinkedList<>();
            for (int t = 0; t < index; t++) {
                nodes.add(defines[t]);
            }
            for (int t = 0; t < inlineItem.defines.length; t++) {
                nodes.add(inlineItem.defines[t]);
            }
            for (int t = index + 1; t < defines.length; t++) {
                nodes.add(defines[t]);
            }
            list.add(new Item(nodes.toArray(new Node[nodes.size()])));
        }
        return list;
    }

    /**
     * @param count 数量
     * @return 结果
     */
    protected Token[] move(int count) {
        Token[] nodes = new Token[count];
        LinkedList<Item> list = new LinkedList<>();
        list.add(this);
        for (; ; ) {
            Item item = list.pollFirst();
            if (item == null) {
                break;
            }
            int p = 0;
            for (int t = 0; ; t++) {
                if (p == count) {
                    break;
                }
                if (t == item.defines.length) {
                    return null;
                }
                if (item.defines[t] instanceof Token) {
                    if (nodes[p] != null) {
                        if (nodes[p] != item.defines[t]) {
                            return null;
                        }
                    } else {
                        nodes[p] = (Token) item.defines[t];
                    }
                    p++;
                } else if (item.defines[t] instanceof Symbol) {
                    list.addAll(item.inline(t));
                }
            }
        }
        return nodes;
    }

    protected Item moveToken(int count) {
        LinkedList<Node> list = new LinkedList<>();
        LinkedList<Node> nodes = new LinkedList<>();
        for (int i = 0; i < defines.length; i++) {
            if (defines[i] instanceof Name && count > 0) {
                if (defines[i] instanceof Token) {
                    list.add(defines[i]);
                    count--;
                } else {
                    throw new RuntimeException();
                }
            } else {
                nodes.add(defines[i]);
            }
        }
        list.addAll(nodes);
        return new Item(list.toArray(new Node[list.size()]));
    }

    protected LinkedList<Item> inlineMove(int count) {
        LinkedList<Item> res = new LinkedList<>();
        LinkedList<Item> use = new LinkedList<>();
        use.add(this);
        for (; ; ) {
            Item item = use.pollFirst();
            if (item == null) {
                break;
            }
            int n = 0;
            for (int p = 0; ; p++) {
                if (n == count) {
                    res.add(item.moveToken(count));
                    break;
                }
                if (p == item.defines.length) {
                    return null;
                }
                if (item.defines[p] instanceof Token) {
                    n++;
                } else if (item.defines[p] instanceof Symbol) {
                    use.addAll(item.inline(p));
                }
            }
        }
        return res;
    }

    public String toSource() {
        if (defines.length == 0) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder(defines[0].toString());
            for (int i = 1; i < defines.length; i++) {
                sb.append(" ");
                sb.append(defines[i].toString());
            }
            return sb.toString();
        }
    }

    @Override
    public String toString() {
        if (defines.length == 0) {
            return "ε";
        } else {
            return toSource();
        }
    }

}
