package net.dxzc.jall;

/**
 * 归约行为.
 */
public final class Action extends Node {

    public Action(String type, String method, int count) {
        if (type == null || method == null || count < 0) {
            throw new RuntimeException();
        }
        this.type = type;
        this.method = method;
        this.count = count;
    }

    public final String type;

    public final String method;

    public final int count;

    @Override
    public String toString() {
        return "{" + method + "(" + count + ")" + type + "}";
    }

}
