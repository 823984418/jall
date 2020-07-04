package net.dxzc.jall;

@Symbols({"L()float", "E()float", "T()float", "F()float"})
public abstract class Test extends Parser {

    public static final int eof = 0;
    public static final int lf = 1;
    public static final int rf = 2;
    public static final int id = 3;
    public static final int add = 4;
    public static final int sub = 5;
    public static final int mut = 6;
    public static final int div = 7;

    @As({"L", "E", "eof"})
    protected float show(float a, String b) {
        System.out.println(a);
        return a;
    }

    @As({"E", "E", "add", "T"})
    protected float add(float a, String b, float c) {
        return a + c;
    }

    @As({"E", "E", "sub", "T"})
    protected float sub(float a, String b, float c) {
        return a - c;
    }

    @As({"E", "T"})
    protected float e(float a) {
        return a;
    }

    @As({"T", "T", "mut", "F"})
    protected float mut(float a, String b, float c) {
        return a * c;
    }

    @As({"T", "T", "div", "F"})
    protected float div(float a, String b, float c) {
        return a / c;
    }

    @As({"T", "F"})
    protected float t(float a) {
        return a;
    }

    @As({"F", "lf", "E", "rf"})
    protected float set(String a, float b, String c) {
        return b;
    }

    @As({"F", "id"})
    protected float value(String a) {
        return Float.parseFloat(a);
    }

}
