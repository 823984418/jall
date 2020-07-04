package net.dxzc.jall;

import java.util.Arrays;
import java.util.LinkedList;

public abstract class Parser {

    public Parser() {
    }

    protected int token;

    private LinkedList<String> queue = new LinkedList<>();

    protected void check(int token) {
        if (this.token != token) {
            throw need(this.token, token);
        }
        next();
    }

    protected void next() {
        queue.add(onNext());
    }

    protected abstract String onNext();

    protected RuntimeException need(int token, int... needs) {
        return new RuntimeException("需要" + Arrays.toString(needs) + "但只有" + token);
    }

    protected String push() {
        return queue.pollFirst();
    }


}
