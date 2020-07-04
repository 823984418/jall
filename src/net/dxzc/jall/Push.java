package net.dxzc.jall;

/**
 * 移入行为.
 */
public final class Push extends Node {

    public static final Push PUSH = new Push(1);

    public Push(int count) {
        if (count <= 0) {
            throw new RuntimeException();
        }
        this.count = count;
    }

    public final int count;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append('>');
        }
        return sb.toString();
    }

}
