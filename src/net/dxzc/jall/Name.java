package net.dxzc.jall;

/**
 * 一个符号.
 * 可以是终结或非终结符号.
 */
public abstract class Name extends Node {

    /**
     * 构建符号并指定名字.
     *
     * @param name 名字
     */
    public Name(String name) {
        if (name == null) {
            throw new RuntimeException();
        }
        this.name = name;
    }

    /**
     * 所具有的名字.
     */
    public final String name;

    @Override
    public String toString() {
        return name;
    }

}
