package net.dxzc.jall;

public abstract class Name extends Node {

    public Name(String name) {
        if (name == null) {
            throw new RuntimeException();
        }
        this.name = name;
    }

    public final String name;

    @Override
    public String toString() {
        return name;
    }

}
