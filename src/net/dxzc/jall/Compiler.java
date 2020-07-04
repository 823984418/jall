package net.dxzc.jall;

import java.util.*;

public class Compiler {

    public void leftInit(Language language) {
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

    public void afterInit(Language language) {
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
                            language.symbols.add(i++, useSymbol);
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
            symbol.items.clear();
            symbol.items.addAll(newItem);
        }
    }

    public String compile(Language language) {
        // 创建空产生式信息
        Item[] empty = new Item[language.symbols.size()];
        for (int i = 0; i < language.symbols.size(); i++) {
            Symbol symbol = language.symbols.get(i);
            for (int t = 0; t < symbol.items.size(); t++) {
                Item item = symbol.items.get(t);
                int firstIndex = item.getFirstNameIndex();
                if (firstIndex < 0) {
                    if (empty[i] != null) {
                        throw new RuntimeException();
                    } else {
                        empty[i] = item;
                    }
                }
            }
        }

        StringBuilder code = new StringBuilder();
        for (int i = 0; i < language.symbols.size(); i++) {
            Symbol symbol = language.symbols.get(i);
            if (i == 0) {
                code.append("public ").append(symbol.type).append(" parser").append("(");
                if (symbol.input.length != 0) {
                    code.append(symbol.input[0]).append(" v0");
                    for (int t = 1; t < symbol.input.length; t++) {
                        code.append(", ").append(symbol.input[0]).append(" v").append(t);
                    }
                }
                code.append(") {\n");
                code.append("next();\n");
                code.append("return ").append(symbol.name).append("(");
                if (symbol.input.length != 0) {
                    code.append("v0");
                    for (int t = 1; t < symbol.input.length; t++) {
                        code.append(", v").append(t);
                    }
                }
                code.append(");\n");
                code.append("}\n\n");
            }
            MethodBuilder builder = new MethodBuilder("private", symbol.name, symbol.type, symbol.input);
            builder.sb.append("switch(token){\n");
            LinkedList<Token> need = new LinkedList<>();
            for (Item item : symbol.items) {
                int firstIndex = item.getFirstNameIndex();
                if (firstIndex < 0) {
                    continue;
                }
                need.add((Token) item.defines[firstIndex]);
                builder.sb.append("case ").append(item.defines[firstIndex]).append(":{\n");
                builder.item(item);
                builder.sb.append("}\n");
            }
            builder.sb.append("default:{\n");
            if (empty[i] != null) {
                builder.item(empty[i]);
            } else {
                builder.sb.append("throw need(token");
                while (!need.isEmpty()) {
                    builder.sb.append(", ");
                    builder.sb.append(need.poll().name);
                }
                builder.sb.append(");\n");
            }
            builder.sb.append("}\n");

            builder.sb.append("}\n");
            code.append(builder.end());
        }

        return code.toString();
    }

}
