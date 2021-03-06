package net.dxzc.jall;

import java.util.*;

/**
 * 为语法提供消左递归和提左公因子的操作.
 */
public class Compiler {

    /**
     * 消除左递归.
     *
     * @param language 处理的语言
     */
    public void eliminateLeftRecursion(Language language) {
        for (int i = 0; i < language.symbols.size(); i++) {
            Symbol symbol = language.symbols.get(i);
            LinkedList<Item> ll = new LinkedList<>();
            LinkedList<Item> rl = new LinkedList<>();
            for (int t = 0; t < symbol.items.size(); t++) {
                Item item = symbol.items.get(t);
                int firstIndex = item.getFirstNameIndex();
                if (firstIndex >= 0) {
                    Name firstName = (Name) item.defines[firstIndex];
                    if (firstName instanceof Symbol) {
                        Symbol firstSymbol = (Symbol) firstName;
                        if (firstSymbol == symbol) {
                            if (firstIndex != 0) {
                                throw new RuntimeException();
                            } else {
                                ll.add(item);
                            }
                        } else if (language.compare(firstSymbol, symbol) < 0) {
                            symbol.items.remove(t);
                            symbol.items.addAll(t, item.inline(firstIndex));
                            t--;
                        } else {
                            rl.add(item);
                        }
                    } else {
                        rl.add(item);
                    }
                } else {
                    rl.add(item);
                }
            }
            if (!ll.isEmpty()) {
                symbol.items.clear();
                Symbol useSymbol = new Symbol(symbol.name + "$", symbol.type, symbol.type);
                language.symbols.add(i + 1, useSymbol);
                for (Item item : rl) {
                    Node[] nodes = new Node[item.defines.length + 1];
                    System.arraycopy(item.defines, 0, nodes, 0, item.defines.length);
                    nodes[item.defines.length] = useSymbol;
                    symbol.items.add(new Item(nodes));
                }
                for (Item item : ll) {
                    Node[] nodes = new Node[item.defines.length];
                    System.arraycopy(item.defines, 1, nodes, 0, nodes.length - 1);
                    nodes[nodes.length - 1] = useSymbol;
                    useSymbol.items.add(new Item(nodes));
                }
                useSymbol.items.add(new Item());
            }
        }

    }

    /**
     * 提取左公因子.
     *
     * @param language 处理的语言
     */
    public void extractLeftCommonFactor(Language language) {
        for (int i = language.symbols.size() - 1; i >= 0; i--) {
            Symbol symbol = language.symbols.get(i);
            // 以Token开头的相同公因子深度
            // 如果以Token开头的仅有一项则为0
            HashMap<Token, Integer> switchMap = new HashMap<>();
            HashMap<Token, LinkedList<Item>> switchItem = new HashMap<>();
            for (int t = 0; t < symbol.items.size(); t++) {
                Item item = symbol.items.get(t);
                int firstIndex = item.getFirstNameIndex();
                if (firstIndex >= 0) {
                    Name firstName = (Name) item.defines[firstIndex];
                    if (firstName instanceof Token) {
                        Token firstToken = (Token) firstName;
                        LinkedList<Item> list = switchItem.get(firstToken);
                        if (list == null) {
                            list = new LinkedList<>();
                            switchItem.put(firstToken, list);
                        }
                        list.add(item);
                    } else {
                        symbol.items.remove(t);
                        symbol.items.addAll(t, item.inline(firstIndex));
                        t--;
                    }
                }
            }
            for (Map.Entry<Token, LinkedList<Item>> entry : switchItem.entrySet()) {
                Token token = entry.getKey();
                LinkedList<Item> list = entry.getValue();
                if (list.size() == 1) {
                    switchMap.put(token, 0);
                } else {
                    int n = 0;
                    root:
                    for (; ; ) {
                        n++;
                        Token[] tokens = null;
                        for (Item item : list) {
                            Token[] newTokens = item.move(n);
                            if (newTokens == null) {
                                break root;
                            }
                            if (tokens == null) {
                                tokens = newTokens;
                            } else {
                                for (int p = 0; p < n; p++) {
                                    if (tokens[p] != newTokens[p]) {
                                        break root;
                                    }
                                }
                            }
                        }
                    }
                    n--;
                    switchMap.put(token, n);
                }
            }
            LinkedList<Item> newItems = new LinkedList<>();
            for (int t = 0; t < symbol.items.size(); t++) {
                Item item = symbol.items.get(t);
                int firstIndex = item.getFirstNameIndex();
                if (firstIndex >= 0) {
                    Token firstToken = (Token) item.defines[firstIndex];
                    int depth = switchMap.get(firstToken);
                    newItems.addAll(item.inlineMove(depth));
                } else {
                    newItems.add(item);
                }
            }
            symbol.items.clear();
            symbol.items.addAll(newItems);

            HashMap<Token, Symbol> symbolMap = new HashMap<>();
            int p = 0;
            LinkedList<Item> newItem = new LinkedList<>();
            boolean add = false;
            for (int t = 0; t < symbol.items.size(); t++) {
                Item item = symbol.items.get(t);
                int firstIndex = item.getFirstNameIndex();
                if (firstIndex >= 0) {
                    Token firstToken = (Token) item.defines[firstIndex];
                    int depth = switchMap.get(firstToken);
                    if (depth == 0) {
                        newItem.add(item);
                    } else {
                        Symbol useSymbol = symbolMap.get(firstToken);
                        if (useSymbol == null) {
                            useSymbol = new Symbol(symbol.name + "_" + p++, symbol.type, symbol.input);
                            language.symbols.add(++i, useSymbol);
                            add = true;
                            Node[] nodes = new Node[depth + 1];
                            System.arraycopy(item.defines, 0, nodes, 0, depth);
                            nodes[depth] = useSymbol;
                            newItem.add(new Item(nodes));
                            symbolMap.put(firstToken, useSymbol);
                        }
                        Node[] nodes = new Node[item.defines.length - depth];
                        System.arraycopy(item.defines, depth, nodes, 0, nodes.length);
                        useSymbol.items.add(new Item(nodes));
                    }
                } else {
                    newItem.add(item);
                }
            }
            if (add) {
                i++;
            }
            symbol.items.clear();
            symbol.items.addAll(newItem);
        }
    }

    public void stackCheck(Language language) {
        for (Symbol symbol : language.symbols) {
            for (Item item : symbol.items) {
                int stack = symbol.input.length;
                for (int i = 0; i < item.defines.length; i++) {
                    Node node = item.defines[i];
                    if (node instanceof Push) {
                        stack += ((Push) node).count;
                    } else if (node instanceof Action) {
                        Action actionNode = (Action) node;
                        if (stack < actionNode.count) {
                            throw new RuntimeException("归约越栈" + symbol.toSymbolString() + " -> " + item + ";"
                                    + "(" + i + ")" + stack + ":" + actionNode.count);
                        }
                        stack -= actionNode.count;
                        stack++;
                    } else if (node instanceof Symbol) {
                        Symbol symbolNode = (Symbol) node;
                        if (stack < symbolNode.input.length) {
                            throw new RuntimeException("符号越栈" + symbol.toSymbolString() + " -> " + item + ";"
                                    + "(" + i + ")" + stack + ":" + symbolNode.input.length);
                        }
                        stack -= symbolNode.input.length;
                        stack++;
                    }
                }
                if (stack != 1) {
                    throw new RuntimeException("错误的栈深度" + symbol.toSymbolString() + " -> " + item + ";" + stack);
                }
            }
        }
    }

}
