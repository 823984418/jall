package net.dxzc.jall;

/**
 * 归约行为.
 */
public final class Action extends Node {

    /**
     * 建立一个归约行为.
     *
     * @param type   归约行为的返回类型
     * @param method 调用的方法/函数
     * @param count  归约数/参数个数
     */
    public Action(String type, String method, int count) {
        if (type == null || method == null || count < 0) {
            throw new RuntimeException();
        }
        this.type = type;
        this.method = method;
        this.count = count;
    }

    /**
     * 归约类型.
     */
    public final String type;

    /**
     * 归约调用.
     */
    public final String method;

    /**
     * 归约数.
     */
    public final int count;

    @Override
    public String toString() {
        return "{" + method + "(" + count + ")" + type + "}";
    }

}
